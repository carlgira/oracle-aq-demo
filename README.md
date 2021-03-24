# oracle-aq-demo
Oracle AQ spring-boot app and instructions to config database and in Weblogic

## Configuration

Follow the instructions on [set-up-an-aq-jms-advanced-queueing-jms](https://blogs.oracle.com/fusionmiddlewaresupport/jms-step-6-how-to-set-up-an-aq-jms-advanced-queueing-jms-for-soa-purposes-v2) to create the next resources:

1. Database:
    * Create database user
    * Create a table to save messages.
    * Create an AQ queue and start it.

2. Weblogic
    * Create datasource pointing to database
    * Create in weblogic JMS Foreing Server using datasource
    * Create ConnectionFactory and queue


## Compilation

Modify the file [application.properties](https://github.com/carlgira/oracle-aq-demo/blob/main/src/main/resources/application.properties) with the values corresponding with your weblogic env.

```
spring.wls.jms.url=t3://localhost:9073
spring.wls.jms.username=weblogic
spring.wls.jms.password=passwords
```

After that compile aplication with maven

```
mvn clean package
```

## Deploy 

After the compilation deploy the file target/oracle-aq-demo-0.0.1.war into weblogic.


## Test

For testing open a sql connection using the aqjmsuser and execute the procedure **dbms_aqadm.create_queue** to add a new message into the queue.

```
DECLARE
   enqueue_options     DBMS_AQ.enqueue_options_t;
   message_properties  DBMS_AQ.message_properties_t;
   message_handle      RAW(16);
   message             SYS.AQ$_JMS_TEXT_MESSAGE;
BEGIN
   	message := sys.aq$_jms_text_message.construct; 
   	message.set_text('SECOND MESSAGE'); 
   DBMS_AQ.ENQUEUE(
      queue_name              => 'userQueue',
      enqueue_options         => enqueue_options,
      message_properties      => message_properties,
      payload                 => message,
      msgid                   => message_handle);
   COMMIT;
END;
/
```

Check the log of weblogic to see the printed message.

\** *Tested on database 19c and 12c*

## References
The original code is from the repo [https://github.com/daitangio/oracle-aq-jms](https://github.com/daitangio/oracle-aq-jms)

