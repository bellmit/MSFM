#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   EVENT = the root directory location for the release component
#
#######################################################################

#######################################################################
# Defines specific jars and locations for this build component 
# 
#######################################################################
EVENT_INTERFACES_JAVA            =$(EVENT_JAVA)/interfaces
EVENT_INTERFACES_CLASS           =$(EVENT_CLASSES)/interfaces
EVENT_INTERFACES_JARNAME         =event_interfaces
EVENT_INTERFACES_JAR             =$(EVENT_REL)/$(EVENT_INTERFACES_JARNAME).$(JAR_EXT)
EVENT_INTERFACES_VERSIONSRC      =$(EVENT_INTERFACES_JAVA)/$(EVENT_INTERFACES_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    EVENT_INTERFACES_BUILD_JAR = $(EVENT_INTERFACES_JAR)
else
    EVENT_INTERFACES_BUILD_JAR = $(EVENT_INTERFACES_CLASS)/$(EVENT_INTERFACES_JARNAME).$(JAR_EXT)
endif
