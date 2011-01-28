BEGIN {ret = 1}
#        check permissions   check whether the owner group is root (owning user will be checked later)
/.*/ {if( $1 ~ /......---/ && $4 ~ /root/ ) {ret = 0} else {ret = 1}};
END {exit ret}