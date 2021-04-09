package com.example.jmsdemo.config;

import com.example.jmsdemo.service.QueueReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.ConnectionFactory;
import javax.sql.DataSource;

@Configuration
public class JMSConfiguration {

    @Autowired
    private JmsPropertiesConfig config;

    @Autowired
    private QueueReaderService jmsReceiver;

    @Bean
    /**
     * Spring bean to WRITE/SEND/ENQUEUE messages on a queue with a certain name
     */
    public JmsTemplate jmsTemplate(ConnectionFactory conFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestinationName(config.getApplicationNotificationQueue());
        jmsTemplate.setSessionTransacted(true);
        jmsTemplate.setConnectionFactory(conFactory);

        return jmsTemplate;
    }

    /**
     * Spring bean to READ/RECEIVE/DEQUEUE messages of a queue with a certain name
     * All of this happens under a code managed transaction
     * to commit the change on Oracle (remove of the message from the queue table)
     * Reference the application custom code handling the message here
     */
    @Bean
    public DefaultMessageListenerContainer messageListenerContainer(ConnectionFactory conFactory, DataSource dataSource) {
        DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
        dmlc.setDestinationName(config.getApplicationNotificationQueue());
        dmlc.setSessionTransacted(true);
        dmlc.setConnectionFactory(conFactory);

        DataSourceTransactionManager manager = new DataSourceTransactionManager();
        manager.setDataSource(dataSource);
        dmlc.setTransactionManager(manager);

        dmlc.setMessageListener(jmsReceiver);
        return dmlc;
    }

}
