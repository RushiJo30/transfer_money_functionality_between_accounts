package com.dws.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;



@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AccountServiceTest {
	
	@InjectMocks
	private AccountsService accountService;
	
	@Mock
	private AccountsRepository accountsRepository;
	
	@Mock
	private NotificationService notificationService;
	
	@Mock
	 private String accountId;
	
	/*
	 * @Mock private final Account account1;
	 * 
	 * @Mock private final Account account2;
	 */
	
	@BeforeEach
	public void setup() {
		
		MockitoAnnotations.openMocks(this);
		
		/*
		 * Account account1 = null; account1.setBalance(BigDecimal.valueOf(1000));
		 * accountsRepository.save(account1);
		 * 
		 * Account account2 = null; account2.setBalance(BigDecimal.valueOf(500));
		 * accountsRepository.save(account2);
		 */
	}
	
	@Test
	public void testConcurrentTransfers() throws InterruptedException {
		
		Account accountA = new Account(accountId);
		accountA.setId("1L");
		accountA.setBalance(BigDecimal.valueOf(1000));
		
		Account accountB = new Account(accountId);
		accountB.setId("2L");
		accountB.setBalance(BigDecimal.valueOf(1000));
		
		Account accountC = new Account(accountId);
		accountC.setId("3L");
		accountC.setBalance(BigDecimal.valueOf(1000));
		
		Mockito.when(accountsRepository.findById(1L)).thenReturn(accountA);
		Mockito.when(accountsRepository.findById(2L)).thenReturn(accountB);
		Mockito.when(accountsRepository.findById(3L)).thenReturn(accountC);
		
		ExecutorService executor = Executors.newFixedThreadPool(2);
		
		executor.submit(() -> accountService.transferMoney(1L, 2L, BigDecimal.valueOf(100)));
		executor.submit(() -> accountService.transferMoney(1L, 3L, BigDecimal.valueOf(200)));
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.MINUTES);
		
		assertEquals(BigDecimal.valueOf(700), accountA.getBalance());
		assertEquals(BigDecimal.valueOf(1100), accountB.getBalance());
		assertEquals(BigDecimal.valueOf(1200), accountC.getBalance());
		
		verify(notificationService).notify(1L, "Transferred 100 to account 2");
		verify(notificationService).notify(2L, "Received 100 from account 1");
		verify(notificationService).notify(1L, "Transferred 200 to account 3");
		verify(notificationService).notify(3L, "Received 200 from account 1");
		
	}
	
	/*
	 * @Test void testTransferMoney() {
	 * 
	 * Long accountFromId = 1L; Long accountToId = 2L; BigDecimal amount =
	 * BigDecimal.valueOf(200); Optional<Integer> op = Optional.empty();
	 * 
	 * accountService.transferMoney(accountFromId, accountToId, amount);
	 * 
	 * 
	 * Account accountFrom = (Account) accountsRepository.findById(accountFromId);
	 * System.out.println(op.orElseThrow(IllegalArgumentException :: new)); Account
	 * accountTo = (Account) accountsRepository.findById(accountToId);
	 * System.out.println(op.orElseThrow(IllegalArgumentException :: new));
	 * 
	 * assertEquals(BigDecimal.valueOf(800), accountFrom.getBalance());
	 * assertEquals(BigDecimal.valueOf(700), accountTo.getBalance());
	 * 
	 * Mockito.verify(notificationService, Mockito.times(1)).notify(accountFromId,
	 * "Transferred 200 to account 2"); Mockito.verify(notificationService,
	 * Mockito.times(1)).notify(accountToId, "Received 200 to account 1"); }
	 */
}
