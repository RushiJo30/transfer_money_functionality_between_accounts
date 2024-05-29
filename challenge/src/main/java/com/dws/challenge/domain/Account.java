package com.dws.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import lombok.Data;

@Data
public class Account {

  @NotNull
  @NotEmpty
  private  String accountId;
  
  private final transient Lock lock = new ReentrantLock();

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private BigDecimal balance;

  public Account(String accountId) {
    this.accountId = accountId;
    this.balance = BigDecimal.ZERO;
  }

  @JsonCreator
  public Account(@JsonProperty("accountId") String accountId,
    @JsonProperty("balance") BigDecimal balance) {
    this.accountId = accountId;
    this.balance = balance;
  }

public <Account extends Throwable> Object orElseThrow(Supplier<? extends Account> exceptionSupplier) {

	return null;
}

public void setId(String accountId) {
	this.accountId = accountId;
	
}

public @NotNull @NotEmpty String getAccountId() {
	return accountId;
}
}
