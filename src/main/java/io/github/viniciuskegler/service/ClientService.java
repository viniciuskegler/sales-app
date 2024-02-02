package io.github.viniciuskegler.service;

import io.github.viniciuskegler.domain.entity.Client;
import io.github.viniciuskegler.domain.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepo;

    public void salvarCliente(Client client){
        validateClient(client);
        clientRepo.save(client);
    }

    private void validateClient(Client client) {
        //TODO
    }

}
