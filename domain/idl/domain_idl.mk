#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   DOMAIN_IDL = the directory location for the idl files
#   DOMAIN_REL = the directory location for the jar files
#
#######################################################################

#######################################################################
# Defines specific jars and locations for this build component 
# 
#######################################################################
DOMAIN_IDL_JAVA            =$(DOMAIN_IDL)/trc_output/java
DOMAIN_IDL_CLASS           =$(DOMAIN_IDL)/trc_output/classes
DOMAIN_IDL_JAVA_JARNAME    =domain_idl_java
DOMAIN_IDL_JARNAME         =domain_idl
DOMAIN_IDL_JAVA_JAR        =$(DOMAIN_REL)/$(DOMAIN_IDL_JAVA_JARNAME).$(JAR_EXT)
DOMAIN_IDL_JAR             =$(DOMAIN_REL)/$(DOMAIN_IDL_JARNAME).$(JAR_EXT)
DOMAIN_IDL_VERSIONSRC      =$(DOMAIN_IDL)/$(DOMAIN_IDL_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    DOMAIN_IDL_JAVA_BUILD_JAR = $(DOMAIN_JAVA_IDL_JAR)
    DOMAIN_IDL_BUILD_JAR      = $(DOMAIN_IDL_JAR)
else
    DOMAIN_IDL_JAVA_BUILD_JAR = $(DOMAIN_IDL_CLASS)/$(DOMAIN_IDL_JAVA_JARNAME).$(JAR_EXT)
    DOMAIN_IDL_BUILD_JAR      = $(DOMAIN_IDL_CLASS)/$(DOMAIN_IDL_JARNAME).$(JAR_EXT)
endif


#######################################################################
# Defines the idl files to build
# 
#######################################################################

DOMAIN_IDL_FILES= \
            exchange.ida \
            constants.ida \
            firm.ida \
            marketDataSummary.ida \
            marketData.ida \
            orderBook.ida \
            product.ida \
            property.ida \
            quote.ida \
            session.ida \
            terminalActivity.ida \
            textMessage.ida \
            trade.ida \
            TradingProperty.ida \
            user.ida \
            order.ida \
            alert.ida \
            Util.ida \
            groupElement.ida \
            sweepAutoLink.ida


