/**
 * 
 */
package com.aspire.banking.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aspire.banking.dao.AccountDAO;
import com.aspire.banking.domain.Account;
import com.aspire.banking.domain.AccountTransfer;
import com.aspire.banking.exception.BankTransactionException;
import com.aspire.banking.utils.AESUtil;
import com.aspire.banking.utils.AccountConstants;

/**
 * @author sreedharan.chandra
 *
 */
@Service

public class AccountService {

	private static final Log LOGGER = LogFactory.getLog(AccountService.class);

	@Autowired
	AccountDAO accountDAO;
	
	@Autowired
    private Environment environment;

	/**
	 * 
	 * @param id
	 * @return Optional<Account>
	 * @throws AccountLockException 
	 */
	public Optional<Account> getAccount(long id) throws BankTransactionException {
		try {
            return accountDAO.findById(id);
        } catch (Exception ex) {
        	LOGGER.error("Error while transfering the amount." + ex.getMessage());
            throw new BankTransactionException("Deadlock occured. Please retry. " + ex.getMessage());
        }
	}

	/**
	 * Persist to the database
	 * @param accountObject
	 * @return Account
	 * @throws BankTransactionException
	 */
	public Account save(Account accountObject) throws BankTransactionException {
		try {
			return createAccount(accountObject);
		} catch (BankTransactionException ex) {
			LOGGER.error(ex.getMessage());
			throw new BankTransactionException(ex.getMessage());
		}
	}

	/**
	 * @param id - AccountId
	 * @return Balance amount
	 * @throws BankTransactionException
	 * @throws AccountLockException 
	 */
	public String getAccountDetails(long id) throws BankTransactionException {
		Optional<Account> senderAccount = getAccount(id);
		if (senderAccount.isPresent()) {
			return senderAccount.get().toString();
		} else {
			throw new BankTransactionException(AccountConstants.INVALID_ACCOUNT_STRING);
		}
	}

	/** Create Account method
	 * encrypts the user enter password with secret key to store it in the database
	 * @param accountObject
	 * @return
	 * @throws BankTransactionException
	 */
	private Account createAccount(Account accountObject) throws BankTransactionException {
		if (StringUtils.isNotBlank(accountObject.getSecret())) {
			if(accountObject.getSecret().matches(environment.getProperty("password.pattern"))) {
				String secretKey = environment.getProperty("secret.key");
				accountObject.setSecret(AESUtil.encrypt(accountObject.getSecret(), secretKey));
				if (accountObject.getBalance().signum() > -1) {
					return accountDAO.save(accountObject);
				} else {
					LOGGER.error(AccountConstants.INVALID_BALANCE_AMOUNT);
					throw new BankTransactionException(AccountConstants.PLEASE_ENTER_VALID_BALANCE_AMOUNT);
				}
			}else {
				LOGGER.error(AccountConstants.PASSWORD_POLICY_NOT_MET);
				throw new BankTransactionException(AccountConstants.PASSWORD_POLICY_NOT_MET);
			}
		} else {
			LOGGER.error(AccountConstants.PLEASE_ENTER_VALID_SECRET_KEY);
			throw new BankTransactionException(AccountConstants.PLEASE_ENTER_VALID_SECRET_KEY);
		}
	}

	/**
	 * @param accountTransferObject
	 * @return
	 * @throws BankTransactionException
	 * @throws AccountLockException 
	 */
	
	public boolean transferAmount(AccountTransfer accountTransferObject) throws BankTransactionException {
		if(accountTransferObject.getFromAccount()!=accountTransferObject.getToAccount()) {
			return doTransaction(accountTransferObject);
		}else {
			throw new BankTransactionException(AccountConstants.INVALID_ACCOUNT_STRING);
		}
	}

	/** In case of any exception/failure, rollback is enabled
	 * @param accountTransferObject
	 * @return
	 * @throws BankTransactionException
	 * @throws AccountLockException 
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean doTransaction(AccountTransfer accountTransferObject) throws BankTransactionException {
		Account senderAccount = validateSenderAccount(accountTransferObject);
		Account receiverAccount = validateReceiverAccount(accountTransferObject);
		return transferAmount(senderAccount, receiverAccount, accountTransferObject.getAmount());
	}

	/**
	 * 
	 * @param accountTransferObject
	 * @return
	 * @throws BankTransactionException
	 * @throws AccountLockException 
	 */
	public Account validateSenderAccount(AccountTransfer accountTransferObject) throws BankTransactionException {
		Optional<Account> senderAccount = null;
		if (StringUtils.isBlank(accountTransferObject.getSecret())) {
			throw new BankTransactionException(AccountConstants.INVALID_TRANSACTION_PLEASE_ENTER_VALID_SECRET_KEY);
		} else {
			senderAccount = getAccount(accountTransferObject.getFromAccount());
			if (!senderAccount.isPresent()) {
				throw new BankTransactionException(AccountConstants.SENDER_ACCOUNT + AccountConstants.IS_INVALID);
			} else {
				validateSenderCredentials(accountTransferObject, senderAccount);
				if (accountTransferObject.getAmount().signum() > 0) {
					if (accountTransferObject.getAmount().compareTo(senderAccount.get().getBalance()) == 1) {
						throw new BankTransactionException(AccountConstants.INSUFFICIENT_BALANCE_IN_THE_ACCOUNT
								+ accountTransferObject.getFromAccount() + AccountConstants.CLOSURE_TAG_STRING);
					}
				}else {
					throw new BankTransactionException(AccountConstants.INVALID_TRANSFER_AMOUNT);
				}
			}
		}
		return senderAccount.get();
	}

	/**
	 * Validates the user entered password.
	 * Decrypts the stored passwords and matches with the user entered password
	 * 
	 * @param accountTransferObject
	 * @param senderAccount
	 * @throws BankTransactionException
	 */
	public void validateSenderCredentials(AccountTransfer accountTransferObject, Optional<Account> senderAccount) throws BankTransactionException {
		String secretKey = environment.getProperty("secret.key");
		if (!AESUtil.decrypt(senderAccount.get().getSecret(), secretKey).equals(accountTransferObject.getSecret())) {
			throw new BankTransactionException(AccountConstants.INVALID_TRANSACTION_PLEASE_ENTER_VALID_SECRET_KEY);
		}
	}

	/**
	 * 
	 * @param accountTransferObject
	 * @return
	 * @throws BankTransactionException
	 * @throws AccountLockException 
	 */
	public Account validateReceiverAccount(AccountTransfer accountTransferObject) throws BankTransactionException {
		Optional<Account> receiverAccount = getAccount(accountTransferObject.getToAccount());
		if (!receiverAccount.isPresent()) {
			throw new BankTransactionException(AccountConstants.RECEIVER_ACCOUNT + AccountConstants.IS_INVALID);
		}
		return receiverAccount.get();
	}

	/**
	 * @param accountTransferObject
	 * @param senderAccount
	 * @param receiverAccount
	 * @return
	 * @throws BankTransactionException
	 */
	public boolean transferAmount(Account senderAccount,
			Account receiverAccount, BigDecimal amount) throws BankTransactionException {
		try {
			BigDecimal senderBalanceAmount = senderAccount.getBalance();
			BigDecimal receiverBalanceAmount = receiverAccount.getBalance();
			senderBalanceAmount = senderBalanceAmount.subtract(amount);
			receiverBalanceAmount = receiverBalanceAmount.add(amount);
			senderAccount.setBalance(senderBalanceAmount);
			receiverAccount.setBalance(receiverBalanceAmount);
			accountDAO.save(senderAccount);
			accountDAO.save(receiverAccount);
			return true;
		} catch (Exception ex) {
			LOGGER.error(AccountConstants.ERROR_WHILE_TRANSFERING_THE_AMOUNT + ex.getMessage());
			throw new BankTransactionException(AccountConstants.INVALID_TRANSACTION_PLEASE_ENTER_VALID_SECRET_KEY);
		}
	}
}
