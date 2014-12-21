#!/bin/ksh

if [ -z $RUN_DIR ] ; then
    HERE_=`pwd`
    cd
    . ./.profile >/dev/null 2>&1
    cd $HERE_
    unset HERE_
fi

cd $RUN_DIR
pwadmin -request showCurrentList
