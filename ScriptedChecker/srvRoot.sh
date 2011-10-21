#!/bin/sh
# $1 should be the apache httpd executable
# retrieve server root     | remove 1st  | and last quote-character
$1 -V | awk -f srvRoot.awk | sed s/^\"// | sed s/\"$//
