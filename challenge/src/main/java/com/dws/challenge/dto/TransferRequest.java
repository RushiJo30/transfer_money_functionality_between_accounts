package com.dws.challenge.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
	
	private Long accountFromId;
	private Long accountToId;
	private BigDecimal amount;

}
