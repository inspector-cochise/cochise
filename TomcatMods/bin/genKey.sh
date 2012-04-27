#!/bin/sh

keytool -genkey -alias tomcat -keyalg RSA -keystore ../conf/.keystore -dname "CN=*, OU=Cochise, O=Unknown, L=Unknown, ST=Unknown, C=Unknown" -storepass changeit -keypass changeit

