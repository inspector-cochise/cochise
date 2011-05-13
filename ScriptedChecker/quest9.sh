#!/bin/bash
# $1 should be the ServerRoot directory
# $2 should be the user under which the apache is started
# $3 should be the permission-pattern (i. e. something like ...------)

#check permissions are set to something matching $3 and the owning user is $2
if stat -L --printf="%n %A %U\n" `find -L $1` | grep -v $1/htdocs | awk -v USER=$2 -v PERM_PAT=$3 -f q9.awk
then
		#echo -e "OK"
		exit 0
fi

#echo -e "NOTOK"
exit 1
