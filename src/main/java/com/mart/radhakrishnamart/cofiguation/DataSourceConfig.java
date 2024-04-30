package com.mart.radhakrishnamart.cofiguation;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {
	
	@Bean
	 public DataSource getDataSource() {
		return DataSourceBuilder.create().driverClassName("com.mysql.cj.jdbc.Driver").url("jdbc:mysql://javadb.ctucmqccavdn.ap-south-1.rds.amazonaws.com:3306/martdb").username("admin").password("root1234").build();
	}
	
	

}
