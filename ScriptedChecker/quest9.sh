#!/bin/bash
# $1 should be the ServerRoot directory
# $2 should be the user under which the apache is started

#check permissions are set to something like ***------ and owning user is $2
if stat -L --printf="%n %A %U\n" `find -L $1` | grep -v $1/htdocs | awk -v USER=$2 -f q9.awk
then
		#echo -e "OK"
		exit 0
fi

#echo -e "NOTOK"
exit 1
