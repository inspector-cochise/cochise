#!/bin/sh
FILE=/tmp/cochise_available.html
MIRROR=https://www.apache.org/dist/httpd/ 
wget -q -O $FILE $MIRROR
grep -oe \"CURRENT-IS-.*\" $FILE | sed {s/\"//g} | sed {s/CURRENT-IS-//}

