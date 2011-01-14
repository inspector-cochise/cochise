

if awk 'BEGIN { if($2 != "L") exit 1 }' -- `passwd -S root`
then
	echo -e "NOTOK\t\tuser root is not deactivated"
	exit 1
else
	echo -e "OK   \t\tuser root correctly deactivated"
	exit 0 
fi