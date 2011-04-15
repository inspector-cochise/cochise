#!/bin/bash
# $1 should be the user
# $2 (optional) should be the group


if [ $1 = "root" ]
then
	echo SHELL1
	exit 1
fi

#does $1 exist?
if passwd -S $1 >& /dev/null
then
	false
else
	echo SHELL2 $1
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
		echo SHELL3 $1 $groupName
		exit 1
	fi
	
	#is $1 member of any other group?
	if cat /etc/group | grep -v :$group: | awk 'BEGIN {FS=":"} {print $4;}' | awk 'BEGIN {RS=","} {print $0;}' | grep $1 >& /dev/null
	then
		echo SHELL4 $1 $groupName
		exit 1
	fi
	
	
#is there any member in $group
elif test $nmbrOfMembers -eq 0
then
# this is ok meesage will be echoed later
else
	echo SHELL6 $1 $groupName
	exit 1
fi

if [ `cat /etc/passwd |  grep -w ^$1 | awk 'BEGIN {FS=":"} {print $7;}'` != "/bin/false" ]
then
	echo SHELL7 $1
	exit 1
fi

echo SHELL5 $1 $groupName
exit 0