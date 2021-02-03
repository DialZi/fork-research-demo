# we are extending everything from tomcat:8.0 image ...
FROM tomcat:latest
MAINTAINER hansdampf
COPY /ROOT.war /usr/local/tomcat/webapps/
EXPOSE 8080
