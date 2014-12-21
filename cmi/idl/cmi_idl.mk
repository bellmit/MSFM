#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   CMI_IDL = the directory location for the idl files
#   CMI_REL = the directory location for the jar files
#
#######################################################################

#######################################################################
# Defines specific jars and locations for this build component 
# 
#######################################################################
CMI_IDL_JAVA            =$(CMI_IDL)/trc_output/java
CMI_IDL_CLASS           =$(CMI_IDL)/trc_output/classes
CMI_IDL_JAVA_JARNAME    =cmi_idl_java
CMI_IDL_JARNAME         =cmi_idl
CMI_IDL_JAVA_JAR        =$(CMI_REL)/$(CMI_IDL_JAVA_JARNAME).$(JAR_EXT)
CMI_IDL_JAR             =$(CMI_REL)/$(CMI_IDL_JARNAME).$(JAR_EXT)
CMI_IDL_VERSIONSRC      =$(CMI_IDL)/$(CMI_IDL_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    CMI_IDL_JAVA_BUILD_JAR = $(CMI_IDL_JAVA_JAR)
    CMI_IDL_BUILD_JAR      = $(CMI_IDL_JAR)
else
    CMI_IDL_JAVA_BUILD_JAR = $(CMI_IDL_CLASS)/$(CMI_IDL_JAVA_JARNAME).$(JAR_EXT)
    CMI_IDL_BUILD_JAR      = $(CMI_IDL_CLASS)/$(CMI_IDL_JARNAME).$(JAR_EXT)
endif


#######################################################################
# Defines the idl files to build
# 
#######################################################################

CMI_IDL_FILES= \
	    cmiAdmin.ida \
	    cmiCallback.ida \
	    cmiCallbackV2.ida \
	    cmiCallbackV3.ida \
	    cmiCallbackV4.ida \
	    cmiConstants.ida \
	    cmiMarketData.ida \
	    cmiOrder.ida \
	    cmiProduct.ida \
	    cmiQuote.ida \
	    cmiSession.ida \
	    cmiErrorCodes.ida \
	    cmiStrategy.ida \
	    cmiUser.ida \
	    cmiUtil.ida \
	    cmi.ida \
	    cmiV2.ida \
	    cmiV3.ida \
	    cmiIntermarket.ida \
	    cmiIntermarketMessages.ida \
	    cmiIntermarketCallback.ida \
	    cmiTraderActivity.ida \
	    cmiV4.ida \
            exceptions.ida \
            cmiTradeMaintenanceService.ida \
            cmiTradeNotification.ida \
            cmiTrade.ida \
	    cmiV5.ida \
	    cmiV6.ida \
	    cmiV7.ida \
	    cmiV8.ida \
	    cmiV9.ida \
	    cmiV10.ida \
            cmiCallbackV5.ida
