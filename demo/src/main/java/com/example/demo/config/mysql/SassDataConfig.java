package com.example.demo.config.mysql;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactorySass",
        transactionManagerRef = "sassTransactionManager",
        basePackages = {"com.example.demo.dao.sass"}) //设置Repository所在位置
public class SassDataConfig {

    @Resource
    @Qualifier("sassDataSource")
    private DataSource sassDataSource;

    @Resource
    private Properties jpaProperties;

    @Bean(name = "sassEntityManager")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return Objects.requireNonNull(entityManagerFactorySass(builder).getObject()).createEntityManager();
    }

    @Bean(name = "sassJdbcTemplate")
    public JdbcTemplate sassJdbcTemplate(){
        return new JdbcTemplate(sassDataSource);
    }

    @Bean(name = "entityManagerFactorySass")
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySass(EntityManagerFactoryBuilder builder) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory
                = builder
                .dataSource(sassDataSource)
                .packages("com.example.demo.entity.sass")//设置实体类所在位置
                .persistenceUnit("sassPersistenceUnit")//持久化单元创建一个默认即可，多个便要分别命名
                .build();
        entityManagerFactory.setJpaProperties(jpaProperties);
        return entityManagerFactory;
    }

    @Bean(name = "sassTransactionManager")
    public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactorySass(builder).getObject());
    }
}