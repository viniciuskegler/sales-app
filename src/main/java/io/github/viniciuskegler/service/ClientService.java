package io.github.viniciuskegler.service;

import io.github.viniciuskegler.domain.entity.Client;
import io.github.viniciuskegler.domain.repository.ClientRepository;
import io.github.viniciuskegler.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepo;

    public void saveClient(Client client){
        validateClient(client);
        try {
            clientRepo.save(client);
        } catch (IllegalArgumentException e){
            throw new BusinessException("Employee is null " + e.getMessage(), "602");
        } catch (Exception e1){
            throw new BusinessException("An unexpected error happened while saving " + e1.getMessage(), "500");
        }
    }

    private void validateClient(Client client) {
        if(client.getName() == null || client.getName().length() < 7){
            throw new BusinessException("Invalid name", "601");
        }
    }

}
