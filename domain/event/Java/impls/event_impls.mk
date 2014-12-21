#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   EVENT_JAVA = the directory location for the java files
#   EVENT_CLASSES = the directory location for the class files
#   EVENT_REL = the directory location for the jar files
#
#######################################################################

#######################################################################
# Defines specific jars and locations for this build component 
# 
#######################################################################
EVENT_IMPLS_JAVA            =$(EVENT_JAVA)/impls
EVENT_IMPLS_CLASS           =$(EVENT_CLASSES)/impls
EVENT_IMPLS_JARNAME         =event_impls
EVENT_IMPLS_JAR             =$(EVENT_REL)/$(EVENT_IMPLS_JARNAME).$(JAR_EXT)
EVENT_IMPLS_VERSIONSRC      =$(EVENT_IMPLS_JAVA)/$(EVENT_IMPLS_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    EVENT_IMPLS_BUILD_JAR = $(EVENT_IMPLS_JAR)
else
    EVENT_IMPLS_BUILD_JAR = $(EVENT_IMPLS_CLASS)/$(EVENT_IMPLS_JARNAME).$(JAR_EXT)
endif
