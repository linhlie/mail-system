
###pre-setting mysql server

```
    [client]
    default-character-set = utf8mb4
    [mysql]
    default-character-set = utf8mb4
    [mysqld]
    character-set-client-handshake = FALSE
    character-set-server = utf8mb4
    collation-server = utf8mb4_unicode_ci
```


### Hot swap / Live reload configuration tutorial
https://www.youtube.com/watch?v=VWF7vCJSqrA

### Build jar
Run it with execute ```mvn clean package```

### Run jar
Run ```java -jar [path-to-jar]/[jar-name]```

Example at root project, run: ```java -jar target/MailReceiver-0.0.1-SNAPSHOT.jar```"# mail-system" 
