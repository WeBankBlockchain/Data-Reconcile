package com.webank.bcreconcile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@PropertySource(value = {
		"file:./config/application.properties",
		"file:./config/datasource.properties",
		"file:./config/reconcile.properties",
		"file:./config/ftp.properties",
		"classpath:application.properties",
		"classpath:datasource.properties",
		"classpath:reconcile.properties",
		"classpath:ftp.properties",
}, encoding = "utf-8",ignoreResourceNotFound = true)
@SpringBootApplication
@EnableTransactionManagement
@EnableConfigurationProperties
public class BcreconcileApplication {

	public static void main(String[] args) {
		SpringApplication.run(BcreconcileApplication.class, args);
	}

}
