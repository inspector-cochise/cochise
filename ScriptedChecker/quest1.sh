#!/bin/bash

if passwd -S root | awk -f q1.awk
then
	echo -e "OK   \t\tuser root correctly deactivated"
	exit 0
else
 	echo -e "NOTOK\t\ti am not being run with root rights or root is not deactivated (passwd -l root)"
	exit 1
fi
