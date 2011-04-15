#!/bin/bash
# $1 should be the file (including path to it)
# $2 (optional) permissions pattern
pat=".......---"
if test $# -gt 1
then
	pat=$2
fi

#check owning group is root and permissions are set to something like ******---
if stat -L -c "%A %G" $1 | awk -v PATTERN=$pat -f QfileSafe.awk
then
	#now check the owning user is a member of the group root
	owningUser=`stat -L -c "%U" $1`
	
	#is $owningUser a member of root?
	if cat /etc/group | grep ^root | awk 'BEGIN {FS=":"} {print $4;}' | awk 'BEGIN {RS=","} {print $0;}' | grep $owningUser >& /dev/null
	then
		echo -e "OK   \t\tYour main file seems to be safe."
		exit 0
	fi
fi

echo -e "NOTOK\t\tUsers not in the group \"root\" may have some access to your file."
exit 1
