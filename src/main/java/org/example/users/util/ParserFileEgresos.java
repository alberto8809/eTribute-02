package org.example.users.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.users.model.PolicyObjFile;
import org.example.users.model.PolicyObjParser;
import org.example.users.model.TypeOfEgresoN;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParserFileEgresos {
    public static Logger LOGGER = LogManager.getLogger(PolicyObjParser.class);
    private static final String server_path = "/home/ubuntu/endpoints/eTribute-all/";

    public static PolicyObjFile getParse(String path) {
        PolicyObjParser values = new PolicyObjParser();
        try {

            File archivoXML = new File(path);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(archivoXML);
            doc.getDocumentElement().normalize();
            values.setTotalAmount(doc.getDocumentElement().getAttribute("Total"));
            String currentDate = doc.getDocumentElement().getAttribute("Fecha");

            values.setTypeOfPayment(Optional.ofNullable(doc.getDocumentElement().getAttribute("FormaPago")).filter(attr -> !attr.isEmpty()).orElse("99"));

            values.setMethodPayment(doc.getDocumentElement().getAttribute("MetodoPago"));
            values.setMetodo(values.getMethodPayment());
            values.setClient(((Element) doc.getElementsByTagName("cfdi:Receptor").item(0)).getAttribute("Nombre"));

            Element emisor = (Element) doc.getElementsByTagName("cfdi:Emisor").item(0);
            values.setRfc(emisor.getAttribute("Rfc"));
            values.setCompanyName(emisor.getAttribute("Nombre"));


            values.setTypeOfComprobante(doc.getDocumentElement().getAttribute("TipoDeComprobante"));
            values.setTimbreFiscalDigital_UUID(((Element) doc.getElementsByTagName("tfd:TimbreFiscalDigital").item(0)).getAttribute("UUID"));
            values.setConcepto_Descripcion(((Element) doc.getElementsByTagName("cfdi:Concepto").item(0)).getAttribute("Descripcion"));
            //  values.setConcepto_Descripcion(((Element) doc.getElementsByTagName("cfdi:Concepto").item(0)).getAttribute("ClaveProdServ"));


            //"------------- Nombre de empresa ----------------
            String companyName = values.getCompanyName();

            //"------------- RegimenFiscalReceptor ----------------
            Optional.ofNullable(doc.getElementsByTagName("cfdi:Receptor").item(0)).map(node -> (Element) node).ifPresent(receptor -> values.setRegimen(receptor.getAttribute("RegimenFiscalReceptor")));


            //"------------- UsoCFDI ----------------
            Optional.ofNullable(doc.getElementsByTagName("cfdi:Receptor").item(0)).map(node -> (Element) node).ifPresent(receptor -> values.setUsoCFDI(receptor.getAttribute("UsoCFDI")));


            String methodPayment;
            if (values.getTypeOfComprobante().equals("P")) {
                NodeList pagos = doc.getElementsByTagName("pago20:Pago");
                values.setMethodPayment(((Element) pagos.item(0)).getAttribute("FormaDePagoP"));

                NodeList totales = doc.getElementsByTagName("pago20:Totales");
                values.setAmount(((Element) totales.item(0)).getAttribute("MontoTotalPagos"));

                values.setImpuestos(Optional.ofNullable(values.getImpuestos()).orElse("0"));
                values.setVenta_id(Optional.ofNullable(values.getVenta_id()).orElse(""));
                values.setVenta_descripcion(Optional.ofNullable(values.getVenta_descripcion()).orElse("defaultVentaDescripcion"));
                values.setCargo(Optional.ofNullable(values.getCargo()).orElse(new ArrayList<>()));
                values.setAbono(Optional.ofNullable(values.getAbono()).orElse(new ArrayList<>()));
                values.setTax_amount(Optional.ofNullable(values.getTax_amount()).orElse(new ArrayList<>()));

                NodeList impPagado = doc.getElementsByTagName("pago20:DoctoRelacionado");
                Element impPay = (Element) impPagado.item(0);
                values.setImpPagado(impPay.getAttribute("ImpPagado"));

                NodeList trasladoP = doc.getElementsByTagName("pago20:TrasladoP");
                values.setVenta_id(((Element) trasladoP.item(0)).getAttribute("ImpuestoP"));

                List<String> conceptoImpuestoPPD = new ArrayList<>();
                List<String> conceptoImportePPD = new ArrayList<>();
                List<String> conceptoTipoFactorPPD = new ArrayList<>();
                List<String> impuestosPPD = new ArrayList<>();
                NodeList trasladoDR = doc.getElementsByTagName("pago20:TrasladoDR");

                for (int j = 0; j < trasladoDR.getLength(); j++) {
                    Element clv = (Element) trasladoDR.item(j);
                    conceptoImpuestoPPD.add(clv.getAttribute("TipoFactorDR"));
                    conceptoTipoFactorPPD.add(clv.getAttribute("ImpuestoDR"));
                    impuestosPPD.add(clv.getAttribute("ImporteDR"));

                }
                values.setConceptoImpuestoPPD(conceptoImpuestoPPD.stream().distinct().collect(Collectors.toList()));
                values.setConceptoImportePPD(conceptoImportePPD.stream().distinct().collect(Collectors.toList()));
                values.setImpuestosPPD(impuestosPPD.stream().distinct().collect(Collectors.toList()));
                values.setConceptoTipoFactorPPD(conceptoTipoFactorPPD.stream().distinct().collect(Collectors.toList()));
                // values.setImpuestos(values.getTraslado().get(values.getTraslado().size() - 1));

            }


            values.setMetodo(doc.getDocumentElement().getAttribute("MetodoPago"));
            Element rEgr = (Element) doc.getElementsByTagName("cfdi:Receptor").item(0);
            String cli = rEgr.getAttribute("Nombre");


            String typeOf = "defaultValue";
            if (!values.getTypeOfComprobante().equals("P") && !values.getTypeOfComprobante().isEmpty()) {
                if (rEgr.getAttribute("Nombre").equals(cli)) {
                    typeOf = values.getMethodPayment().equals("PUE") ? "Egreso" : values.getMethodPayment().equals("PPD") ? "Diario" : typeOf;
                }
            }


            NodeList[] impuestosNodes = {doc.getElementsByTagName("cfdi:Impuestos"), doc.getElementsByTagName("pago20:ImpuestosP")};

            for (NodeList impuestos : impuestosNodes) {
                if (impuestos.getLength() > 0) {
                    Element impuestoElement = (Element) impuestos.item(0);
                    String totalImpuestosTrasladados = impuestoElement.getAttribute("TotalImpuestosTrasladados");
                    if (!totalImpuestosTrasladados.isEmpty() && !totalImpuestosTrasladados.equals("0")) {
                        values.setImpuestos(totalImpuestosTrasladados);
                    }
                }
            }
            //R2
            if (values.getTypeOfComprobante().equals("E") && values.getMethodPayment().equals("PUE")) {
                List<String> conceptoImpuestoPPD = new ArrayList<>();
                List<String> conceptoImportePPD = new ArrayList<>();
                List<String> conceptoTipoFactorPPD = new ArrayList<>();
                List<String> impuestosPPD = new ArrayList<>();
                NodeList trasladoDR = doc.getElementsByTagName("cfdi:Traslado");

                for (int j = 0; j < trasladoDR.getLength(); j++) {
                    Element clv = (Element) trasladoDR.item(j);
                    conceptoImpuestoPPD.add(clv.getAttribute("TipoFactor"));
                    conceptoTipoFactorPPD.add(clv.getAttribute("Impuesto"));
                    impuestosPPD.add(clv.getAttribute("Importe"));

                }
                values.setConceptoImpuestoPPD(conceptoImpuestoPPD.stream().distinct().collect(Collectors.toList()));
                values.setConceptoImportePPD(conceptoImportePPD.stream().distinct().collect(Collectors.toList()));
                values.setImpuestosPPD(impuestosPPD.stream().distinct().collect(Collectors.toList()));
                values.setConceptoTipoFactorPPD(conceptoTipoFactorPPD.stream().distinct().collect(Collectors.toList()));

            } else if (values.getTypeOfComprobante().equals("E") && values.getMethodPayment().equals("PPD")) {
                List<String> conceptoImpuestoPPD = new ArrayList<>();
                List<String> conceptoImportePPD = new ArrayList<>();
                List<String> conceptoTipoFactorPPD = new ArrayList<>();
                List<String> impuestosPPD = new ArrayList<>();
                NodeList trasladoDR = doc.getElementsByTagName("cfdi:Traslado");

                for (int j = 0; j < trasladoDR.getLength(); j++) {
                    Element clv = (Element) trasladoDR.item(j);
                    conceptoImpuestoPPD.add(clv.getAttribute("TipoFactor"));
                    conceptoTipoFactorPPD.add(clv.getAttribute("Impuesto"));
                    impuestosPPD.add(clv.getAttribute("Importe"));

                }
                values.setConceptoImpuestoPPD(conceptoImpuestoPPD.stream().distinct().collect(Collectors.toList()));
                values.setConceptoImportePPD(conceptoImportePPD.stream().distinct().collect(Collectors.toList()));
                values.setImpuestosPPD(impuestosPPD.stream().distinct().collect(Collectors.toList()));
                values.setConceptoTipoFactorPPD(conceptoTipoFactorPPD.stream().distinct().collect(Collectors.toList()));

            }

            if (values.getTypeOfComprobante().equals("N")) {
                values.setAmount("0.0");
                NodeList receptor = doc.getElementsByTagName("nomina12:Deducciones");
                Element receptor1 = (Element) receptor.item(0);
                TypeOfEgresoN tipoDeEgresoN = new TypeOfEgresoN();
                tipoDeEgresoN.setCuenta("601.23");
                tipoDeEgresoN.setDescripcion("Previsión social");
                tipoDeEgresoN.setAbonoIMSS("211.01");
                tipoDeEgresoN.setAbonoDescipcion("Provisión de IMSS patronal por pagar");
                tipoDeEgresoN.setIsr("216.01");
                tipoDeEgresoN.setIsrDescripcion("Impuestos retenidos de ISR por sueldos y salarios");
                List<String> percepciones = new ArrayList<>();
                percepciones.add(receptor1.getAttribute("TotalOtrasDeducciones") == null ? "0" : receptor1.getAttribute("TotalOtrasDeducciones"));
                percepciones.add(receptor1.getAttribute("TotalImpuestosRetenidos") == null ? "0" : receptor1.getAttribute("TotalImpuestosRetenidos"));
                tipoDeEgresoN.setPercepciones(percepciones);
                values.setTypeOfEgresoN(tipoDeEgresoN);

                values.setRetencion_importe(new ArrayList<>());
                values.setImpuestos("0.0");
                values.setCargo(new ArrayList<>());
                values.setAbono(new ArrayList<>());
                values.setVenta_id("209.01");
                values.setIva(new HashMap<>());
                values.setTax_amount(new ArrayList<>());

            }


            if (values.getTypeOfComprobante().equals("I") && values.getMethodPayment().equals("PPD")) {
                values.setImpuestos(Optional.ofNullable(doc.getElementsByTagName("cfdi:Impuestos")).filter(impe -> impe.getLength() > 0).map(impe -> ((Element) impe.item(impe.getLength() - 1)).getAttribute("TotalImpuestosTrasladados")).filter(attr -> !attr.isEmpty()).orElse("0"));
                Element rgim = (Element) doc.getElementsByTagName("cfdi:Emisor").item(0);
                values.setRegimen(rgim.getAttribute("RegimenFiscal"));

                values.setRetencion_importe(new ArrayList<>());
                values.setAmount("0.0");
                values.setIva(new HashMap<>());


                List<String> impuesto = new ArrayList<>();
                List<String> importe = new ArrayList<>();


                NodeList retencion = doc.getElementsByTagName("cfdi:Retencion");//cfdiTraslado
                if (retencion != null || retencion.getLength() > 0) {
                    for (int i = 0; i < retencion.getLength(); i++) {
                        Element rcp = (Element) retencion.item(i);
                        if (!rcp.getAttribute("Base").isEmpty()) {
                            impuesto.add(rcp.getAttribute("Impuesto"));
                            importe.add(rcp.getAttribute("Importe"));
                        }
                    }
                }
                values.setRetencionId(impuesto);
                values.setRetencionPago(importe);



            } else if (values.getTypeOfComprobante().equals("I") && values.getMethodPayment().equals("PUE")) {


                values.setImpuestos(Optional.ofNullable(doc.getElementsByTagName("cfdi:Impuestos")).filter(impe -> impe.getLength() > 0).map(impe -> ((Element) impe.item(impe.getLength() - 1)).getAttribute("TotalImpuestosTrasladados")).filter(attr -> !attr.isEmpty()).orElse("0"));

                Element rgim = (Element) doc.getElementsByTagName("cfdi:Emisor").item(0);
                values.setRegimen(rgim.getAttribute("RegimenFiscal"));

                values.setVenta_id(Optional.ofNullable(values.getVenta_id()).orElse(""));
                values.setVenta_descripcion(Optional.ofNullable(values.getVenta_descripcion()).orElse("defaultVentaDescripcion"));
                values.setCargo(Optional.ofNullable(values.getCargo()).orElse(new ArrayList<>()));
                values.setAbono(Optional.ofNullable(values.getAbono()).orElse(new ArrayList<>()));
                values.setTax_amount(Optional.ofNullable(values.getTax_amount()).orElse(new ArrayList<>()));

                values.setRetencion_importe(new ArrayList<>());
                values.setIva(new HashMap<>());
                values.setVenta_descripcion("");

                List<String> impuesto = new ArrayList<>();
                List<String> importe = new ArrayList<>();


                NodeList retencion = doc.getElementsByTagName("cfdi:Retencion");//cfdiTraslado
                if (retencion != null || retencion.getLength() > 0) {
                    for (int i = 0; i < retencion.getLength(); i++) {
                        Element rcp = (Element) retencion.item(i);
                        if (!rcp.getAttribute("Base").isEmpty()) {
                            impuesto.add(rcp.getAttribute("Impuesto"));
                            importe.add(rcp.getAttribute("Importe"));
                        }
                    }
                }
                values.setRetencionId(impuesto);
                values.setRetencionPago(importe);


            }

            //------------- Traslados ----------------
            NodeList impeT = doc.getElementsByTagName("cfdi:Traslados");

            if (values.getTypeOfComprobante().equals("I") && values.getMethodPayment().equals("PUE") || values.getTypeOfComprobante().equals("I") && values.getMethodPayment().equals("PPD")) {
                values.setDescuento(doc.getDocumentElement().getAttribute("Descuento"));
            }

            List<String> transladoIm = new ArrayList<>();
            for (int i = 0; i < impeT.getLength(); i++) {
                String importe = ((Element) impeT.item(i)).getAttribute("Importe");
                transladoIm.add(importe.isEmpty() ? "0" : importe);
            }


            //"------------- Concepto ----------------
            values.setConcepto_Descripcion(doc.getDocumentElement().getAttribute("Descripcion"));

            //"------------- amount ----------------
            List<String> abono = IntStream.range(0, doc.getElementsByTagName("cfdi:Concepto").getLength()).mapToObj(i -> ((Element) doc.getElementsByTagName("cfdi:Concepto").item(i)).getAttribute("Importe")).collect(Collectors.toList());
            values.setAbono(abono);


            //"------------- ClaveProdServ ----------------
            // Datp: para tipo P no es necesario la ClaveProdServ

            if (!values.getTypeOfComprobante().equals("P")) {
                values.setClaveProdServ(Optional.ofNullable(doc.getElementsByTagName("cfdi:Concepto").item(0)).map(node -> (Element) node).map(element -> element.getAttribute("ClaveProdServ")).filter(attr -> attr != null).map(Collections::singletonList).orElseGet(ArrayList::new));

                List<String> translado = IntStream.range(0, doc.getElementsByTagName("cfdi:Traslado").getLength()).mapToObj(i -> (Element) doc.getElementsByTagName("cfdi:Traslado").item(i)).peek(element -> values.setImpuestoId(element.getAttribute("Impuesto"))).map(element -> element.getAttribute("Importe").isEmpty() ? "0" : element.getAttribute("Importe")).collect(Collectors.toList());
                values.setTraslado(translado);

                List<String> retencion_importe = IntStream.range(0, doc.getElementsByTagName("cfdi:Concepto").getLength()).mapToObj(i -> ((Element) doc.getElementsByTagName("cfdi:Concepto").item(i)).getAttribute("Importe")).collect(Collectors.toList());
            } else {
                values.setTraslado(new ArrayList<>());
                values.setRetencion_importe(new ArrayList<>());
                values.setClaveProdServ(new ArrayList<>());
            }


            //"------------- TimbreFiscalDigital ----------------
            Element uudi = (Element) doc.getElementsByTagName("tfd:TimbreFiscalDigital").item(0);
            values.setTimbreFiscalDigital_UUID(uudi.getAttribute("UUID"));

            //"------------- Impuesto ----------------
//            if (!values.getTypeOfComprobante().equals("I") && !values.getMethodPayment().equals("PPD")) {
//                NodeList imp = doc.getElementsByTagName("pago20:Totales");
//                if (imp.getLength() > 0) {
//                    Element ip = (Element) imp.item(0);
//                    values.setImpuestos(ip.getAttribute("MontoTotalPagos"));
//                }
//                NodeList dr = doc.getElementsByTagName("pago20:TrasladoDR");
//                Element d = (Element) dr.item(0);
//                if (d == null) {
//                    LOGGER.error(" archivo no cumple caracteristicas de R´s:  {}", path.substring(server_path.length() + values.getRfc().length() + values.getTypeOfPayment().length() + "xml/".length(), path.length()));
//                } else {
//                    values.setAmount(d.getAttribute("ImporteDR"));
//                }
//            }


            values.setAmount(values.getTotalAmount() == null ? "0.0" : values.getTotalAmount());
            values.setImpuestoId(values.getImpuestoId() == null ? "0" : values.getImpuestoId());

            //------------- Egreso fin----------------
            return new PolicyObjFile(values, path, cli, currentDate.substring(0, 10), typeOf);


        } catch (Exception e) {
            LOGGER.error("ParserFileEgresos: {} . {} . {}", e.getMessage(), e, path);
        }
        return null;
    }

}
