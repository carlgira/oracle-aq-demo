# oracle-aq-demo
Oracle AQ spring-boot app.

## Configuration

1. Database:

   1.1 Create database user 
```sql
sqlplus system/password as SYSDBA

CREATE USER aqsuser IDENTIFIED BY aqsuser;
GRANT connect, resource TO aqsuser IDENTIFIED BY aqsuser;
GRANT aq_user_role TO aqsuser;
GRANT execute ON sys.dbms_aqadm TO aqsuser;
GRANT execute ON sys.dbms_aq TO aqsuser;
GRANT execute ON sys.dbms_aqin TO aqsuser;
GRANT execute ON sys.dbms_aqjms TO aqsuser;
GRANT UNLIMITED TABLESPACE TO aqsuser;

```

   1.2 Create the queue
```sql
connect aqsuser/aqsuser;

BEGIN
dbms_aqadm.create_queue_table (
queue_table => 'myQueueTable',
queue_payload_type => 'sys.aq$_jms_text_message',
multiple_consumers => false
);
END;
/

BEGIN
dbms_aqadm.create_queue (
queue_name => 'userQueue',
queue_table => 'myQueueTable'
);
END;
/

BEGIN
dbms_aqadm.start_queue (
queue_name => 'userQueue');
END;
/

```

## Build

1. Go to https://www.oracle.com/es/database/technologies/appdev/jdbc-downloads.html and download the jdbc driver for your database.
2. Install the .jar into the local maven repository.
```
# For ojdbc8.jar would be like
mvn install:install-file -Dfile=ojdbc8.jar -DgroupId=com.oracle -DartifactId=ojdbc8 -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true
```

Modify the file "application.properties" with the values corresponding with env.

```
app.datasource.url=jdbc:oracle:thin:@//localhost:1521/orcl
app.datasource.username=aqsuser
app.datasource.password=aqsuser
```

After that compile aplication with maven

```
mvn clean package
```


## Test

Start the spring-boot app

```
java -jar target/oracle-aq-demo-0.0.1.war
```


For testing open a sql connection using the aqsuser and execute the procedure **dbms_aqadm.create_queue** to add a new message into the queue.

```
DECLARE
   enqueue_options     DBMS_AQ.enqueue_options_t;
   message_properties  DBMS_AQ.message_properties_t;
   message_handle      RAW(16);
   message             SYS.AQ$_JMS_TEXT_MESSAGE;
BEGIN
   	message := sys.aq$_jms_text_message.construct; 
   	message.set_text('Aqsuser MESSAGE'); 
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

Check the log to see the message.

** *Tested on database 19c and 12c*

## References
The original code is from the repo [https://github.com/daitangio/oracle-aq-jms](https://github.com/daitangio/oracle-aq-jms)

