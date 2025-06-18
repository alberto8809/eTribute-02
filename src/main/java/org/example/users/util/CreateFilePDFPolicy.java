package org.example.users.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.users.model.PolicyObjFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import static org.example.users.util.Abono.*;
import static org.example.users.util.CFDI.*;
import static org.example.users.util.Regimen.*;


public class CreateFilePDFPolicy {

    private static final String local_path = "/Users/marioalberto/IdeaProjects/eTribute-02/";
    //private static final String server_path = "/home/ubuntu/endpoints/eTribute-all/";
    public static final Logger LOGGER = LogManager.getLogger(CreateFilePDFPolicy.class);

    public CreateFilePDFPolicy() {
    }


    public static boolean makeFileEgreso(PolicyObjFile policyObjFile, String fileName, String rfc, String type) {
        try {

            String cargoTotal = "";
            String abonoTotal = "";
            File uploadDir = new File(local_path + rfc + "/pdf/" + type + "/");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(local_path + rfc + "/pdf/" + type + "/" + fileName + ".pdf"));
            document.open();

            //---------------------   header  --------------------- //

            PdfPTable table = new PdfPTable(2);

            PdfPCell client = new PdfPCell(new Paragraph(policyObjFile.getClient(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            client.setHorizontalAlignment(Element.ALIGN_CENTER);
            client.setBorderColor(BaseColor.WHITE);
            client.setBackgroundColor(new BaseColor(182, 208, 226));

            policyObjFile.setTypeOf(policyObjFile.getTypeOf().equals("defaultValue") ? "Egreso" : policyObjFile.getTypeOf());

            PdfPCell folio = new PdfPCell(new Paragraph("P ó l i z a" + "\n" + " Tipo: " + policyObjFile.getTypeOf() + "  Folio: " + policyObjFile.getFolio(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK.darker())));
            folio.setHorizontalAlignment(Element.ALIGN_CENTER);
            folio.setBorderColor(BaseColor.WHITE);
            folio.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell company = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getCompanyName(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
            company.setHorizontalAlignment(Element.ALIGN_CENTER);
            company.setBorderColor(BaseColor.WHITE);
            company.setBackgroundColor(new BaseColor(229, 231, 233));


            PdfPCell date_ = new PdfPCell(new Paragraph(policyObjFile.getDate(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, Font.BOLD, BaseColor.BLACK)));
            date_.setHorizontalAlignment(Element.ALIGN_CENTER);
            date_.setBorderColor(BaseColor.WHITE);
            date_.setBackgroundColor(new BaseColor(229, 231, 233));


            table.addCell(client);
            table.addCell(folio);
            table.addCell(company);
            table.addCell(date_);
            table.setHorizontalAlignment(2);
            table.setWidthPercentage(60);

            PdfPTable headerTable = new PdfPTable(4);
            headerTable.setWidthPercentage(100);

            PdfPCell account = new PdfPCell(new Paragraph("Cuenta", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            account.setHorizontalAlignment(Element.ALIGN_CENTER);
            account.setBorderColor(BaseColor.WHITE);
            account.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell concept = new PdfPCell(new Paragraph("Concepto", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            concept.setHorizontalAlignment(Element.ALIGN_CENTER);
            concept.setPaddingRight(40);
            concept.setBorderColor(BaseColor.WHITE);
            concept.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell charge = new PdfPCell(new Paragraph("Cargo", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            charge.setHorizontalAlignment(Element.ALIGN_CENTER);
            charge.setBorderColor(BaseColor.WHITE);
            charge.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell payment = new PdfPCell(new Paragraph("Abono", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            payment.setHorizontalAlignment(Element.ALIGN_CENTER);
            payment.setBorderColor(BaseColor.WHITE);
            payment.setBackgroundColor(new BaseColor(182, 208, 226));


            headerTable.addCell(account);
            headerTable.addCell(concept);
            headerTable.addCell(charge);
            headerTable.addCell(payment);


            //--------- body
            PdfPTable bodyTable = new PdfPTable(4);
            bodyTable.setWidthPercentage(100);

            PdfPTable cargoTable = new PdfPTable(4);
            cargoTable.setWidthPercentage(100);

            PdfPTable abonoTable = new PdfPTable(4);
            abonoTable.setWidthPercentage(100);

            //---------------------   end header   --------------------- //


            //---------------------   starting R1  I AND PUE --------------------- //
            if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(I.getValue()) && policyObjFile.getPolicyObj().getMethodPayment().equals(PUE.getValue())) {
                if (List.of(RG601.getRegimen(), RG603.getRegimen(), RG620.getRegimen(),
                        RG622.getRegimen(), RG623.getRegimen(), RG625.getRegimen(),
                        RG612.getRegimen(), RG621.getRegimen(), RG626.getRegimen()).contains(policyObjFile.getPolicyObj().getRegimen()) &&
                        List.of(G03.getValue(), G01.getValue(), G02.getValue(), I01.getValue(),
                                I02.getValue(), I03.getValue(), I04.getValue(), I05.getValue(),
                                I06.getValue(), I07.getValue(), I08.getValue()).contains(policyObjFile.getPolicyObj().getUsoCFDI())) {
                    LOGGER.info("139 --- {} . {} . {}  . {} .  {} . {} . {} . {} , {} --- R1 PUE", type, policyObjFile.getPolicyObj().getTypeOfComprobante(), policyObjFile.getPolicyObj().getMethodPayment(), policyObjFile.getPolicyObj().getRegimen(), policyObjFile.getPolicyObj().getUsoCFDI(), policyObjFile.getCuenta_method(), policyObjFile.getTax_id(), policyObjFile.getCuenta(), fileName);


                    if (policyObjFile.getPolicyObj().getDescuento().isEmpty()) {
                        policyObjFile.getPolicyObj().setDescuento("0.00");
                    }

                    double suma = 0;
                    for (int i = 0; i < policyObjFile.getPolicyObj().getRetencionPago().size(); i++) {
                        suma += Double.parseDouble(policyObjFile.getPolicyObj().getRetencionPago().get(i));
                    }

                    PdfPCell accountBody = new PdfPCell(new Paragraph(policyObjFile.getCuenta(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    accountBody.setBorderColorBottom(BaseColor.BLACK);
                    accountBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell descriptionBody = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getConcepto_Descripcion() + "\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    descriptionBody.setBorderColorBottom(BaseColor.BLACK);
                    descriptionBody.setHorizontalAlignment(Element.ALIGN_CENTER);


                    PdfPCell paymentBody = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.valueOf(policyObjFile.getPolicyObj().getAbono().get(0))), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    paymentBody.setBorderColorLeft(BaseColor.WHITE);
                    paymentBody.setBorderColorRight(BaseColor.WHITE);
                    paymentBody.setBorderColorTop(BaseColor.WHITE);
                    paymentBody.setBorderColorBottom(BaseColor.BLACK);
                    paymentBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell Body = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    Body.setBorderColorBottom(BaseColor.BLACK);
                    Body.setHorizontalAlignment(Element.ALIGN_CENTER);


                    bodyTable.addCell(accountBody);
                    bodyTable.addCell(descriptionBody);
                    bodyTable.addCell(paymentBody);
                    bodyTable.addCell(Body);

                    double imp = suma + Double.parseDouble(policyObjFile.getPolicyObj().getAmount());
                    abonoTotal = String.valueOf(imp);

                    if (!policyObjFile.getTax_id().isEmpty() && A119_01.equals(policyObjFile.getTax_id().get(0))) {


                        PdfPCell accountCargo2 = new PdfPCell(new Paragraph(policyObjFile.getTax_id().get(0), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        accountCargo2.setBorderColorBottom(BaseColor.BLACK);
                        accountCargo2.setHorizontalAlignment(Element.ALIGN_CENTER);
                        accountCargo2.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell descripcionCargo2 = new PdfPCell(new Paragraph(policyObjFile.getTax_description().get(0), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        descripcionCargo2.setBorderColorBottom(BaseColor.BLACK);
                        descripcionCargo2.setHorizontalAlignment(Element.ALIGN_CENTER);
                        descripcionCargo2.setHorizontalAlignment(Element.ALIGN_CENTER);


                        PdfPCell abono2 = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.valueOf(policyObjFile.getPolicyObj().getImpuestos())), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        abono2.setBorderColorBottom(BaseColor.BLACK);
                        abono2.setHorizontalAlignment(Element.ALIGN_CENTER);
                        abono2.setHorizontalAlignment(Element.ALIGN_CENTER);
                        PdfPCell cargo2 = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        cargo2.setBorderColorBottom(BaseColor.BLACK);
                        cargo2.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cargo2.setHorizontalAlignment(Element.ALIGN_CENTER);

                        cargoTable.addCell(accountCargo2);
                        cargoTable.addCell(descripcionCargo2);
                        cargoTable.addCell(abono2);
                        cargoTable.addCell(cargo2);

                        cargoTotal = String.valueOf((Double.parseDouble(policyObjFile.getPolicyObj().getImpuestos()) + Double.parseDouble(policyObjFile.getPolicyObj().getAbono().get(0))));
                        for (int i = 0; i < policyObjFile.getPolicyObj().getRetencionId().size(); i++) {

                            PdfPCell accountCargo = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionId().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                            accountCargo.setBorderColorBottom(BaseColor.BLACK);
                            accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                            accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                            PdfPCell descripcionCargo = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionDesc().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                            descripcionCargo.setBorderColorBottom(BaseColor.BLACK);
                            descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                            descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);


                            PdfPCell abono = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                            abono.setBorderColorBottom(BaseColor.BLACK);
                            abono.setHorizontalAlignment(Element.ALIGN_CENTER);
                            abono.setHorizontalAlignment(Element.ALIGN_CENTER);
                            PdfPCell cargo = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.parseDouble(policyObjFile.getPolicyObj().getRetencionPago().get(i))), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                            cargo.setBorderColorBottom(BaseColor.BLACK);
                            cargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                            cargoTable.addCell(accountCargo);
                            cargoTable.addCell(descripcionCargo);
                            cargoTable.addCell(abono);
                            cargoTable.addCell(cargo);
                        }
                    } else {

                        // LOGGER.error("{}", policyObjFile);
                        for (int i = 0; i < policyObjFile.getTax_id().size(); i++) {

                            PdfPCell accountCargo = new PdfPCell(new Paragraph(policyObjFile.getTax_id().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                            accountCargo.setBorderColorBottom(BaseColor.BLACK);
                            accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                            accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                            PdfPCell descripcionCargo = new PdfPCell(new Paragraph(policyObjFile.getTax_description().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                            descripcionCargo.setBorderColorBottom(BaseColor.BLACK);
                            descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                            descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);


                            PdfPCell abono = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                            abono.setBorderColorBottom(BaseColor.BLACK);
                            abono.setHorizontalAlignment(Element.ALIGN_CENTER);
                            abono.setHorizontalAlignment(Element.ALIGN_CENTER);

                            //PdfPCell cargo = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.valueOf(policyObjFile.getPolicyObj().getAbono().get(i))), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                            PdfPCell cargo = new PdfPCell(new Paragraph("0.0", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                            cargo.setBorderColorBottom(BaseColor.BLACK);
                            cargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                            cargoTable.addCell(accountCargo);
                            cargoTable.addCell(descripcionCargo);
                            cargoTable.addCell(abono);
                            cargoTable.addCell(cargo);

                        }
                    }

                    PdfPCell accountAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getVenta_id(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    accountAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell descripcionAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getVenta_descripcion(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    descripcionAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);


                    PdfPCell AbonoPPD = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    AbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell AbonoPagoPPD = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.parseDouble(policyObjFile.getPolicyObj().getTotalAmount())), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    AbonoPagoPPD.setBorderColorBottom(BaseColor.BLACK);
                    AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    abonoTable.addCell(accountAbonoPPD);
                    abonoTable.addCell(descripcionAbonoPPD);
                    abonoTable.addCell(AbonoPPD);
                    abonoTable.addCell(AbonoPagoPPD);


                }
            } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(I.getValue()) && policyObjFile.getPolicyObj().getMetodo().equals(PPD.getValue())) {
                if (List.of(RG601.getRegimen(), RG603.getRegimen(), RG620.getRegimen(),
                        RG622.getRegimen(), RG623.getRegimen(), RG625.getRegimen(),
                        RG612.getRegimen(), RG621.getRegimen(), RG626.getRegimen()).contains(policyObjFile.getPolicyObj().getRegimen())
                        && List.of(G03.getValue(), G01.getValue(), G02.getValue(), I01.getValue(), I02.getValue(),
                        I03.getValue(), I04.getValue(), I05.getValue(), I06.getValue(), I07.getValue(),
                        I08.getValue()).contains(policyObjFile.getPolicyObj().getUsoCFDI())) {


                    if (policyObjFile.getCuenta() == null) {
                        policyObjFile.setCuenta("0");
                    }

                    LOGGER.info("289 --- {} . {} . {}  . {} .  {} . {} . {} . {} . {} --- R1 PPD ", type, policyObjFile.getPolicyObj().getTypeOfComprobante(), policyObjFile.getPolicyObj().getMethodPayment(), policyObjFile.getPolicyObj().getRegimen(), policyObjFile.getPolicyObj().getUsoCFDI(), policyObjFile.getCuenta_method(), policyObjFile.getTax_id(), policyObjFile.getCuenta(), fileName);
                    if (policyObjFile.getPolicyObj().getClaveProdServ().get(0).equals("78101801")) {
                        policyObjFile.setCuenta("501.01");
                        policyObjFile.getPolicyObj().setConcepto_Descripcion("Costo de venta");

                        PdfPCell accountBody = new PdfPCell(new Paragraph(policyObjFile.getCuenta(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        accountBody.setBorderColorBottom(BaseColor.BLACK);
                        accountBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell descriptionBody = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getConcepto_Descripcion() + "\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        descriptionBody.setBorderColorBottom(BaseColor.BLACK);
                        descriptionBody.setHorizontalAlignment(Element.ALIGN_CENTER);


                        PdfPCell paymentBody = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.parseDouble(policyObjFile.getPolicyObj().getTotalAmount())), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        paymentBody.setBorderColorLeft(BaseColor.WHITE);
                        paymentBody.setBorderColorRight(BaseColor.WHITE);
                        paymentBody.setBorderColorTop(BaseColor.WHITE);
                        paymentBody.setBorderColorBottom(BaseColor.BLACK);
                        paymentBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell Body = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        Body.setBorderColorBottom(BaseColor.BLACK);
                        Body.setHorizontalAlignment(Element.ALIGN_CENTER);


                        bodyTable.addCell(accountBody);
                        bodyTable.addCell(descriptionBody);
                        bodyTable.addCell(paymentBody);
                        bodyTable.addCell(Body);

                        PdfPCell accountCargo = new PdfPCell(new Paragraph("503.01", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        accountCargo.setBorderColorBottom(BaseColor.BLACK);
                        accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                        accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell descripcionCargo = new PdfPCell(new Paragraph("Devoluciones, descuentos o bonificaciones sobre compras", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        descripcionCargo.setBorderColorBottom(BaseColor.BLACK);
                        descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                        descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);


                        PdfPCell cargoCargo = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        cargoCargo.setBorderColorBottom(BaseColor.BLACK);
                        cargoCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cargoCargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell cargo = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.parseDouble(policyObjFile.getPolicyObj().getTotalAmount())), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        cargo.setBorderColorBottom(BaseColor.BLACK);
                        cargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                        cargoTable.addCell(accountCargo);
                        cargoTable.addCell(descripcionCargo);
                        cargoTable.addCell(cargoCargo);
                        cargoTable.addCell(cargo);


                        PdfPCell accountAbono = new PdfPCell(new Paragraph(policyObjFile.getCuenta_method(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        accountAbono.setBorderColorBottom(BaseColor.BLACK);
                        accountAbono.setHorizontalAlignment(Element.ALIGN_CENTER);
                        accountAbono.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell descripcionAbono = new PdfPCell(new Paragraph(policyObjFile.getDescription_methods(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        descripcionAbono.setBorderColorBottom(BaseColor.BLACK);
                        descripcionAbono.setHorizontalAlignment(Element.ALIGN_CENTER);
                        descripcionAbono.setHorizontalAlignment(Element.ALIGN_CENTER);


                        PdfPCell Abono = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.parseDouble(policyObjFile.getPolicyObj().getTraslado().get(0))), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        Abono.setBorderColorBottom(BaseColor.BLACK);
                        Abono.setHorizontalAlignment(Element.ALIGN_CENTER);
                        Abono.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell AbonoPago = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        AbonoPago.setBorderColorBottom(BaseColor.BLACK);
                        AbonoPago.setHorizontalAlignment(Element.ALIGN_CENTER);
                        AbonoPago.setHorizontalAlignment(Element.ALIGN_CENTER);

                        abonoTable.addCell(accountAbono);
                        abonoTable.addCell(descripcionAbono);
                        abonoTable.addCell(Abono);
                        abonoTable.addCell(AbonoPago);


                        PdfPCell accountAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getTax_id().get(0), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        accountAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                        accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                        accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell descripcionAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getTax_description().get(0), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        descripcionAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                        descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                        descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);


                        PdfPCell AbonoPPD = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        AbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                        AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                        AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell AbonoPagoPPD = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.parseDouble(policyObjFile.getPolicyObj().getTraslado().get(1))), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        AbonoPagoPPD.setBorderColorBottom(BaseColor.BLACK);
                        AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                        AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                        abonoTable.addCell(accountAbonoPPD);
                        abonoTable.addCell(descripcionAbonoPPD);
                        abonoTable.addCell(AbonoPPD);
                        abonoTable.addCell(AbonoPagoPPD);


                        double aux = Double.parseDouble(policyObjFile.getPolicyObj().getAmount()) + Double.parseDouble(policyObjFile.getPolicyObj().getImpuestos());
                        cargoTotal = String.valueOf(aux);
                        abonoTotal = String.valueOf(aux);
                    } else {
                        // LOGGER.info("407 else {}", policyObjFile);
                        double suma = 0;
                        for (int i = 0; i < policyObjFile.getPolicyObj().getAbono().size(); i++) {
                            suma += Double.parseDouble(policyObjFile.getPolicyObj().getAbono().get(i));
                        }
                        // CreateFilePDFBalance.decimal(Double.parseDouble(policyObjFile.getPolicyObj().getTotalAmount()));
                        cargoTotal = String.valueOf((Double.parseDouble(policyObjFile.getPolicyObj().getImpuestos()) + suma));

                        PdfPCell accountBody = new PdfPCell(new Paragraph(policyObjFile.getCuenta(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        accountBody.setBorderColorBottom(BaseColor.BLACK);
                        accountBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell descriptionBody = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getConcepto_Descripcion() + "\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        descriptionBody.setBorderColorBottom(BaseColor.BLACK);
                        descriptionBody.setHorizontalAlignment(Element.ALIGN_CENTER);


                        PdfPCell paymentBody = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.parseDouble(String.valueOf(suma))), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        paymentBody.setBorderColorLeft(BaseColor.WHITE);
                        paymentBody.setBorderColorRight(BaseColor.WHITE);
                        paymentBody.setBorderColorTop(BaseColor.WHITE);
                        paymentBody.setBorderColorBottom(BaseColor.BLACK);
                        paymentBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell Body = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        Body.setBorderColorBottom(BaseColor.BLACK);
                        Body.setHorizontalAlignment(Element.ALIGN_CENTER);


                        bodyTable.addCell(accountBody);
                        bodyTable.addCell(descriptionBody);
                        bodyTable.addCell(paymentBody);
                        bodyTable.addCell(Body);


                        PdfPCell accountCargo = new PdfPCell(new Paragraph(policyObjFile.getCuenta_method(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        accountCargo.setBorderColorBottom(BaseColor.BLACK);
                        accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                        accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell descripcionCargo = new PdfPCell(new Paragraph(policyObjFile.getDescription_methods(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        descripcionCargo.setBorderColorBottom(BaseColor.BLACK);
                        descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                        descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);


                        PdfPCell cargoCargo = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.parseDouble(policyObjFile.getPolicyObj().getImpuestos())), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        cargoCargo.setBorderColorBottom(BaseColor.BLACK);
                        cargoCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cargoCargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell cargo = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        cargo.setBorderColorBottom(BaseColor.BLACK);
                        cargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                        cargoTable.addCell(accountCargo);
                        cargoTable.addCell(descripcionCargo);
                        cargoTable.addCell(cargoCargo);
                        cargoTable.addCell(cargo);


                        double imp = Double.parseDouble(policyObjFile.getPolicyObj().getImpuestos()) + suma;
                        abonoTotal = String.valueOf(imp);
                        double ab = Double.parseDouble(abonoTotal);
                        if (!policyObjFile.getTax_id().isEmpty()) {
                            for (int i = 0; i < policyObjFile.getTax_id().size(); i++) {

                                PdfPCell accountAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getTax_id().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                                accountAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                                accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                                accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                                PdfPCell descripcionAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getTax_description().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                                descripcionAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                                descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                                descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);


                                PdfPCell AbonoPPD = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.parseDouble(String.valueOf(policyObjFile.getPolicyObj().getImpuestos()))), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                                AbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                                AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                                AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                                PdfPCell AbonoPagoPPD = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                                AbonoPagoPPD.setBorderColorBottom(BaseColor.BLACK);
                                AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                                AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                                abonoTable.addCell(accountAbonoPPD);
                                abonoTable.addCell(descripcionAbonoPPD);
                                abonoTable.addCell(AbonoPPD);
                                abonoTable.addCell(AbonoPagoPPD);


                            }

                        }

                        ab += Double.valueOf(Double.parseDouble(policyObjFile.getPolicyObj().getImpuestos()));
                        cargoTotal = String.valueOf(ab);

                        PdfPCell accountAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getVenta_id(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        accountAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                        accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                        accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell descripcionAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getVenta_descripcion(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        descripcionAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                        descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                        descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);


                        PdfPCell AbonoPPD = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        AbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                        AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                        AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell AbonoPagoPPD = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.parseDouble(policyObjFile.getPolicyObj().getTotalAmount())), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        AbonoPagoPPD.setBorderColorBottom(BaseColor.BLACK);
                        AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                        AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                        abonoTable.addCell(accountAbonoPPD);
                        abonoTable.addCell(descripcionAbonoPPD);
                        abonoTable.addCell(AbonoPPD);
                        abonoTable.addCell(AbonoPagoPPD);
                        //---------------------   end R1  I and PDD--------------------- //
                    }
                }
                //---------------------   only if is R2 PUE --------------------- //
            } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(E.getValue()) && policyObjFile.getPolicyObj().getMetodo().equals(PUE.getValue())) {
                if (List.of(RG601.getRegimen(), RG603.getRegimen(), RG612.getRegimen(),
                        RG605.getRegimen(), RG606.getRegimen(), RG607.getRegimen(),
                        RG608.getRegimen(), RG610.getRegimen(), RG611.getRegimen(),
                        RG612.getRegimen(), RG614.getRegimen(), RG615.getRegimen(),
                        RG616.getRegimen(), RG620.getRegimen(), RG621.getRegimen(),
                        RG623.getRegimen(), RG622.getRegimen(), RG624.getRegimen(),
                        RG625.getRegimen(), RG626.getRegimen()).contains(policyObjFile.getPolicyObj().getRegimen()) &&
                        List.of(G01.getValue(), G03.getValue(), G02.getValue(),
                                I01.getValue(), I02.getValue(),
                                I03.getValue(), I04.getValue(), I05.getValue(), I06.getValue(), I07.getValue(),
                                I08.getValue(), S01.getValue()).contains(policyObjFile.getPolicyObj().getUsoCFDI())) {


//                    if (policyObjFile.getCuenta() == null) {
//                        policyObjFile.setCuenta("0");
//                    }
                    //LOGGER.info("571  --- {} . {} . {}  . {} .  {} . {} . {} . {} --- R2  PUE", type, policyObjFile.getPolicyObj().getTypeOfComprobante(), policyObjFile.getPolicyObj().getMethodPayment(), policyObjFile.getPolicyObj().getRegimen(), policyObjFile.getPolicyObj().getUsoCFDI(), policyObjFile.getCuenta_method(), policyObjFile.getTax_id(), policyObjFile.getCuenta());
//                    LOGGER.error("{}", policyObjFile);
                    PdfPCell accountBody = new PdfPCell(new Paragraph(policyObjFile.getCuenta_method(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    accountBody.setBorderColorBottom(BaseColor.BLACK);
                    accountBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell descriptionBody = new PdfPCell(new Paragraph(policyObjFile.getDescription_methods() + "\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    descriptionBody.setBorderColorBottom(BaseColor.BLACK);
                    descriptionBody.setHorizontalAlignment(Element.ALIGN_CENTER);


                    PdfPCell paymentBody = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.parseDouble(policyObjFile.getPolicyObj().getTotalAmount())), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    paymentBody.setBorderColorLeft(BaseColor.WHITE);
                    paymentBody.setBorderColorRight(BaseColor.WHITE);
                    paymentBody.setBorderColorTop(BaseColor.WHITE);
                    paymentBody.setBorderColorBottom(BaseColor.BLACK);
                    paymentBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell Body = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    Body.setBorderColorBottom(BaseColor.BLACK);
                    Body.setHorizontalAlignment(Element.ALIGN_CENTER);


                    bodyTable.addCell(accountBody);
                    bodyTable.addCell(descriptionBody);
                    bodyTable.addCell(paymentBody);
                    bodyTable.addCell(Body);
                    int middle = policyObjFile.getPolicyObj().getRetencionId().size() / 2;
                    for (int i = 0; i < middle; i++) {

                        PdfPCell accountAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionId().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        accountAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                        accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                        accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell descripcionAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionDesc().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        descripcionAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                        descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                        descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);


                        PdfPCell AbonoPPD = new PdfPCell(new Paragraph("0.0.0", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        AbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                        AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                        AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell AbonoPagoPPD = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        AbonoPagoPPD.setBorderColorBottom(BaseColor.BLACK);
                        AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                        AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                        cargoTable.addCell(accountAbonoPPD);
                        cargoTable.addCell(descripcionAbonoPPD);
                        cargoTable.addCell(AbonoPPD);
                        cargoTable.addCell(AbonoPagoPPD);


                    }

                    for (int j = middle; j < policyObjFile.getPolicyObj().getRetencionId().size(); j++) {


                        PdfPCell accountCargo = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionId().get(j), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        accountCargo.setBorderColorBottom(BaseColor.BLACK);
                        accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                        accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                        PdfPCell descripcionCargo = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionDesc().get(j), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        descripcionCargo.setBorderColorBottom(BaseColor.BLACK);
                        descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                        descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);


                        PdfPCell abono = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        abono.setBorderColorBottom(BaseColor.BLACK);
                        abono.setHorizontalAlignment(Element.ALIGN_CENTER);
                        abono.setHorizontalAlignment(Element.ALIGN_CENTER);

                        //PdfPCell cargo = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.valueOf(policyObjFile.getPolicyObj().getAbono().get(i))), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        PdfPCell cargo = new PdfPCell(new Paragraph("0.0-1", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                        cargo.setBorderColorBottom(BaseColor.BLACK);
                        cargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                        abonoTable.addCell(accountCargo);
                        abonoTable.addCell(descripcionCargo);
                        abonoTable.addCell(abono);
                        abonoTable.addCell(cargo);

                    }


                    //---------------------   end R2 PUE --------------------- //
                }

            } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(E.getValue()) && policyObjFile.getPolicyObj().getMetodo().equals(PPD.getValue())) {
                //---------------------   only if is R2 PPD --------------------- //
                if (List.of(RG601.getRegimen(), RG603.getRegimen(), RG612.getRegimen(),
                        RG605.getRegimen(), RG606.getRegimen(), RG607.getRegimen(),
                        RG608.getRegimen(), RG610.getRegimen(), RG611.getRegimen(),
                        RG612.getRegimen(), RG614.getRegimen(), RG615.getRegimen(),
                        RG616.getRegimen(), RG620.getRegimen(), RG621.getRegimen(),
                        RG623.getRegimen(), RG622.getRegimen(), RG624.getRegimen(),
                        RG625.getRegimen(), RG626.getRegimen()).contains(policyObjFile.getPolicyObj().getRegimen()) &&
                        List.of(G01.getValue(), G03.getValue(), G02.getValue(),
                                I01.getValue(), I02.getValue(),
                                I03.getValue(), I04.getValue(), I05.getValue(), I06.getValue(), I07.getValue(),
                                I08.getValue(), S01.getValue()).contains(policyObjFile.getPolicyObj().getUsoCFDI())) {

                }
                LOGGER.info("619  --- {} . {} . {}  . {} .  {} . {} . {} . {} --- R2  PDD", type, policyObjFile.getPolicyObj().getTypeOfComprobante(), policyObjFile.getPolicyObj().getMethodPayment(), policyObjFile.getPolicyObj().getRegimen(), policyObjFile.getPolicyObj().getUsoCFDI(), policyObjFile.getCuenta_method(), policyObjFile.getTax_id(), policyObjFile.getCuenta());

                PdfPCell accountBody = new PdfPCell(new Paragraph(policyObjFile.getCuenta_method(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                accountBody.setBorderColorBottom(BaseColor.BLACK);
                accountBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell descriptionBody = new PdfPCell(new Paragraph(policyObjFile.getDescription_methods() + "\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                descriptionBody.setBorderColorBottom(BaseColor.BLACK);
                descriptionBody.setHorizontalAlignment(Element.ALIGN_CENTER);


                PdfPCell paymentBody = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.parseDouble(policyObjFile.getPolicyObj().getTotalAmount())), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                paymentBody.setBorderColorLeft(BaseColor.WHITE);
                paymentBody.setBorderColorRight(BaseColor.WHITE);
                paymentBody.setBorderColorTop(BaseColor.WHITE);
                paymentBody.setBorderColorBottom(BaseColor.BLACK);
                paymentBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell Body = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                Body.setBorderColorBottom(BaseColor.BLACK);
                Body.setHorizontalAlignment(Element.ALIGN_CENTER);


                bodyTable.addCell(accountBody);
                bodyTable.addCell(descriptionBody);
                bodyTable.addCell(paymentBody);
                bodyTable.addCell(Body);

                int middle = policyObjFile.getPolicyObj().getRetencionId().size() / 2;
                for (int i = 0; i < middle; i++) {

                    PdfPCell accountAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionId().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    accountAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell descripcionAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionDesc().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    descripcionAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);


                    PdfPCell AbonoPPD = new PdfPCell(new Paragraph("0.0.0", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    AbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell AbonoPagoPPD = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    AbonoPagoPPD.setBorderColorBottom(BaseColor.BLACK);
                    AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    cargoTable.addCell(accountAbonoPPD);
                    cargoTable.addCell(descripcionAbonoPPD);
                    cargoTable.addCell(AbonoPPD);
                    cargoTable.addCell(AbonoPagoPPD);


                }

                for (int j = middle; j < policyObjFile.getPolicyObj().getRetencionId().size(); j++) {


                    PdfPCell accountCargo = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionId().get(j), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    accountCargo.setBorderColorBottom(BaseColor.BLACK);
                    accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell descripcionCargo = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionDesc().get(j), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    descripcionCargo.setBorderColorBottom(BaseColor.BLACK);
                    descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);


                    PdfPCell abono = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    abono.setBorderColorBottom(BaseColor.BLACK);
                    abono.setHorizontalAlignment(Element.ALIGN_CENTER);
                    abono.setHorizontalAlignment(Element.ALIGN_CENTER);

                    //PdfPCell cargo = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.valueOf(policyObjFile.getPolicyObj().getAbono().get(i))), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    PdfPCell cargo = new PdfPCell(new Paragraph("0.0-1", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    cargo.setBorderColorBottom(BaseColor.BLACK);
                    cargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                    abonoTable.addCell(accountCargo);
                    abonoTable.addCell(descripcionCargo);
                    abonoTable.addCell(abono);
                    abonoTable.addCell(cargo);

                }


            } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(RETENCIONES.getValue())) {
                //---------------------   only if is R3  --------------------- //
                if (List.of(
                        RG605.getRegimen(), RG606.getRegimen(), RG607.getRegimen(), RG608.getRegimen(),
                        RG610.getRegimen(), RG611.getRegimen(), RG612.getRegimen(), RG614.getRegimen(),
                        RG615.getRegimen(), RG616.getRegimen(), RG620.getRegimen(), RG621.getRegimen(),
                        RG622.getRegimen(), RG623.getRegimen(), RG624.getRegimen(), RG625.getRegimen(),
                        RG626.getRegimen()).contains(policyObjFile.getPolicyObj().getRegimen())) {


                }


                //---------------------   only if is R4  --------------------- //
            } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(N.getValue())) {
                if (policyObjFile.getPolicyObj().getRegimen().equals(RG605.getRegimen()) &&
                        policyObjFile.getPolicyObj().getUsoCFDI().equals(CN01.getValue())) {

                    LOGGER.info("1265 R4 --- {} . {} . {}  . {} .  {} . {} . {} . {} , {} --- R4 ", type, policyObjFile.getPolicyObj().getTypeOfComprobante(), policyObjFile.getPolicyObj().getMethodPayment(), policyObjFile.getPolicyObj().getRegimen(), policyObjFile.getPolicyObj().getUsoCFDI(), policyObjFile.getCuenta_method(), policyObjFile.getTax_id(), policyObjFile.getCuenta(), fileName);
                }
                //---------------------   end R4  --------------------- //
                //---------------------   only if is R5  --------------------- //
            } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(P.getValue())
                    && policyObjFile.getPolicyObj().getUsoCFDI().equals(CP01.getValue())
                    && List.of(RG601.getRegimen(), RG603.getRegimen(), RG605.getRegimen(),
                    RG606.getRegimen(), RG607.getRegimen(), RG608.getRegimen(),
                    RG610.getRegimen(), RG611.getRegimen(), RG612.getRegimen(),
                    RG614.getRegimen(), RG615.getRegimen(), RG616.getRegimen(),
                    RG620.getRegimen(), RG621.getRegimen(), RG622.getRegimen(),
                    RG623.getRegimen(), RG624.getRegimen(), RG625.getRegimen(),
                    RG626.getRegimen()).contains(policyObjFile.getPolicyObj().getRegimen())) {
                LOGGER.info("1265 --- {} . {} . {}  . {} .  {} . {} . {} . {} , {} --- R5 ", type, policyObjFile.getPolicyObj().getTypeOfComprobante(), policyObjFile.getPolicyObj().getMethodPayment(), policyObjFile.getPolicyObj().getRegimen(), policyObjFile.getPolicyObj().getUsoCFDI(), policyObjFile.getCuenta_method(), policyObjFile.getTax_id(), policyObjFile.getCuenta(), fileName);

                PdfPCell accountBody = new PdfPCell(new Paragraph(policyObjFile.getCuenta_method(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                accountBody.setBorderColorBottom(BaseColor.BLACK);
                accountBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell descriptionBody = new PdfPCell(new Paragraph(policyObjFile.getDescription_methods() + "\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                descriptionBody.setBorderColorBottom(BaseColor.BLACK);
                descriptionBody.setHorizontalAlignment(Element.ALIGN_CENTER);


                PdfPCell paymentBody = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.parseDouble(policyObjFile.getPolicyObj().getTotalAmount())), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                paymentBody.setBorderColorLeft(BaseColor.WHITE);
                paymentBody.setBorderColorRight(BaseColor.WHITE);
                paymentBody.setBorderColorTop(BaseColor.WHITE);
                paymentBody.setBorderColorBottom(BaseColor.BLACK);
                paymentBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell Body = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                Body.setBorderColorBottom(BaseColor.BLACK);
                Body.setHorizontalAlignment(Element.ALIGN_CENTER);


                bodyTable.addCell(accountBody);
                bodyTable.addCell(descriptionBody);
                bodyTable.addCell(paymentBody);
                bodyTable.addCell(Body);

                if (policyObjFile.getPolicyObj().getConceptoTipoFactorPPD().get(0).equals("002")) {
                    PdfPCell accountAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionId().get(0), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    accountAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell descripcionAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionDesc().get(0), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    descripcionAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);


                    PdfPCell AbonoPPD = new PdfPCell(new Paragraph("0.0.5", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    AbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell AbonoPagoPPD = new PdfPCell(new Paragraph("0.0.5", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    AbonoPagoPPD.setBorderColorBottom(BaseColor.BLACK);
                    AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    cargoTable.addCell(accountAbonoPPD);
                    cargoTable.addCell(descripcionAbonoPPD);
                    cargoTable.addCell(AbonoPPD);
                    cargoTable.addCell(AbonoPagoPPD);


                    PdfPCell accountCargo = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionId().get(1), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    accountCargo.setBorderColorBottom(BaseColor.BLACK);
                    accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell descripcionCargo = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionDesc().get(1), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    descripcionCargo.setBorderColorBottom(BaseColor.BLACK);
                    descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);


                    PdfPCell abono = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    abono.setBorderColorBottom(BaseColor.BLACK);
                    abono.setHorizontalAlignment(Element.ALIGN_CENTER);
                    abono.setHorizontalAlignment(Element.ALIGN_CENTER);

                    //PdfPCell cargo = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.valueOf(policyObjFile.getPolicyObj().getAbono().get(i))), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    PdfPCell cargo = new PdfPCell(new Paragraph("0.0-5", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    cargo.setBorderColorBottom(BaseColor.BLACK);
                    cargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                    abonoTable.addCell(accountCargo);
                    abonoTable.addCell(descripcionCargo);
                    abonoTable.addCell(abono);
                    abonoTable.addCell(cargo);
                }


            }

//                //---------------------   end R5  --------------------- //

//            //---------------------   footer of file  --------------------- //

            PdfPTable footer = new PdfPTable(3);

            PdfPCell sumFooter = new PdfPCell(new Paragraph("SUMAS IGUALES", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            sumFooter.setHorizontalAlignment(Element.ALIGN_CENTER);
            sumFooter.setBackgroundColor(new BaseColor(182, 208, 226));
            if (cargoTotal == null || cargoTotal.isEmpty()) {
                cargoTotal = "0.00";
            }

            if (abonoTotal == null || abonoTotal.isEmpty()) {
                abonoTotal = "0.00";
            }
            PdfPCell sumCargo = new PdfPCell(new Paragraph("$" + CreateFilePDFBalance.decimal(Double.parseDouble(cargoTotal)), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
            sumCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell sumAbono = new PdfPCell(new Paragraph("$" + CreateFilePDFBalance.decimal(Double.parseDouble(abonoTotal)), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
            sumAbono.setHorizontalAlignment(Element.ALIGN_CENTER);


            footer.addCell(sumFooter);
            footer.addCell(sumCargo);
            footer.addCell(sumAbono);

            footer.setTotalWidth(523);
            footer.setHorizontalAlignment(2);
            footer.setWidthPercentage(60);


            //---------------------   last values of file  --------------------- //


            PdfPTable headerLastValues = new PdfPTable(4);
            headerLastValues.setWidthPercentage(100);

            PdfPCell cellDate = new PdfPCell(new Paragraph("Fecha", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            cellDate.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellDate.setBorderColor(BaseColor.WHITE);
            cellDate.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell cellConcept = new PdfPCell(new Paragraph("Proveedor / Cliente / Nómina", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            cellConcept.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellConcept.setBorderColor(BaseColor.WHITE);
            cellConcept.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell cellFolio = new PdfPCell(new Paragraph("Folio Fiscal", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            cellFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellFolio.setBorderColor(BaseColor.WHITE);
            cellFolio.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell cellTotal = new PdfPCell(new Paragraph("Total", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            cellTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTotal.setBorderColor(BaseColor.WHITE);
            cellTotal.setBackgroundColor(new BaseColor(182, 208, 226));

            headerLastValues.addCell(cellDate);
            headerLastValues.addCell(cellConcept);
            headerLastValues.addCell(cellFolio);
            headerLastValues.addCell(cellTotal);


            PdfPTable lastValues = new PdfPTable(4);

            lastValues.setWidthPercentage(100);

            PdfPCell cellUUID = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getTimbreFiscalDigital_UUID(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.NORMAL, BaseColor.BLACK)));
            cellUUID.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellUUID.setBorderColor(BaseColor.BLACK);

            PdfPCell totalAmount = new PdfPCell(new Paragraph("$" + CreateFilePDFBalance.decimal(Double.parseDouble(policyObjFile.getPolicyObj().getTotalAmount())), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.NORMAL, BaseColor.BLACK)));
            totalAmount.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalAmount.setBorderColor(BaseColor.BLACK);


            PdfPCell provedor = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getCompanyName(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.NORMAL, BaseColor.BLACK)));
            provedor.setHorizontalAlignment(Element.ALIGN_CENTER);
            provedor.setBorderColor(BaseColor.BLACK);


            PdfPCell end_date = new PdfPCell(new Paragraph(policyObjFile.getDate(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.NORMAL, BaseColor.BLACK)));
            end_date.setHorizontalAlignment(Element.ALIGN_CENTER);
            end_date.setBorderColor(BaseColor.BLACK);


            lastValues.addCell(end_date);
            lastValues.addCell(provedor);
            lastValues.addCell(cellUUID);
            lastValues.addCell(totalAmount);


            document.add(table);
            document.add(new Paragraph("\n"));
            document.add(headerTable);
            document.add(bodyTable);
            document.add(cargoTable);
            document.add(abonoTable);
            document.add(footer);
            document.add(new Paragraph("\n\n\n\n"));
            document.add(headerLastValues);
            document.add(lastValues);
            document.close();

            //UploadFileToS3_Policies.uploadPDF(fileName + ".pdf", rfc, type);
            return true;

        } catch (Exception e) {
            LOGGER.error("CreateFilePDFPolicy makeFileEgreso {} {} ", e.getMessage(), e.getLocalizedMessage());
        }
        return false;
    }


    public static boolean makeFileIngreso(PolicyObjFile policyObjFile, String fileName, String rfc, String type) {
        try {
            String cargoTotal = "";
            String abonoTotal = "";

            File uploadDir = new File(local_path + rfc + "/pdf/" + type + "/");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(local_path + rfc + "/pdf/" + type + "/" + fileName + ".pdf"));
            document.open();

            //---------------------   header of file  --------------------- //

            PdfPTable table = new PdfPTable(2);

            PdfPCell client = new PdfPCell(new Paragraph(policyObjFile.getClient(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            client.setHorizontalAlignment(Element.ALIGN_CENTER);
            client.setBorderColor(BaseColor.WHITE);
            client.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell folio = new PdfPCell(new Paragraph("P ó l i z a" + "\n" + " Tipo: " + policyObjFile.getTypeOf() + "  Folio: " + policyObjFile.getFolio(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK.darker())));
            folio.setHorizontalAlignment(Element.ALIGN_CENTER);
            folio.setBorderColor(BaseColor.WHITE);
            folio.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell company = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getCompanyName(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
            company.setHorizontalAlignment(Element.ALIGN_CENTER);
            company.setBorderColor(BaseColor.WHITE);
            company.setBackgroundColor(new BaseColor(229, 231, 233));


            PdfPCell date_ = new PdfPCell(new Paragraph(policyObjFile.getDate(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, Font.BOLD, BaseColor.BLACK)));
            date_.setHorizontalAlignment(Element.ALIGN_CENTER);
            date_.setBorderColor(BaseColor.WHITE);
            date_.setBackgroundColor(new BaseColor(229, 231, 233));


            table.addCell(client);
            table.addCell(folio);
            table.addCell(company);
            table.addCell(date_);
            table.setHorizontalAlignment(2);
            table.setWidthPercentage(60);

            PdfPTable headerTable = new PdfPTable(4);
            headerTable.setWidthPercentage(100);

            PdfPCell account = new PdfPCell(new Paragraph("Cuenta", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            account.setHorizontalAlignment(Element.ALIGN_CENTER);
            account.setBorderColor(BaseColor.WHITE);
            account.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell concept = new PdfPCell(new Paragraph("Concepto", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            concept.setHorizontalAlignment(Element.ALIGN_CENTER);
            concept.setPaddingRight(40);
            concept.setBorderColor(BaseColor.WHITE);
            concept.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell charge = new PdfPCell(new Paragraph("Cargo", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            charge.setHorizontalAlignment(Element.ALIGN_CENTER);
            charge.setBorderColor(BaseColor.WHITE);
            charge.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell payment = new PdfPCell(new Paragraph("Abono", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            payment.setHorizontalAlignment(Element.ALIGN_CENTER);
            payment.setBorderColor(BaseColor.WHITE);
            payment.setBackgroundColor(new BaseColor(182, 208, 226));


            headerTable.addCell(account);
            headerTable.addCell(concept);
            headerTable.addCell(charge);
            headerTable.addCell(payment);


            //--------- body
            PdfPTable bodyTable = new PdfPTable(4);
            bodyTable.setWidthPercentage(100);

            PdfPTable cargoTable = new PdfPTable(4);
            cargoTable.setWidthPercentage(100);

            PdfPTable abonoTable = new PdfPTable(4);
            abonoTable.setWidthPercentage(100);

            //---------------------   end header   -----


            //---------------------header end-------------------- - //


            //---------------------   starting R6  I AND PPD --------------------- //
            if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(I.getValue())
                    && List.of(G01.getValue(), G03.getValue(), I01.getValue(), I02.getValue(),
                    I03.getValue(), I04.getValue(), I05.getValue(), I06.getValue(), I07.getValue(),
                    I08.getValue(), S01.getValue()).contains(policyObjFile.getPolicyObj().getUsoCFDI())
                    && List.of(RG601.getRegimen(), RG603.getRegimen(), RG606.getRegimen(),
                    RG607.getRegimen(), RG608.getRegimen(), RG610.getRegimen(), RG612.getRegimen(), RG614.getRegimen(),
                    RG620.getRegimen(), RG621.getRegimen()).contains(policyObjFile.getPolicyObj().getRegimen())
                    && policyObjFile.getPolicyObj().getMethodPayment().equals(PPD.getValue())) {

                LOGGER.info("1030 --- {} . {} . {}  . {} .  {} . {} . {} . {} , {} --- R6 PPD", type, policyObjFile.getPolicyObj().getTypeOfComprobante(), policyObjFile.getPolicyObj().getMethodPayment(), policyObjFile.getPolicyObj().getRegimen(), policyObjFile.getPolicyObj().getUsoCFDI(), policyObjFile.getCuenta_method(), policyObjFile.getTax_id(), policyObjFile.getCuenta(), fileName);


                PdfPCell accountBody = new PdfPCell(new Paragraph(policyObjFile.getCuenta(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                accountBody.setBorderColorBottom(BaseColor.BLACK);
                accountBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell descriptionBody = new PdfPCell(new Paragraph(policyObjFile.getCuenta_method() + "\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                descriptionBody.setBorderColorBottom(BaseColor.BLACK);
                descriptionBody.setHorizontalAlignment(Element.ALIGN_CENTER);


                PdfPCell paymentBody = new PdfPCell(new Paragraph("0.06", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                paymentBody.setBorderColorLeft(BaseColor.WHITE);
                paymentBody.setBorderColorRight(BaseColor.WHITE);
                paymentBody.setBorderColorTop(BaseColor.WHITE);
                paymentBody.setBorderColorBottom(BaseColor.BLACK);
                paymentBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell Body = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                Body.setBorderColorBottom(BaseColor.BLACK);
                Body.setHorizontalAlignment(Element.ALIGN_CENTER);


                bodyTable.addCell(accountBody);
                bodyTable.addCell(descriptionBody);
                bodyTable.addCell(paymentBody);
                bodyTable.addCell(Body);


                for (int i = 0; i < policyObjFile.getPolicyObj().getRetencionId().size(); i++) {

                    PdfPCell accountAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionId().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    accountAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell descripcionAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionDesc().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    descripcionAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);


                    PdfPCell AbonoPPD = new PdfPCell(new Paragraph("0.0.6", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    AbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell AbonoPagoPPD = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    AbonoPagoPPD.setBorderColorBottom(BaseColor.BLACK);
                    AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    cargoTable.addCell(accountAbonoPPD);
                    cargoTable.addCell(descripcionAbonoPPD);
                    cargoTable.addCell(AbonoPPD);
                    cargoTable.addCell(AbonoPagoPPD);


                }
                //cargo
                for (int j = 0; j < policyObjFile.getTax_id().size(); j++) {


                    PdfPCell accountCargo = new PdfPCell(new Paragraph(policyObjFile.getTax_id().get(j), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    accountCargo.setBorderColorBottom(BaseColor.BLACK);
                    accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell descripcionCargo = new PdfPCell(new Paragraph(policyObjFile.getTax_description().get(j), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    descripcionCargo.setBorderColorBottom(BaseColor.BLACK);
                    descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);


                    PdfPCell abono = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    abono.setBorderColorBottom(BaseColor.BLACK);
                    abono.setHorizontalAlignment(Element.ALIGN_CENTER);
                    abono.setHorizontalAlignment(Element.ALIGN_CENTER);

                    //PdfPCell cargo = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.valueOf(policyObjFile.getPolicyObj().getAbono().get(i))), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    PdfPCell cargo = new PdfPCell(new Paragraph("0.0-66", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    cargo.setBorderColorBottom(BaseColor.BLACK);
                    cargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                    abonoTable.addCell(accountCargo);
                    abonoTable.addCell(descripcionCargo);
                    abonoTable.addCell(abono);
                    abonoTable.addCell(cargo);

                }


                PdfPCell accountAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getVenta_id(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                accountAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell descripcionAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getVenta_descripcion(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                descripcionAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);


                PdfPCell AbonoPPD = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                AbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell AbonoPagoPPD = new PdfPCell(new Paragraph("abon", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                AbonoPagoPPD.setBorderColorBottom(BaseColor.BLACK);
                AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                abonoTable.addCell(accountAbonoPPD);
                abonoTable.addCell(descripcionAbonoPPD);
                abonoTable.addCell(AbonoPPD);
                abonoTable.addCell(AbonoPagoPPD);


                //---------------------   starting R6  I AND PUE --------------------- //
            } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(I.getValue())
                    && List.of(G01.getValue(), G03.getValue(), I01.getValue(), I02.getValue(),
                    I03.getValue(), I04.getValue(), I05.getValue(), I06.getValue(), I07.getValue(),
                    I08.getValue(), S01.getValue()).contains(policyObjFile.getPolicyObj().getUsoCFDI())
                    && List.of(RG601.getRegimen(), RG603.getRegimen(), RG606.getRegimen(),
                    RG607.getRegimen(), RG608.getRegimen(), RG610.getRegimen(), RG612.getRegimen(), RG614.getRegimen(),
                    RG620.getRegimen(), RG621.getRegimen()).contains(policyObjFile.getPolicyObj().getRegimen())
                    && policyObjFile.getPolicyObj().getMethodPayment().equals(PUE.getValue())) {

                LOGGER.info("1136 --- {} . {} . {}  . {} .  {} . {} . {} . {} , {} --- R6 PUE", type, policyObjFile.getPolicyObj().getTypeOfComprobante(), policyObjFile.getPolicyObj().getMethodPayment(), policyObjFile.getPolicyObj().getRegimen(), policyObjFile.getPolicyObj().getUsoCFDI(), policyObjFile.getCuenta_method(), policyObjFile.getTax_id(), policyObjFile.getCuenta(), fileName);


                PdfPCell accountBody = new PdfPCell(new Paragraph(policyObjFile.getCuenta(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                accountBody.setBorderColorBottom(BaseColor.BLACK);
                accountBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell descriptionBody = new PdfPCell(new Paragraph(policyObjFile.getCuenta_method() + "\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                descriptionBody.setBorderColorBottom(BaseColor.BLACK);
                descriptionBody.setHorizontalAlignment(Element.ALIGN_CENTER);


                PdfPCell paymentBody = new PdfPCell(new Paragraph("0.06", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                paymentBody.setBorderColorLeft(BaseColor.WHITE);
                paymentBody.setBorderColorRight(BaseColor.WHITE);
                paymentBody.setBorderColorTop(BaseColor.WHITE);
                paymentBody.setBorderColorBottom(BaseColor.BLACK);
                paymentBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell Body = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                Body.setBorderColorBottom(BaseColor.BLACK);
                Body.setHorizontalAlignment(Element.ALIGN_CENTER);


                bodyTable.addCell(accountBody);
                bodyTable.addCell(descriptionBody);
                bodyTable.addCell(paymentBody);
                bodyTable.addCell(Body);


                for (int i = 0; i < policyObjFile.getPolicyObj().getRetencionId().size(); i++) {

                    PdfPCell accountAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionId().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    accountAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell descripcionAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionDesc().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    descripcionAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);


                    PdfPCell AbonoPPD = new PdfPCell(new Paragraph("0.0.6", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    AbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell AbonoPagoPPD = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    AbonoPagoPPD.setBorderColorBottom(BaseColor.BLACK);
                    AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    cargoTable.addCell(accountAbonoPPD);
                    cargoTable.addCell(descripcionAbonoPPD);
                    cargoTable.addCell(AbonoPPD);
                    cargoTable.addCell(AbonoPagoPPD);


                }
                //cargo
                for (int j = 0; j < policyObjFile.getTax_id().size(); j++) {


                    PdfPCell accountCargo = new PdfPCell(new Paragraph(policyObjFile.getTax_id().get(j), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    accountCargo.setBorderColorBottom(BaseColor.BLACK);
                    accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    accountCargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell descripcionCargo = new PdfPCell(new Paragraph(policyObjFile.getTax_description().get(j), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    descripcionCargo.setBorderColorBottom(BaseColor.BLACK);
                    descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    descripcionCargo.setHorizontalAlignment(Element.ALIGN_CENTER);


                    PdfPCell abono = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    abono.setBorderColorBottom(BaseColor.BLACK);
                    abono.setHorizontalAlignment(Element.ALIGN_CENTER);
                    abono.setHorizontalAlignment(Element.ALIGN_CENTER);

                    //PdfPCell cargo = new PdfPCell(new Paragraph(CreateFilePDFBalance.decimal(Double.valueOf(policyObjFile.getPolicyObj().getAbono().get(i))), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    PdfPCell cargo = new PdfPCell(new Paragraph("0.0-66", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    cargo.setBorderColorBottom(BaseColor.BLACK);
                    cargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cargo.setHorizontalAlignment(Element.ALIGN_CENTER);

                    abonoTable.addCell(accountCargo);
                    abonoTable.addCell(descripcionCargo);
                    abonoTable.addCell(abono);
                    abonoTable.addCell(cargo);

                }


                PdfPCell accountAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getVenta_id(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                accountAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell descripcionAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getVenta_descripcion(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                descripcionAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);


                PdfPCell AbonoPPD = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                AbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell AbonoPagoPPD = new PdfPCell(new Paragraph("abon", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                AbonoPagoPPD.setBorderColorBottom(BaseColor.BLACK);
                AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                abonoTable.addCell(accountAbonoPPD);
                abonoTable.addCell(descripcionAbonoPPD);
                abonoTable.addCell(AbonoPPD);
                abonoTable.addCell(AbonoPagoPPD);

                //---------------------   end R6  I and PUE--------------------- //


            } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(E.getValue())
                    && List.of(G01.getValue(), G02.getValue(), G03.getValue(),
                    I01.getValue(), I02.getValue(), I03.getValue(), I04.getValue(),
                    I05.getValue(), I06.getValue(), I07.getValue(), I08.getValue(),
                    S01.getValue()).contains(policyObjFile.getPolicyObj().getUsoCFDI())
                    && List.of(RG601.getRegimen(), RG603.getRegimen(), RG606.getRegimen(),
                    RG607.getRegimen(), RG608.getRegimen(), RG610.getRegimen(), RG612.getRegimen(),
                    RG614.getRegimen(), RG620.getRegimen(), RG621.getRegimen(), RG622.getRegimen(),
                    RG623.getRegimen()).contains(policyObjFile.getPolicyObj().getRegimen())) {

                LOGGER.info("1158 --- {} . {} . {}  . {} .  {} . {} . {} . {} , {} --- R7 ", type, policyObjFile.getPolicyObj().getTypeOfComprobante(), policyObjFile.getPolicyObj().getMethodPayment(), policyObjFile.getPolicyObj().getRegimen(), policyObjFile.getPolicyObj().getUsoCFDI(), policyObjFile.getCuenta_method(), policyObjFile.getTax_id(), policyObjFile.getCuenta(), fileName);


                //---------------------   end R7  --------------------- //

                //---------------------   starting R8   --------------------- //
            } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(RETENCIONES.getValue())
                    && List.of(RG601.getRegimen(), RG603.getRegimen()).contains(policyObjFile.getPolicyObj().getRegimen())) {


                LOGGER.info("1165 --- {} . {} . {}  . {} .  {} . {} . {} . {} , {} --- R8", type, policyObjFile.getPolicyObj().getTypeOfComprobante(), policyObjFile.getPolicyObj().getMethodPayment(), policyObjFile.getPolicyObj().getRegimen(), policyObjFile.getPolicyObj().getUsoCFDI(), policyObjFile.getCuenta_method(), policyObjFile.getTax_id(), policyObjFile.getCuenta(), fileName);

                //---------------------   end R8  --------------------- //

                //---------------------   starting R9  N --------------------- //
            } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(N.getValue())
                    && policyObjFile.getPolicyObj().getUsoCFDI().equals(CN01.getValue())
                    && List.of(RG601.getRegimen(), RG603.getRegimen(), RG605.getRegimen(),
                    RG606.getRegimen(), RG607.getRegimen(), RG608.getRegimen(), RG610.getRegimen(),
                    RG611.getRegimen(), RG612.getRegimen(), RG614.getRegimen(), RG615.getRegimen(),
                    RG616.getRegimen(), RG620.getRegimen(), RG621.getRegimen(), RG622.getRegimen(),
                    RG623.getRegimen(), RG624.getRegimen(), RG625.getRegimen(), RG626.getRegimen()).contains(policyObjFile.getPolicyObj().getRegimen())) {
                LOGGER.info("1398  --- {} . {} . {}  . {} .  {} . {} . {} . {} , {} --- R9 ", type, policyObjFile.getPolicyObj().getTypeOfComprobante(), policyObjFile.getPolicyObj().getMethodPayment(), policyObjFile.getPolicyObj().getRegimen(), policyObjFile.getPolicyObj().getUsoCFDI(), policyObjFile.getCuenta_method(), policyObjFile.getTax_id(), policyObjFile.getCuenta(), fileName);


                PdfPCell accountBody = new PdfPCell(new Paragraph(policyObjFile.getCuenta(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                accountBody.setBorderColorBottom(BaseColor.BLACK);
                accountBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell descriptionBody = new PdfPCell(new Paragraph(policyObjFile.getCuenta_method() + "\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                descriptionBody.setBorderColorBottom(BaseColor.BLACK);
                descriptionBody.setHorizontalAlignment(Element.ALIGN_CENTER);


                PdfPCell paymentBody = new PdfPCell(new Paragraph("0.06", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                paymentBody.setBorderColorLeft(BaseColor.WHITE);
                paymentBody.setBorderColorRight(BaseColor.WHITE);
                paymentBody.setBorderColorTop(BaseColor.WHITE);
                paymentBody.setBorderColorBottom(BaseColor.BLACK);
                paymentBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell Body = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                Body.setBorderColorBottom(BaseColor.BLACK);
                Body.setHorizontalAlignment(Element.ALIGN_CENTER);


                bodyTable.addCell(accountBody);
                bodyTable.addCell(descriptionBody);
                bodyTable.addCell(paymentBody);
                bodyTable.addCell(Body);


                for (int i = 0; i < policyObjFile.getPolicyObj().getRetencionId().size(); i++) {

                    PdfPCell accountAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionId().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    accountAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell descripcionAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionDesc().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    descripcionAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);


                    PdfPCell AbonoPPD = new PdfPCell(new Paragraph("0.0.6", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    AbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell AbonoPagoPPD = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    AbonoPagoPPD.setBorderColorBottom(BaseColor.BLACK);
                    AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    cargoTable.addCell(accountAbonoPPD);
                    cargoTable.addCell(descripcionAbonoPPD);
                    cargoTable.addCell(AbonoPPD);
                    cargoTable.addCell(AbonoPagoPPD);


                }

                PdfPCell accountAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getVenta_id(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                accountAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell descripcionAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getVenta_descripcion(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                descripcionAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);


                PdfPCell AbonoPPD = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                AbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell AbonoPagoPPD = new PdfPCell(new Paragraph("abon", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                AbonoPagoPPD.setBorderColorBottom(BaseColor.BLACK);
                AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                abonoTable.addCell(accountAbonoPPD);
                abonoTable.addCell(descripcionAbonoPPD);
                abonoTable.addCell(AbonoPPD);
                abonoTable.addCell(AbonoPagoPPD);


                //---------------------   end R9  --------------------- //


            } else if (policyObjFile.getPolicyObj().getTypeOfComprobante().equals(P.getValue())
                    && policyObjFile.getPolicyObj().getUsoCFDI().equals(CP01.getValue())
                    && List.of(RG601.getRegimen(), RG603.getRegimen(), RG605.getRegimen(),
                    RG606.getRegimen(), RG607.getRegimen(), RG608.getRegimen(),
                    RG610.getRegimen(), RG611.getRegimen(), RG612.getRegimen(),
                    RG614.getRegimen(), RG615.getRegimen(), RG616.getRegimen(),
                    RG620.getRegimen(), RG621.getRegimen(), RG622.getRegimen(),
                    RG623.getRegimen(), RG624.getRegimen(), RG625.getRegimen(),
                    RG626.getRegimen()).contains(policyObjFile.getPolicyObj().getRegimen())) {


                LOGGER.info("1265 --- {} . {} . {}  . {} .  {} . {} . {} . {} , {} --- R10 ", type, policyObjFile.getPolicyObj().getTypeOfComprobante(), policyObjFile.getPolicyObj().getMethodPayment(), policyObjFile.getPolicyObj().getRegimen(), policyObjFile.getPolicyObj().getUsoCFDI(), policyObjFile.getCuenta_method(), policyObjFile.getTax_id(), policyObjFile.getCuenta(), fileName);


                PdfPCell accountBody = new PdfPCell(new Paragraph(policyObjFile.getCuenta(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                accountBody.setBorderColorBottom(BaseColor.BLACK);
                accountBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell descriptionBody = new PdfPCell(new Paragraph(policyObjFile.getCuenta_method() + "\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                descriptionBody.setBorderColorBottom(BaseColor.BLACK);
                descriptionBody.setHorizontalAlignment(Element.ALIGN_CENTER);


                PdfPCell paymentBody = new PdfPCell(new Paragraph("0.06", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                paymentBody.setBorderColorLeft(BaseColor.WHITE);
                paymentBody.setBorderColorRight(BaseColor.WHITE);
                paymentBody.setBorderColorTop(BaseColor.WHITE);
                paymentBody.setBorderColorBottom(BaseColor.BLACK);
                paymentBody.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell Body = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                Body.setBorderColorBottom(BaseColor.BLACK);
                Body.setHorizontalAlignment(Element.ALIGN_CENTER);


                bodyTable.addCell(accountBody);
                bodyTable.addCell(descriptionBody);
                bodyTable.addCell(paymentBody);
                bodyTable.addCell(Body);


                for (int i = 0; i < policyObjFile.getPolicyObj().getRetencionId().size(); i++) {

                    PdfPCell accountAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionId().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    accountAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell descripcionAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getRetencionDesc().get(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    descripcionAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);


                    PdfPCell AbonoPPD = new PdfPCell(new Paragraph("0.0.6", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    AbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                    AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPCell AbonoPagoPPD = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                    AbonoPagoPPD.setBorderColorBottom(BaseColor.BLACK);
                    AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                    AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                    cargoTable.addCell(accountAbonoPPD);
                    cargoTable.addCell(descripcionAbonoPPD);
                    cargoTable.addCell(AbonoPPD);
                    cargoTable.addCell(AbonoPagoPPD);


                }

                PdfPCell accountAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getVenta_id(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                accountAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                accountAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell descripcionAbonoPPD = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getVenta_descripcion(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                descripcionAbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                descripcionAbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);


                PdfPCell AbonoPPD = new PdfPCell(new Paragraph("0.00", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                AbonoPPD.setBorderColorBottom(BaseColor.BLACK);
                AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                AbonoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell AbonoPagoPPD = new PdfPCell(new Paragraph("abon", FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
                AbonoPagoPPD.setBorderColorBottom(BaseColor.BLACK);
                AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);
                AbonoPagoPPD.setHorizontalAlignment(Element.ALIGN_CENTER);

                abonoTable.addCell(accountAbonoPPD);
                abonoTable.addCell(descripcionAbonoPPD);
                abonoTable.addCell(AbonoPPD);
                abonoTable.addCell(AbonoPagoPPD);


                //---------------------   end R10  --------------------- //
            }


            //---------------------   footer of file  --------------------- //

            PdfPTable footer = new PdfPTable(3);

            PdfPCell sumFooter = new PdfPCell(new Paragraph("SUMAS IGUALES", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            sumFooter.setHorizontalAlignment(Element.ALIGN_CENTER);
            sumFooter.setBackgroundColor(new BaseColor(182, 208, 226));
            if (cargoTotal == null || cargoTotal.isEmpty()) {
                cargoTotal = "0.00";
            }

            if (abonoTotal == null || abonoTotal.isEmpty()) {
                abonoTotal = "0.00";
            }
            PdfPCell sumCargo = new PdfPCell(new Paragraph("$" + CreateFilePDFBalance.decimal(Double.parseDouble(cargoTotal)), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
            sumCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell sumAbono = new PdfPCell(new Paragraph("$" + CreateFilePDFBalance.decimal(Double.parseDouble(abonoTotal)), FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, BaseColor.BLACK)));
            sumAbono.setHorizontalAlignment(Element.ALIGN_CENTER);


            footer.addCell(sumFooter);
            footer.addCell(sumCargo);
            footer.addCell(sumAbono);

            footer.setTotalWidth(523);
            footer.setHorizontalAlignment(2);
            footer.setWidthPercentage(60);


            //---------------------   last values of file  --------------------- //


            PdfPTable headerLastValues = new PdfPTable(4);
            headerLastValues.setWidthPercentage(100);

            PdfPCell cellDate = new PdfPCell(new Paragraph("Fecha", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            cellDate.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellDate.setBorderColor(BaseColor.WHITE);
            cellDate.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell cellConcept = new PdfPCell(new Paragraph("Proveedor / Cliente / Nómina", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            cellConcept.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellConcept.setBorderColor(BaseColor.WHITE);
            cellConcept.setBackgroundColor(new BaseColor(182, 208, 226));

            PdfPCell cellFolio = new PdfPCell(new Paragraph("Folio Fiscal", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            cellFolio.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellFolio.setBorderColor(BaseColor.WHITE);
            cellFolio.setBackgroundColor(new BaseColor(182, 208, 226));


            PdfPCell cellTotal = new PdfPCell(new Paragraph("Total", FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
            cellTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTotal.setBorderColor(BaseColor.WHITE);
            cellTotal.setBackgroundColor(new BaseColor(182, 208, 226));

            headerLastValues.addCell(cellDate);
            headerLastValues.addCell(cellConcept);
            headerLastValues.addCell(cellFolio);
            headerLastValues.addCell(cellTotal);


            PdfPTable lastValues = new PdfPTable(4);

            lastValues.setWidthPercentage(100);

            PdfPCell cellUUID = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getTimbreFiscalDigital_UUID(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.NORMAL, BaseColor.BLACK)));
            cellUUID.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellUUID.setBorderColor(BaseColor.BLACK);

            PdfPCell totalAmount = new PdfPCell(new Paragraph("$" + CreateFilePDFBalance.decimal(Double.parseDouble(policyObjFile.getPolicyObj().getTotalAmount())), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.NORMAL, BaseColor.BLACK)));
            totalAmount.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalAmount.setBorderColor(BaseColor.BLACK);


            PdfPCell provedor = new PdfPCell(new Paragraph(policyObjFile.getPolicyObj().getCompanyName(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.NORMAL, BaseColor.BLACK)));
            provedor.setHorizontalAlignment(Element.ALIGN_CENTER);
            provedor.setBorderColor(BaseColor.BLACK);


            PdfPCell end_date = new PdfPCell(new Paragraph(policyObjFile.getDate(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, Font.NORMAL, BaseColor.BLACK)));
            end_date.setHorizontalAlignment(Element.ALIGN_CENTER);
            end_date.setBorderColor(BaseColor.BLACK);


            lastValues.addCell(end_date);
            lastValues.addCell(provedor);
            lastValues.addCell(cellUUID);
            lastValues.addCell(totalAmount);


            document.add(table);
            document.add(new Paragraph("\n"));
            document.add(headerTable);
            document.add(bodyTable);
            document.add(cargoTable);
            document.add(abonoTable);
            document.add(footer);
            document.add(new Paragraph("\n\n\n\n"));
            document.add(headerLastValues);
            document.add(lastValues);
            document.close();

            //------------ end lastValues

            /* Uploading file to S3*/
            //UploadFileToS3_Policies.uploadPDF(fileName + ".pdf", rfc, type);
            return true;
        } catch (Exception e) {
            LOGGER.error("CreateFilePDFPolicy makeFileIngreso {} {}", e.getMessage(), e.getStackTrace());
        }
        return false;
    }


}
