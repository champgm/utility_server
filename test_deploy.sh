#!/usr/bin/env bash

#!/usr/bin/env bash
export JAVA_HOME=`/usr/libexec/java_home -v '1.8*'`
sh ~/tomcat/libexec/bin/catalina.sh stop || true
mvn -U clean install
rm -rf ~/tomcat/libexec/webapps/server-war-1.0-SNAPSHOT
cp server-war/target/server-war-1.0-SNAPSHOT.war ~/tomcat/libexec/webapps/
sh ~/tomcat/libexec/bin/catalina.sh start

while [ ! -f ~/tomcat/libexec/webapps/server-war-1.0-SNAPSHOT/index.html ]
do
  echo "Waiting for Tomcat to deploy WAR"
  sleep 2
done

touch ~/tomcat/libexec/webapps/server-war-1.0-SNAPSHOT/deploy_done