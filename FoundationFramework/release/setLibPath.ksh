VOB=/vobs/dte

#############################################################
# Set the LD_LIBRARY_PATH
#############################################################
LD_LIBRARY_PATH=$RTHOME/lib/sun4_solaris
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$JAVA_HOME/jre/lib/sparc
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/java/lib
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/tools/jdbc/lib
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/lib
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$ORBIXWEB_HOME/bin
if [ -a $VOB/ace_tools/release/solaris_2_51_sparc_sw42/dynlib ]; then
    LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$VOB/ace_tools/release/solaris_2_51_sparc_sw42/dynlib
else
    LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$VOB/ace_tools/release/solaris_2_51_sparc_sw42_nopt_trace/dynlib
fi
if [ -a $VOB/MessagingSystem/release/solaris_2_51_sparc_sw42/dynlib ]; then
    LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$VOB/MessagingSystem/release/solaris_2_51_sparc_sw42/dynlib
else
    LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$VOB/MessagingSystem/release/solaris_2_51_sparc_sw42_nopt_trace/dynlib
fi

if [ -a $VOB/javautil/release/solaris_2_51_sparc_sw42/dynlib ]; then
    LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$VOB/javautil/release/solaris_2_51_sparc_sw42/dynlib
else
    LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$VOB/javautil/release/solaris_2_51_sparc_sw42_nopt_trace/dynlib
fi
#LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$VOB/tools/release/solaris_2_51_sparc_sw42/dynlib
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$VOB/common/release/solaris_2_51_sparc_sw42/dynlib
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$VOB/tools/ospace/solaris_sw4x_v21/lib

export LD_LIBRARY_PATH
