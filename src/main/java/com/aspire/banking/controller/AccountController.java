/**
 * 
 */
package com.aspire.banking.controller;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.aspire.banking.domain.Account;
import com.aspire.banking.domain.AccountTransfer;
import com.aspire.banking.exception.AccountLockException;
import com.aspire.banking.exception.BankTransactionException;
import com.aspire.banking.service.AccountService;
import com.aspire.banking.utils.AccountConstants;
import com.google.gson.Gson;

/**
 * @author sreedharan.chandra
 *
 */
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
	
	private static final Log LOGGER = LogFactory.getLog(AccountController.class);
			
	@Autowired
	private AccountService accountService;
	
	@Autowired
    private Environment environment;

	@RequestMapping(value = AccountConstants.HOME_URL, method = RequestMethod.GET)
	public String viewSendMoneyPage(Model model) {
		LOGGER.info(AccountConstants.WELCOME_TO_BANKING_SYSTEM);
		return AccountConstants.WELCOME_TO_BANKING_SYSTEM;
	}
	
	@RequestMapping(value = AccountConstants.ID_URL_STRING, method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> getAccount(@PathVariable(AccountConstants.ID_PATH_VARIABLE) long id) {
		try {
			return new ResponseEntity<>(accountService.getAccountDetails(id), HttpStatus.OK);
		} catch(BankTransactionException ex) {
			LOGGER.error(ex);
			return new ResponseEntity<>(AccountConstants.ERROR+ex.getMessage(), HttpStatus.BAD_REQUEST);
		}catch(Exception ex) {
			LOGGER.error(ex);
			return new ResponseEntity<>(AccountConstants.INVALID_ACCOUNT_STRING, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@RequestMapping(value = AccountConstants.HOME_SLASH_STRING, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> createAccount(@RequestBody Map<String, Object> payload) {
		try {
			Gson gson = new Gson();
			Account account = gson.fromJson(payload.toString(), Account.class);
			LOGGER.debug(account.getBalance()+"--"+account.getSecret());
			long id= accountService.save(account).getId();
			return new ResponseEntity<>(AccountConstants.ACCOUNT_CREATED_SUCCESSFULLY_ACCOUNT_NUMBER +id, HttpStatus.OK);
		}catch(BankTransactionException ex) {
			LOGGER.error(ex);
			return new ResponseEntity<>(AccountConstants.ACCOUNT_CREATION_FAILED +ex.getMessage() + AccountConstants.PLEASE_TRY_AGAIN_WITH_VALID_DATA, HttpStatus.BAD_REQUEST);
		}catch(Exception ex) {
			LOGGER.error(ex);
			return new ResponseEntity<>(AccountConstants.ACCOUNT_CREATION_FAILED + AccountConstants.PLEASE_TRY_AGAIN_WITH_VALID_DATA, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = AccountConstants.TRANSFER, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> transferAccount(@RequestBody Map<String, Object> payload) {
		ResponseEntity<String> response = null;
		try {
			Gson gson = new Gson();
			boolean doRetry = false;
			int retryCount = 0;
			int maxRetryCount = environment.getProperty("max.retry.transfer")!=null?Integer.parseInt(environment.getProperty("max.retry.transfer")):3;
			do {
				try {
					doRetry = false;
					AccountTransfer accountTransfer = gson.fromJson(payload.toString(), AccountTransfer.class);
					LOGGER.debug(accountTransfer.getFromAccount()+"--"+accountTransfer.getToAccount());
					if(accountService.transferAmount(accountTransfer)) {
						return new ResponseEntity<>(AccountConstants.AMOUNT_TRANSFERED_SUCCESSFULLY, HttpStatus.OK);
					}else {
						return new ResponseEntity<>(AccountConstants.TRANSACTION_FAILED_PLEASE_TRY_AGAIN, HttpStatus.OK);
					}
				} catch (AccountLockException ex) {
					LOGGER.error("*************************************************************** " + ex.getMessage());
					response = new ResponseEntity<>(ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
					doRetry = true;
					++retryCount;
				} 
			}while (doRetry && retryCount < maxRetryCount);
		}

		catch(BankTransactionException ex) {
			LOGGER.error(ex);
			response = new ResponseEntity<>(AccountConstants.TRANSACTION_FAILED_PLEASE_TRY_AGAIN + ex.getMessage(), HttpStatus.BAD_REQUEST);
		}catch(Exception ex) {
			LOGGER.error(ex);
			response = new ResponseEntity<>(AccountConstants.TRANSACTION_FAILED_PLEASE_TRY_AGAIN + AccountConstants.PLEASE_TRY_AGAIN_WITH_VALID_DATA, HttpStatus.BAD_REQUEST);
		}
		return response;

	}

}
