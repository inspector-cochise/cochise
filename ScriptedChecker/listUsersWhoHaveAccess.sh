#!/bin/sh
# list all users who have some access to $1
# if $1 is a directory the list includes all users who have some access to one of it's children
#
# access means: read, write, execute, ownership, member of group whith group-ownership

list_for_file()
{
	USER=`ls -dl $1 | awk '{print $3}'`
	GROUP=`ls -dl $1 | awk '{print $4}'`
	GID=`grep ^$GROUP: /etc/group | awk -F: '{print $3}'`
	
	if ls -dl $1 | awk '/......---/ {exit 1}'
	then
		echo everybody has access
		return 1
	fi
	
	#group members from /etc/group
	MEMBERS1=`grep ^$GROUP: /etc/group | awk -F: '{print $4}' | awk -v RS=, '{print $1}' | grep -v ^$`
	#group members from /etc/passwd
	MEMBERS2=`grep .*:.*:.*:$GID: /etc/passwd | awk -F: '{print $1}'`
	
	if [ -z "$MEMBERS1" -a -z "$MEMBERS2" ]
	then
		echo "root\n$USER" | sort | uniq
	elif [ -z "$MEMBERS1" ]
	then
		echo "root\n$USER\n$MEMBERS2" | sort | uniq
	elif [ -z "$MEMBERS2" ]
	then
		echo "root\n$USER\n$MEMBERS1" | sort | uniq
	else	
		echo "root\n$USER\n$MEMBERS1\n$MEMBERS2" | sort | uniq
	fi
}


TEMPFILE=`mktemp`

for filename in `find $1`
do
	list_for_file $filename >> $TEMPFILE
	if [ $? = 1 ]
	then
		rm $TEMPFILE
		echo everybody has access
		exit 1
	fi
done

sort $TEMPFILE | uniq
rm $TEMPFILE
