package com.example.demo.config.mysql;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "sourceEntityManagerFactory",
        transactionManagerRef = "SourceTransactionManager",
        basePackages = {"com.example.demo.dao.risk"})
public class SourceDataConfig {
    @Resource
    @Qualifier("sourceDataSource")
    private DataSource sourceDataSource;

    @Resource
    private Properties jpaProperties;

    @Bean(name = "sourceJdbcTemplate")
    @Primary
    public JdbcTemplate sourceJdbcTemplate(){
        return new JdbcTemplate(sourceDataSource);
    }


    @Primary
    @Bean(name = "sourceEntityManager")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactorySource(builder).getObject().createEntityManager();
    }

    /**
     * 设置实体类所在位置
     */
    @Bean(name = "sourceEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySource(EntityManagerFactoryBuilder builder) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = builder
                .dataSource(sourceDataSource)
                .packages("com.example.demo.entity.risk")
                .persistenceUnit("sourcePersistenceUnit")
                .build();
        entityManagerFactory.setJpaProperties(jpaProperties);
        return entityManagerFactory;
    }

    @Primary
    @Bean(name = "SourceTransactionManager")
    public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactorySource(builder).getObject());
    }

}
