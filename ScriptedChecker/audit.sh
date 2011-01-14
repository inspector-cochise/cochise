#!/bin/bash
#Dieses Skrip soll assistiern Abschnitt 4 des Papers "ISi-Check -- Sicheres Bereitstellen von Web-Angeboten mit Apache" vom BSI zu checken

#Abschnitt 4.1 Betriebssystem
#-------------

frage1()
{
	if [ "$highSecurity" = "y" ]
	then
		if awk 'BEGIN { if($2 != "L") exit 1 }' -- `passwd -S root`
		then
			echo -e "NOTOK\t\tuser root is not deactivated"
			problems=`expr $problems + 1`
			frage1=0
		else
			echo -e "OK   \t\tuser root correctly deactivated" 
			frage1=1
		fi
	else
		echo -e "OK   \t\tquestion skipped (no high security level requested)"
	fi
}

#Abschnitt 4.2 Apache Web-Server
#-------------

#Software:

frage2()
{
	echo -e "MAN    \t\tPlease check for new patches for your apache."
	echo -e "       \t\tIs your apache well patched (0/1)?"
}


frage5()
{
	echo -e "||   \t\tOnly one possible configuration file is allowed."
	echo -e "vv   \t\tChecking subquestions:"
	frage5a
	frage5b

	if test $frage5a = 1 -a $frage5b = 1
	then
		echo -e "OK   \t\tThe file" $mainConfFile "is your only possible configuration file."
		frage5=1
	else
		echo -e "NOTOK\t\tThere is more than one possible configuration file."
		frage5=0
		problems=`expr $problems + 1`
	fi
}

frage6()
{
	apache2 -t >& /dev/null
	if [ $? != 0 ]
	then
		echo -e "NOTOK\t\tsyntax problems detected"
		problems=`expr $problems + 1`
		frage6=0
		echo -e "     \t\tWanna see more information (0/1)?" tmpVar
		read tmpVar
		if [ $tmpVar = 1 ]
		then
			apache2 -t
			echo -e ""
		fi
		echo -e "Due to these syntax problems the following test are not reliable anymore."
		echo -e "I will stop now. Please correct these syntax problems."
		return 1
	else
		echo -e "OK   \t\tno syntax problems in config file detected"
		frage6=1
	fi
}




#================================
#Erste Anweisungen
#================================


if [ `whoami` != "root" ]
then
	echo error: this script has to be executed by root
	return 1
fi


#================================
#grundlegende Informationen einsammeln
#================================

if [ "$firstTime" != "no" ]
then
	echo "First of all I will need some information."
	echo "Is a high level of security required (y/n)?"
	read highSecurity
	
	echo "Is a high level of secrecy required (y/n)?"
	read highSecrecy
	
	echo "What is the main configuration file for your apache server? (usally something like /etc/apache2/httpd.conf or /etc/apache2/apache2.conf)"
	read mainConfFile
fi

firstTime="no"
problems=0

#================================
#ab hier geht's richtig los
#================================

echo -e "\nLet's start!"
echo "entering section 4"
echo -e "\tentering subsection 4.1"
frage1
echo -e "\tentering subsection 4.2"
frage2
frage6

echo "I'm finished and I detected" $problems "problems."


