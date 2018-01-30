package com.mcb.javajuniortask.controller;

import com.mcb.javajuniortask.dto.ClientDTO;
import com.mcb.javajuniortask.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/debts")
public class RestControllerForClientService {

	@Autowired
	private ClientService clientService;

	@RequestMapping(value="/add-client", method=RequestMethod.POST)
	public String processRegistration(@RequestBody String clientName) {
		if(clientName == null) {
			return "Error:Name of client was null";
		}
		UUID id = clientService.addClient(clientName);
		return "Successful Registration, id:"+id;
	}

	@RequestMapping(value="/add-debt-to-client", method=RequestMethod.POST)
	public String processDebt(@RequestBody UUID clientId,
							  @RequestBody BigDecimal debtValue) {
		if(clientId == null) {
			return "Error:clientId was null";
		}
		if(debtValue == null) {
			return "Error:debtValue was null";
		}
		UUID id = clientService.addDebtToClient(clientId,debtValue);
		return "Successful Debt, id:"+id;
	}

	@RequestMapping(value="/pay-to-client-debt", method=RequestMethod.POST)
	public String processPay(@RequestBody UUID clientId,
							 @RequestBody UUID debtId,
							 @RequestBody BigDecimal debtValue) {
		if(clientId == null) {
			return "Error:clientId was null";
		}
		if(debtId == null) {
			return "Error:clientId was null";
		}
		if(debtValue == null) {
			return "Error:debtValue was null";
		}
		UUID id = clientService.payToClientDebt(clientId, debtId, debtValue);
		return "Successful Pay, id:"+id;
	}

	@RequestMapping(value="/show-all-clients", method=RequestMethod.GET)
	public Iterable<ClientDTO> showClients() {
		return clientService.showAllClients();
	}

}
