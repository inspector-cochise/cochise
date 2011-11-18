#!/bin/sh
$1 -v | grep -oe "/[[:digit:]]\.[[:digit:]]*\.[[:digit:]]*" | sed {s/"\/"//}

