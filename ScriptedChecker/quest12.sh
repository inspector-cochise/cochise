#!/bin/bash
# $1 should be the user
# $2 should be the group

#does $1 exist?
if passwd -S $1 >& /dev/null
then
	echo User $1 does not exist.
	exit 1
fi

#is $1 locked?
if passwd -S $1 | awk '{if $2 ~ /L/ {exit 1} else {exit 0} }'
then
	echo User $1 is not locked. A login would may be possible.
	exit 1
fi

#first check $1 is the only member of $2
nmbrOfMembers=`cat /etc/group | grep ^$2 | awk 'BEGIN {FS=":"} {print $4;}' | awk 'BEGIN {RS=","} {print $0;}' | wc -w`

#is $1 a member of $2?
if cat /etc/group | grep ^$2 | awk 'BEGIN {FS=":"} {print $4;}' | awk 'BEGIN {RS=","} {print $0;}' | grep $1 >& /dev/null
then
	#is $1 the only member of $2
	if test $nmbrOfMembers -ne 1
	then
		echo $1 is not the only member of $2.
		exit 1
	fi
	
	#is $1 member of any other group?
	if cat /etc/group | grep -v ^$2 | awk 'BEGIN {FS=":"} {print $4;}' | awk 'BEGIN {RS=","} {print $0;}' | grep $1
	then
		echo $1 is member of another group than only $2.
		exit 1
	fi
	
	echo Seems like $1 is the only member of $2 and not member of any other group.
	
#is there any member in $2
elif test $nmbrOfMembers -eq 0
then
	echo $2 is empty and $1 is not a member of any other group.
else
	echo $1 is not member of $2 but there are other members in $2.
	exit 1
fi

exit 0