package org.example.users.util;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.users.model.Response;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

public class UploadFileToS3_Policies {
    private static final String bucketName = "xmlfilesback";

    //private static String local_path = "/Users/marioalberto/IdeaProjects/eTribute-all5/";
    private static final String server_path = "/home/ubuntu/endpoints/eTribute-all/";

    public static Logger LOGGER = LogManager.getLogger(UploadFileToS3_Policies.class);


    public static void upload(String xml, String rfc) {

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1)
                    .build();

            File file = new File(server_path + rfc + "/xml/" + xml);
            //PutObjectRequest request = new PutObjectRequest(bucketName, rfc + "/" + date + "/" + xml, file);
            PutObjectRequest request = new PutObjectRequest(bucketName, rfc + "/xml/" + xml, file);
            s3Client.putObject(request);

        } catch (SdkClientException e) {
            e.printStackTrace();
        }

    }

    public static void uploadPDF(String pdf, String rfc, String type) {
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1)
                    .build();

            File file = new File(server_path + rfc + "/pdf/" + type + "/" + pdf);
            PutObjectRequest request = new PutObjectRequest(bucketName, rfc + "/pdf/" + type + "/" + pdf, file);
            s3Client.putObject(request);

        } catch (SdkClientException e) {
            e.printStackTrace();
        }

    }


    public static Map<String, Object> getFileFromAWS(String rfc, String xml, String initial_date, String final_date) {
        List<Response> responses = new ArrayList<>();
        List<Response> ings = new ArrayList<>();
        List<Response> emitidas = new ArrayList<>();
        List<Response> recibidas = new ArrayList<>();
        Map<String, String> egresosXML = new HashMap<>();
        Map<String, String> ingresosXML = new HashMap<>();
        Map<String, Object> facturas = new HashMap<>();
        int expirationTime = 3600;


        try {

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1)
                    .build();

            ListObjectsRequest request2 = new ListObjectsRequest()
                    .withBucketName(bucketName)
                    .withPrefix(rfc);

            ObjectListing objectListing;
            do {
                objectListing = s3Client.listObjects(request2);

                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                    if (objectSummary.getKey().contains(rfc + "/" + xml + "/EGRESOS/")) {
                        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectSummary.getKey(), HttpMethod.GET);
                        request.setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000));
                        String objectUrl = s3Client.generatePresignedUrl(request).toString();
                        request.getRequestParameters();
                        String file = objectSummary.getKey().replace(rfc + "/" + xml + "/EGRESOS/", "");

                        if (file.length() > 2) {
                            if (rfc.equals(objectSummary.getKey().substring(0, 13))) {
                                LocalDate dates = LocalDate.parse(file.substring(0, 10));
                                LocalDate initial = LocalDate.parse(initial_date);
                                LocalDate ending = LocalDate.parse(final_date);
                                if ((dates.isAfter(initial) && dates.isBefore(ending)) || (dates.isEqual(initial) || dates.isEqual(ending))) {
                                    egresosXML.put(file, objectUrl);
                                }
                            }
                        }
                    } else if (objectSummary.getKey().contains(rfc + "/" + xml + "/INGRESOS/")) {
                        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectSummary.getKey(), HttpMethod.GET);
                        request.setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000));
                        String objectUrl = s3Client.generatePresignedUrl(request).toString();
                        request.getRequestParameters();
                        String file = objectSummary.getKey().replace(rfc + "/" + xml + "/INGRESOS/", "");
                        if (file.length() > 2) {
                            if (rfc.equals(objectSummary.getKey().substring(0, 13))) {
                                LocalDate dates = LocalDate.parse(file.substring(0, 10));
                                LocalDate initial = LocalDate.parse(initial_date);
                                LocalDate ending = LocalDate.parse(final_date);
                                if ((dates.isAfter(initial) && dates.isBefore(ending)) || (dates.isEqual(initial) || dates.isEqual(ending))) {
                                    ingresosXML.put(file, objectUrl);
                                }
                            }
                        }
                    }

                }
                //removing key or values emptys
                ingresosXML.entrySet().removeIf(ent -> ent.getValue().isEmpty() || ent.getKey().isEmpty());
                egresosXML.entrySet().removeIf(ent -> ent.getValue().isEmpty() || ent.getKey().isEmpty());


                request2.setMarker(objectListing.getNextMarker());

            } while (objectListing.isTruncated());

            if (xml.equals("xml")) {

                for (Map.Entry<String, String> egreXML : egresosXML.entrySet()) {
                    Response response = ParserFileIngresos.getParseValues(egreXML.getValue(), rfc, "EGRESOS", egreXML.getKey());
                    responses.add(response);
                }

                for (Map.Entry<String, String> ingXML : ingresosXML.entrySet()) {
                    Response response = ParserFileIngresos.getParseValues(ingXML.getValue(), rfc, "INGRESOS", ingXML.getKey());
                    ings.add(response);
                }

                for (Response response : responses) {
                    if (response.getDescripcion() != null) {
                        emitidas.add(response);
                    }
                }
                for (Response ingsR : ings) {
                    if (ingsR.getDescripcion() != null) {
                        recibidas.add(ingsR);
                    }
                }
            }


            facturas.put("Emitidas", emitidas);
            facturas.put("Recibidas", recibidas);
            facturas.put("listOfEgresos" + xml, egresosXML);
            facturas.put("listOfIngresos" + xml, ingresosXML);
            // FileUtils.deleteDirectory(new File(rfc));


        } catch (
                Exception e) {
            throw new RuntimeException(e);
        }
        return facturas;
    }

}
