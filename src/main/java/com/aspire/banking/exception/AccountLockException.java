/**
 * 
 */
package com.aspire.banking.exception;

/**
 * @author sreedharan.chandra
 *
 */
public class AccountLockException extends Exception {
  
    private static final long serialVersionUID = -3128681006635769411L;
     
    public AccountLockException(String message) {
        super(message);
    }
}