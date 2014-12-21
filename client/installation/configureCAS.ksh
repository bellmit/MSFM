#!/bin/ksh
# Usage: ksh configureCAS.ksh [args]          # normal use
# Usage: DEBUG=1 ksh configureCAS.ksh [args]  # for testing

# -------------------- Constants --------------------

PROG=$( basename $0 )
NOW=$( date +%Y%m%d-%H%M%S )
TMPFILE=/tmp/configureCAS.$$.tmp
SSCONFIG_FILE=cboe.ssconfig.cm

# When debugging, don't execute destructive commands
GUARD=${DEBUG:+echo GUARD:} # if DEBUG is set, GUARD is "echo GUARD:"

# Error codes from /usr/include/sys/errno.h
ENOENT=2    # No such file or directory
EINVAL=22   # Invalid argument

# -------------------- Subroutines --------------------

usage()
{
    print 'Usage: '$PROG' -h | [-e environment] [-c clientType] [-l "fixEngineList"]'
}

# @param 1 error code for exit
# @param 2 message to report
fail()
{
    print -u2 "$PROG: $2"
    exit $1
}

# @param 1 variable to set
# @param 2 file to scan
# @param 3 variable to look for in input
getExport()
{
    eval $1="'"$( grep <$2 "^export $3=" | cut -d= -f2)"'"
}

# @param 1 file to change
# @param 2 old string to modify
# @param 3 new string to use as replacement
editFile()
{
    if [ -z "$2" ] ; then
        fail $EINVAL "Failed to edit $1, old string empty, new string [$3]"
    fi
    sed <$1 "s#$2#$3#g" >$TMPFILE
    mv $TMPFILE $1
}

# @param 1 variable to set
# @param 2 file to edit
# @param 3 new value
setExport()
{
    sed <$2 "s#^export $1=.*#export $1=$3#" >$TMPFILE
    mv $TMPFILE $2
}

# @param1 Name of original file
# @param2 Name of modified file
showDiffs()
{
    typeset ORIGINAL=$1
    typeset CHANGED=$2
    if ! diff $ORIGINAL $CHANGED >$TMPFILE ; then
        # 1 (false) means differences were found
        echo ".... $ORIGINAL ...."
        grep '^<' $TMPFILE
        cat <<_DASH_
----
_DASH_
        grep '^>' $TMPFILE
    fi
    rm $TMPFILE
}

# -------------------- Main Program --------------------

unalias rm

##########
# Read command line -- some new settings
##########

while getopts :he:r:c:l: flag; do
    case $flag in
    h)
        NEED_HELP=1
        ;;
    e)
        if [ -z "$OPTARG" ] || [[ "$OPTARG" = -* ]] ; then
            fail $EINVAL "Missing argument to -$flag switch"
        fi
        if [ -n "$SBT_ENVIRONMENT_NEW" ] ; then
            fail $EINVAL "Multiple -$flag switches"
        fi
        SBT_ENVIRONMENT_NEW="$OPTARG"
        ;;
    c)
        if [ -z "$OPTARG" ] || [[ "$OPTARG" = -* ]] ; then
            fail $EINVAL "Missing argument to -$flag switch"
        fi
        if [ -n "$CLIENT_TYPE_NEW" ] ; then
            fail $EINVAL "Multiple -$flag switches"
        fi
        CLIENT_TYPE_NEW="$OPTARG"
        ;;
    l)
        if [ -z "$OPTARG" ] || [[ "$OPTARG" = -* ]] ; then
            fail $EINVAL "Missing argument to -$flag switch"
        fi
        if [ -n "$ENGINE_LIST_NEW" ] ; then
            fail $EINVAL "Multiple -$flag switches"
        fi
        ENGINE_LIST_NEW="$OPTARG"
        ;;
    *)
        fail $EINVAL "Unknown switch $OPTARG"
        ;;
    esac
done

##########
# Determine INFRA_HOME and set up pathnames to configuration files
##########

# Force INFRA_HOME_NEW to end in "/infra": remove /infra suffix if any,
# and always add /infra
INFRA_HOME_NEW=${HOME%/infra}/infra

SET_CONTEXT_FILE=$INFRA_HOME_NEW/config/bin/setContext
if [ ! -f $SET_CONTEXT_FILE ] ; then
    echo "File $SET_CONTEXT_FILE not found"
    INFRA_HOME_NEW=$HOME
    SET_CONTEXT_FILE=$INFRA_HOME_NEW/config/bin/setContext
    if [ ! -f $SET_CONTEXT_FILE ] ; then
        fail $ENOENT "$PROG: File $SET_CONTEXT_FILE not found, quitting"
    fi
    echo "Using file $SET_CONTEXT_FILE instead"
fi

CONFIG_DIR=${INFRA_HOME_NEW%/config}/config
REPOSITORY_SERVICE_FILE=$CONFIG_DIR/properties/xml/RepositoryServiceLocal.xml
CONFIG_SSCONFIG_FILE=$CONFIG_DIR/properties/$SSCONFIG_FILE

##########
# Get current ("old") settings
##########

getExport INFRA_HOME_OLD      $SET_CONTEXT_FILE  INFRA_HOME
getExport RUN_DIR_LIST_OLD    $SET_CONTEXT_FILE  RUN_DIR_LIST
getExport ENGINE_LIST_OLD     $SET_CONTEXT_FILE  ENGINE_LIST
getExport SBT_ENVIRONMENT_OLD $SET_CONTEXT_FILE  SBT_ENVIRONMENT
getExport CLIENT_TYPE_OLD     $SET_CONTEXT_FILE  CLIENT_TYPE

# Extract Repository Server Name out of XML
REPOSITORY_SERVICE_NAME_OLD=$( \
    grep "RepositoryService name" <$REPOSITORY_SERVICE_FILE | cut -d'"' -f2 )
HOSTNAME_OLD=${REPOSITORY_SERVICE_NAME_OLD#$SBT_PREFIX}

if [ -n "$NEED_HELP" ] ; then
    usage
    print "Current settings: -e $SBT_ENVIRONMENT_OLD -c $CLIENT_TYPE_OLD -l $ENGINE_LIST_OLD"
    exit 0
fi

##########
# Get new settings
##########

# If SBT_ENVIRONMENT_NEW is empty, set it from SBT_ENVIRONMENT_OLD
SBT_ENVIRONMENT_NEW=${SBT_ENVIRONMENT_NEW:-$SBT_ENVIRONMENT_OLD}


CLIENT_TYPE_NEW=${CLIENT_TYPE_NEW:-$CLIENT_TYPE_OLD}
case $CLIENT_TYPE_NEW in
cas|fixcas|sacas|mdcas|cfix|ipdcas)
    # Input is exactly correct, keep it as is
    ;;
*)
    fail $EINVAL "Unrecognized client type: $CLIENT_TYPE_NEW"
    ;;
esac


ENGINE_LIST_NEW=${ENGINE_LIST_NEW:-$ENGINE_LIST_OLD}
# Remove any " characters from ENGINE_LIST_NEW
ENGINE_LIST_NEW=$( echo $ENGINE_LIST_NEW | tr -d \" )
if [ "$CLIENT_TYPE_NEW" == "fixcas" ] && [ -z "$ENGINE_LIST_NEW" ] ; then
    fail $EINVAL "Missing engine list for fixcas"
fi


# RUN_DIR_LIST is empty for fixcas, list of installed engines for other clients
RUN_DIR_LIST_NEW=
cd $INFRA_HOME_NEW
for engine_id in v2mdcas[0-9][0-9] v2cfix[0-9][0-9] v2cas[0-9][0-9] \
                 v2sacas[0-9][0-9] v2ipdcas[0-9][0-9]
do
    if [ -L $engine_id ] ; then
        RUN_DIR_LIST_NEW=$RUN_DIR_LIST_NEW' $INFRA_HOME/'$engine_id
    fi
done
if [ -n "$RUN_DIR_LIST_NEW" ] ; then
    # If not empty, remove leading space and enclose in "double quotes"
    RUN_DIR_LIST_NEW='"'${RUN_DIR_LIST_NEW# }'"'
fi


HOSTNAME_NEW=$( hostname )
REPOSITORY_SERVICE_NAME_NEW=${SBT_PREFIX}${HOSTNAME_NEW}


if [ -n "$DEBUG" ] ; then
    echo "DEBUG: Old and new values"
    echo "DEBUG: INFRA_HOME [$INFRA_HOME_OLD] -> [$INFRA_HOME_NEW]"
    echo "DEBUG: SBT_ENVIRONMENT [$SBT_ENVIRONMENT_OLD] -> [$SBT_ENVIRONMENT_NEW]"
    echo "DEBUG: RUN_DIR_LIST [$RUN_DIR_LIST_OLD] -> [$RUN_DIR_LIST_NEW]"
    echo "DEBUG: CLIENT_TYPE [$CLIENT_TYPE_OLD] -> [$CLIENT_TYPE_NEW]"
    echo "DEBUG: ENGINE_LIST [$ENGINE_LIST_OLD] -> [$ENGINE_LIST_NEW]"
    echo "DEBUG: REPOSITORY_SERVICE_NAME [$REPOSITORY_SERVICE_NAME_OLD] -> [$REPOSITORY_SERVICE_NAME_NEW]"
    echo "DEBUG: HOSTNAME [$HOSTNAME_OLD] -> [$HOSTNAME_NEW]"
fi

##########
# Modify setContext file
##########

if [ -n "$DEBUG" ] ; then
    REAL_SET_CONTEXT_FILE=$SET_CONTEXT_FILE
    SET_CONTEXT_FILE=/tmp/setcontext.$$.txt
    cp $REAL_SET_CONTEXT_FILE $SET_CONTEXT_FILE
fi

chmod +w $SET_CONTEXT_FILE
if [[ $CLIENT_TYPE_NEW = @(fixcas|cfix) ]] ; then
    setExport ENGINE_LIST $SET_CONTEXT_FILE "\"$ENGINE_LIST_NEW\""
fi
setExport INFRA_HOME      $SET_CONTEXT_FILE "$INFRA_HOME_NEW"
setExport SBT_ENVIRONMENT $SET_CONTEXT_FILE "$SBT_ENVIRONMENT_NEW"
setExport CLIENT_TYPE     $SET_CONTEXT_FILE "$CLIENT_TYPE_NEW"
setExport RUN_DIR_LIST    $SET_CONTEXT_FILE "$RUN_DIR_LIST_NEW"

if [ -n "$DEBUG" ] ; then
    showDiffs $REAL_SET_CONTEXT_FILE $SET_CONTEXT_FILE
    rm $SET_CONTEXT_FILE
    SET_CONTEXT_FILE=$REAL_SET_CONTEXT_FILE
fi
chmod -w $SET_CONTEXT_FILE

##########
# Link cboe.ssconfig.cm file from config/properties to run_dir/properties
##########

# To start, there's a file in run_dir/properties and a file,
# a symbolic link, or nothing in config/properties. When we're done, we want a
# file in run_dir/properties and a symbolic link in config/properties.

if [ -e $CONFIG_SSCONFIG_FILE ] && [ ! -L $CONFIG_SSCONFIG_FILE ] ; then
    # Real file, rename it
    $GUARD mv $CONFIG_SSCONFIG_FILE $CONFIG_SSCONFIG_FILE.$NOW
fi
if [ ! -e $CONFIG_SSCONFIG_FILE ] ; then
    # Create a symbolic link
    $GUARD ln -s ../../run_dir/properties/$SSCONFIG_FILE $CONFIG_SSCONFIG_FILE
fi


##########
# Modify RepositoryServiceLocal.xml file
##########

if [ -n "$DEBUG" ] ; then
    REAL_REPOSITORY_SERVICE_FILE=$REPOSITORY_SERVICE_FILE
    REPOSITORY_SERVICE_FILE=/tmp/$( basename $REPOSITORY_SERVICE_FILE ).$$.tmp
    cat $REAL_REPOSITORY_SERVICE_FILE >$REPOSITORY_SERVICE_FILE
fi

chmod +w $REPOSITORY_SERVICE_FILE

editFile $REPOSITORY_SERVICE_FILE "$REPOSITORY_SERVICE_NAME_OLD" \
                                  "$REPOSITORY_SERVICE_NAME_NEW"

chmod -w $REPOSITORY_SERVICE_FILE

if [ -n "$DEBUG" ] ; then
    showDiffs $REAL_REPOSITORY_SERVICE_FILE $REPOSITORY_SERVICE_FILE
    rm -f $REPOSITORY_SERVICE_FILE
fi

##########
# Set up $RUN_DIR/ior/initrefs.ior
##########

if [[ $CLIENT_TYPE_NEW = !(mdcas|cfix) ]] ; then
    # Do this for all but mdcas and cfix (Infra group takes care of those)
    RUN_DIR_INITREFS=$INFRA_HOME_NEW/run_dir/ior/initrefs.ior
    CONFIG_INITREFS=$INFRA_HOME_NEW/config/ior/initrefs.ior
    if [ -L $RUN_DIR_INITREFS ] ; then
        # Remove existing symbolic link; it may be obsolete
        rm $RUN_DIR_INITREFS
    fi
    if [ -e $RUN_DIR_INITREFS ] ; then
        # If there's an actual file, rename it out of the way
        mv $RUN_DIR_INITREFS $RUN_DIR_INITREFS.$NOW
    fi
    # Create the correct symbolic link
    ln -s $CONFIG_INITREFS $RUN_DIR_INITREFS
fi

##########
# Configure files in $RUN_DIR/properties
##########

if [ -z "$DEBUG" ] ; then
    configProps
fi

echo "***** Changes are done. *****"
