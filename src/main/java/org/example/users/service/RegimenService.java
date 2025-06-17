package org.example.users.service;

import org.example.users.model.Regimen;
import org.example.users.repository.RegimenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegimenService {

    @Autowired
    RegimenRepository regimenRepository;

    public List<Regimen> getAllRegimen(){return  regimenRepository.findAll();}
}
