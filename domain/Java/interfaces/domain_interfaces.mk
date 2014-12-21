#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   DOMAIN_JAVA = the directory location for the java files
#   DOMAIN_CLASSES = the directory location for the class files
#   DOMAIN_REL = the directory location for the jar files
#
#######################################################################

#######################################################################
# Defines specific jars and locations for this build component 
# 
#######################################################################
DOMAIN_INTERFACES_JAVA            =$(DOMAIN_JAVA)/interfaces
DOMAIN_INTERFACES_CLASS           =$(DOMAIN_CLASSES)/interfaces
DOMAIN_INTERFACES_JARNAME         =domain_interfaces
DOMAIN_INTERFACES_JAR             =$(DOMAIN_REL)/$(DOMAIN_INTERFACES_JARNAME).$(JAR_EXT)
DOMAIN_INTERFACES_VERSIONSRC      =$(DOMAIN_INTERFACES_JAVA)/$(DOMAIN_INTERFACES_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    DOMAIN_INTERFACES_BUILD_JAR = $(DOMAIN_INTERFACES_JAR)
else
    DOMAIN_INTERFACES_BUILD_JAR = $(DOMAIN_INTERFACES_CLASS)/$(DOMAIN_INTERFACES_JARNAME).$(JAR_EXT)
endif
