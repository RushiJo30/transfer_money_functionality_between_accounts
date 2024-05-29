package com.dws.challenge.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dws.challenge.dto.TransferRequest;
import com.dws.challenge.service.AccountsService;

@RestController
@RequestMapping("/api/transfer")
public class TransferContoller {
	
	private final AccountsService accountService;
	
	public TransferContoller(AccountsService accountService) {
		this.accountService = accountService;
	}
	
	@PostMapping
	public  ResponseEntity transferMoney(@RequestBody TransferRequest transferRequest) {
		accountService.transferMoney(transferRequest.getAccountFromId(), transferRequest.getAccountToId(), transferRequest.getAmount());
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
