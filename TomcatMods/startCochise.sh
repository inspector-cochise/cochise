#!/bin/sh

KEYSTORE="./conf/.keystore"
STOREPASS="changeit"
KEYPASS="changeit"
CERTPARAMS="CN=*,OU=Cochise,O=Unknown,L=Unknown,ST=Unknown,C=Unknown"

if [ ! -e $KEYSTORE ]
then
	keytool -genkey -alias tomcat -keyalg RSA -keystore $KEYSTORE -dname $CERTPARAMS -storepass $STOREPASS -keypass $KEYPASS
fi

PW1="1"
PW2="2"
while [ $PW1 != $PW2 ]
do
	echo -n "Please choose a password to protect cochise from unsolicited use: "
	stty -echo
	read PW1
	stty echo
	echo ""

	echo -n "Please reenter the password: "
	stty -echo
	read PW2
	stty echo
	echo ""

	if [ $PW1 != $PW2 ]
	then
		echo "Your passwords were not equal..."
	fi
done

if [ $1 = "debug" ]
then
	export JAVA_OPTS="-DcochisePass=$PW1 -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y"
else
	export JAVA_OPTS="-DcochisePass=$PW1"
fi

cd bin
./startup.sh

