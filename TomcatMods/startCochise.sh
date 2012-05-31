#!/bin/sh

#check if java is existent
java > /dev/null 2>&1
if [ $? != 0 ]
then
	echo "Seems like there is no jre (i.e. java command) in your path. Please put a java command (at least version 1.6) in your path."
	exit 1
fi

JAVA_VERSION=`java -version 2>&1 | head -n 1`
#check if java is at least version 6
if [ 6 -gt `echo $JAVA_VERSION | grep -o "[[:digit:]]\.[[:digit:]]*" | grep -o "[[:digit:]]*$"` ]
then
	echo "Seems like your jre is to old for cochise. You have " $JAVA_VERSION " . You will need at least version 1.6 ."
	exit 1
fi


KEYSTORE="./conf/.keystore"
STOREPASS="changeit"
KEYPASS="changeit"
CERTPARAMS="CN=*, OU=Cochise, O=Unknown, L=Unknown, ST=Unknown, C=Unknown"

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
		echo "Your passwords have not been equal..."
	fi
done

export JAVA_OPTS="-DcochisePass=$PW1"

cd bin
./startup.sh

