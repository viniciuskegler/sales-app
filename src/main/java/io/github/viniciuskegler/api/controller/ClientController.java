package io.github.viniciuskegler.api.controller;

import io.github.viniciuskegler.domain.entity.Client;
import io.github.viniciuskegler.domain.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepo;


    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<Client>> getAll(){
        return ResponseEntity.ok(clientRepo.findAll());
    }

    @ResponseBody
    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Client> getAll(@PathVariable("id") Integer id){
        return clientRepo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
