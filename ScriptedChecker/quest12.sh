#!/bin/bash
# $1 should be the user
# $2 (optional) should be the group


if [ $1 = "root" ]
then
	echo root has too many rights
	exit 1
fi

#does $1 exist?
if passwd -S $1 >& /dev/null
then
	false
else
	echo User $1 does not exist.
	exit 1
fi

if test $# -gt 1
then
	groupName=$2
	group=`cat /etc/group | grep -w ^$groupName | awk 'BEGIN {FS=":"} {print $3;}'`
else
	group=`cat /etc/passwd |  grep -w ^$1 | awk 'BEGIN {FS=":"} {print $4;}'`
	groupName=`cat /etc/group | grep :$group: | awk 'BEGIN {FS=":"} {print $1;}'`
fi

#first check $1 is the only member of $group
nmbrOfMembers=`cat /etc/group | grep :$group: | awk 'BEGIN {FS=":"} {print $4;}' | awk 'BEGIN {RS=","} {print $0;}' | wc -w`

#is $1 a member of $group?
if cat /etc/group | grep :$group: | awk 'BEGIN {FS=":"} {print $4;}' | awk 'BEGIN {RS=","} {print $0;}' | grep $1 >& /dev/null
then
	#is $1 the only member of $group
	if test $nmbrOfMembers -ne 1
	then
		echo $1 is not the only member of $groupName.
		exit 1
	fi
	
	#is $1 member of any other group?
	if cat /etc/group | grep -v :$group: | awk 'BEGIN {FS=":"} {print $4;}' | awk 'BEGIN {RS=","} {print $0;}' | grep $1 >& /dev/null
	then
		echo $1 is member of another group than only $groupName.
		exit 1
	fi
	
	echo Seems like $1 is the only member of $groupName and not member of any other group.
	
#is there any member in $group
elif test $nmbrOfMembers -eq 0
then
	echo $groupName is empty and $1 is not a member of any other group.
else
	echo $1 is not member of $groupName but there are other members in $groupName.
	exit 1
fi

#is $1 locked?
#if passwd -S $1 | awk '{if($2 ~ /L/) {exit 1} else {exit 0} }'
#then
#	echo User $1 is not locked. A login would may be possible.
#	exit 1
#fi

if [ `cat /etc/passwd |  grep -w ^$1 | awk 'BEGIN {FS=":"} {print $7;}'` != "/bin/false" ]
then
	echo User $1 has a login-shell different from /bin/false. It may be possible to login as $1 .
	exit 1
fi

exit 0