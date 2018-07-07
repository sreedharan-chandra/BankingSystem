package com.account.banking;

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
import com.aspire.banking.exception.AccountLockException;
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
		Account account = new AccountBuilder().createAccount(11, "test", "vqwe@23", 1000);
		Account account12 = new AccountBuilder().createAccount(12, "test1", "vq@we23", 1000);
		accountService.save(account);
		accountService.save(account12);
	}


	@Test()
	public void testTransferSuccess() throws Exception {
		AccountTransfer accountTransfer = new AccountBuilder().createAccountTransfer(1, 2, 15, "vqwe@23");
		assertEquals(accountService.transferAmount(accountTransfer), true);
		Optional<Account> receiverAccount = accountService.getAccount(2);
		assertNotNull(receiverAccount.get());
		assertEquals(1015, receiverAccount.get().getBalance(), 0);
	}

	@Test(expected = BankTransactionException.class)
	public void testInvalidTransferAmount() throws BankTransactionException, AccountLockException {
		AccountTransfer accountTransfer = new AccountBuilder().createAccountTransfer(1, 2, -10, "vqwe@23");
		accountService.transferAmount(accountTransfer);
	}

	@Test(expected = BankTransactionException.class)
	public void testInvalidSenderAccount() throws BankTransactionException, AccountLockException {
		AccountTransfer accountTransfer = new AccountBuilder().createAccountTransfer(-1, 2, 10, "vqwe@23");
		accountService.transferAmount(accountTransfer);
	}

	@Test(expected = BankTransactionException.class)
	public void testInvalidReceiverAccount() throws BankTransactionException, AccountLockException {
		AccountTransfer accountTransfer = new AccountBuilder().createAccountTransfer(1, -2, 10, "vqwe@23");
		accountService.transferAmount(accountTransfer);
	}

	@Test(expected = BankTransactionException.class)
	public void testInvalidSecret() throws BankTransactionException, AccountLockException {
		AccountTransfer accountTransfer = new AccountBuilder().createAccountTransfer(11, -12, -10, "");
		accountService.transferAmount(accountTransfer);
	}

	@Test()
	public void testCreateAccount() throws BankTransactionException {
		Account account = new AccountBuilder().createAccountWithOutId("test", "test", 1000);
		Account savedAccount = accountService.save(account);

		assertTrue(savedAccount.getId() > 0);
		assertEquals("test", savedAccount.getFullName());
		assertEquals(1000, savedAccount.getBalance(), 0);
	}

	@Test(expected = BankTransactionException.class)
	public void testCreateAccountInvalidAmount() throws BankTransactionException {
		Account account = new AccountBuilder().createAccountWithOutId("test", "test", -1000);
		accountService.save(account);
	}

	@Test(expected = BankTransactionException.class)
	public void testCreateAccountWithOutSecret() throws BankTransactionException {
		Account account = new AccountBuilder().createAccountWithOutId("test", "", -1000);
		accountService.save(account);
	}

}






















