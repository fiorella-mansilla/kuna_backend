package com.kuna_backend.services;

import com.kuna_backend.entities.Client;
import com.kuna_backend.repositories.ClientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAllClients() {
        return (List<Client>) clientRepository.findAll();
    }

    public Client getClient (Integer id) {
        return clientRepository.findById(id).get();
    }

    public void createClient (Client client) {
        clientRepository.save(client);
    }

    public void deleteClient (Integer id) {
        clientRepository.deleteById(id);
    }

}