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
CFIX_INTERFACES_JAVA        =$(CFIX_JAVA)/interfaces
CFIX_INTERFACES_CLASS       =$(CFIX_CLASSES)/interfaces
CFIX_INTERFACES_JARNAME     =cfix_interfaces
CFIX_INTERFACES_JAR         =$(CFIX_REL)/$(CFIX_INTERFACES_JARNAME).$(JAR_EXT)
CFIX_INTERFACES_VERSIONSRC  =$(CFIX_INTERFACES_JAVA)/$(CFIX_INTERFACES_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CFIX_INTERFACES_BUILD_JAR = $(CFIX_INTERFACES_JAR)
else
    CFIX_INTERFACES_BUILD_JAR = $(CFIX_INTERFACES_CLASS)/$(CFIX_INTERFACES_JARNAME).$(JAR_EXT)
endif
