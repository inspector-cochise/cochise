#!/bin/bash

exit_Ubuntu=10
exit_SUSE=20
exit_RedHat=30
exit_Debian=40

if cat /etc/issue | grep -i Ubuntu >& /dev/null
then
	echo Ubuntu
	exit $exit_Ubuntu
fi

if cat /etc/issue | grep -i Debian >& /dev/null
then
	echo Debian
	exit $exit_Debian
fi

if cat /etc/issue | grep -i SUSE >& /dev/null
then
	echo SUSE
	exit $exit_SUSE
fi

if cat /etc/issue | grep -i "Red Hat" >& /dev/null
then
	echo Red Hat
	exit $exit_RedHat
fi

echo unknown
exit 0
