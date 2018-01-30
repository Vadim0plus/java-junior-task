package com.mcb.javajuniortask.service;

import com.mcb.javajuniortask.dto.ClientDTO;
import com.mcb.javajuniortask.model.Client;
import com.mcb.javajuniortask.model.Debt;
import com.mcb.javajuniortask.model.Pay;
import com.mcb.javajuniortask.repository.ClientRepository;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ShellComponent
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @ShellMethod("Shows all clients in db")
    @Transactional
    public Iterable<ClientDTO> showAllClients() {
        return StreamSupport.stream(clientRepository.findAll().spliterator(), false).map(client -> {
            ClientDTO clientDTO = new ClientDTO();
            clientDTO.setName(client.getName());
            clientDTO.setTotalDebt(client.getDebts().stream().map(Debt::getValue).reduce(BigDecimal::add).orElse(BigDecimal.ZERO));
            return clientDTO;
        }).collect(Collectors.toList());
    }

    @ShellMethod("Adds client to db")
    @Transactional
    public UUID addClient(@ShellOption String name) {
        Client client = new Client();
        client.setName(name);
        client.setId(UUID.randomUUID());
        client = clientRepository.save(client);
        return client.getId();
    }

    @ShellMethod("Adds debt to client")
    @Transactional
    public UUID addDebtToClient(@ShellOption UUID clientId, @ShellOption BigDecimal value) {
        Client client = clientRepository.findOne(clientId);
        Debt debt = new Debt();
        debt.setValue(value);
        debt.setId(UUID.randomUUID());
        debt.setClient(client);
        client.getDebts().add(debt);
        clientRepository.save(client);
        return debt.getId();
    }

    @ShellMethod("Pays for specified client debt")
    @Transactional
    public UUID payToClientDebt(@ShellOption UUID clientId,
                                @ShellOption UUID debtId,
                                @ShellOption BigDecimal value) {
        if(clientId == null) {
            throw new NullPointerException("clientId was null");
        }
        if(debtId == null) {
            throw new NullPointerException("clientId was null");
        }
        if(value == null) {
            throw new NullPointerException("clientId was null");
        }
        if(value.floatValue() < 0) {
            throw new IllegalArgumentException("Pay value was less than zero");
        }
        Client client = clientRepository.findOne(clientId);
        if (client == null) {
            throw new RuntimeException("Client Record not found");
        }
        Debt debt = new Debt();
        debt.setId(debtId);
        int idx = client.getDebts().indexOf(debt);
        if (idx < 0) {
            throw new RuntimeException("Debt Record not found");
        }
        Debt clientDebt = client.getDebts().get(idx);
        BigDecimal oldValue = clientDebt.getValue();
        /* We assume that it is not allowed to pay a debt less than zero */
        if (oldValue.compareTo(value) < 0) {
            throw new RuntimeException("it is not allowed to pay a debt less than zero");
        }
        if (oldValue.compareTo(value) == 0) {
            client.getDebts().remove(clientDebt);
        }
        clientDebt.setValue(oldValue.subtract(value));
        Pay pay = new Pay();
        pay.setValue(value);
        pay.setId(UUID.randomUUID());
        pay.setClient(client);
        pay.setDebt(clientDebt);
        client.getPays().add(pay);
        return pay.getId();
    }

}
