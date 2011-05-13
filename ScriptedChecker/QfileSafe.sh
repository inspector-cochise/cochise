#!/bin/bash
# $1 should be the file (including path to it)
# $2 (optional) permissions pattern
pat=".......---"
if test $# -gt 1
then
	pat=$2
fi

if stat -L -c "%A %U" $1 | awk -v PATTERN=$pat -f QfileSafe.awk
then
	echo -e "OK   \t\tYour file seems to be safe."
	exit 0
fi

echo -e "NOTOK\t\tOther Users than \"root\" may have some access to your file."
exit 1
