#!/bin/bash
# $1 should be the path to the file (enclosing directory)
# $2 should be the name of the file (filename only, no path)

#check owning group is root and permissions are set to something like ******---
if ls -l $1 | grep -w $2 | awk -f QfileSafe.awk
then
	#now check the owning user is a member of the group root
	owningUser=`ls -l $1 | grep -w $2 | awk '{print $3}'`
	if cat /etc/group | grep ^root | awk 'BEGIN {FS=":"} {print $4;}' | awk 'BEGIN {RS=","} {print $0;}' | grep $owningUser >& /dev/null
	then
		echo -e "OK   \t\tYour main file seems to be safe."
		exit 0
	fi
fi

echo -e "NOTOK\t\tUsers not in the group \"root\" may have some access to your file."
exit 1
