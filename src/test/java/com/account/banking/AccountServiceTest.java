package com.account.banking;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aspire.banking.ApplicationMain;
import com.aspire.banking.dao.AccountDAO;
import com.aspire.banking.domain.Account;
import com.aspire.banking.domain.AccountTransfer;
import com.aspire.banking.exception.BankTransactionException;
import com.aspire.banking.service.AccountService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationMain.class, H2JpaConfig.class})
public class AccountServiceTest {

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountDAO accountDAO;

	@Before
	public void setup() throws BankTransactionException {
		Account account = new AccountBuilder().createAccount(11, "test", "Dvqwe@231", BigDecimal.valueOf(1000));
		Account account12 = new AccountBuilder().createAccount(12, "test1", "Dvqwe@231", BigDecimal.valueOf(1000));
		accountService.save(account);
		accountService.save(account12);
	}


	@Test()
	public void testTransferSuccess() throws Exception {
		AccountTransfer accountTransfer = new AccountBuilder().createAccountTransfer(1, 2, BigDecimal.valueOf(15), "Dvqwe@231");
		assertEquals(accountService.transferAmount(accountTransfer), true);
		Optional<Account> receiverAccount = accountService.getAccount(2);
		assertNotNull(receiverAccount.get());
		assertEquals(BigDecimal.valueOf(1015), receiverAccount.get().getBalance());
	}

	@Test(expected = BankTransactionException.class)
	public void testInvalidTransferAmount() throws BankTransactionException {
		AccountTransfer accountTransfer = new AccountBuilder().createAccountTransfer(1, 2, BigDecimal.valueOf(-10), "Dvqwe@231");
		accountService.transferAmount(accountTransfer);
	}

	@Test(expected = BankTransactionException.class)
	public void testInvalidSenderAccount() throws BankTransactionException {
		AccountTransfer accountTransfer = new AccountBuilder().createAccountTransfer(-1, 2, BigDecimal.valueOf(10), "Dvqwe@231");
		accountService.transferAmount(accountTransfer);
	}

	@Test(expected = BankTransactionException.class)
	public void testInvalidReceiverAccount() throws BankTransactionException {
		AccountTransfer accountTransfer = new AccountBuilder().createAccountTransfer(1, -2, BigDecimal.valueOf(10), "Dvqwe@231");
		accountService.transferAmount(accountTransfer);
	}

	@Test(expected = BankTransactionException.class)
	public void testInvalidSecret() throws BankTransactionException {
		AccountTransfer accountTransfer = new AccountBuilder().createAccountTransfer(11, -12, BigDecimal.valueOf(-10), "");
		accountService.transferAmount(accountTransfer);
	}

	@Test()
	public void testCreateAccount() throws BankTransactionException {
		Account account = new AccountBuilder().createAccountWithOutId("test", "Dvqwe@231", BigDecimal.valueOf(1000));
		Account savedAccount = accountService.save(account);

		assertTrue(savedAccount.getId() > 0);
		assertEquals("test", savedAccount.getFullName());
		assertEquals(BigDecimal.valueOf(1000), savedAccount.getBalance());
	}

	@Test(expected = BankTransactionException.class)
	public void testCreateAccountInvalidAmount() throws BankTransactionException {
		Account account = new AccountBuilder().createAccountWithOutId("test", "Dvqwe@231", BigDecimal.valueOf(-1000));
		accountService.save(account);
	}

	@Test(expected = BankTransactionException.class)
	public void testCreateAccountWithOutSecret() throws BankTransactionException {
		Account account = new AccountBuilder().createAccountWithOutId("test", "", BigDecimal.valueOf(-1000));
		accountService.save(account);
	}
	
	@Test(expected = BankTransactionException.class)
	public void testSameAccountTransferFailure() throws BankTransactionException {
		AccountTransfer accountTransfer = new AccountBuilder().createAccountTransfer(1, 1, BigDecimal.valueOf(10), "Dvqwe@231");
		accountService.transferAmount(accountTransfer);
	}

}






















