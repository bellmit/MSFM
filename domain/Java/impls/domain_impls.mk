#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   DOMAIN_JAVA = the directory location for the Java files
#   DOMAIN_CLASSES = the directory location for the class files
#   DOMAIN_REL = the directory location for jar files
#
#######################################################################


#######################################################################
# Defines specific jars and locations for this build component 
# 
#######################################################################
DOMAIN_IMPLS_JAVA            =$(DOMAIN_JAVA)/impls
DOMAIN_IMPLS_CLASS           =$(DOMAIN_CLASSES)/impls
DOMAIN_IMPLS_JARNAME         =domain_impls
DOMAIN_IMPLS_JAR             =$(DOMAIN_REL)/$(DOMAIN_IMPLS_JARNAME).$(JAR_EXT)
DOMAIN_IMPLS_VERSIONSRC      =$(DOMAIN_IMPLS_JAVA)/$(DOMAIN_IMPLS_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    DOMAIN_IMPLS_BUILD_JAR = $(DOMAIN_IMPLS_JAR)
else
    DOMAIN_IMPLS_BUILD_JAR = $(DOMAIN_IMPLS_CLASS)/$(DOMAIN_IMPLS_JARNAME).$(JAR_EXT)
endif
