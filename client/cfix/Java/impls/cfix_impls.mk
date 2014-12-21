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
CFIX_IMPLS_JAVA        =$(CFIX_JAVA)/impls
CFIX_IMPLS_CLASS       =$(CFIX_CLASSES)/impls
CFIX_IMPLS_JARNAME     =cfix_impls
CFIX_IMPLS_JAR         =$(CFIX_REL)/$(CFIX_IMPLS_JARNAME).$(JAR_EXT)
CFIX_IMPLS_VERSIONSRC  =$(CFIX_IMPLS_JAVA)/$(CFIX_IMPLS_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CFIX_IMPLS_BUILD_JAR = $(CFIX_IMPLS_JAR)
else
    CFIX_IMPLS_BUILD_JAR = $(CFIX_IMPLS_CLASS)/$(CFIX_IMPLS_JARNAME).$(JAR_EXT)
endif
