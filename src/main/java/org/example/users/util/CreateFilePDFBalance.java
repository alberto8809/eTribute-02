package org.example.users.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.users.model.Auxiliar;
import org.example.users.model.Balance;
import org.example.users.model.PolicyObjFile;
import org.example.users.model.PolicytoDB;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateFilePDFBalance {
    private static final String FILENAME = "sample.pdf";


    public CreateFilePDFBalance() {
    }


    public static void makeFileAuxiliar(List<Auxiliar> auxiliar) {


        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(FILENAME));
            document.open();


            PdfPTable headerTable = new PdfPTable(6);

            headerTable.setWidthPercentage(100);

            PdfPCell cellBody = new PdfPCell(new Paragraph("Cuenta", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            cellBody.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellBody.setBorderColor(BaseColor.WHITE);
            cellBody.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell cellName = new PdfPCell(new Paragraph("Nombre", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            cellName.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellName.setPaddingRight(40);
            cellName.setBorderColor(BaseColor.WHITE);
            cellName.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell cellCharge = new PdfPCell(new Paragraph("Saldo Inicial", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            cellCharge.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellCharge.setBorderColor(BaseColor.WHITE);
            cellCharge.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell cellAb = new PdfPCell(new Paragraph("Debe", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            cellAb.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellAb.setBorderColor(BaseColor.WHITE);
            cellAb.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell cellHaber = new PdfPCell(new Paragraph("Haber", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            cellHaber.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellHaber.setBorderColor(BaseColor.WHITE);
            cellHaber.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell cellFinal = new PdfPCell(new Paragraph("Saldo Final", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            cellFinal.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellFinal.setBorderColor(BaseColor.WHITE);
            cellFinal.setBackgroundColor(new BaseColor(182, 208, 226));

            headerTable.addCell(cellBody);
            headerTable.addCell(cellName);
            headerTable.addCell(cellCharge);
            headerTable.addCell(cellAb);
            headerTable.addCell(cellHaber);
            headerTable.addCell(cellFinal);


            //---------------------   body of file  --------------------- //

            PdfPTable bodyTable = new PdfPTable(6);

            bodyTable.setWidthPercentage(100);

            for (Auxiliar auxiliar1 : auxiliar) {

                PdfPCell account = new PdfPCell(new Paragraph(auxiliar1.getCuenta(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                account.setBorderColorBottom(BaseColor.BLACK);
                account.setHorizontalAlignment(Element.ALIGN_CENTER);
                account.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell name = new PdfPCell(new Paragraph(auxiliar1.getDescripcion(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                name.setBorderColorBottom(BaseColor.BLACK);
                name.setHorizontalAlignment(Element.ALIGN_CENTER);
                name.setHorizontalAlignment(Element.ALIGN_CENTER);


                PdfPCell inicial = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                inicial.setBorderColorBottom(BaseColor.BLACK);
                inicial.setHorizontalAlignment(Element.ALIGN_CENTER);
                inicial.setHorizontalAlignment(Element.ALIGN_CENTER);

                /* sum of all total  -  debe */
                PdfPCell debe = new PdfPCell(new Paragraph(auxiliar1.getDebe(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                debe.setBorderColorBottom(BaseColor.BLACK);
                debe.setHorizontalAlignment(Element.ALIGN_CENTER);
                debe.setHorizontalAlignment(Element.ALIGN_CENTER);


                /* sum of all total  -  haber*/
                PdfPCell haber = new PdfPCell(new Paragraph(auxiliar1.getHaber(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                haber.setBorderColorBottom(BaseColor.BLACK);
                haber.setHorizontalAlignment(Element.ALIGN_CENTER);
                haber.setHorizontalAlignment(Element.ALIGN_CENTER);

                /* rest of inicial  -  final */
                PdfPCell saldo_final = new PdfPCell(new Paragraph("", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                saldo_final.setBorderColorBottom(BaseColor.BLACK);
                saldo_final.setHorizontalAlignment(Element.ALIGN_CENTER);
                saldo_final.setHorizontalAlignment(Element.ALIGN_CENTER);


                bodyTable.addCell(account);
                bodyTable.addCell(name);
                bodyTable.addCell(inicial);
                bodyTable.addCell(debe);
                bodyTable.addCell(haber);
                bodyTable.addCell(saldo_final);


            }


            PdfPTable allBody = new PdfPTable(8);

            allBody.setWidthPercentage(100);

            PdfPCell fecha = new PdfPCell(new Paragraph("Fecha", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            fecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            fecha.setBorderColor(BaseColor.WHITE);
            fecha.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell poliza = new PdfPCell(new Paragraph("Póliza", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            poliza.setHorizontalAlignment(Element.ALIGN_CENTER);
            poliza.setPaddingRight(40);
            poliza.setBorderColor(BaseColor.WHITE);
            poliza.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell referencia = new PdfPCell(new Paragraph("Referencia", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            referencia.setHorizontalAlignment(Element.ALIGN_CENTER);
            referencia.setBorderColor(BaseColor.WHITE);
            referencia.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell descripcion = new PdfPCell(new Paragraph("Descripción", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            descripcion.setHorizontalAlignment(Element.ALIGN_CENTER);
            descripcion.setBorderColor(BaseColor.WHITE);
            descripcion.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell saldo_inicial = new PdfPCell(new Paragraph("Saldo Inical", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            saldo_inicial.setHorizontalAlignment(Element.ALIGN_CENTER);
            saldo_inicial.setBorderColor(BaseColor.WHITE);
            saldo_inicial.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell debe = new PdfPCell(new Paragraph("Debe", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            debe.setHorizontalAlignment(Element.ALIGN_CENTER);
            debe.setBorderColor(BaseColor.WHITE);
            debe.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell haber = new PdfPCell(new Paragraph("Haber", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            haber.setHorizontalAlignment(Element.ALIGN_CENTER);
            haber.setBorderColor(BaseColor.WHITE);
            haber.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell saldo_final = new PdfPCell(new Paragraph("Saldo Final", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            saldo_final.setHorizontalAlignment(Element.ALIGN_CENTER);
            saldo_final.setBorderColor(BaseColor.WHITE);
            saldo_final.setBackgroundColor(new BaseColor(182, 208, 226));


            allBody.addCell(fecha);
            allBody.addCell(poliza);
            allBody.addCell(referencia);
            allBody.addCell(descripcion);
            allBody.addCell(saldo_inicial);
            allBody.addCell(debe);
            allBody.addCell(haber);
            allBody.addCell(saldo_final);

            PdfPTable body_Content = new PdfPTable(8);

            body_Content.setWidthPercentage(100);

            for (Auxiliar auxiliar2 : auxiliar) {

                PdfPCell fecha1 = new PdfPCell(new Paragraph(auxiliar2.getFecha(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                fecha1.setHorizontalAlignment(Element.ALIGN_CENTER);
                fecha1.setBorderColor(BaseColor.WHITE);
                fecha1.setBackgroundColor(new BaseColor(182, 208, 226));

                PdfPCell folio = new PdfPCell(new Paragraph(auxiliar2.getPoliza(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                folio.setHorizontalAlignment(Element.ALIGN_CENTER);
                folio.setPaddingRight(40);
                folio.setBorderColor(BaseColor.WHITE);
                folio.setBackgroundColor(new BaseColor(182, 208, 226));


                PdfPCell referencia2 = new PdfPCell(new Paragraph("", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                referencia2.setHorizontalAlignment(Element.ALIGN_CENTER);
                referencia2.setBorderColor(BaseColor.WHITE);
                referencia2.setBackgroundColor(new BaseColor(182, 208, 226));

                PdfPCell concepto = new PdfPCell(new Paragraph(auxiliar2.getDescripcion(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                concepto.setHorizontalAlignment(Element.ALIGN_CENTER);
                concepto.setBorderColor(BaseColor.WHITE);
                concepto.setBackgroundColor(new BaseColor(182, 208, 226));

                PdfPCell saldo_inicial2 = new PdfPCell(new Paragraph("", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                saldo_inicial2.setHorizontalAlignment(Element.ALIGN_CENTER);
                saldo_inicial2.setBorderColor(BaseColor.WHITE);
                saldo_inicial2.setBackgroundColor(new BaseColor(182, 208, 226));


                PdfPCell cargo = new PdfPCell(new Paragraph(auxiliar2.getDebe(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                cargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cargo.setBorderColor(BaseColor.WHITE);
                cargo.setBackgroundColor(new BaseColor(182, 208, 226));


                PdfPCell abono = new PdfPCell(new Paragraph(auxiliar2.getHaber(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                abono.setHorizontalAlignment(Element.ALIGN_CENTER);
                abono.setBorderColor(BaseColor.WHITE);
                abono.setBackgroundColor(new BaseColor(182, 208, 226));


                PdfPCell saldo_final2 = new PdfPCell(new Paragraph("", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                saldo_final2.setHorizontalAlignment(Element.ALIGN_CENTER);
                saldo_final2.setBorderColor(BaseColor.WHITE);
                saldo_final2.setBackgroundColor(new BaseColor(182, 208, 226));


                body_Content.addCell(fecha1);
                body_Content.addCell(folio);
                body_Content.addCell(referencia2);
                body_Content.addCell(concepto);
                body_Content.addCell(saldo_inicial2);
                body_Content.addCell(cargo);
                body_Content.addCell(abono);
                body_Content.addCell(saldo_final2);

            }


            document.add(headerTable);
            document.add(bodyTable);
            document.add(allBody);
            document.add(body_Content);
            document.close();

        } catch (Exception e) {
            System.out.println("Ex " + e.getMessage() + e.getCause());


        }

    }

    public static boolean makeFileBalance(List<Balance> balance) {

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(FILENAME));
            document.open();


            PdfPTable headerTable = new PdfPTable(8);
            headerTable.setWidthPercentage(100);

            PdfPCell fecha = new PdfPCell(new Paragraph("Cuenta", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            fecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            fecha.setBorderColor(BaseColor.WHITE);
            fecha.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell poliza = new PdfPCell(new Paragraph("Nombre", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            poliza.setHorizontalAlignment(Element.ALIGN_CENTER);
            poliza.setPaddingRight(40);
            poliza.setBorderColor(BaseColor.WHITE);
            poliza.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell referencia = new PdfPCell(new Paragraph("Deudor", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            referencia.setHorizontalAlignment(Element.ALIGN_CENTER);
            referencia.setBorderColor(BaseColor.WHITE);
            referencia.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell descripcion = new PdfPCell(new Paragraph("Acredor", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            descripcion.setHorizontalAlignment(Element.ALIGN_CENTER);
            descripcion.setBorderColor(BaseColor.WHITE);
            descripcion.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell saldo_inicial = new PdfPCell(new Paragraph("Debe", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            saldo_inicial.setHorizontalAlignment(Element.ALIGN_CENTER);
            saldo_inicial.setBorderColor(BaseColor.WHITE);
            saldo_inicial.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell debe = new PdfPCell(new Paragraph("Haber", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            debe.setHorizontalAlignment(Element.ALIGN_CENTER);
            debe.setBorderColor(BaseColor.WHITE);
            debe.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell haber = new PdfPCell(new Paragraph("Deudor", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            haber.setHorizontalAlignment(Element.ALIGN_CENTER);
            haber.setBorderColor(BaseColor.WHITE);
            haber.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell saldo_final = new PdfPCell(new Paragraph("Acredor", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            saldo_final.setHorizontalAlignment(Element.ALIGN_CENTER);
            saldo_final.setBorderColor(BaseColor.WHITE);
            saldo_final.setBackgroundColor(new BaseColor(182, 208, 226));


            headerTable.addCell(fecha);
            headerTable.addCell(poliza);
            headerTable.addCell(referencia);
            headerTable.addCell(descripcion);
            headerTable.addCell(saldo_inicial);
            headerTable.addCell(debe);
            headerTable.addCell(haber);
            headerTable.addCell(saldo_final);


            PdfPTable bodyTable = new PdfPTable(8);

            bodyTable.setWidthPercentage(100);

            for (Balance balance1 : balance) {

                PdfPCell fecha1 = new PdfPCell(new Paragraph(balance1.getCuenta(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                fecha1.setHorizontalAlignment(Element.ALIGN_CENTER);
                fecha1.setBorderColor(BaseColor.WHITE);
                fecha1.setBackgroundColor(new BaseColor(182, 208, 226));

                PdfPCell folio = new PdfPCell(new Paragraph(balance1.getNombre(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                folio.setHorizontalAlignment(Element.ALIGN_CENTER);
                folio.setPaddingRight(40);
                folio.setBorderColor(BaseColor.WHITE);
                folio.setBackgroundColor(new BaseColor(182, 208, 226));


                PdfPCell referencia2 = new PdfPCell(new Paragraph(balance1.getDeudor_inicial(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                referencia2.setHorizontalAlignment(Element.ALIGN_CENTER);
                referencia2.setBorderColor(BaseColor.WHITE);
                referencia2.setBackgroundColor(new BaseColor(182, 208, 226));

                PdfPCell concepto = new PdfPCell(new Paragraph(balance1.getAcredor_inicial(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                concepto.setHorizontalAlignment(Element.ALIGN_CENTER);
                concepto.setBorderColor(BaseColor.WHITE);
                concepto.setBackgroundColor(new BaseColor(182, 208, 226));

                PdfPCell saldo_inicial2 = new PdfPCell(new Paragraph(balance1.getDebe(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                saldo_inicial2.setHorizontalAlignment(Element.ALIGN_CENTER);
                saldo_inicial2.setBorderColor(BaseColor.WHITE);
                saldo_inicial2.setBackgroundColor(new BaseColor(182, 208, 226));


                PdfPCell cargo = new PdfPCell(new Paragraph(balance1.getHaber(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                cargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cargo.setBorderColor(BaseColor.WHITE);
                cargo.setBackgroundColor(new BaseColor(182, 208, 226));


                PdfPCell abono = new PdfPCell(new Paragraph(balance1.getDeudor_final(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                abono.setHorizontalAlignment(Element.ALIGN_CENTER);
                abono.setBorderColor(BaseColor.WHITE);
                abono.setBackgroundColor(new BaseColor(182, 208, 226));


                PdfPCell saldo_final2 = new PdfPCell(new Paragraph(balance1.getAcredor_final(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                saldo_final2.setHorizontalAlignment(Element.ALIGN_CENTER);
                saldo_final2.setBorderColor(BaseColor.WHITE);
                saldo_final2.setBackgroundColor(new BaseColor(182, 208, 226));


                bodyTable.addCell(fecha1);
                bodyTable.addCell(folio);
                bodyTable.addCell(referencia2);
                bodyTable.addCell(concepto);
                bodyTable.addCell(saldo_inicial2);
                bodyTable.addCell(cargo);
                bodyTable.addCell(abono);
                bodyTable.addCell(saldo_final2);

            }


            document.add(headerTable);
            document.add(bodyTable);
            document.close();
            return true;
        } catch (Exception e) {
            System.out.println("Ex " + e.getMessage() + e.getCause());


        }
        return false;
    }

    public static String decimal(Double value) {
        //BigDecimal formatNumber = new BigDecimal(value);
        //return "" + formatNumber.setScale(2, RoundingMode.DOWN);
        return String.format("%,.2f", value);
    }

    public static HashMap<String, String> getAccounts(PolicyObjFile policyObjFile) {
        HashMap<String, String> map = new HashMap<>();
        if (policyObjFile.getTax_id().get(0) != null) {
            for (int i = 0; i < policyObjFile.getTax_id().size(); i++) {
                map.put(policyObjFile.getTax_id().get(i), policyObjFile.getTax_description().get(i));
            }
        }
        map.put(policyObjFile.getPolicyObj().getVenta_id(), policyObjFile.getPolicyObj().getVenta_descripcion());
        map.put(policyObjFile.getCuenta(), policyObjFile.getDescription_methods());
        return map;

    }

    public static HashMap<String, String> getAccountsByRetencionId(PolicyObjFile policyObjFile) {
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < policyObjFile.getPolicyObj().getRetencionId().size(); i++) {
            map.put(policyObjFile.getPolicyObj().getRetencionId().get(i), policyObjFile.getPolicyObj().getRetencionDesc().get(i));
        }
        if (policyObjFile.getCuenta() != null) {
            if (policyObjFile.getDescription_methods() != null) {
                map.put(policyObjFile.getCuenta(), policyObjFile.getDescription_methods());
            } else {
                map.put(policyObjFile.getCuenta(), policyObjFile.getCuenta_method());
            }
        } else if (policyObjFile.getCuenta_method() != null) {
            map.put(policyObjFile.getCuenta_method(), policyObjFile.getDescription_methods());
        }
        if (policyObjFile.getTax_id() != null) {
            for (int i = 0; i < policyObjFile.getTax_id().size(); i++) {
                map.put(policyObjFile.getTax_id().get(i), policyObjFile.getTax_description().get(i));
            }
        }

        map.put(policyObjFile.getPolicyObj().getVenta_id(), policyObjFile.getPolicyObj().getVenta_descripcion());

        return map;
    }

    public static List<PolicytoDB> saveObjDataBase(PolicyObjFile policyObjFile, Map<String, String> map, String rfc, String type, int account_id, File file) {

        List<PolicytoDB> policytoDBList = new ArrayList<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {

            PolicytoDB policytoDB = new PolicytoDB();
            policytoDB.setCliente(policyObjFile.getClient());
            policytoDB.setUsuario(rfc);
            policytoDB.setTipo(type);
            policytoDB.setProveedor(policyObjFile.getPolicyObj().getCompanyName());
            policytoDB.setPoliza(policyObjFile.getFolio());
            policytoDB.setCuenta(Double.parseDouble(entry.getKey()));
            policytoDB.setDescripcion(entry.getValue());

            List<String> debe = policyObjFile.getPolicyObj().getTraslado();
            double sumDebe = 0;
            if (debe != null) {
                for (String db : debe) {
                    sumDebe += Double.parseDouble(db);

                }
            }

            policytoDB.setDebe(policyObjFile.getPolicyObj().getAmount());
            if (policyObjFile.getPolicyObj().getRetencion_importe() != null) {
                List<String> haber = policyObjFile.getPolicyObj().getRetencion_importe();
                double sumHaber = 0;
                if (haber != null) {
                    for (String db : haber) {
                        sumHaber += Double.parseDouble(db);

                    }
                }
            }

            policytoDB.setHaber(String.valueOf(policyObjFile.getPolicyObj().getSubtotal()));
            policytoDB.setFecha(policyObjFile.getDate());
            policytoDB.setReferencia(policyObjFile.getPolicyObj().getTimbreFiscalDigital_UUID());
            policytoDB.setTotal(policyObjFile.getPolicyObj().getAmount());
            policytoDB.setSaldo_inicial("0.00");
            double fin = Double.parseDouble(policytoDB.getSaldo_inicial()) + Double.parseDouble(policytoDB.getTotal());
            policytoDB.setSaldo_final(String.valueOf(fin));
            policytoDB.setAccount_id(account_id);
            policytoDB.setFile_name(file.getName());
            policytoDBList.add(policytoDB);

        }

        return policytoDBList;

    }

}
