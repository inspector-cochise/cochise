#!/bin/sh
#echoes the ctime of $1 in unix-time (i.e. seconds since 1/1/1970 00:00:00)
date --date="`stat --printf=%z $1`" +%s
