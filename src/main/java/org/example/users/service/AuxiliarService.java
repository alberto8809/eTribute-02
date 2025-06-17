package org.example.users.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.users.model.*;
import org.example.users.repository.AuxiliarRepository;
import org.example.users.repository.CuentaContableRepository;
import org.example.users.util.CreateFilePDFBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class AuxiliarService {

    @Autowired
    AuxiliarRepository auxiliarRepository;
    @Autowired
    CuentaContableRepository cuentaContableRepository;
    @Autowired
    JdbcClient jdbc;
    public static final Logger LOGGER = LogManager.getLogger(AuxiliarService.class);

    public Map<String, List<ListAuxiliar>> getListAccounts(String account, String inicial_date, String final_date) {
        Map<String, List<ListAuxiliar>> list = new HashMap<>();
        Map<String, ListAuxiliar> addingResponse = new HashMap<>();
        List<ListAuxiliar> response = new ArrayList<>();
        List<Auxiliar> list1 = auxiliarRepository.getAuxiliar(account, inicial_date, final_date);
        if (list1 != null) {
            for (Auxiliar aux : list1) {
                ListAuxiliar listAuxiliar = new ListAuxiliar();
                listAuxiliar.setConcepto(aux.getDescripcion());
                listAuxiliar.setCuenta(aux.getCuenta());
                if (!aux.getCuenta().equals("0.0")) {
                    addingResponse.put(aux.getCuenta(), listAuxiliar);
                }

            }
        }

        for (Map.Entry<String, ListAuxiliar> re : addingResponse.entrySet()) {
            response.add(re.getValue());
        }

        list.put("cuentas", response);

        return list;
    }

    public Map<String, Object> getValuesOfTable(String cuenta, String initial, String final_date) {
        Map<String, Object> obj = new HashMap<>();
        List<Auxiliar> auxList = auxiliarRepository.valuesOfTable(cuenta, initial, final_date);
        HeaderAuxiliar headerAux = new HeaderAuxiliar();

        if (!auxList.isEmpty()) {

            headerAux.setCuenta(auxList.get(0).getCuenta());
            headerAux.setNombre(auxList.get(0).getDescripcion());
            headerAux.setSaldoInicial(headerAux.getSaldoInicial() == null ? "0" : auxiliarRepository.getSumInicial(cuenta));
            headerAux.setDebe(headerAux.getDebe() == null ? auxiliarRepository.getSumDebeByAccountId(cuenta) : headerAux.getDebe());
            headerAux.setHaber(headerAux.getHaber() == null ? auxiliarRepository.getSumHaberByAccountId(cuenta) : headerAux.getHaber());
            float sumaFinal = (Float.parseFloat(headerAux.getSaldoInicial() == null ? "0" : headerAux.getSaldoInicial()) + Float.parseFloat(headerAux.getHaber() == null ? "0" : headerAux.getHaber())) - Float.parseFloat(headerAux.getDebe() == null ? "0" : headerAux.getDebe());
            headerAux.setSaldoFinal(headerAux.getSaldoFinal() == null ? auxiliarRepository.getSaldoFinal(cuenta) : String.valueOf(sumaFinal));

            obj.put("header", headerAux);
            obj.put("body", auxList);
            obj.put("url_pdf", "");
            //revisar el debe y haber
            return obj;
        } else {
            return null;
        }

    }

    public boolean createFileByAccount(String cuenta, String inicial, String final_) {
        List<Auxiliar> auxiliars = auxiliarRepository.valuesOfPDF(cuenta, inicial, final_);
        if (!auxiliars.isEmpty()) {
            CreateFilePDFBalance.makeFileAuxiliar(auxiliars);
            return true;
        }
        return false;
    }


    public Map<String, Object> getAllBalance(String account_id, String initial_date, String final_date) {
        List<Auxiliar> listAuxiliars = auxiliarRepository.getAuxiliar(account_id, initial_date, final_date);
        List<Object> values = new ArrayList<>();
        Map<String, String> acc = new HashMap<>();
        Map<String, Object> obj = new HashMap<>();
        Map<String, Object> obj2 = new HashMap<>();
        if (!listAuxiliars.isEmpty()) {
            for (Auxiliar listAuxiliar : listAuxiliars) {
                if (!listAuxiliar.getCuenta().equals("0.0")) {
                    acc.put(listAuxiliar.getCuenta(), listAuxiliar.getDescripcion());
                }
            }

            for (Map.Entry<String, String> re : acc.entrySet()) {
                Balance balance = new Balance();
                balance.setCuenta(re.getKey());
                balance.setNombre(re.getValue());
                //balance.setDeudor_inicial(auxiliarRepository.getDeudor_inicial(listAuxiliar.getCuenta(), initial_date, final_date));
                balance.setDeudor_inicial("0");
                //balance.setAcredor_inicial(auxiliarRepository.getAcredor_final(listAuxiliar.getCuenta(), initial_date, final_date));
                balance.setAcredor_inicial("0");
                balance.setDebe(auxiliarRepository.getSumCargo(re.getKey()));
                balance.setHaber(auxiliarRepository.getSumAbono(re.getKey()));

                float sum = Float.parseFloat(balance.getDeudor_inicial()) + Float.parseFloat(balance.getDebe());
                float sum1 = Float.parseFloat(balance.getAcredor_inicial()) + Float.parseFloat(balance.getHaber());
                balance.setDeudor_final(String.valueOf(sum));
                balance.setAcredor_final(String.valueOf(sum1));
                obj2.put(re.getKey(), balance);
            }

            float deudorInicial_total = 0;
            float acredorinical_total = 0;
            float debe_total = 0;
            float haber_total = 0;
            float deudorFinal_total = 0;
            float acredorFinal_total = 0;
            int i = 0;
            for (Map.Entry<String, Object> ob : obj2.entrySet()) {
                values.add(ob.getValue());
                Balance b = (Balance) values.get(i);
                deudorInicial_total += Float.parseFloat(b.getDebe());
                acredorinical_total += Float.parseFloat(b.getHaber());
                debe_total += Float.parseFloat(b.getDebe());
                haber_total += Float.parseFloat(b.getHaber());
                deudorFinal_total += deudorInicial_total + Float.parseFloat(b.getDebe());
                acredorFinal_total += acredorinical_total + Float.parseFloat(b.getHaber());
                i++;
            }


            BalanceTotal balanceTotal = new BalanceTotal();
            balanceTotal.setDeudorInicial_total(String.valueOf(deudorInicial_total));
            balanceTotal.setAcredorinical_total(String.valueOf(acredorinical_total));
            balanceTotal.setDebe_total(String.valueOf(debe_total));
            balanceTotal.setHaber_total(String.valueOf(haber_total));
            balanceTotal.setDeudorFinal_total(String.valueOf(deudorFinal_total));
            balanceTotal.setAcredorFinal_total(String.valueOf(acredorFinal_total));


            obj.put("balanza", values);
            obj.put("sumas", balanceTotal);
            obj.put("url_pdf", "");
            return obj;
        } else {
            return null;
        }

    }


    public Map<String, Object> getListBalanceDate(String account_id, String inicial_date, String final_date) {
        HashMap<String, Object> response = new HashMap<>();
        List<Object> generaBalanceResponses = new ArrayList<>();
        List<ListAuxiliar> uax = new ArrayList<>();
        List<Map<String, Object>> obj = jdbc.sql("SELECT * FROM dbmaster.cuentas WHERE codigo_agrupador='101' OR codigo_agrupador ='102' OR codigo_agrupador ='103' OR codigo_agrupador ='105' OR codigo_agrupador ='107' OR codigo_agrupador ='110' OR codigo_agrupador ='113' OR codigo_agrupador ='114' OR codigo_agrupador ='118' OR codigo_agrupador ='119'")
                .query().listOfRows();

        Map<String, List<ListAuxiliar>> map = getListAccounts(account_id, inicial_date, final_date);

        for (Map.Entry<String, List<ListAuxiliar>> ob : map.entrySet()) {
            uax = ob.getValue();
        }

        List<String> uax2 = new ArrayList<>();
        for (ListAuxiliar list : uax) {
            uax2.add(list.getCuenta().substring(0, 3));
        }


        double total_activoCirculante = 0;
        for (Map<String, Object> cuentaContable : obj) {
            GeneraBalanceResponse generaBalanceResponse = new GeneraBalanceResponse();
            generaBalanceResponse.setCuenta_id(cuentaContable.get("codigo_agrupador").toString());
            generaBalanceResponse.setDescripcion(cuentaContable.get("nombre_cuenta").toString());

            total_activoCirculante += generaBalanceResponse.getTotal();

            for (String id : uax2) {
                if (generaBalanceResponse.getCuenta_id().contains(id)) {
                    generaBalanceResponse.setTotal(Float.parseFloat(auxiliarRepository.getSaldoFinalBalance(id)));
                }
            }

            generaBalanceResponses.add(generaBalanceResponse);
        }

        response.put("Activo_Circulante", generaBalanceResponses);


        List<Map<String, Object>> obj2 = jdbc.sql("SELECT *  FROM dbmaster.cuentas WHERE codigo_agrupador='201' OR codigo_agrupador ='205' OR codigo_agrupador ='208' OR codigo_agrupador ='209' OR codigo_agrupador ='210' OR codigo_agrupador ='213' OR codigo_agrupador ='216' OR codigo_agrupador ='218'")
                .query().listOfRows();

        List<Object> pasivo = new ArrayList<>();
        double total_pasivoCirculante = 0;
        for (Map<String, Object> cuentaContable : obj2) {
            GeneraBalanceResponse generaBalanceResponse = new GeneraBalanceResponse();
            generaBalanceResponse.setCuenta_id(cuentaContable.get("codigo_agrupador").toString());
            generaBalanceResponse.setDescripcion(cuentaContable.get("nombre_cuenta").toString());
            generaBalanceResponse.setTotal(0);
            total_pasivoCirculante += generaBalanceResponse.getTotal();
            pasivo.add(generaBalanceResponse);
        }
        response.put("Pasivo_Circulante", pasivo);

        List<CuentaContable> list2 = cuentaContableRepository.getValuestoBlanaceActivoFijo(account_id, inicial_date, final_date);
        List<Object> activo = new ArrayList<>();
        double total_activoFijo = 0;
        for (CuentaContable cuentaContable : list2) {
            GeneraBalanceResponse generaBalanceResponse = new GeneraBalanceResponse();
            generaBalanceResponse.setCuenta_id(cuentaContable.getCodigo_agrupador());
            generaBalanceResponse.setDescripcion(cuentaContable.getNombre_cuenta());
            generaBalanceResponse.setTotal(0);
            total_activoFijo += generaBalanceResponse.getTotal();
            activo.add(generaBalanceResponse);
        }

        response.put("Activo_Fijo", activo);


        List<CuentaContable> list3 = cuentaContableRepository.getValuesBalanceActivoDiferido(account_id, inicial_date, final_date);
        List<Object> activo_diferido = new ArrayList<>();
        double total_activoDiferido = 0;
        for (CuentaContable cuentaContable : list3) {
            GeneraBalanceResponse generaBalanceResponse = new GeneraBalanceResponse();
            generaBalanceResponse.setCuenta_id(cuentaContable.getCodigo_agrupador());
            generaBalanceResponse.setDescripcion(cuentaContable.getNombre_cuenta());
            generaBalanceResponse.setTotal(0);
            total_activoDiferido += generaBalanceResponse.getTotal();
            activo_diferido.add(generaBalanceResponse);
        }

        response.put("Activo_Diferido", activo_diferido);

        List<Map<String, Object>> obj3 = jdbc.sql("SELECT *  FROM dbmaster.cuentas WHERE codigo_agrupador='301' OR codigo_agrupador ='304' OR codigo_agrupador ='305'")
                .query().listOfRows();

        List<Object> capital = new ArrayList<>();
        double total_capital = 0;
        for (Map<String, Object> cuentaContable : obj3) {
            GeneraBalanceResponse generaBalanceResponse = new GeneraBalanceResponse();
            generaBalanceResponse.setCuenta_id(cuentaContable.get("codigo_agrupador").toString());
            generaBalanceResponse.setDescripcion(cuentaContable.get("nombre_cuenta").toString());
            generaBalanceResponse.setTotal(0);
            total_capital += generaBalanceResponse.getTotal();
            capital.add(generaBalanceResponse);
        }

        response.put("Capital_Contable", capital);


        Map<String, Double> totales = new HashMap<>();
        totales.put("Total_Activo_Circulante", total_activoCirculante);
        totales.put("Total_Pasivo_Circulante", total_pasivoCirculante);
        totales.put("Total_Activo_Fijo", total_activoFijo);
        //totales.put("Total_Pasivo_Largo_Plazo", getSum);
        totales.put("Total_Pasivo_Largo_Plazo", 0.0);
        totales.put("Total_Activo_Diferido", total_activoDiferido);
        totales.put("Total_Capital_Diferido", total_capital);
        totales.put("TOTAL_ACTIVO", total_activoCirculante + total_activoDiferido);
        totales.put("TOTAL_PASIVO_MAS_CAPITAL", total_pasivoCirculante + total_capital);
        response.put("Totales", totales);


        return response;
    }


    public Map<Object, Object> getValuesOfResults(String account_id, String initial, String final_) {

        Map<Object, Object> back = new HashMap<>();
        Map<Object, Object> obj = new HashMap<>();
        List<Object> list2 = new ArrayList<>();

        CuentaContable c = cuentaContableRepository.getValuesOf401(account_id, initial, final_);
        BalanceGeneral b = new BalanceGeneral();
        b.setNivel(c.getNivel());
        b.setCodigo_agrupador(c.getCodigo_agrupador());
        b.setNombre_cuenta(c.getNombre_cuenta());
        List<String> acc3 = cuentaContableRepository.getAccountsOfTable("401", initial, final_);
        double sum3 = 0;
        for (String s : acc3) {
            sum3 += Double.parseDouble(cuentaContableRepository.getValuesOfTable(s, initial, final_));
        }

        b.setImporte_mensual(String.valueOf(sum3));
        b.setPorcentaje_mensual("0");
        b.setImporte_anual(String.valueOf(sum3));
        b.setPorcentaje_anual("0");


        CuentaContable d = cuentaContableRepository.getValuesOf501(account_id, initial, final_);
        BalanceGeneral b2 = new BalanceGeneral();
        b2.setNivel(d.getNivel());
        b2.setCodigo_agrupador(d.getCodigo_agrupador());
        b2.setNombre_cuenta(d.getNombre_cuenta());
        List<String> acc2 = cuentaContableRepository.getAccountsOfTable("501", initial, final_);
        double sum2 = 0;
        for (String s : acc2) {
            sum2 += Double.parseDouble(cuentaContableRepository.getValuesOfTable(s, initial, final_));
        }
        b2.setImporte_mensual(String.valueOf(sum2));
        b2.setPorcentaje_mensual("0");
        b2.setImporte_anual(String.valueOf(sum2));
        b2.setPorcentaje_anual("0");


        CuentaContable c9 = cuentaContableRepository.getValuesOf605(account_id, initial, final_);
        BalanceGeneral b9 = new BalanceGeneral();
        b9.setNivel(c9.getNivel());
        b9.setCodigo_agrupador(c9.getCodigo_agrupador());
        b9.setNombre_cuenta(c9.getNombre_cuenta());
        b9.setImporte_anual("0");
        List<String> acc9 = cuentaContableRepository.getAccountsOfTable("605", initial, final_);
        double sum9 = 0;
        for (String s : acc9) {
            sum9 += Double.parseDouble(cuentaContableRepository.getValuesOfTable(s, initial, final_));
        }
        b9.setImporte_mensual(String.valueOf(sum9));
        b9.setPorcentaje_mensual("0");
        b9.setImporte_anual(String.valueOf(sum9));
        b9.setPorcentaje_anual("0");


        CuentaContable c3 = cuentaContableRepository.getValuesOf503(account_id, initial, final_);
        BalanceGeneral b3 = new BalanceGeneral();
        b3.setNivel(c3.getNivel());
        b3.setCodigo_agrupador(c3.getCodigo_agrupador());
        b3.setNombre_cuenta(c3.getNombre_cuenta());
        List<String> acc33 = cuentaContableRepository.getAccountsOfTable("503", initial, final_);
        double sum33 = 0;
        for (String s : acc33) {
            sum33 += Double.parseDouble(cuentaContableRepository.getValuesOfTable(s, initial, final_));
        }
        b3.setImporte_mensual(String.valueOf(sum33));
        b3.setPorcentaje_mensual("0");
        b3.setImporte_anual(String.valueOf(sum33));
        b3.setPorcentaje_anual("0");


        CuentaContable c4 = cuentaContableRepository.getValuesOf601(account_id, initial, final_);
        BalanceGeneral b4 = new BalanceGeneral();
        b4.setNivel(c4.getNivel());
        b4.setCodigo_agrupador(c4.getCodigo_agrupador());
        b4.setNombre_cuenta(c4.getNombre_cuenta());
        List<String> acc = cuentaContableRepository.getAccountsOfTable("601", initial, final_);
        double sum = 0;
        for (String s : acc) {
            sum += Double.parseDouble(cuentaContableRepository.getValuesOfTable(s, initial, final_));
        }
        b4.setImporte_mensual(String.valueOf(sum));
        b4.setPorcentaje_mensual("0");
        b4.setImporte_anual(String.valueOf(sum));
        b4.setPorcentaje_anual("0");


        CuentaContable d1 = cuentaContableRepository.getValuesOf613(account_id, initial, final_);
        BalanceGeneral b0 = new BalanceGeneral();
        b0.setNivel(d1.getNivel());
        b0.setCodigo_agrupador(d1.getCodigo_agrupador());
        b0.setNombre_cuenta(d1.getNombre_cuenta());
        List<String> acc13 = cuentaContableRepository.getAccountsOfTable("613", initial, final_);
        double sum13 = 0;
        for (String s : acc13) {
            sum13 += Double.parseDouble(cuentaContableRepository.getValuesOfTable(s, initial, final_));
        }
        b0.setImporte_mensual(String.valueOf(sum13));
        b0.setPorcentaje_mensual("0");
        b0.setImporte_anual(String.valueOf(sum13));
        b0.setPorcentaje_anual("0");

        CuentaContable c6 = cuentaContableRepository.getValuesOf704(account_id, initial, final_);
        BalanceGeneral b6 = new BalanceGeneral();
        b6.setNivel(c6.getNivel());
        b6.setCodigo_agrupador(c6.getCodigo_agrupador());
        b6.setNombre_cuenta(c6.getNombre_cuenta());

        List<String> acc04 = cuentaContableRepository.getAccountsOfTable("704", initial, final_);
        double sum04 = 0;
        for (String s : acc04) {
            sum04 += Double.parseDouble(cuentaContableRepository.getValuesOfTable(s, initial, final_));
        }
        b0.setImporte_mensual(String.valueOf(sum04));
        b0.setPorcentaje_mensual("0");
        b0.setImporte_anual(String.valueOf(sum04));
        b0.setPorcentaje_anual("0");


        CuentaContable c7 = cuentaContableRepository.getValuesOf701(account_id, initial, final_);
        BalanceGeneral b7 = new BalanceGeneral();
        b7.setNivel(c7.getNivel());
        b7.setCodigo_agrupador(c7.getCodigo_agrupador());
        b7.setNombre_cuenta(c7.getNombre_cuenta());

        List<String> acc01 = cuentaContableRepository.getAccountsOfTable("701", initial, final_);
        double sum01 = 0;
        for (String s : acc01) {
            sum01 += Double.parseDouble(cuentaContableRepository.getValuesOfTable(s, initial, final_));
        }
        b7.setImporte_mensual(String.valueOf(sum01));
        b7.setPorcentaje_mensual("0");
        b7.setImporte_anual(String.valueOf(sum01));
        b7.setPorcentaje_anual("0");


        list2.add(b);
        list2.add(b2);
        list2.add(b9);
        list2.add(b3);
        list2.add(b4);
        list2.add(b0);
        list2.add(b6);
        list2.add(b7);


        obj.put("registro", list2);
        back.put("body", obj);
        back.put("url_pdf", "");
        return back;
    }
}