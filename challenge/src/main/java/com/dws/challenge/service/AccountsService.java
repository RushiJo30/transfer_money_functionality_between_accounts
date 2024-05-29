package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;
  private final NotificationService notificationService;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
    this.accountsRepository = accountsRepository;
    this.notificationService = notificationService;
  }

  public Account addAccount(Account account) {
   return accountsRepository.save(account);
  }

  public Account getAccount(Long accountId) {
    return accountsRepository.findById(accountId);
  }
  
  public void transferMoney(Long accountFromId, Long accountToId, BigDecimal amount) {
	  
	  if(amount.compareTo(BigDecimal.ZERO) <= 0) {
		  throw new IllegalArgumentException("Transfer amount must be positive");
	  }
	  
	  Optional<Integer> op = Optional.empty();
	 
	  Account accountFrom = getAccount(accountFromId);
	  Account accountTo =  getAccount(accountToId);
	  
	  Account firstLock = accountFromId < accountToId ? accountFrom : accountTo;
	  Account secondLock = accountFromId < accountToId ? accountTo : accountFrom;
	  
	  firstLock.getLock().lock();
	  try {
		  secondLock.getLock().lock();
		  try {
			  if(accountFrom.getBalance().compareTo(amount) < 0) {
				  throw new RuntimeException("Insufficient funds in account :" + accountFromId);
			  }
			  accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
			  accountTo.setBalance(accountTo.getBalance().add(amount));
			  
			  accountsRepository.save(accountFrom);
			  accountsRepository.save(accountTo);
			  
			  notificationService.notify(accountFromId, "Transferred " + amount +" to account " +accountToId);
			  notificationService.notify(accountToId, "Received " + amount +" from account " +accountFromId);
		  } finally {
			  secondLock.getLock().unlock();
		  }
	  } finally {
		  firstLock.getLock().unlock();
	  }
  }
}
