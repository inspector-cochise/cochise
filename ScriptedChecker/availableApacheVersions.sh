#!/bin/sh
FILE=/tmp/cochise_available.html
MIRROR=https://www.apache.org/dist/httpd/
wget -q -O $FILE $MIRROR
grep -oe \"httpd-.*\.tar\.gz\.sha1\" $FILE | sed {s/\"//g} | grep -oe "httpd-[[:digit:]]\.[[:digit:]]\.[[:digit:]]*" | grep -oe "[[:digit:]]\.[[:digit:]]\.[[:digit:]]*" | sort | uniq

