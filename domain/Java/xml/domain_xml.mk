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
DOMAIN_XML_JAVA            =$(DOMAIN_JAVA)/xml
DOMAIN_XML_CLASS           =$(DOMAIN_CLASSES)/xml
DOMAIN_XML_JARNAME         =domain_xml
DOMAIN_XML_JAR             =$(DOMAIN_REL)/$(DOMAIN_XML_JARNAME).$(JAR_EXT)
DOMAIN_XML_VERSIONSRC      =$(DOMAIN_XML_JAVA)/$(DOMAIN_XML_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    DOMAIN_XML_BUILD_JAR = $(DOMAIN_XML_JAR)
else
    DOMAIN_XML_BUILD_JAR = $(DOMAIN_XML_CLASS)/$(DOMAIN_XML_JARNAME).$(JAR_EXT)
endif
