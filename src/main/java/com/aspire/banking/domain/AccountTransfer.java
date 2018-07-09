/**
 * 
 */
package com.aspire.banking.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author sreedharan.chandra
 *
 */
@Entity
@Table(name = "AccountTransfer" )
public class AccountTransfer {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TransferId", updatable = false, nullable = false)
    private Long id;
 
    @Column(name = "TransactionDate", length = 128, nullable = false)
    @Temporal(TemporalType.DATE)
    private Date transactionDate;
 
    @Column(name = "FromAccount", nullable = false)
    private long fromAccount;
    
    @Column(name = "ToAccount", nullable = false)
    private long toAccount;
    
    @Column(name = "Amount", nullable = false)
    private BigDecimal amount;
    
    private String secret;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Long getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(Long fromAccount) {
		this.fromAccount = fromAccount;
	}

	public long getToAccount() {
		return toAccount;
	}

	public void setToAccount(long toAccount) {
		this.toAccount = toAccount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
    
    
 
}