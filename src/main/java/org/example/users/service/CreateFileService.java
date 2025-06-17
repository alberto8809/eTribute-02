package org.example.users.service;


import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.users.model.*;
import org.example.users.model.CuentaContable;
import org.example.users.repository.*;
import org.example.users.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.users.util.Abono.*;
import static org.example.users.util.CFDI.*;
import static org.example.users.util.Cargo.*;
import static org.example.users.util.CuentaContable.*;
import static org.example.users.util.Payment.*;
import static org.example.users.util.Percepcion.*;
import static org.example.users.util.Regimen.*;


@Service
public class CreateFileService {
    private static String local_path = "/Users/marioalberto/IdeaProjects/EtributeBack-1/";
    //private static final String server_path = "/home/ubuntu/endpoints/eTribute-all/";
    public static final Logger LOGGER = LogManager.getLogger(CreateFileService.class);

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClaveProductoServRepository claveProductoServRepository;
    @Autowired
    private CuentaContableRepository cuentaContableRepository;
    @Autowired
    private MethodOfPaymentRepository methodOfPaymentRepository;
    @Autowired
    private SaveObjRepository saveObjRepository;
    @Autowired
    private FileDescriptorMetrics file;


    public CreateFileService() {
    }

    public CreateFileService(AccountRepository accountRepository, ClaveProductoServRepository claveProductoServRepository, CuentaContableRepository cuentaContableRepository, MethodOfPaymentRepository methodOfPaymentRepository, SaveObjRepository saveObjRepository) {
        this.accountRepository = accountRepository;
        this.claveProductoServRepository = claveProductoServRepository;
        this.cuentaContableRepository = cuentaContableRepository;
        this.methodOfPaymentRepository = methodOfPaymentRepository;
        this.saveObjRepository = saveObjRepository;
    }

    public List<String> getClaveProductoService(List<String> c_claveprodserv, String type, List<String> nomine) {
        List<String> claveProductoServs = new ArrayList<>();

        for (String clv : c_claveprodserv) {
            if (type.equals(E.getValue())) {
                claveProductoServs.add(claveProductoServRepository.getClaveProductoS(R010.getValue()));
            } else if (type.equals(N.getValue())) {

                claveProductoServs.add(claveProductoServRepository.getClaveProductoS(clv));
                for (String value : nomine) {
                    claveProductoServs.add(methodOfPaymentRepository.getCuentaContableByNomina(value));
                }
            } else {
                claveProductoServs.add(claveProductoServRepository.getClaveProductoS(clv));
            }
        }
        return claveProductoServs;
    }


    public List<String> getCuentaCobtableList(List<String> claveProductoServ) {
        List<String> account = new ArrayList<>();

        for (String clv : claveProductoServ) {
            account.add(cuentaContableRepository.getCuantaContableMethod(clv));
        }
        return account;
    }

    public List<String> getIvaIeps(Map<String, String> claveProductoServ, String type, String amount) {
        List<String> iva = new ArrayList<>();

        if (type.equals(P.getValue())) {
            for (String clv : claveProductoServ.keySet()) {
                iva.add(methodOfPaymentRepository.getCuentaContableByTax(clv));
            }
        } else {

            for (String clv : claveProductoServ.keySet()) {
                iva.add(methodOfPaymentRepository.getCuentaContableByTax(clv));
            }
        }
        return iva;
    }

    public boolean uploadToS3(MultipartFile[] files, String rfc) throws IOException {
        if (files.length < 0) {
            throw new RuntimeException("Uploaded file is empty");
        }

        String fileName = "";
        for (MultipartFile file : files) {
            fileName = file.getOriginalFilename();
            File uploadDir = new File(local_path + rfc + "/xml");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Path filePath = Paths.get(local_path + rfc + "/xml", fileName);
            Files.write(filePath, file.getBytes());

            UploadFileToS3_Policies.upload(fileName, rfc);

        }
        return true;
    }


    public Map<String, Object> getDescriptionPolicy(String rfc, String initial_date, String final_date) {

        Map<String, Object> filesXMLFromAWS = UploadFileToS3_Policies.getFileFromAWS(rfc, "xml", initial_date, final_date);
        Map<String, Object> finalResult = new HashMap<>();

        int value = createPolicy(rfc, EGRESOS.getValue(), initial_date, final_date);
        if (value == 1) {

            Map<String, Object> filesPDFromAWS = UploadFileToS3_Policies.getFileFromAWS(rfc, "pdf", initial_date, final_date);

            List<Response> eg = (List<Response>) filesXMLFromAWS.get("Emitidas");
            List<Response> rv = (List<Response>) filesXMLFromAWS.get("Recibidas");
            Map<String, String> obj3 = (Map<String, String>) filesPDFromAWS.get("listOfEgresospdf");
            Map<String, String> obj4 = (Map<String, String>) filesPDFromAWS.get("listOfIngresospdf");

            for (Map.Entry<String, String> r : obj3.entrySet()) {
                String filePDF = r.getKey().replace(".pdf", "");
                for (Response r1 : eg) {
                    if (r1.getUrl_xml().contains(filePDF)) {
                        r1.setUrl_pdf(r.getValue());
                    }
                }

            }

            for (Map.Entry<String, String> r : obj4.entrySet()) {
                String filePDF = r.getKey().replace(".pdf", "");
                for (Response rd : rv) {
                    if (rd.getUrl_xml().contains(filePDF)) {
                        rd.setUrl_pdf(r.getValue());
                    }
                }

            }

            eg.removeIf(n -> n.getUrl_pdf() == null);
            rv.removeIf(n -> n.getUrl_pdf() == null);


            finalResult.put("Emitidas", filesXMLFromAWS.get("Recibidas"));
            finalResult.put("Recibidas", filesXMLFromAWS.get("Emitidas"));

        } else if (value == 2) {
            LOGGER.error(" -----------------------------------------------------------------------------------------  \n\n ");

            createPolicy(rfc, INGRESOS.getValue(), initial_date, final_date);

            Map<String, Object> filesPDFromAWS = UploadFileToS3_Policies.getFileFromAWS(rfc, "pdf", initial_date, final_date);

            List<Response> eg = (List<Response>) filesXMLFromAWS.get("Emitidas");
            List<Response> rv = (List<Response>) filesXMLFromAWS.get("Recibidas");
            Map<String, String> obj3 = (Map<String, String>) filesPDFromAWS.get("listOfEgresospdf");
            Map<String, String> obj4 = (Map<String, String>) filesPDFromAWS.get("listOfIngresospdf");

            for (Map.Entry<String, String> r : obj3.entrySet()) {
                String filePDF = r.getKey().replace(".pdf", "");
                for (Response r1 : eg) {
                    if (r1.getUrl_xml().contains(filePDF)) {
                        r1.setUrl_pdf(r.getValue());
                    }
                }

            }

            for (Map.Entry<String, String> r : obj4.entrySet()) {
                String filePDF = r.getKey().replace(".pdf", "");
                for (Response rd : rv) {
                    if (rd.getUrl_xml().contains(filePDF)) {
                        rd.setUrl_pdf(r.getValue());
                    }
                }

            }


            eg.removeIf(n -> n.getUrl_pdf() == null);
            rv.removeIf(n -> n.getUrl_pdf() == null);


            finalResult.put("Emitidas", filesXMLFromAWS.get("Recibidas"));
            finalResult.put("Recibidas", filesXMLFromAWS.get("Emitidas"));


        }
        return finalResult;
    }


    public int createPolicy(String rfc, String type, String initial_date, String final_date) {
        try {
            Random rand = new Random();
            int account_id = accountRepository.getAccountByAccount_id(rfc);
            List<CuentaContable> cuentaContable = new ArrayList<>();
            File folder = new File(local_path + rfc + "/xml/" + type + "/");
            File[] listOfFiles = folder.listFiles();
            List<String> objFromDB = saveObjRepository.getObjFromDB(rfc, type, initial_date, final_date);

            if (!objFromDB.isEmpty()) {
                if (Arrays.stream(listOfFiles).anyMatch(n -> n.getName().equals(objFromDB.get(0)))) {
                    return 1;
                }
            } else {
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        if (type.equals(EGRESOS.getValue())) {
                            PolicyObjFile policyObjFile = ParserFileEgresos.getParse(local_path + rfc + "/xml/" + type + "/" + file.getName());
                            if (policyObjFile != null) {
                                //---------------------   starting R1  I AND PUE --------------------- //
                                if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(I.getValue()) && policyObjFile.getPolicyObj().getMethodPayment().equals(PUE.getValue())) {
                                    // LOGGER.error("R1  I and PUE {}", file.getName());
                                    if (List.of(PAY99.getValue(), PAY01.getValue(), PAY04.getValue(), PAY05.getValue(), PAY06.getValue(), PAY08.getValue(), PAY12.getValue(), PAY13.getValue(), PAY14.getValue(), PAY15.getValue(), PAY17.getValue(), PAY23.getValue(), PAY24.getValue(), PAY25.getValue(), PAY26.getValue(), PAY27.getValue(), PAY28.getValue(), PAY29.getValue(), PAY30.getValue(), PAY31.getValue()).contains(policyObjFile.getPolicyObj().getTypeOfPayment())) {
                                        policyObjFile.getPolicyObj().setVenta_id(C205_99.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(OTRASFORMAS.getValue());
                                    } else {
                                        policyObjFile.getPolicyObj().setVenta_id(C102_01.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(BANCOS.getValue());
                                    }
                                    // --  case  totalImpTras 002 and 003 to Cargo I and K excel
                                    List<String> claveProductoServ = getClaveProductoService(policyObjFile.getPolicyObj().getClaveProdServ(), policyObjFile.getPolicyObj().getTypeOfComprobante(), policyObjFile.getPolicyObj().getTraslado());
                                    cuentaContable.add(cuentaContableRepository.getCuantaContable(policyObjFile.getPolicyObj().getVenta_id()));
                                    policyObjFile.getPolicyObj().setVenta_descripcion(cuentaContableRepository.getCuentaContableVenta(policyObjFile.getPolicyObj().getVenta_id()));

                                    cuentaContable.add(cuentaContableRepository.getCuantaContable(claveProductoServ.isEmpty() ? PAY01.getValue() : claveProductoServ.get(0)));
                                    List<String> id = new ArrayList<>();

                                    if (TAX02.getValue().equals(policyObjFile.getPolicyObj().getImpuestoId()) || policyObjFile.getCuenta_method() == null) {
                                        policyObjFile.setCuenta_method(C118_01.getValue());
                                        policyObjFile.setDescription_methods(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta_method()));
                                        id.add(A216_10.getValue());
                                    } else if (TAX03.getValue().equals(policyObjFile.getPolicyObj().getImpuestoId()) || policyObjFile.getCuenta_method() == null) {
                                        policyObjFile.setCuenta_method(C118_03.getValue());
                                        policyObjFile.setDescription_methods(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta_method()));
                                        id.add(A216_04.getValue());//adding
                                    }

                                    if (!policyObjFile.getPolicyObj().getRetencionId().isEmpty()) {
                                        id.add(A216_04.getValue());
                                        id.add(A119_01.getValue());
                                    } else {
                                        id.add(C118_01.getValue());
                                    }

                                    if (policyObjFile.getPolicyObj().getRetencionId() != null) {
                                        Collections.sort(policyObjFile.getPolicyObj().getRetencionPago());

                                        List<String> idR = new ArrayList<>();
                                        for (String n : policyObjFile.getPolicyObj().getRetencionId()) {
                                            if (n.equals(TAX01.getValue())) {
                                                idR.add(methodOfPaymentRepository.getCuentaContableByTax(TAX05.getValue()));
                                            } else if (n.equals(TAX02.getValue())) {
                                                idR.add(methodOfPaymentRepository.getCuentaContableByTax(TAX06.getValue()));

                                            }
                                        }
                                        Collections.sort(idR);
                                        List<String> des = new ArrayList<>();
                                        for (String idAcc : idR) {
                                            des.add(cuentaContableRepository.getCuantaContableMethod(idAcc));
                                        }

                                        policyObjFile.getPolicyObj().setRetencionId(idR.stream().distinct().collect(Collectors.toList()));
                                        policyObjFile.getPolicyObj().setRetencionDesc(des);
                                    }


                                    //  id.add(policyObjFile.getCuenta_method() == null ? C118_01.getValue() : policyObjFile.getCuenta_method());
                                    Collections.sort(id);
                                    policyObjFile.setTax_id(id);
                                    List<String> desc = new ArrayList<>();
                                    //desc.add(policyObjFile.getDescription_methods());

                                    for (String idAcc : id) {
                                        desc.add(cuentaContableRepository.getCuantaContableMethod(idAcc));
                                    }
                                    Collections.sort(desc);
                                    policyObjFile.setTax_description(desc.stream().distinct().collect(Collectors.toList()));
                                    List<String> cargo = new ArrayList<>();
                                    if (policyObjFile.getPolicyObj().getRetencion_importe() != null) {
                                        List<String> abonos = policyObjFile.getPolicyObj().getRetencion_importe();
                                        if (abonos != null) {
                                            for (String clv : abonos) {
                                                cargo.add(cuentaContableRepository.getCuantaContableMethod(clv));
                                            }
                                        }
                                    }
                                    policyObjFile.getPolicyObj().setCargo(cargo);
                                    int rand_int1 = rand.nextInt(1000000000);
                                    policyObjFile.setFolio(String.valueOf(rand_int1));
                                    policyObjFile.getPolicyObj().setConcepto_Descripcion(cuentaContableRepository.getCuantaContableMethod(claveProductoServ.get(0)));
                                    policyObjFile.setCuenta(claveProductoServ.get(0));

                                    //map contiene las cuentas
                                    Map<String, String> map = CreateFilePDFBalance.getAccounts(policyObjFile);


                                    if (CreateFilePDFPolicy.makeFileEgreso(policyObjFile, file.getName().replace(".xml", ""), rfc, type)) {

                                        List<PolicytoDB> obj = CreateFilePDFBalance.saveObjDataBase(policyObjFile, map, rfc, type, account_id, file);
//                                        saveObjRepository.saveAll(obj);

                                    }
                                    //LOGGER.error("{}", policyObjFile);


                                } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(I.getValue()) && policyObjFile.getPolicyObj().getMethodPayment().equals(PPD.getValue())) {
                                    // LOGGER.error("R1  I and PPD {}", file.getName());

                                    if (List.of(PAY99.getValue(), PAY01.getValue(), PAY04.getValue(), PAY05.getValue(), PAY06.getValue(), PAY08.getValue(), PAY12.getValue(), PAY13.getValue(), PAY14.getValue(), PAY15.getValue(), PAY17.getValue(), PAY23.getValue(), PAY24.getValue(), PAY25.getValue(), PAY26.getValue(), PAY27.getValue(), PAY28.getValue(), PAY29.getValue(), PAY30.getValue(), PAY31.getValue()).contains(policyObjFile.getPolicyObj().getTypeOfPayment())) {
                                        policyObjFile.getPolicyObj().setVenta_id(C205_99.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(OTRASFORMAS.getValue());
                                    } else {
                                        policyObjFile.getPolicyObj().setVenta_id(C102_01.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(BANCOS.getValue());
                                    }

                                    List<String> id = new ArrayList<>();

                                    if (TAX02.getValue().equals(policyObjFile.getPolicyObj().getImpuestoId()) || policyObjFile.getCuenta_method() == null) {
                                        policyObjFile.setCuenta_method(C119_01.getValue());
                                        policyObjFile.setDescription_methods(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta_method()));
                                        id.add(A216_10.getValue());
                                    } else if (TAX03.getValue().equals(policyObjFile.getPolicyObj().getImpuestoId()) || policyObjFile.getCuenta_method() == null) {
                                        policyObjFile.setCuenta_method(C119_03.getValue());
                                        policyObjFile.setDescription_methods(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta_method()));
                                        id.add(A216_04.getValue());//adding
                                    }

                                    policyObjFile.setTax_id(id);
                                    List<String> desc = new ArrayList<>();
                                    desc.add(cuentaContableRepository.getCuentaContableVenta(policyObjFile.getTax_id().get(0)));
                                    policyObjFile.setTax_description(desc);
                                    //only if is moral
                                    String moral = accountRepository.getMoralByAccountId(account_id);
                                    if (moral.equals("Sí")) {

                                        List<String> idR = new ArrayList<>();
                                        if (policyObjFile.getPolicyObj().getRetencionId() != null) {
                                            for (String n : policyObjFile.getPolicyObj().getRetencionId()) {
                                                if (n.equals(TAX01.getValue())) {
                                                    idR.add(methodOfPaymentRepository.getCuentaContableByTax(TAX05.getValue()));
                                                } else if (n.equals(TAX02.getValue())) {
                                                    idR.add(methodOfPaymentRepository.getCuentaContableByTax(TAX06.getValue()));

                                                }
                                            }
                                        }
                                        Collections.sort(idR);
                                        List<String> des = new ArrayList<>();
                                        for (String idAcc : idR) {
                                            des.add(cuentaContableRepository.getCuantaContableMethod(idAcc));
                                        }


                                        policyObjFile.getPolicyObj().setRetencionId(idR.stream().distinct().collect(Collectors.toList()));
                                        policyObjFile.getPolicyObj().setRetencionDesc(des);


                                        policyObjFile.setCuenta(claveProductoServRepository.getClaveProductoS(policyObjFile.getPolicyObj().getClaveProdServ().get(0)));
                                        policyObjFile.getPolicyObj().setConcepto_Descripcion(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));

                                        int rand_int1 = rand.nextInt(1000000000);
                                        policyObjFile.setFolio(String.valueOf(rand_int1));
                                        Map<String, String> map = CreateFilePDFBalance.getAccounts(policyObjFile);

                                        if (CreateFilePDFPolicy.makeFileEgreso(policyObjFile, file.getName().replace(".xml", ""), rfc, type)) {
                                            List<PolicytoDB> obj = CreateFilePDFBalance.saveObjDataBase(policyObjFile, map, rfc, type, account_id, file);

//                                        saveObjRepository.saveAll(obj);
                                        }

                                    }

                                } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(E.getValue()) && policyObjFile.getPolicyObj().getMethodPayment().equals(PUE.getValue())) {
                                    //R2 PUE
                                    if (List.of(PAY01.getValue(), PAY04.getValue(), PAY05.getValue(),
                                            PAY06.getValue(), PAY08.getValue(), PAY12.getValue(),
                                            PAY13.getValue(), PAY14.getValue(), PAY15.getValue(),
                                            PAY17.getValue(), PAY23.getValue(), PAY24.getValue(),
                                            PAY25.getValue(), PAY26.getValue(), PAY27.getValue(),
                                            PAY28.getValue(), PAY99.getValue(), PAY29.getValue(),
                                            PAY30.getValue(), PAY31.getValue()).contains(policyObjFile.getPolicyObj().getTypeOfPayment())) {

                                        policyObjFile.getPolicyObj().setVenta_id(C205_99.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(OTRASFORMAS.getValue());

                                    } else {

                                        policyObjFile.getPolicyObj().setVenta_id(C102_01.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(BANCOS.getValue());

                                    }

                                    List<String> id = new ArrayList<>();
                                    List<String> des = new ArrayList<>();


                                    policyObjFile.setCuenta_method(A503_01.getValue());
                                    policyObjFile.setDescription_methods(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta_method()));

                                    if (policyObjFile.getPolicyObj().getConceptoTipoFactorPPD() != null) {
                                        for (String imp : policyObjFile.getPolicyObj().getConceptoTipoFactorPPD()) {
                                            if (imp.equals(TAX02.getValue())) {
                                                //Impuestos TotalImpuestosTrasladados == 002
                                                id.add(A118_01.getValue());
                                                //RetencionImpuesto="002
                                                id.add(C216_10.getValue());
                                            } else if (TAX03.getValue().equals(imp)) {
                                                //TotalImpuestosTrasladados "003"
                                                id.add(A118_03.getValue());

                                            } else if (TAX01.getValue().equals(imp)) {
                                                //RetencionImpuesto="001"
                                                id.add(C216_04.getValue());
                                            }
                                        }
                                    }


                                    id.add(C216_03.getValue());
                                    Collections.sort(id);
                                    for (String idAcc : id) {
                                        des.add(cuentaContableRepository.getCuantaContableMethod(idAcc));
                                    }

                                    policyObjFile.getPolicyObj().setRetencionId(id);
                                    policyObjFile.getPolicyObj().setRetencionDesc(des);

                                    int rand_int1 = rand.nextInt(1000000000);
                                    policyObjFile.setFolio(String.valueOf(rand_int1));

                                    HashMap<String, String> map = CreateFilePDFBalance.getAccountsByRetencionId(policyObjFile);

                                    if (CreateFilePDFPolicy.makeFileEgreso(policyObjFile, file.getName().replace(".xml", ""), rfc, type)) {
                                        List<PolicytoDB> obj = CreateFilePDFBalance.saveObjDataBase(policyObjFile, map, rfc, type, account_id, file);

//                                        saveObjRepository.saveAll(obj);
                                    }

                                    //LOGGER.error("{}", policyObjFile);
                                    // LOGGER.error("R2 PUE {}", file.getName());
                                } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(E.getValue()) && policyObjFile.getPolicyObj().getMethodPayment().equals(PPD.getValue())) {
                                    //R2 PPD

                                    if (List.of(PAY01.getValue(), PAY04.getValue(), PAY05.getValue(),
                                            PAY06.getValue(), PAY08.getValue(), PAY12.getValue(),
                                            PAY13.getValue(), PAY14.getValue(), PAY15.getValue(),
                                            PAY17.getValue(), PAY23.getValue(), PAY24.getValue(),
                                            PAY25.getValue(), PAY26.getValue(), PAY27.getValue(),
                                            PAY28.getValue(), PAY99.getValue(), PAY29.getValue(),
                                            PAY30.getValue(), PAY31.getValue()).contains(policyObjFile.getPolicyObj().getTypeOfPayment())) {

                                        policyObjFile.getPolicyObj().setVenta_id(C205_99.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(OTRASFORMAS.getValue());

                                    } else {

                                        policyObjFile.getPolicyObj().setVenta_id(C102_01.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(BANCOS.getValue());

                                    }

                                    List<String> id = new ArrayList<>();
                                    List<String> des = new ArrayList<>();


                                    policyObjFile.setCuenta_method(A503_01.getValue());
                                    policyObjFile.setDescription_methods(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta_method()));

                                    if (policyObjFile.getPolicyObj().getConceptoTipoFactorPPD() != null) {
                                        for (String imp : policyObjFile.getPolicyObj().getConceptoTipoFactorPPD()) {
                                            if (imp.equals(TAX02.getValue())) {
                                                //Impuestos TotalImpuestosTrasladados == 002
                                                id.add(A119_01.getValue());
                                                //RetencionImpuesto="002
                                                id.add(C216_10.getValue());
                                            } else if (TAX03.getValue().equals(imp)) {
                                                //TotalImpuestosTrasladados "003"
                                                id.add(A119_03.getValue());

                                            } else if (TAX01.getValue().equals(imp)) {
                                                //RetencionImpuesto="001"
                                                id.add(C216_04.getValue());
                                            }
                                        }
                                    }

                                    Collections.sort(id);
                                    id.add(C216_03.getValue());

                                    for (String idAcc : id) {
                                        des.add(cuentaContableRepository.getCuantaContableMethod(idAcc));
                                    }

                                    policyObjFile.getPolicyObj().setRetencionId(id);
                                    policyObjFile.getPolicyObj().setRetencionDesc(des);

                                    int rand_int1 = rand.nextInt(1000000000);
                                    policyObjFile.setFolio(String.valueOf(rand_int1));

                                    HashMap<String, String> map = CreateFilePDFBalance.getAccountsByRetencionId(policyObjFile);


                                    if (CreateFilePDFPolicy.makeFileEgreso(policyObjFile, file.getName().replace(".xml", ""), rfc, type)) {
                                        List<PolicytoDB> obj = CreateFilePDFBalance.saveObjDataBase(policyObjFile, map, rfc, type, account_id, file);

//                                        saveObjRepository.saveAll(obj);
                                    }

                                    LOGGER.error("{}", policyObjFile);
                                    //LOGGER.error("R2 PPD {}", file.getName());


                                } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(RETENCIONES.getValue())) {
                                    policyObjFile.getPolicyObj().setVenta_id(C102_01.getValue());
                                    policyObjFile.getPolicyObj().setVenta_descripcion(BANCOS.getValue());
                                    //R3

                                    List<String> id = new ArrayList<>();
                                    List<String> des = new ArrayList<>();

                                    id.add(A401_04.getValue());
                                    //TotalIVATrasladado
                                    id.add(A208_01.getValue());
                                    //MonTotalporUsoPlataforma
                                    id.add(C205_99.getValue());
                                    //ImpuestoRet="001"
                                    id.add(A114_06.getValue());
                                    //ImpuestoRet = "002"
                                    id.add(A114_04.getValue());
                                    //"ImpuestoRet="003"
                                    id.add(A114_07.getValue());

                                    for (String idAcc : id) {
                                        des.add(cuentaContableRepository.getCuantaContableMethod(idAcc));
                                    }

                                    policyObjFile.getPolicyObj().setRetencionId(id);
                                    policyObjFile.getPolicyObj().setRetencionDesc(des);


                                    //LOGGER.error("R3 {}", file.getName());
                                    int rand_int1 = rand.nextInt(1000000000);
                                    policyObjFile.setFolio(String.valueOf(rand_int1));
                                    HashMap<String, String> map = CreateFilePDFBalance.getAccountsByRetencionId(policyObjFile);

                                    if (CreateFilePDFPolicy.makeFileEgreso(policyObjFile, file.getName().replace(".xml", ""), rfc, type)) {
                                        List<PolicytoDB> obj = CreateFilePDFBalance.saveObjDataBase(policyObjFile, map, rfc, type, account_id, file);

//                                        saveObjRepository.saveAll(obj);

                                    }
                                    // LOGGER.error("{}", policyObjFile);

                                } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(N.getValue())) {

                                    List<String> id = new ArrayList<>();
                                    List<String> des = new ArrayList<>();

                                    policyObjFile.getPolicyObj().setVenta_id(C102_01.getValue());
                                    policyObjFile.getPolicyObj().setVenta_descripcion(BANCOS.getValue());
                                    //abono
                                    policyObjFile.setCuenta_method(A401_39.getValue());
                                    policyObjFile.setDescription_methods(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta_method()));

                                    id.add(C601_87.getValue());
                                    id.add(C114_05.getValue());

                                    for (String idAcc : id) {
                                        des.add(cuentaContableRepository.getCuantaContableMethod(idAcc));
                                    }

                                    policyObjFile.getPolicyObj().setRetencionId(id);
                                    policyObjFile.getPolicyObj().setRetencionDesc(des);


                                    //R4
                                    // LOGGER.error("R4 {}", file.getName());
                                    int rand_int1 = rand.nextInt(1000000000);
                                    policyObjFile.setFolio(String.valueOf(rand_int1));

                                    HashMap<String, String> map = CreateFilePDFBalance.getAccountsByRetencionId(policyObjFile);


                                    if (CreateFilePDFPolicy.makeFileEgreso(policyObjFile, file.getName().replace(".xml", ""), rfc, type)) {
                                        List<PolicytoDB> obj = CreateFilePDFBalance.saveObjDataBase(policyObjFile, map, rfc, type, account_id, file);

//                                        saveObjRepository.saveAll(obj);
                                    }
                                    //LOGGER.error("{}", policyObjFile);

                                } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(P.getValue())) {
                                    //R5
                                    List<String> id = new ArrayList<>();
                                    List<String> des = new ArrayList<>();
                                    if (List.of(PAY01.getValue(), PAY04.getValue(), PAY05.getValue(),
                                            PAY06.getValue(), PAY08.getValue(), PAY12.getValue(),
                                            PAY13.getValue(), PAY14.getValue(), PAY15.getValue(),
                                            PAY17.getValue(), PAY23.getValue(), PAY24.getValue(),
                                            PAY25.getValue(), PAY26.getValue(), PAY27.getValue(),
                                            PAY99.getValue(), PAY29.getValue(), PAY30.getValue(),
                                            PAY31.getValue()).contains(policyObjFile.getPolicyObj().getTypeOfPayment())) {

                                        policyObjFile.getPolicyObj().setVenta_id(C205_99.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(OTRASFORMAS.getValue());

                                    } else {

                                        policyObjFile.getPolicyObj().setVenta_id(C102_01.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(BANCOS.getValue());

                                    }

                                    policyObjFile.setCuenta_method(C205_99.getValue());
                                    policyObjFile.setDescription_methods(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta_method()));

                                    for (String imp : policyObjFile.getPolicyObj().getConceptoTipoFactorPPD()) {
                                        if (imp.equals(TAX02.getValue())) {
                                            id.add(C118_01.getValue());
                                            id.add(A119_01.getValue());
                                        }
                                    }
                                    Collections.sort(id);
                                    id.stream().distinct().collect(Collectors.toList());
                                    for (String idAcc : id) {
                                        des.add(cuentaContableRepository.getCuantaContableMethod(idAcc));
                                    }

                                    policyObjFile.getPolicyObj().setRetencionId(id);
                                    policyObjFile.getPolicyObj().setRetencionDesc(des);

                                    int rand_int1 = rand.nextInt(1000000000);
                                    policyObjFile.setFolio(String.valueOf(rand_int1));

                                    HashMap<String, String> map = CreateFilePDFBalance.getAccountsByRetencionId(policyObjFile);


                                    if (CreateFilePDFPolicy.makeFileEgreso(policyObjFile, file.getName().replace(".xml", ""), rfc, type)) {

                                        List<PolicytoDB> obj = CreateFilePDFBalance.saveObjDataBase(policyObjFile, map, rfc, type, account_id, file);

//                                        saveObjRepository.saveAll(obj);

                                    }

                                    // LOGGER.error("{}", policyObjFile);
                                    //LOGGER.error("R5 {}", file.getName());
                                }

                            }


                        } else if (type.equals(INGRESOS.getValue())) {


                            PolicyObjFile policyObjFile = ParserFileIngresos.getParse(local_path + rfc + "/xml/" + type + "/" + file.getName());
                            // LOGGER.error("{}", policyObjFile);

                            if (policyObjFile != null) {
                                if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(I.getValue()) && policyObjFile.getPolicyObj().getMethodPayment().equals(PUE.getValue())) {
                                    if (List.of(PAY01.getValue(), PAY04.getValue(), PAY05.getValue(),
                                            PAY06.getValue(), PAY08.getValue(), PAY12.getValue(),
                                            PAY13.getValue(), PAY14.getValue(), PAY15.getValue(),
                                            PAY17.getValue(), PAY23.getValue(), PAY24.getValue(),
                                            PAY25.getValue(), PAY26.getValue(), PAY27.getValue(),
                                            PAY99.getValue(), PAY29.getValue(), PAY30.getValue(),
                                            PAY31.getValue()).contains(policyObjFile.getPolicyObj().getTypeOfPayment())) {
                                        policyObjFile.getPolicyObj().setVenta_id(C205_99.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(OTRASFORMAS.getValue());

                                    } else {

                                        policyObjFile.getPolicyObj().setVenta_id(C102_01.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(BANCOS.getValue());

                                    }


                                    if (policyObjFile.getPolicyObj().getConceptoTipoFactorPPD() != null) {
                                        for (String imp : policyObjFile.getPolicyObj().getConceptoTipoFactorPPD()) {
                                            if (imp.equals("Tasa")) {
                                                policyObjFile.setCuenta(A401_01.getValue());
                                                policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                            } else if (imp.equals("Exento")) {
                                                policyObjFile.setCuenta(A401_07.getValue());
                                                policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                            }
                                        }
                                    }

                                    List<String> ab = new ArrayList<>();
                                    List<String> abd = new ArrayList<>();
                                    if (policyObjFile.getPolicyObj().getConceptoTipoFactorPPD() != null) {
                                        for (String impPPD : policyObjFile.getPolicyObj().getImpuestosPPD()) {
                                            if (impPPD.equals(TAX02.getValue())) {
                                                ab.add(A208_01.getValue());
                                                abd.add(cuentaContableRepository.getCuantaContableMethod(A209_01.getValue()));
                                            } else if (impPPD.equals(TAX03.getValue())) {
                                                ab.add(A208_02.getValue());
                                                abd.add(cuentaContableRepository.getCuantaContableMethod(A209_02.getValue()));
                                            }

                                        }
                                    }
                                    List<String> taxPUE = new ArrayList<>();
                                    List<String> descTax = new ArrayList<>();
                                    if (policyObjFile.getPolicyObj().getImpuestosPPD() != null) {
                                        for (String impPPD : policyObjFile.getPolicyObj().getImpuestosPPD()) {
                                            if (impPPD.equals(TAX02.getValue())) {
                                                //tax.add(C114_04.getValue());
                                                taxPUE.add(C119_01.getValue());
                                            } else if (impPPD.equals(TAX01.getValue()) &&
                                                    policyObjFile.getPolicyObj().getRegimen().equals(RG606.getRegimen()) &&
                                                    policyObjFile.getPolicyObj().getUsoCFDI().equals(I02.getValue())) {
                                                taxPUE.add(C114_03.getValue());
                                            } else if (impPPD.equals(TAX01.getValue()) &&
                                                    policyObjFile.getPolicyObj().getRegimen().equals(RG612.getRegimen()) &&
                                                    policyObjFile.getPolicyObj().getUsoCFDI().equals(I06.getValue())) {
                                                taxPUE.add(C114_02.getValue());
                                            }
                                        }
                                    }

                                    policyObjFile.setTax_id(taxPUE.stream().distinct().collect(Collectors.toList()));

                                    for (String idAcc : policyObjFile.getTax_id()) {
                                        descTax.add(cuentaContableRepository.getCuantaContableMethod(idAcc));
                                    }


                                    policyObjFile.setTax_description(descTax.stream().distinct().collect(Collectors.toList()));
                                    policyObjFile.getPolicyObj().setRetencionId(ab.stream().distinct().collect(Collectors.toList()));
                                    policyObjFile.getPolicyObj().setRetencionDesc(abd.stream().distinct().collect(Collectors.toList()));

                                    int rand_int1 = rand.nextInt(1000000000);
                                    policyObjFile.setFolio(String.valueOf(rand_int1));

                                    HashMap<String, String> map = CreateFilePDFBalance.getAccountsByRetencionId(policyObjFile);


                                    if (CreateFilePDFPolicy.makeFileIngreso(policyObjFile, file.getName().replace(".xml", ""), rfc, type)) {
                                        List<PolicytoDB> obj = CreateFilePDFBalance.saveObjDataBase(policyObjFile, map, rfc, type, account_id, file);

//                                        saveObjRepository.saveAll(obj);

                                    }


                                    //R6
                                    //LOGGER.error("R6 I AND PUE {}", file.getName());
                                    //LOGGER.error("{}", policyObjFile);

                                } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(I.getValue()) && policyObjFile.getPolicyObj().getMethodPayment().equals(PPD.getValue())) {
                                    //R6

                                    for (String imp : policyObjFile.getPolicyObj().getConceptoTipoFactorPPD()) {
                                        if (imp.equals("Tasa")) {
                                            policyObjFile.setCuenta(A401_01.getValue());
                                            policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                        } else if (imp.equals("Exento")) {
                                            policyObjFile.setCuenta(A401_07.getValue());
                                            policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                        }
                                    }


                                    List<String> ab = new ArrayList<>();
                                    List<String> abd = new ArrayList<>();
                                    for (String impPPD : policyObjFile.getPolicyObj().getImpuestosPPD()) {
                                        if (impPPD.equals(TAX02.getValue())) {
                                            ab.add(A209_01.getValue());
                                            abd.add(cuentaContableRepository.getCuantaContableMethod(A209_01.getValue()));
                                        } else if (impPPD.equals(TAX03.getValue())) {
                                            ab.add(A209_02.getValue());
                                            abd.add(cuentaContableRepository.getCuantaContableMethod(A209_02.getValue()));
                                        }

                                    }

                                    policyObjFile.getPolicyObj().setRetencionId(ab.stream().distinct().collect(Collectors.toList()));
                                    policyObjFile.getPolicyObj().setRetencionDesc(abd.stream().distinct().collect(Collectors.toList()));

                                    List<String> tax = new ArrayList<>();
                                    List<String> descTax = new ArrayList<>();
                                    for (String impPPD : policyObjFile.getPolicyObj().getImpuestosPPD()) {
                                        if (impPPD.equals(TAX02.getValue())) {
                                            //tax.add(C114_04.getValue());
                                            tax.add(C119_01.getValue());
                                        } else if (impPPD.equals(TAX01.getValue()) &&
                                                policyObjFile.getPolicyObj().getRegimen().equals(RG606.getRegimen()) &&
                                                policyObjFile.getPolicyObj().getUsoCFDI().equals(I02.getValue())) {
                                            tax.add(C114_03.getValue());
                                        } else if (impPPD.equals(TAX01.getValue()) &&
                                                policyObjFile.getPolicyObj().getRegimen().equals(RG612.getRegimen()) &&
                                                policyObjFile.getPolicyObj().getUsoCFDI().equals(I06.getValue())) {
                                            tax.add(C114_02.getValue());
                                        }
                                    }

                                    policyObjFile.setTax_id(tax.stream().distinct().collect(Collectors.toList()));

                                    for (String idAcc : policyObjFile.getTax_id()) {
                                        descTax.add(cuentaContableRepository.getCuantaContableMethod(idAcc));
                                    }

                                    policyObjFile.setTax_description(descTax.stream().distinct().collect(Collectors.toList()));


                                    policyObjFile.getPolicyObj().setVenta_id(C105_01.getValue());
                                    policyObjFile.getPolicyObj().setVenta_descripcion(cuentaContableRepository.getCuantaContableMethod(C105_01.getValue()));

                                    int rand_int1 = rand.nextInt(1000000000);
                                    policyObjFile.setFolio(String.valueOf(rand_int1));

                                    HashMap<String, String> map = CreateFilePDFBalance.getAccountsByRetencionId(policyObjFile);

                                    if (CreateFilePDFPolicy.makeFileIngreso(policyObjFile, file.getName().replace(".xml", ""), rfc, type)) {
                                        List<PolicytoDB> obj = CreateFilePDFBalance.saveObjDataBase(policyObjFile, map, rfc, type, account_id, file);

//                                        saveObjRepository.saveAll(obj);

                                    }

                                    //LOGGER.error("R6 I AND PPD {}", file.getName());

                                } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(E.getValue())) {
                                    //R7
                                    if (List.of(PAY01.getValue(), PAY04.getValue(), PAY05.getValue(),
                                            PAY06.getValue(), PAY08.getValue(), PAY12.getValue(),
                                            PAY13.getValue(), PAY14.getValue(), PAY15.getValue(),
                                            PAY17.getValue(), PAY23.getValue(), PAY24.getValue(),
                                            PAY25.getValue(), PAY26.getValue(), PAY27.getValue(),
                                            PAY99.getValue(), PAY29.getValue(), PAY30.getValue(),
                                            PAY31.getValue()).contains(policyObjFile.getPolicyObj().getTypeOfPayment())) {

                                        policyObjFile.getPolicyObj().setVenta_id(C205_99.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(OTRASFORMAS.getValue());

                                    } else {

                                        policyObjFile.getPolicyObj().setVenta_id(C102_01.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(BANCOS.getValue());

                                    }


                                    policyObjFile.setCuenta(C402_01.getValue());
                                    policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));

                                    //TotalImpuestosTrasladados
                                    //if 002 and pue 208.01
                                    //if 002 and ppd 209.01

                                    //TotalImpuestosTrasladados
                                    //if 002 and pue 208.02
                                    //if 002 and ppd 209.02

                                    //RetencioImpuesto
                                    //if 002 114.04
                                    //if 001 114.03

                                    //CUENTA DE REGISTRO = ABONO SI ES "PPD" 105.01
                                    //CUENTA DE REGISTRO = ABONO SI ES "PUE" ventaId and ventaDescripcion

                                    int rand_int1 = rand.nextInt(1000000000);
                                    policyObjFile.setFolio(String.valueOf(rand_int1));

                                    HashMap<String, String> map = CreateFilePDFBalance.getAccountsByRetencionId(policyObjFile);


                                    if (CreateFilePDFPolicy.makeFileIngreso(policyObjFile, file.getName().replace(".xml", ""), rfc, type)) {
                                        List<PolicytoDB> obj = CreateFilePDFBalance.saveObjDataBase(policyObjFile, map, rfc, type, account_id, file);
//                                        saveObjRepository.saveAll(obj);

                                    }
                                    // LOGGER.error("{}", policyObjFile);
                                    //LOGGER.error("R7 {}", file.getName());
                                } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(RETENCIONES.getValue())) {
                                    //R8

                                    policyObjFile.getPolicyObj().setVenta_id(C102_01.getValue());
                                    policyObjFile.getPolicyObj().setVenta_descripcion(BANCOS.getValue());

                                    if (policyObjFile.getPolicyObj().getRegimen().equals(RG601.getRegimen())) {
                                        policyObjFile.setCuenta(C301_01.getValue());
                                        policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                    } else if (policyObjFile.getPolicyObj().getRegimen().equals(RG603.getRegimen())) {
                                        policyObjFile.setCuenta(C304_01.getValue());
                                        policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                    }


                                    // obtener unu xml de ejemplo de retenciones
                                    //A216_07.getValue();
                                    //A216_08.getValue();
                                    //A102_01.getValue();
                                    int rand_int1 = rand.nextInt(1000000000);
                                    policyObjFile.setFolio(String.valueOf(rand_int1));

                                    HashMap<String, String> map = CreateFilePDFBalance.getAccountsByRetencionId(policyObjFile);


                                    if (CreateFilePDFPolicy.makeFileIngreso(policyObjFile, file.getName().replace(".xml", ""), rfc, type)) {
                                        List<PolicytoDB> obj = CreateFilePDFBalance.saveObjDataBase(policyObjFile, map, rfc, type, account_id, file);
//                                        saveObjRepository.saveAll(obj);

                                    }
                                    // LOGGER.error("{}", policyObjFile);
                                    //LOGGER.error("R8 {}", file.getName());
                                } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(N.getValue())) {
                                    //R9
                                    if (List.of(PAY01.getValue(), PAY04.getValue(), PAY05.getValue(), PAY06.getValue(), PAY08.getValue(), PAY12.getValue(), PAY13.getValue(), PAY14.getValue(), PAY15.getValue(), PAY17.getValue(), PAY23.getValue(), PAY24.getValue(), PAY25.getValue(), PAY26.getValue(), PAY27.getValue(), PAY99.getValue(), PAY29.getValue(), PAY30.getValue(), PAY31.getValue()).contains(policyObjFile.getPolicyObj().getTypeOfPayment())) {

                                        policyObjFile.getPolicyObj().setVenta_id(C205_99.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(OTRASFORMAS.getValue());

                                    } else {

                                        policyObjFile.getPolicyObj().setVenta_id(C102_01.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(BANCOS.getValue());

                                    }
                                    // -------- abono  -------------------
                                    if (policyObjFile.getPolicyObj().getImpuestoId().equals(P001.getValue())) {
                                        policyObjFile.setCuenta(C601_01.getValue());
                                        policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                    } else if (policyObjFile.getPolicyObj().getImpuestoId().equals(P002.getValue())) {
                                        policyObjFile.setCuenta(C601_12.getValue());
                                        policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                    } else if (policyObjFile.getPolicyObj().getImpuestoId().equals(P003.getValue())) {
                                        policyObjFile.setCuenta(C601_21.getValue());
                                        policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                    } else if (policyObjFile.getPolicyObj().getImpuestoId().equals(P004.getValue()) ||
                                            policyObjFile.getPolicyObj().getImpuestoId().equals(P006.getValue()) ||
                                            policyObjFile.getPolicyObj().getImpuestoId().equals(P011.getValue()) ||
                                            policyObjFile.getPolicyObj().getImpuestoId().equals(P012.getValue()) ||
                                            policyObjFile.getPolicyObj().getImpuestoId().equals(P014.getValue()) ||
                                            policyObjFile.getPolicyObj().getImpuestoId().equals(P015.getValue()) ||
                                            policyObjFile.getPolicyObj().getImpuestoId().equals(P024.getValue())) {
                                        policyObjFile.setCuenta(C601_23.getValue());
                                        policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                    } else if (policyObjFile.getPolicyObj().getImpuestoId().equals(P005.getValue())) {
                                        policyObjFile.setCuenta(C601_19.getValue());
                                        policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                    } else if (policyObjFile.getPolicyObj().getImpuestoId().equals(P009.getValue()) ||
                                            policyObjFile.getPolicyObj().getImpuestoId().equals(P013.getValue())) {
                                        policyObjFile.setCuenta(C601_25.getValue());
                                        policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                    } else if (policyObjFile.getPolicyObj().getImpuestoId().equals(P010.getValue())) {
                                        policyObjFile.setCuenta(C601_05.getValue());
                                        policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                    } else if (policyObjFile.getPolicyObj().getImpuestoId().equals(P019.getValue())) {
                                        policyObjFile.setCuenta(C601_03.getValue());
                                        policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                    } else if (policyObjFile.getPolicyObj().getImpuestoId().equals(P020.getValue())) {
                                        policyObjFile.setCuenta(C601_08.getValue());
                                        policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));

                                    } else if (policyObjFile.getPolicyObj().getImpuestoId().equals(P021.getValue())) {
                                        policyObjFile.setCuenta(C601_07.getValue());
                                        policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                    } else if (policyObjFile.getPolicyObj().getImpuestoId().equals(P022.getValue()) ||
                                            policyObjFile.getPolicyObj().getImpuestoId().equals(P023.getValue()) ||
                                            policyObjFile.getPolicyObj().getImpuestoId().equals(P025.getValue())) {
                                        policyObjFile.setCuenta(C601_13.getValue());
                                        policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));
                                    }
                                    //TotalSueldos el valor

                                    List<String> id = new ArrayList<>();
                                    List<String> des = new ArrayList<>();

                                    if (policyObjFile.getPolicyObj().getImpPagado().equals(TAX02.getValue())) {
                                        id.add(C110_01.getValue());
                                        des.add(cuentaContableRepository.getCuantaContableMethod(C101_01.getValue()));
                                    }


                                    for (String trl : policyObjFile.getPolicyObj().getTraslado()) {
                                        if (trl.equals(TAX01.getValue())) {
                                            id.add(A211_01.getValue());

                                        } else if (trl.equals(TAX02.getValue())) {
                                            id.add(A216_01.getValue());

                                        }

                                    }
                                    for (String idAcc : id) {
                                        des.add(cuentaContableRepository.getCuantaContableMethod(idAcc));
                                    }
                                    policyObjFile.getPolicyObj().setRetencionId(id);
                                    policyObjFile.getPolicyObj().setRetencionDesc(des);
                                    //retencion infonavit - 211.03

                                    int rand_int1 = rand.nextInt(1000000000);
                                    policyObjFile.setFolio(String.valueOf(rand_int1));

                                    HashMap<String, String> map = CreateFilePDFBalance.getAccountsByRetencionId(policyObjFile);

                                    if (CreateFilePDFPolicy.makeFileIngreso(policyObjFile, file.getName().replace(".xml", ""), rfc, type)) {
                                        List<PolicytoDB> obj = CreateFilePDFBalance.saveObjDataBase(policyObjFile, map, rfc, type, account_id, file);

//                                        saveObjRepository.saveAll(obj);

                                    }

                                    // LOGGER.error("{}", policyObjFile);
                                    //LOGGER.error("R9 {}", file.getName());

                                } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(P.getValue())) {
                                    //R10
                                    List<String> id = new ArrayList<>();
                                    List<String> des = new ArrayList<>();
                                    if (List.of(PAY01.getValue(), PAY04.getValue(), PAY05.getValue(), PAY06.getValue(), PAY08.getValue(), PAY12.getValue(), PAY13.getValue(), PAY14.getValue(), PAY15.getValue(), PAY17.getValue(), PAY23.getValue(), PAY24.getValue(), PAY25.getValue(), PAY26.getValue(), PAY27.getValue(), PAY99.getValue(), PAY29.getValue(), PAY30.getValue(), PAY31.getValue()).contains(policyObjFile.getPolicyObj().getTypeOfPayment())) {

                                        policyObjFile.getPolicyObj().setVenta_id(C205_99.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(OTRASFORMAS.getValue());

                                    } else {

                                        policyObjFile.getPolicyObj().setVenta_id(C102_01.getValue());
                                        policyObjFile.getPolicyObj().setVenta_descripcion(BANCOS.getValue());

                                    }

                                    policyObjFile.setCuenta(A105_01.getValue());
                                    policyObjFile.setCuenta_method(cuentaContableRepository.getCuantaContableMethod(policyObjFile.getCuenta()));

                                    if (policyObjFile.getPolicyObj().getImpuestoId().equals(TAX02.getValue())) {
                                        id.add(C209_01.getValue());
                                        id.add(A208_01.getValue());
                                    }

                                    for (String idAcc : id) {
                                        des.add(cuentaContableRepository.getCuantaContableMethod(idAcc));
                                    }

                                    policyObjFile.getPolicyObj().setRetencionId(id);
                                    policyObjFile.getPolicyObj().setRetencionDesc(des);

                                    int rand_int1 = rand.nextInt(1000000000);
                                    policyObjFile.setFolio(String.valueOf(rand_int1));

                                    HashMap<String, String> map = CreateFilePDFBalance.getAccountsByRetencionId(policyObjFile);

                                    if (CreateFilePDFPolicy.makeFileIngreso(policyObjFile, file.getName().replace(".xml", ""), rfc, type)) {
                                        List<PolicytoDB> obj = CreateFilePDFBalance.saveObjDataBase(policyObjFile, map, rfc, type, account_id, file);
//                                        saveObjRepository.saveAll(obj);

                                    }

                                    //LOGGER.error("R10 {}", file.getName());

                                }
                            }

                        }
                    }
                }
            }
            return 2;

        } catch (Exception e) {
            LOGGER.error("createPolicy - createFile: {} . {} ", e.getMessage(), e.getStackTrace());
        }
        return 0;
    }


}


