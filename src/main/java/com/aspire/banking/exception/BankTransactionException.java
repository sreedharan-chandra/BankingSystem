/**
 * 
 */
package com.aspire.banking.exception;

/**
 * @author sreedharan.chandra
 *
 */
public class BankTransactionException extends RuntimeException {
  
    private static final long serialVersionUID = -3128681006635769411L;
     
    public BankTransactionException(String message) {
        super(message);
    }
}