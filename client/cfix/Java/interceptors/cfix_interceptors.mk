#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   CFIX_JAVA = the directory location for the java files
#   CFIX_CLASSES = the directory location for the class files
#   CFIX_REL = the directory location for the jar files
#
#######################################################################

#######################################################################
# Defines specific jars and locations for this build component 
# 
#######################################################################
CFIX_INTERCEPTORS_JAVA        =$(CFIX_JAVA)/interceptors
CFIX_INTERCEPTORS_CLASS       =$(CFIX_CLASSES)/interceptors
CFIX_INTERCEPTORS_JARNAME     =cfix_interceptors
CFIX_INTERCEPTORS_JAR         =$(CFIX_REL)/$(CFIX_INTERCEPTORS_JARNAME).$(JAR_EXT)
CFIX_INTERCEPTORS_VERSIONSRC  =$(CFIX_INTERCEPTORS_JAVA)/$(CFIX_INTERCEPTORS_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CFIX_INTERCEPTORS_BUILD_JAR = $(CFIX_INTERCEPTORS_JAR)
else
    CFIX_INTERCEPTORS_BUILD_JAR = $(CFIX_INTERCEPTORS_CLASS)/$(CFIX_INTERCEPTORS_JARNAME).$(JAR_EXT)
endif
