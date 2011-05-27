#!/bin/sh
java > /dev/null 2>&1
if [ $? != 0 ]
then
	echo "Seems like there is no jre (i.e. java command) in your path. Please put a java command (at least version 1.6) in your path."
	exit 1
fi


if [ "$1" = "debug" ]
then
	$1=""
	vmargs="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"
else
	vmargs=""
fi
cols=`tput cols`
lines=`tput lines`
vmargs=`echo $vmargs -DCOLUMNS=$cols -DLINES=$lines`
java $vmargs -jar audit.jar "$@"
