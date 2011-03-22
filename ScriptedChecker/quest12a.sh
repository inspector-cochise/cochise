#!/bin/bash

ps -eo "%U %G %p %P %a" | grep $1 | grep -w 1
