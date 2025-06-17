package org.example.users.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.users.service.CreateFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;



@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@ResponseBody
@RequestMapping("/")
public class PolicyController {

    @Autowired
    private CreateFileService createFileService;

    public PolicyController() {
    }

    public PolicyController(CreateFileService createFileService) {
        this.createFileService = createFileService;
    }


    @PostMapping("policy/")
    public ResponseEntity<Map<String, Object>> getDescription(@RequestParam(name = "rfc") String rfc, @RequestParam(name = "initial_date") String initial_date, @RequestParam(name = "final_date") String final_date) {
        Map<String, Object> descriptions = createFileService.getDescriptionPolicy(rfc, initial_date, final_date);
        for (Map.Entry<String, Object> map : descriptions.entrySet()) {
            if (map.getValue() != null) {
                return new ResponseEntity<>(descriptions, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /* upload files - xml , and create folder in S3 using rfc  numbers 7*/
    @PostMapping("policy/upload")
    public ResponseEntity<HttpStatus> uploadFilesToS3(@RequestParam(name = "files") MultipartFile[] files, @RequestParam(name = "rfc") String rfc) throws IOException {
        if (createFileService.uploadToS3(files, rfc)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }


}
