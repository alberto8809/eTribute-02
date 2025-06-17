package org.example.users.controller;

import org.example.users.model.Regimen;
import org.example.users.service.RegimenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/")
public class RegimenController {

    @Autowired
    private RegimenService regimenService;

    public RegimenController(RegimenService regimenService) {
        this.regimenService = regimenService;
    }

    @GetMapping("regimens")
    public List<Regimen> getListOfRegimen() {return regimenService.getAllRegimen() ;}
}
