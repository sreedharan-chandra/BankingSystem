package com.aspire.banking.domain;

import java.math.BigDecimal;
import java.util.Base64;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name = "Account" )
public class Account {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;
 
    @Column(name = "name", length = 128, nullable = false)
    private String fullName;
 
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;
    
    @Column(name = "secret", nullable = false)
    private String secret;
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public String getFullName() {
        return fullName;
    }
 
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
 
    public BigDecimal getBalance() {
        return balance;
    }
 
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public String toString() {
		byte[] decodedBytes = Base64.getDecoder().decode(this.secret);
		return "Name: "+this.fullName+", Id: "+this.getId()+" , Balance: "+this.balance+" ,Secret: "+new String(decodedBytes);
		
	}
}
