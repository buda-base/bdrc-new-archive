# Running
From this development directory: (`.../bdrc-new-archive/bdrc-fcrepo-client`)
```shell
# For example, if you git cloned into ~/dev
export BUILD_ROOT=~/dev/bdrc-new-archive
mvn [clean] package
java -cp $BUILD_ROOT/bdrc-fcrepo-client/target/bdrc-fcrepo-client-1.0-SNAPSHOT-Install/bdrc-fcrepo-client-1.0-SNAPSHOT/lib:$BUILD_ROOT/bdrc-fcrepo-client/target/bdrc-fcrepo-client-1.0-SNAPSHOT-Install/bdrc-fcrepo-client-1.0-SNAPSHOT/bdrc-fcrepo-client-1.0-SNAPSHOT.jar  -Dlog4j2.configuration=file:$BUILD_ROOT/bdrc-fcrepo-client/src/main/resources/log4j2.properties io.bdrc.lib.Main
```
