mv Servicio.java Servicio/negocio/
cd Servicio/

javac -cp $CATALINA_HOME/lib/javax.ws.rs-api-2.0.1.jar:$CATALINA_HOME/lib/gson-2.3.1.jar:. negocio/Servicio.java
rm WEB-INF/classes/negocio/*
cp negocio/*.class WEB-INF/classes/negocio/.
jar cvf Servicio.war WEB-INF META-INF
rm ../apache-tomcat-8.5.63/webapps/Servicio.war 
rm -r ../apache-tomcat-8.5.63/webapps/Servicio
cp Servicio.war ../apache-tomcat-8.5.63/webapps/
cd ../apache-tomcat-8.5.63/webapps/
ls

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export CATALINA_HOME=/home/ubuntu/apache-tomcat-8.5.63

sh $CATALINA_HOME/bin/catalina.sh start
sh $CATALINA_HOME/bin/catalina.sh stop


