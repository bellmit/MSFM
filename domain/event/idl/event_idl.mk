#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   EVENT_IDL = the directory location for the idl files
#   EVENT_REL = the directory location for the jar files
#
#######################################################################

#######################################################################
# Defines specific jars and locations for this build component 
# 
#######################################################################
EVENT_IDL_JAVA            =$(EVENT_IDL)/trc_output/java
EVENT_IDL_CLASS           =$(EVENT_IDL)/trc_output/classes
EVENT_IDL_JAVA_JARNAME    =event_idl_java
EVENT_IDL_JARNAME         =event_idl
EVENT_IDL_JAVA_JAR        =$(EVENT_REL)/$(EVENT_IDL_JAVA_JARNAME).$(JAR_EXT)
EVENT_IDL_JAR             =$(EVENT_REL)/$(EVENT_IDL_JARNAME).$(JAR_EXT)
EVENT_IDL_VERSIONSRC      =$(EVENT_IDL)/$(EVENT_IDL_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    EVENT_IDL_JAVA_BUILD_JAR = $(EVENT_IDL_JAVA_JAR)
    EVENT_IDL_BUILD_JAR      = $(EVENT_IDL_JAR)
else
    EVENT_IDL_JAVA_BUILD_JAR = $(EVENT_IDL_CLASS)/$(EVENT_IDL_JAVA_JARNAME).$(JAR_EXT)
    EVENT_IDL_BUILD_JAR      = $(EVENT_IDL_CLASS)/$(EVENT_IDL_JARNAME).$(JAR_EXT)
endif


#######################################################################
# Defines the idl files to build
# The extension ida is for Async and idn is for No Async.
#######################################################################

EVENT_IDL_FILES= \
    consumers.idn \
    InternalConsumers.idn \
    events.idn \
    internalEvents.idn \
    OrderStatusSubscriptionService.idn \
    QuoteStatusSubscriptionService.idn \
    QuoteStatusSubscriptionServiceExt.idn \
    CalendarAdminEventService.ida \
    PropertyEventService.ida \
    UserMaintenanceEventService.ida \
    QuoteStatusService.ida \
    OrderStatusService.ida \
    ProductMaintenanceEventService.ida \
    TradingSessionEventStateService.ida \
    GroupElementEventService.ida \
    AlertEventService.ida \
    GroupCancelEventService.ida \
    ohsConsumers.idn \
    ohsEvents.idn \
    internalCallback.ida


#---------------------------------------------------------------------


