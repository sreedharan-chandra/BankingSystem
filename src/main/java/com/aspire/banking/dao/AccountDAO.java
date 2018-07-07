/**
 * 
 */
package com.aspire.banking.dao;

import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aspire.banking.domain.Account;

/**
 * @author sreedharan.chandra
 *
 */
@Repository
public interface AccountDAO extends CrudRepository<Account, Long>{

	/**
	 *
	 * @param accountId
	 * @return Account entity
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Override
	Optional<Account> findById(Long accountId);
	
}
