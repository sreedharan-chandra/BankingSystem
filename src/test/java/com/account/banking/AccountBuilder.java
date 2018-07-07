/**
 * 
 */
package com.account.banking;

import org.springframework.stereotype.Component;

import com.aspire.banking.domain.Account;
import com.aspire.banking.domain.AccountTransfer;

/**
 * @author sreedharan.chandra
 *
 * Builds {@link Account} domain objects
 * 
 * 
 * 
 * 
 */
@Component
public class AccountBuilder {

	private AccountTransfer accountTransfer = null;
	private Account account = null;
	
	 public AccountTransfer createAccountTransfer(long fromId, long toId, double amount, String secret){	
		accountTransfer = new AccountTransfer();
		accountTransfer.setFromAccount(fromId);
		accountTransfer.setToAccount(toId);
		accountTransfer.setAmount(amount);
		accountTransfer.setSecret(secret);
		return accountTransfer;
	}

	public Account createAccount(long i, String fullName, String secret, double balance) {
		account = new Account();
		account.setFullName(fullName);
		account.setId(i);
		account.setSecret(secret);
		account.setBalance(balance);
		return account;
	}
	
	public Account createAccountWithOutId( String fullName, String secret, double balance) {
		account = new Account();
		account.setFullName(fullName);
		account.setSecret(secret);
		account.setBalance(balance);
		return account;
	}
	
}
