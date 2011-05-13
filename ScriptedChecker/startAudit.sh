#!/bin/sh
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
