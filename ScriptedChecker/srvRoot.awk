BEGIN {FS="="}
/.*-D.*HTTPD_ROOT.*/	{print $2}
END {}
