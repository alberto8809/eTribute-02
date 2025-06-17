package org.example.users.model;

import lombok.*;


import java.util.List;
import java.util.Map;


//@Entity
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@Table(name = "PolicyObjParser")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PolicyObjParser {
    private String regimen;
    private String usoCFDI;
    private List<String> ClaveProdServ;
    private String concepto_Descripcion;
    private String amount;
    private List<String> traslado;
    private String impuestos;
    private List<String> retencion_importe;
    private String timbreFiscalDigital_UUID;
    private String totalAmount;
    private String typeOfComprobante;
    private String venta_id;
    private String venta_descripcion;
    private String metodo;
    private List<String> cargo;
    private List<String> abono;
    private double subtotal;
    private Map<String, String> iva;
    private List<String> tax_amount;
    private String rfc;
    private String methodPayment;
    private String typeOfPayment;
    private String client;
    private String companyName;
    private TypeOfEgresoN typeOfEgresoN;
    private String descuento;
    private String impuestoId;
    private String impPagado;
    private List<String> retencionId;
    private List<String> retencionPago;
    private List<String> retencionDesc;
    private List<String> conceptoImpuestoPPD;
    private List<String> conceptoImportePPD;
    private List<String> conceptoTipoFactorPPD;
    private List<String> impuestosPPD;


}
