package org.example.users.util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.users.model.Descripcion;
import org.example.users.model.PolicyObjFile;
import org.example.users.model.PolicyObjParser;
import org.example.users.model.Response;
import org.springframework.expression.spel.ast.OpGE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;


public class ParserFileIngresos {

    public static final Logger LOGGER = LogManager.getLogger(ParserFileIngresos.class);
    private static String local_path = "/Users/marioalberto/IdeaProjects/EtributeBack-1/";
    //private static final String server_path = "/home/ubuntu/endpoints/eTribute-all/";

    public static Response getParseValues(String pathFromAWS, String rfc, String type, String fileName) {

        Response response = new Response();
        Descripcion descripcion = new Descripcion();

        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(createFileFromURL(pathFromAWS, rfc, type, fileName));
            doc.getDocumentElement().normalize();
            Element comprobanteElement = doc.getDocumentElement();

            /* get date from xml*/
            Element date = doc.getDocumentElement();
            descripcion.setFecha(date.getAttribute("Fecha").substring(0, 10));

            if (type.equals("EGRESOS")) {
                NodeList repectEgr = comprobanteElement.getElementsByTagName("cfdi:Emisor");
                Element cliente = (Element) repectEgr.item(0);
                descripcion.setCliente(cliente.getAttribute("Nombre"));
            } else if (type.equals("INGRESOS")) {
                NodeList repectEgr = comprobanteElement.getElementsByTagName("cfdi:Receptor");
                Element cliente = (Element) repectEgr.item(0);
                descripcion.setCliente(cliente.getAttribute("Nombre"));
            }

            Element total = doc.getDocumentElement();
            descripcion.setCantidad(total.getAttribute("Total"));

            response.setDescripcion(descripcion);
            response.setUrl_xml(pathFromAWS);
            //archivoXML.delete();


        } catch (Exception e) {
            LOGGER.error("error { " + e.getLocalizedMessage() + " }  {}", e.getMessage());
        }
        return response;
    }


    public static PolicyObjFile getParse(String path) {

        PolicyObjParser values = new PolicyObjParser();
        try {

            File archivoXML = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivoXML);
            doc.getDocumentElement().normalize();

            Element total = doc.getDocumentElement();
            //"------------- total  ---------------
            values.setTotalAmount(total.getAttribute("Total"));

            Element date = doc.getDocumentElement();
            String currentDate = date.getAttribute("Fecha");
            Element payment1 = doc.getDocumentElement();
            values.setTypeOfPayment(payment1.getAttribute("FormaPago").isEmpty() || payment1.getAttribute("FormaPago") == null ? "99" : payment1.getAttribute("FormaPago"));

            Element comprobante = doc.getDocumentElement();
            values.setTypeOfComprobante(comprobante.getAttribute("TipoDeComprobante"));
            Element method = doc.getDocumentElement();
            values.setMethodPayment(method.getAttribute("MetodoPago"));
            values.setMetodo(method.getAttribute("MetodoPago"));

            NodeList repectEgr = doc.getElementsByTagName("cfdi:Receptor");
            Element rEgr = (Element) repectEgr.item(0);
            values.setClient(rEgr.getAttribute("Nombre"));
            NodeList rfc = doc.getElementsByTagName("cfdi:Emisor");
            Element RFC = (Element) rfc.item(0);
            values.setRfc(RFC.getAttribute("Rfc"));

            String typeOf = switch (values.getMethodPayment()) {
                case "PUE" -> "Egreso";
                case "PPD" -> "Diario";
                default -> "";
            };

            NodeList emisor = doc.getElementsByTagName("cfdi:Emisor");
            Element rcp = (Element) emisor.item(0);
            values.setCompanyName(rcp.getAttribute("Nombre"));
            NodeList receptor = doc.getElementsByTagName("cfdi:Receptor");
            Element regimen = (Element) receptor.item(0);
            values.setRegimen(regimen.getAttribute("RegimenFiscalReceptor"));
            Element useCFDI = (Element) receptor.item(0);
            values.setUsoCFDI(useCFDI.getAttribute("UsoCFDI"));


            NodeList conceptosList = doc.getElementsByTagName("cfdi:Concepto");
            Element description = (Element) conceptosList.item(0);
            values.setConcepto_Descripcion(description.getAttribute("Descripcion"));

            Element amount = (Element) conceptosList.item(0);
            values.setAmount(amount.getAttribute("Importe"));

            NodeList timbre = doc.getElementsByTagName("tfd:TimbreFiscalDigital");
            Element uudi = (Element) timbre.item(0);
            values.setTimbreFiscalDigital_UUID(uudi.getAttribute("UUID"));


            // "------------- I and PUE ---------------
            if (values.getTypeOfComprobante().equals("I") && values.getMethodPayment().equals("PUE")) {
                NodeList retencion = doc.getElementsByTagName("cfdi:Retencion");
                List<String> retencion_importe = new ArrayList<>();
                for (int i = 0; i < retencion.getLength(); i++) {
                    Element retencionR = (Element) retencion.item(i);
                    retencion_importe.add(retencionR.getAttribute("Importe").isEmpty() ? "0.00" : retencionR.getAttribute("Importe"));
                }
                values.setRetencion_importe(retencion_importe.stream().distinct().collect(Collectors.toList()));
                NodeList traslados = doc.getElementsByTagName("cfdi:Traslado");

                List<String> translado = new ArrayList<>();
                for (int i = 0; i < traslados.getLength(); i++) {
                    Element retencionR = (Element) traslados.item(i);
                    translado.add(retencionR.getAttribute("Importe").isEmpty() ? "0.00" : retencionR.getAttribute("Importe"));

                }
                values.setTraslado(translado);
                values.setSubtotal(Double.parseDouble(comprobante.getAttribute("SubTotal")));

                NodeList iva = doc.getElementsByTagName("cfdi:Retencion");
                Map<String, String> iva2 = new HashMap<>();
                for (int j = 0; j < iva.getLength(); j++) {
                    Element clv = (Element) iva.item(j);
                    iva2.put(clv.getAttribute("Impuesto"), clv.getAttribute("Importe"));

                }
                values.setIva(iva2);
                if (values.getUsoCFDI().equals("G03") ||
                        values.getUsoCFDI().equals("G01") ||
                        values.getUsoCFDI().equals("I01") ||
                        values.getUsoCFDI().equals("I02") ||
                        values.getUsoCFDI().equals("I03") ||
                        values.getUsoCFDI().equals("I04") ||
                        values.getUsoCFDI().equals("I05") ||
                        values.getUsoCFDI().equals("I06") ||
                        values.getUsoCFDI().equals("I07") ||
                        values.getUsoCFDI().equals("I08") ||
                        values.getUsoCFDI().equals("S01")) {
                    NodeList iva3 = doc.getElementsByTagName("cfdi:Concepto");
                    List<String> conceptoImpuestoPPD = new ArrayList<>();
                    List<String> conceptoImportePPD = new ArrayList<>();
                    List<String> conceptoTipoFactorPPD = new ArrayList<>();
                    List<String> impuestosPPD = new ArrayList<>();
                    for (int j = 0; j < iva3.getLength(); j++) {
                        Element clv = (Element) iva3.item(j);
                        conceptoImportePPD.add(clv.getAttribute("Importe"));
                    }
                    NodeList iva4 = doc.getElementsByTagName("cfdi:Traslado");
                    for (int j = 1; j < iva4.getLength(); j++) {
                        Element clv = (Element) iva4.item(j);
                        conceptoImpuestoPPD.add(clv.getAttribute("Base"));
                        conceptoTipoFactorPPD.add(clv.getAttribute("TipoFactor"));
                        impuestosPPD.add(clv.getAttribute("Impuesto"));

                    }
                    values.setConceptoImpuestoPPD(conceptoImpuestoPPD.stream().distinct().collect(Collectors.toList()));
                    values.setConceptoImportePPD(conceptoImportePPD.stream().distinct().collect(Collectors.toList()));
                    values.setImpuestosPPD(impuestosPPD.stream().distinct().collect(Collectors.toList()));
                    values.setConceptoTipoFactorPPD(conceptoTipoFactorPPD.stream().distinct().collect(Collectors.toList()));
                    values.setImpuestos(values.getTraslado().get(values.getTraslado().size() - 1));

                }

                NodeList tipo = doc.getElementsByTagName("cfdi:Concepto");
                List<String> claveProd = new ArrayList<>();
                for (int j = 0; j < tipo.getLength(); j++) {
                    Element clv = (Element) tipo.item(j);
                    claveProd.add(clv.getAttribute("ClaveProdServ"));
                }
                values.setClaveProdServ(claveProd);
                values.setImpuestos(values.getImpuestos() == null ? "0.00" : values.getImpuestos());
                values.setVenta_id(values.getVenta_id() == null ? "defaultVentaId" : values.getVenta_id());
                values.setVenta_descripcion(values.getVenta_descripcion() == null ? "defaultVentaDescripcion" : values.getVenta_descripcion());
                values.setCargo(values.getCargo() == null ? new ArrayList<>() : values.getCargo());
                values.setAbono(values.getAbono() == null ? new ArrayList<>() : values.getAbono());
                values.setTax_amount(values.getTax_amount() == null ? new ArrayList<>() : values.getTax_amount());


            }

            // ------------- I and PPD ---------------
            else if (values.getTypeOfComprobante().equals("I") && values.getMethodPayment().equals("PPD")) {
                NodeList retencion = doc.getElementsByTagName("cfdi:Retencion");
                List<String> retencion_importe = new ArrayList<>();
                for (int i = 0; i < retencion.getLength(); i++) {
                    Element retencionR = (Element) retencion.item(i);
                    retencion_importe.add(retencionR.getAttribute("Importe").isEmpty() ? "0.00" : retencionR.getAttribute("Importe"));
                }
                values.setRetencion_importe(retencion_importe.stream().distinct().collect(Collectors.toList()));
                NodeList traslados = doc.getElementsByTagName("cfdi:Traslado");

                List<String> translado = new ArrayList<>();
                for (int i = 0; i < traslados.getLength(); i++) {
                    Element retencionR = (Element) traslados.item(i);
                    translado.add(retencionR.getAttribute("Importe").isEmpty() ? "0.00" : retencionR.getAttribute("Importe"));

                }
                values.setTraslado(translado);
                values.setSubtotal(Double.parseDouble(comprobante.getAttribute("SubTotal")));

                NodeList iva = doc.getElementsByTagName("cfdi:Retencion");
                Map<String, String> iva2 = new HashMap<>();
                for (int j = 0; j < iva.getLength(); j++) {
                    Element clv = (Element) iva.item(j);
                    iva2.put(clv.getAttribute("Impuesto"), clv.getAttribute("Importe"));

                }
                values.setIva(iva2);

                if (values.getUsoCFDI().equals("G03") ||
                        values.getUsoCFDI().equals("G01") ||
                        values.getUsoCFDI().equals("I01") ||
                        values.getUsoCFDI().equals("I02") ||
                        values.getUsoCFDI().equals("I03") ||
                        values.getUsoCFDI().equals("I04") ||
                        values.getUsoCFDI().equals("I05") ||
                        values.getUsoCFDI().equals("I06") ||
                        values.getUsoCFDI().equals("I07") ||
                        values.getUsoCFDI().equals("I08") ||
                        values.getUsoCFDI().equals("S01")) {
                    NodeList iva3 = doc.getElementsByTagName("cfdi:Concepto");
                    List<String> conceptoImpuestoPPD = new ArrayList<>();
                    List<String> conceptoImportePPD = new ArrayList<>();
                    List<String> conceptoTipoFactorPPD = new ArrayList<>();
                    List<String> impuestosPPD = new ArrayList<>();
                    for (int j = 0; j < iva3.getLength(); j++) {
                        Element clv = (Element) iva3.item(j);
                        conceptoImportePPD.add(clv.getAttribute("Importe"));
                    }

                    NodeList iva4 = doc.getElementsByTagName("cfdi:Traslado");
                    for (int j = 1; j < iva4.getLength(); j++) {
                        Element clv = (Element) iva4.item(j);
                        conceptoImpuestoPPD.add(clv.getAttribute("Base"));
                        conceptoTipoFactorPPD.add(clv.getAttribute("TipoFactor"));
                        impuestosPPD.add(clv.getAttribute("Impuesto"));

                    }


                    values.setConceptoImpuestoPPD(conceptoImpuestoPPD.stream().distinct().collect(Collectors.toList()));
                    values.setConceptoImportePPD(conceptoImportePPD.stream().distinct().collect(Collectors.toList()));
                    values.setImpuestosPPD(impuestosPPD.stream().distinct().collect(Collectors.toList()));
                    values.setConceptoTipoFactorPPD(conceptoTipoFactorPPD.stream().distinct().collect(Collectors.toList()));
                    values.setImpuestos(values.getTraslado().get(values.getTraslado().size() - 1));

                }


                NodeList tipo = doc.getElementsByTagName("cfdi:Concepto");
                List<String> claveProd = new ArrayList<>();
                for (int j = 0; j < tipo.getLength(); j++) {
                    Element clv = (Element) tipo.item(j);
                    claveProd.add(clv.getAttribute("ClaveProdServ"));
                }
                values.setClaveProdServ(claveProd);
                values.setImpuestos(values.getImpuestos() == null ? "0.00" : values.getImpuestos());
                values.setVenta_id(values.getVenta_id() == null ? "defaultVentaId" : values.getVenta_id());
                values.setVenta_descripcion(values.getVenta_descripcion() == null ? "defaultVentaDescripcion" : values.getVenta_descripcion());
                values.setCargo(values.getCargo() == null ? new ArrayList<>() : values.getCargo());
                values.setAbono(values.getAbono() == null ? new ArrayList<>() : values.getAbono());
                values.setTax_amount(values.getTax_amount() == null ? new ArrayList<>() : values.getTax_amount());


            } else if (values.getTypeOfComprobante().equals("E")) {
                NodeList rcp2 = doc.getElementsByTagName("cfdi:Receptor");
                Element rcp3 = (Element) rcp2.item(0);
                values.setMetodo(rcp3.getAttribute("Nombre"));

                values.setSubtotal(Double.parseDouble(comprobante.getAttribute("SubTotal")));

                // NodeList impe;
//                if (doc.getElementsByTagName("cfdi:Impuestos") == null) {
//                    LOGGER.error(" archivo no cumpple caracteristicas de R´s:  {}", path.substring(local_path.length() + values.getRfc().length() + values.getTypeOfPayment().length() + "xml/".length(), path.length()));
//                    impe = (NodeList) new ArrayList<>();
//                } else {
//                    impe = doc.getElementsByTagName("cfdi:Impuestos");
//
//                }

//                Element ime = (Element) impe.item(impe.getLength() - 1);
//
//                if (ime == null) {
//                    LOGGER.error(" archivo no cumpple caracteristicas de R´s:  {}", path.substring(local_path.length() + values.getRfc().length() + values.getTypeOfPayment().length() + "xml/".length(), path.length()));
//                } else {
//                    values.setImpuestos(ime.getAttribute("Traslados").isEmpty() ? "0.00" : ime.getAttribute("Traslados"));
//                }

                NodeList ClaveProdServ = doc.getElementsByTagName("cfdi:Concepto");
                Element claveProdServ = (Element) ClaveProdServ.item(0);
                List<String> clv = new ArrayList<>();
                clv.add(claveProdServ.getAttribute("ClaveProdServ"));
                values.setClaveProdServ(clv);

                NodeList traslados = doc.getElementsByTagName("cfdi:Traslado");
                List<String> translado = new ArrayList<>();
                for (int i = 0; i < traslados.getLength(); i++) {
                    Element retencionR = (Element) traslados.item(i);
                    translado.add(retencionR.getAttribute("Importe").isEmpty() ? "0.00" : retencionR.getAttribute("Importe"));

                }
                values.setTraslado(translado);
                NodeList retencion = doc.getElementsByTagName("cfdi:Concepto");
                List<String> retencion_importe = new ArrayList<>();
                for (int i = 0; i < retencion.getLength(); i++) {
                    Element retencionR = (Element) retencion.item(i);
                    retencion_importe.add(retencionR.getAttribute("Importe"));
                }
                values.setRetencion_importe(retencion_importe);
                values.setImpuestos(values.getImpuestos() == null ? "0.00" : values.getImpuestos());
                values.setVenta_id(values.getVenta_id() == null ? "defaultVentaId" : values.getVenta_id());
                values.setVenta_descripcion(values.getVenta_descripcion() == null ? "defaultVentaDescripcion" : values.getVenta_descripcion());
                values.setCargo(values.getCargo() == null ? new ArrayList<>() : values.getCargo());
                values.setAbono(values.getAbono() == null ? new ArrayList<>() : values.getAbono());
                values.setTax_amount(values.getTax_amount() == null ? new ArrayList<>() : values.getTax_amount());

            } else if (values.getTypeOfComprobante().equals("N")) {

                values.setSubtotal(Double.parseDouble(comprobante.getAttribute("SubTotal")));
                NodeList impe;
                if (doc.getElementsByTagName("cfdi:Impuestos") == null) {
                    LOGGER.error(" archivo no cumpple caracteristicas de R´s:  {}", path.substring(local_path.length() + values.getRfc().length() + values.getTypeOfPayment().length() + "xml/".length(), path.length()));
                    impe = (NodeList) new ArrayList<>();
                } else {
                    impe = doc.getElementsByTagName("cfdi:Impuestos");

                }
                if (impe.getLength() > 0) {
                    Element impuestosElement = (Element) impe.item(0);
                    String totalImpuestosTrasladados = impuestosElement.getAttribute("TotalImpuestosTrasladados");

                    if (totalImpuestosTrasladados.isEmpty()) {
                        totalImpuestosTrasladados = "0.00";
                    }
                    values.setImpuestos(totalImpuestosTrasladados);
                } else {
                    values.setImpuestos("0.00");
                }
                NodeList imp = doc.getElementsByTagName("cfdi:Traslado");
                Map<String, String> iva = new HashMap<>();
                List<String> importte = new ArrayList<>();
                for (int j = 0; j < imp.getLength(); j++) {
                    Element clv = (Element) imp.item(j);
                    iva.put(clv.getAttribute("Impuesto"), clv.getAttribute("Importe"));
                }
                values.setIva(iva);
                importte.
                        addAll(iva.values());

                values.setTax_amount(importte);
                NodeList tipo = doc.getElementsByTagName("cfdi:Concepto");
                List<String> claveProd = new ArrayList<>();
                for (int j = 0; j < tipo.getLength(); j++) {
                    Element clv = (Element) tipo.item(j);
                    claveProd.add(clv.getAttribute("ClaveProdServ"));
                }
                values.setClaveProdServ(claveProd);

                NodeList traslados = doc.getElementsByTagName("nomina12:Deduccion");
                List<String> translado = new ArrayList<>();
                for (int i = 0; i < traslados.getLength(); i++) {
                    Element retencionR = (Element) traslados.item(i);
                    translado.add(retencionR.getAttribute("TipoDeduccion").isEmpty() ? "N/A" : retencionR.getAttribute("TipoDeduccion"));

                }
                values.setTraslado(translado);

                NodeList retencion = doc.getElementsByTagName("nomina12:Deduccion");
                List<String> retencion_importe = new ArrayList<>();
                for (int i = 0; i < retencion.getLength(); i++) {
                    Element retencionR = (Element) retencion.item(i);
                    retencion_importe.add(retencionR.getAttribute("Importe"));
                }


                NodeList nmin = doc.getElementsByTagName("nomina12:OtroPago");
                Element nm = (Element) nmin.item(0);
                values.setImpPagado(nm.getAttribute("TipoOtroPago"));


                NodeList percep = doc.getElementsByTagName("nomina12:Percepciones");
                Element totalSueldos = (Element) percep.item(0);

                NodeList pr = doc.getElementsByTagName("nomina12:Percepcion");
                Element prc = (Element) pr.item(0);
                values.setImpuestoId(prc.getAttribute("TipoPercepcion"));

                String totaS = totalSueldos.getAttribute("TotalSueldos") == null ? "0.00" : totalSueldos.getAttribute("TotalSueldos");
                retencion_importe.add(totaS.isEmpty() || totaS == null ? "0" : totaS);
                retencion_importe.add(values.getTotalAmount());
                values.setRetencion_importe(retencion_importe);
                values.setImpuestos(values.getImpuestos() == null ? "0.00" : values.getImpuestos());
                values.setVenta_id(values.getVenta_id() == null ? "defaultVentaId" : values.getVenta_id());
                values.setVenta_descripcion(values.getVenta_descripcion() == null ? "defaultVentaDescripcion" : values.getVenta_descripcion());
                values.setCargo(values.getCargo() == null ? new ArrayList<>() : values.getCargo());
                values.setAbono(values.getAbono() == null ? new ArrayList<>() : values.getAbono());
                values.setTax_amount(values.getTax_amount() == null ? new ArrayList<>() : values.getTax_amount());


            } else if (values.getTypeOfComprobante().equals("P")) {

                NodeList percep = doc.getElementsByTagName("pago20:Totales");
                Element totalSueldos = (Element) percep.item(0);
                String dou = totalSueldos.getAttribute("TotalTrasladosImpuestoIVA16").isEmpty() || totalSueldos.getAttribute("TotalTrasladosImpuestoIVA16") == null ? "0" : totalSueldos.getAttribute("TotalTrasladosImpuestoIVA16");
                values.setSubtotal(Double.parseDouble(dou.isEmpty() || dou == null ? "0.00" : dou));

                NodeList pago = doc.getElementsByTagName("pago20:Pago");
                Element pay = (Element) pago.item(0);
                values.setImpuestos(pay.getAttribute("FormaDePagoP"));

                NodeList receptor2 = doc.getElementsByTagName("cfdi:Receptor");
                Element regimen31 = (Element) receptor2.item(0);
                values.setRegimen(regimen31.getAttribute("RegimenFiscalReceptor"));
                Element useCFDI2 = (Element) receptor2.item(0);
                values.setUsoCFDI(useCFDI2.getAttribute("UsoCFDI"));

                values.setMetodo("N/A");
                values.setMethodPayment("N/A");

//                if (values.getMethodPayment().equals("N/A")) {
//                    LOGGER.error(" archivo no cumpple caracteristicas de R´s:  {}", path.substring(local_path.length() + values.getRfc().length() + values.getTypeOfPayment().length() + "xml/".length(), path.length()));
//                }
                values.setAmount(amount.getAttribute("Importe"));

                NodeList docu = doc.getElementsByTagName("cfdi:Concepto");
                Element d = (Element) docu.item(0);


                NodeList timbre34 = doc.getElementsByTagName("tfd:TimbreFiscalDigital");
                Element uudi34 = (Element) timbre34.item(0);
                values.setTimbreFiscalDigital_UUID(uudi34.getAttribute("UUID"));

                values.setConcepto_Descripcion("");
                values.setTotalAmount(values.getAmount());

                NodeList trasP = doc.getElementsByTagName("pago20:TrasladoP");
                Element impuestoP = (Element) trasP.item(0);
                values.setImpuestoId(impuestoP.getAttribute("ImpuestoP"));

                NodeList ClaveProdServ = doc.getElementsByTagName("cfdi:Concepto");
                Element claveProdServ = (Element) ClaveProdServ.item(0);
                List<String> clv = new ArrayList<>();
                clv.add(claveProdServ.getAttribute("ClaveProdServ"));
                values.setClaveProdServ(clv);
                NodeList payment2 = doc.getElementsByTagName("pago20:Pago");
                Element pay3 = (Element) payment2.item(0);
                values.setTypeOfPayment(pay3.getAttribute("FormaDePagoP").isEmpty() || pay3.getAttribute("FormaDePagoP") == null ? "99" : pay3.getAttribute("FormaDePagoP"));
                values.setImpuestos(values.getImpuestos() == null ? "0.00" : values.getImpuestos());
                values.setVenta_id(values.getVenta_id() == null ? "defaultVentaId" : values.getVenta_id());
                values.setVenta_descripcion(values.getVenta_descripcion() == null ? "defaultVentaDescripcion" : values.getVenta_descripcion());
                values.setCargo(values.getCargo() == null ? new ArrayList<>() : values.getCargo());
                values.setAbono(values.getAbono() == null ? new ArrayList<>() : values.getAbono());
                values.setTax_amount(values.getTax_amount() == null ? new ArrayList<>() : values.getTax_amount());

            }
            //LOGGER.error("{}", values.getAmount());
            return new PolicyObjFile(values, path, values.getClient(), currentDate.substring(0, 10), typeOf);

        } catch (Exception e) {
            LOGGER.error("ParserFileIngresos " + e.getMessage() + e.getLocalizedMessage());
        }
        return null;
    }


    public static File createFileFromURL(String urlFromAWS, String rfc, String type, String fileName) {
        try {

            URL url = new URL(urlFromAWS);
            File file = new File(local_path + rfc + "/xml/" + type + "/" + fileName);
            FileUtils.copyURLToFile(url, file);
            return file;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
