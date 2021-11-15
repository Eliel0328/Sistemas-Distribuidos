sudo vi /etc/rc.local

#!/bin/bash
runuser -l ubuntu -c 'export JAVA_HOME=/usr;export CATALINA_HOME=/home/ubuntu/apache-tomcat-8.5.72;sh $CATALINA_HOME/bin/catalina.sh start'
exit 0

sudo chmod +x /etc/rc.local