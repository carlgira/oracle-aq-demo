package com.example.jmsdemo.config;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jms.AQjmsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class OracleAQConfiguration {

    @Autowired
    private JmsPropertiesConfig config;

    @Bean
    /**
     * Spring bean with the configuration details of where the Oracle database is containing the QUEUES
     */
    public DataSource dataSource() throws SQLException {
        OracleDataSource ds = new OracleDataSource();
        ds.setUser(config.getUsername());
        ds.setPassword(config.getPassword());
        ds.setURL(config.getUrl());
        System.out.println(config.getUsername() + " " + config.getPassword() + " " + config.getUrl() );
        ds.setImplicitCachingEnabled(true);
        ds.setFastConnectionFailoverEnabled(true);
        return ds;
    }

    @Bean
    /**
     * The KEY component effectively connecting to the Oracle AQ system using the datasource input
     */
    public QueueConnectionFactory connectionFactory(DataSource dataSource) throws JMSException {
        return AQjmsFactory.getQueueConnectionFactory(dataSource);
    }

}
