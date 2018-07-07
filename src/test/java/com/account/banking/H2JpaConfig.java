package com.account.banking;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.aspire.banking")
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
public class H2JpaConfig {
	// ...
}
