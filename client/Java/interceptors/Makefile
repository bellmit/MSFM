include /vobs/dte/sbtcommon/build/defs/client.mk
include $(CLIENT)/build/client.mk
include $(CLIENT_INTERCEPTORS_DEP)


JAVADIR=$(CLIENT_INTERCEPTORS_JAVA)
CLASSDIR=$(CLIENT_INTERCEPTORS_CLASS)
RELDIR=$(CLIENT_REL)
JARNAME=$(CLIENT_INTERCEPTORS_JARNAME)
VERSIONSRC=$(CLIENT_INTERCEPTORS_VERSIONSRC)

MYCLASSPATH=$(CLIENT_INTERCEPTORS_CLASSPATH)


INTERCEPTORS_OUT=$(CLIENT_INTERCEPTORS)/$(JARNAME).out

include $(GENERIC_JAVA_MK)


#########################################
#
#Each entry follows the form
#    interceptorName-interfaceName
#########################################
EXCEPTION_INTERCEPTORS=\
    com.cboe.application.cas.UserAccessInterceptor-com.cboe.interfaces.application.UserAccess \
    com.cboe.application.cas.UserAccessV2Interceptor-com.cboe.interfaces.application.UserAccessV2 \
    com.cboe.application.cas.UserAccessV3Interceptor-com.cboe.interfaces.application.UserAccessV3 \
    com.cboe.application.cas.IntermarketUserAccessInterceptor-com.cboe.interfaces.application.IntermarketUserAccess \
    com.cboe.application.cas.UserAccessPCQSInterceptor-com.cboe.interfaces.application.UserAccessPCQS \
    com.cboe.application.cas.UserAccessOMTInterceptor-com.cboe.interfaces.application.UserAccessOMT \
    com.cboe.application.cas.UserAccessTMSInterceptor-com.cboe.interfaces.application.UserAccessTMS \
    com.cboe.application.cas.UserAccessV5Interceptor-com.cboe.interfaces.application.UserAccessV5 \
    com.cboe.application.cas.UserAccessV6Interceptor-com.cboe.interfaces.application.UserAccessV6 \
    com.cboe.application.cas.UserAccessV7Interceptor-com.cboe.interfaces.application.UserAccessV7 \
    com.cboe.application.cas.UserAccessV8Interceptor-com.cboe.interfaces.application.UserAccessV8 \
    com.cboe.application.cas.UserAccessV9Interceptor-com.cboe.interfaces.application.UserAccessV9 \
    com.cboe.application.inprocess.userAccess.InProcessUserAccessInterceptor-com.cboe.interfaces.application.inprocess.UserAccess \
    com.cboe.expressApplication.cas.UserAccessV4Interceptor-com.cboe.interfaces.expressApplication.UserAccessV4 \
    com.cboe.floorApplication.userAccess.UserAccessFloorInterceptor-com.cboe.interfaces.floorApplication.UserAccessFloor \



CAS_INTERCEPTORS=\
    com.cboe.application.marketData.SystemMarketQueryInterceptor-com.cboe.interfaces.application.SystemMarketQuery \
    com.cboe.application.marketData.IntermarketQueryInterceptor-com.cboe.interfaces.application.IntermarketQuery \
    com.cboe.application.imManualHandling.IntermarketManualHandlingInterceptor-com.cboe.interfaces.application.IntermarketManualHandling \
    com.cboe.application.order.UserOrderQueryInterceptor-com.cboe.interfaces.application.OrderQueryV6 \
    com.cboe.application.product.ProductQueryManagerInterceptor-com.cboe.interfaces.application.ProductQueryManager \
    com.cboe.application.quote.UserQuoteInterceptor-com.cboe.interfaces.application.QuoteV7 \
    com.cboe.application.nbboAgent.NBBOAgentInterceptor-com.cboe.interfaces.application.NBBOAgent \
    com.cboe.application.nbboAgent.NBBOAgentSessionManagerInterceptor-com.cboe.interfaces.application.NBBOAgentSessionManager \
    com.cboe.application.session.SessionManagerInterceptor-com.cboe.interfaces.application.SessionManager \
    com.cboe.expressApplication.session.SessionManagerV4Interceptor-com.cboe.interfaces.expressApplication.SessionManagerV4 \
    com.cboe.application.tmsSession.SessionManagerTMSInterceptor-com.cboe.interfaces.application.SessionManagerTMS \
    com.cboe.application.session.SessionManagerV5Interceptor-com.cboe.interfaces.application.SessionManagerV5 \
    com.cboe.application.imSession.IntermarketSessionManagerInterceptor-com.cboe.interfaces.application.IntermarketUserSessionManager \
    com.cboe.application.pcqsSession.PCQSSessionManagerInterceptor-com.cboe.interfaces.application.PCQSSessionManager \
    com.cboe.application.omtSession.OMTSessionManagerInterceptor-com.cboe.interfaces.application.OMTSessionManager \
    com.cboe.application.userServices.UserPreferenceQueryInterceptor-com.cboe.interfaces.application.UserPreferenceQuery \
    com.cboe.application.userServices.UserTradingParametersInterceptor-com.cboe.interfaces.application.UserTradingParametersV5 \
    com.cboe.application.userServices.UserHistoryInterceptor-com.cboe.interfaces.application.UserHistory \
    com.cboe.application.administrator.AdministratorInterceptor-com.cboe.interfaces.application.Administrator \
    com.cboe.application.orderEntry.UserOrderEntryInterceptor-com.cboe.interfaces.application.OrderEntryV9 \
    com.cboe.application.productDefinition.ProductDefinitionInterceptor-com.cboe.interfaces.application.ProductDefinition \
    com.cboe.application.tradingSession.TradingSessionInterceptor-com.cboe.interfaces.application.TradingSession \
    com.cboe.application.inprocess.session.InProcessSessionManagerInterceptor-com.cboe.interfaces.application.inprocess.InProcessSessionManager \
    com.cboe.application.inprocess.quoteEntry.QuoteEntryInterceptor-com.cboe.interfaces.application.inprocess.QuoteEntry \
    com.cboe.application.inprocess.quoteQuery.QuoteQueryInterceptor-com.cboe.interfaces.application.inprocess.QuoteQuery \
    com.cboe.application.inprocess.orderQuery.OrderQueryInterceptor-com.cboe.interfaces.application.inprocess.UserOrderQuery \
    com.cboe.application.inprocess.floorTrade.FloorTradeConsumerInterceptor-com.cboe.interfaces.application.inprocess.FloorTradeConsumer \
    com.cboe.application.inprocess.marketData.MarketQueryInterceptor-com.cboe.interfaces.application.inprocess.MarketQuery \
    com.cboe.application.inprocess.marketData.RemoteMarketQueryInterceptor-com.cboe.interfaces.application.inprocess.RemoteMarketQuery \
    com.cboe.application.inprocess.tradingSession.InProcessTradingSessionInterceptor-com.cboe.interfaces.application.inprocess.InProcessTradingSession \
    com.cboe.expressApplication.marketData.MarketQueryV4Interceptor-com.cboe.interfaces.expressApplication.ExpressMarketQuery \
    com.cboe.application.tradeMaintenance.ExternalTradeMaintenanceServiceInterceptor-com.cboe.interfaces.application.ExternalTradeMaintenanceService \
    com.cboe.application.productConfiguration.ProductConfigurationQueryServiceInterceptor-com.cboe.interfaces.application.ProductConfigurationQueryService \
    com.cboe.application.orderManagement.OrderManagementServiceInterceptor-com.cboe.interfaces.application.OrderManagementService \
    com.cboe.application.parOrderManagementService.ParOrderManagementServiceInterceptor-com.cboe.interfaces.application.ParOrderManagementService \
    com.cboe.application.activityService.ActivityServiceInterceptor-com.cboe.interfaces.application.ActivityService \
    com.cboe.floorApplication.floorSession.FloorSessionManagerInterceptor-com.cboe.interfaces.floorApplication.FloorSessionManager \
    com.cboe.floorApplication.manualReporting.ManualReportingServiceInterceptor-com.cboe.interfaces.floorApplication.ManualReportingService \
    com.cboe.application.FloorTradeMaintenanceServiceInterceptor-com.cboe.interfaces.application.FloorTradeMaintenanceService \
    com.cboe.application.session.SessionManagerV6Interceptor-com.cboe.interfaces.application.SessionManagerV6 \
    com.cboe.application.session.SessionManagerV7Interceptor-com.cboe.interfaces.application.SessionManagerV7 \
    com.cboe.application.session.SessionManagerV8Interceptor-com.cboe.interfaces.application.SessionManagerV8 \
    com.cboe.application.session.SessionManagerV9Interceptor-com.cboe.interfaces.application.SessionManagerV9


define generate-interceptors
	$(clean-interceptors)
	@MAKEFILE_CLASSPATH=$(MYCLASSPATH); \
    MAKEFILE_JAVA_HOME=$(JAVAHOME); \
    export MAKEFILE_CLASSPATH MAKEFILE_JAVA_HOME; \
	$(foreach INTERCEPTOR,$(EXCEPTION_INTERCEPTORS),/vobs/dte/client/Java/interceptors/generateExceptionInterceptor $(subst -, ,$(INTERCEPTOR)); )\
	$(foreach INTERCEPTOR,$(CAS_INTERCEPTORS),/vobs/dte/client/Java/interceptors/generateCasInterceptor $(subst -, ,$(INTERCEPTOR)); )\
	$(foreach INTERCEPTOR,$(CMI_CONSUMER_INTERCEPTORS),/vobs/dte/client/Java/interceptors/generateCMIConsumerInterceptor $(subst -, ,$(INTERCEPTOR)); )
    touch $(INTERCEPTORS_OUT)
endef

define clean-interceptors
	/usr/bin/rm -f $(JAVADIR)/*.java
    /usr/bin/rm -f $(INTERCEPTORS_OUT)
endef

################################################################
# Build rules
#
################################################################

all: $(BUILD_JAR)

$(BUILD_JAR): $(BUILD_VERSION)
	@$(jar-all)

$(JAVA_COMPILE):: $(INTERCEPTORS_OUT)

$(INTERCEPTORS_OUT):
	@$(generate-interceptors)

java: $(JAVA_COMPILE)

jars:
	$(jar-all)

clean: gen_clean
	@$(clean-interceptors)

stage_install: gen_stage_install

install: gen_install

uninstall: gen_uninstall


