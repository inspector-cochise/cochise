BEGIN {ret = 1}
#        check permissions and owning user
/.*/	{ if( $2 ~ PERM_PAT && $3 ~ USER ) {ret = ret * 1} else { print $1; ret = 0;} };
END {exit 1 - ret}
