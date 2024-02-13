# Running
From this development directory: (`/home/jimk/dev/bdrc-fcrepo-client`)
```shell
```shell
mvn [clean] package
java -cp /home/jimk/dev/bdrc-fcrepo-client/target/bdrc-fcrepo-client-1.0-SNAPSHOT-Install/bdrc-fcrepo-client-1.0-SNAPSHOT/lib:/home/jimk/dev/bdrc-fcrepo-client/target/bdrc-fcrepo-client-1.0-SNAPSHOT-Install/bdrc-fcrepo-client-1.0-SNAPSHOT/bdrc-fcrepo-client-1.0-SNAPSHOT.jar  -Dlog4j2.configuration=file:/home/jimk/dev/bdrc-fcrepo-client/src/main/resources/log4j2.properties io.bdrc.lib.Main