/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.persistence.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.jolbox.bonecp.BoneCPDataSource;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@ComponentScan(basePackages = {"org.apromore.persistence.entity","org.apromore.persistence.repository"})
public class JPAConfig {

	public static final String REPOSITORY_PACKAGE = "org.apromore.persistence.repository";
	public static final String ENTITY_MANAGER_FACTORY = "entityManagerFactory";
	public static final String PERSISTENCE_UNIT_NAME = "Apromore";
	public static final String TRANSACTION_MANAGER = "transactionManager";

	@Value("${jdbc.username}")
	private String dbUser;

	@Value("${jdbc.password}")
	private String password;

	@Value("${jdbc.url}")
	private String url;

	@Value("${jdbc.driver}")
	private String driver;

	@Value("${jpa.database}")
	private String context;

	@Value("${jpa.databasePlatform}")
	private String jpaPlatform;

	@Value("${jpa.showSql}")
	private boolean showSql;

	@Value("${jpa.generateDDL}")
	private boolean generateDDL;

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		BoneCPDataSource ds = new BoneCPDataSource();
		ds.setJdbcUrl(url);
		ds.setDriverClass(driver);
		ds.setUsername(dbUser);
		ds.setPassword(password);
		ds.setMaxConnectionsPerPartition(30);
		ds.setMinConnectionsPerPartition(10);
		ds.setPartitionCount(5);
		ds.setAcquireIncrement(5);
		ds.setStatementCacheSize(100);
		ds.setReleaseHelperThreads(3);
		return ds;
	}

	@Bean
	@DependsOn({ "liquibase" })
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan("org.apromore.persistence.entity");
		em.setJpaVendorAdapter(jpaVendorAdapter());
		em.setJpaProperties(additionalProperties());
		return em;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setShowSql(showSql);
		vendorAdapter.setGenerateDdl(generateDDL);
		vendorAdapter.setDatabasePlatform(jpaPlatform);
		return vendorAdapter;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager(entityManagerFactory().getObject());
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Bean
	public SpringLiquibase liquibase() {

		SpringLiquibase liquibase = new SpringLiquibase();

		liquibase.setDataSource(dataSource());
		liquibase.setChangeLog("classpath:db/migration/changeLog.yaml");
		liquibase.setContexts(context);
//		liquibase.setDropFirst(true);
		return liquibase;
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(unpooledDataSource());
	}
	
	@Bean
	public static PropertyPlaceholderConfigurer properties() {
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		Resource[] resources = new ClassPathResource[] { new ClassPathResource("db.properties") };
		ppc.setLocations(resources);
		ppc.setIgnoreUnresolvablePlaceholders(true);
		return ppc;
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", "none");
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		properties.setProperty("hibernate.default_batch_fetch_size", "4");
		properties.setProperty("hibernate.cache.use_query_cache", "true");
		properties.setProperty("hibernate.cache.use_second_level_cache", "true");
		properties.setProperty("hibernate.cache.region.factory_class",
				"org.hibernate.cache.ehcache.EhCacheRegionFactory");
		return properties;
	}

	DataSource unpooledDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driver);
		dataSource.setUrl(url);
		dataSource.setUsername(dbUser);
		dataSource.setPassword(password);
		return dataSource;
	}
}