BEGIN {ret = 1}
/root/ {if($2 == "L") {ret=0} else {ret=1}}
END {exit ret}
