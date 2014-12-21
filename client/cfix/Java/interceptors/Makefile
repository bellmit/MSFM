include /vobs/dte/sbtcommon/build/defs/client.mk
include $(CFIX)/build/cfix.mk
include $(CFIX_INTERCEPTORS_DEP)


JAVADIR=$(CFIX_INTERCEPTORS_JAVA)
CLASSDIR=$(CFIX_INTERCEPTORS_CLASS)
RELDIR=$(CFIX_REL)
JARNAME=$(CFIX_INTERCEPTORS_JARNAME)
VERSIONSRC=$(CFIX_INTERCEPTORS_VERSIONSRC)

MYCLASSPATH=$(CFIX_INTERCEPTORS_CLASSPATH)

INTERCEPTORS_OUT=$(CFIX_INTERCEPTORS)/$(JARNAME).out

include $(GENERIC_JAVA_MK)


CAS_INTERCEPTORS=\
    com.cboe.cfix.cas.sessionManager.CfixSessionManagerInterceptor-com.cboe.interfaces.cfix.CfixSessionManager\
    com.cboe.cfix.cas.marketData.CfixMarketDataQueryInterceptor-com.cboe.interfaces.cfix.CfixMarketDataQueryIF\
    com.cboe.cfix.cas.marketData.CfixMDXMarketDataQueryInterceptor-com.cboe.interfaces.cfix.CfixMDXMarketDataQueryIF\


define generate-interceptors
	$(clean-interceptors)
	@MAKEFILE_CLASSPATH=$(MYCLASSPATH); \
	MAKEFILE_JAVA_HOME=$(JAVAHOME); \
	export MAKEFILE_CLASSPATH MAKEFILE_JAVA_HOME; \
	$(foreach INTERCEPTOR,$(CAS_INTERCEPTORS),/vobs/dte/client/Java/interceptors/generateCasInterceptor $(subst -, ,$(INTERCEPTOR)); )
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





