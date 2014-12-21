##############################################################################################
# Third party JARS and Class hierarchies
# This should replace the version currently available at: /vobs/dte/build/make/defs/tpt.mk
#
##############################################################################################

# MOUNT these VOBs...
#####################
## /vobs/tpt/tpt_tools1
## /vobs/tpt/tpt_tools
## /vobs/tpt/tpt_jdk
## /vobs/tpt/tpt_jre
## /vobs/tpt/tpt_Commons
## /vobs/tpt/tpt_ant
## /vobs/tpt/tpt_JFree

TOOLS_TPT		=/vobs/tpt/tpt_tools

JDK_TOOLS 		=/vobs/tpt/tpt_jdk/solaris/run_dir/lib/tools.$(JAR_EXT)

APACHE_ANT_TPT  	=$(TOOLS_TPT)/ant/run_dir/lib/ant.$(JAR_EXT)
APACHE_TOMCAT_TPT 	=$(TOOLS_TPT)/Apache_Tomcat/run_dir/lib/jasper-runtime.$(JAR_EXT)
AVALON_FRAMEWORK_TPT 	=$(TOOLS_TPT)/avalon-framework/run_dir/avalon-framework.$(JAR_EXT)
BATIK_TPT 		=$(TOOLS_TPT)/batik/run_dir/batik-fop.$(JAR_EXT)
CEWOLF_TPT 		=$(TOOLS_TPT)/cewolf/run_dir/lib/cewolf.$(JAR_EXT)
COMMONS_BEANUTILS_TPT 	=$(TOOLS_TPT)/commons-beanutils/run_dir/commons-beanutils.$(JAR_EXT)
COMMONS_CHAIN_TPT 	=$(TOOLS_TPT)/commons-chain/run_dir/commons-chain.$(JAR_EXT)
COMMONS_COLLECTIONS_TPT =$(TOOLS_TPT)/commons-collections/run_dir/commons-collections.$(JAR_EXT)
COMMONS_DIGESTER_TPT 	=$(TOOLS_TPT)/commons-digester/run_dir/commons-digester.$(JAR_EXT)
COMMONS_EL_TPT 		=$(TOOLS_TPT)/commons-el/run_dir/commons-el.$(JAR_EXT)
COMMONS_FILEUPLOAD_TPT 	=$(TOOLS_TPT)/commons-fileupload/run_dir/commons-fileupload.$(JAR_EXT)
COMMONS_IO_TPT 		=$(TOOLS_TPT)/commons-io/run_dir/commons-io.$(JAR_EXT)
COMMONS_LANG_TPT 	=$(TOOLS_TPT)/commons-lang/run_dir/commons-lang.$(JAR_EXT)
COMMONS_LOGGING_TPT 	=$(TOOLS_TPT)/commons-logging/run_dir/commons-logging.$(JAR_EXT)
COMMONS_NET_TPT 	=$(TOOLS_TPT)/commons-net/run_dir/commons-net.$(JAR_EXT)
COMMONS_VALIDATOR_TPT 	=$(TOOLS_TPT)/commons-validator/run_dir/commons-validator.$(JAR_EXT)
CRIMSON_TPT 		=$(TOOLS_TPT)/Crimson/run_dir/crimson.$(JAR_EXT)
DISPLAYTAG_TPT 		=$(TOOLS_TPT)/displaytag/run_dir/displaytag.$(JAR_EXT)
EHCACHE_TPT 		=$(TOOLS_TPT)/ehcache/run_dir/ehcache.$(JAR_EXT)
EXTREMECOMPONENTS_TPT 	=$(TOOLS_TPT)/extremecomponents/run_dir/extremecomponents.$(JAR_EXT)
FRAMEWORK_TPT 		=$(TOOLS_TPT)/framework/run_dir/framework.$(JAR_EXT)
FOP_TPT 		=$(TOOLS_TPT)/fop/run_dir/fop.$(JAR_EXT)
ITEXT_TPT 		=$(TOOLS_TPT)/iText/run_dir/itext.$(JAR_EXT)
J2EE_TPT 		=$(TOOLS_TPT)/j2ee/run_dir/j2ee.$(JAR_EXT)
J2SSH_TPT 		=$(TOOLS_TPT)/j2ssh/run_dir/j2ssh.$(JAR_EXT)
JAF_TPT 		=$(TOOLS_TPT)/jaf/run_dir/activation.$(JAR_EXT)
JAKARTA_ORO_TPT 	=$(TOOLS_TPT)/oro/run_dir/jakarta-oro.$(JAR_EXT)
JAMON_TPT 		=$(TOOLS_TPT)/JAMon/run_dir/jamon.$(JAR_EXT)
JASPERREPORTS_TPT 	=$(TOOLS_TPT)/jasperreports/run_dir/jasperreports.$(JAR_EXT)
JAVAMAIL_TPT 		=$(TOOLS_TPT)/javamail/run_dir/mail.$(JAR_EXT)
JCACHE_TPT 		=$(TOOLS_TPT)/jcache/run_dir/jcache.$(JAR_EXT)
JETTY_TPT 		=$(TOOLS_TPT)/Jetty/run_dir/jetty-5.1.1/lib/org.mortbay.jetty.$(JAR_EXT)
JEXCELAPI_TPT 		=$(TOOLS_TPT)/jexcelapi/run_dir/jxl.$(JAR_EXT)
JFREE_JCOMMON_TPT 	=$(TOOLS_TPT)/jfreechart/run_dir/lib/jcommon.$(JAR_EXT)
JFREE_JFREECHART_TPT 	=$(TOOLS_TPT)/jfreechart/run_dir/lib/jfreechart.$(JAR_EXT)
JGL_TPT 		=$(TOOLS_TPT)/jgl/run_dir/jars/jgl.$(JAR_EXT)
JSTL_TPT 		=$(TOOLS_TPT)/jstl/run_dir/jstl.$(JAR_EXT)
JUNIT_TPT 		=$(TOOLS_TPT)/junit/run_dir/junit.$(JAR_EXT)
JUNITSERVICE_TPT	=$(TOOLS_TPT)/junitservice/run_dir/JUnitService.$(JAR_EXT)
LOG4J_EXTRAS_TPT 	=$(TOOLS_TPT)/log4j-extras/run_dir/apache-log4j-extras.$(JAR_EXT)
LOG4J_TPT 		=$(TOOLS_TPT)/log4j/run_dir/log4j.$(JAR_EXT)
ORACLE_JDBC_TPT 	=$(TOOLS_TPT)/ojdbc/run_dir/ojdbc14.$(JAR_EXT)
POI_TPT 		=$(TOOLS_TPT)/poi/run_dir/poi.$(JAR_EXT)
SQL92_PARSER_TPT 	=$(TOOLS_TPT)/sql92_parser/run_dir/sql92_parser.$(JAR_EXT)
STANDARD_TPT 		=$(TOOLS_TPT)/standard/run_dir/standard.$(JAR_EXT)
STRUTS_TPT 		=$(TOOLS_TPT)/struts/run_dir/lib/struts-core.$(JAR_EXT)
STRUTS_LAYOUT_TPT 	=$(TOOLS_TPT)/struts-Layout/run_dir/src/library/Struts-Layout.$(JAR_EXT)
STRUTS_MENU_TPT 	=$(TOOLS_TPT)/struts-menu/run_dir/struts-menu.$(JAR_EXT)
TAGLIBS_DATETIME_TPT 	=$(TOOLS_TPT)/jakarta-taglibs/run_dir/taglibs-datetime.$(JAR_EXT)
VELOCITY_TPT 		=$(TOOLS_TPT)/velocity/run_dir/velocity.$(JAR_EXT)
XALAN_TPT 		=$(TOOLS_TPT)/xalan/run_dir/xalan.$(JAR_EXT)
XERCESIMPL_TPT 		=$(TOOLS_TPT)/xercesImpl/run_dir/xercesImpl.$(JAR_EXT)
XML_APIS_TPT 		=$(TOOLS_TPT)/xml-apis/run_dir/xml-apis.$(JAR_EXT)



# CURRENT SBT TPT REFERENCES

APPIAJAR		=/vobs/dte/appia/lib/appia_oe.jar
JAVA_MAILJAR		=/vobs/tpt/tpt_tools/javamail/run_dir/mail.jar
JAVA_ACTIVATIONJAR	=/vobs/dte/tools/java/jars/activation.jar

TPT_APACHEJAR		=$(TOOLS_TPT)/tpt_Commons/commons-net/run_dir/commons-net.jar
TPT_JCOMMONJAR		=$(TOOLS_TPT)/tpt_JFree/jcommon/run_dir/jcommon.jar
TPT_JFREECHARTJAR	=$(TOOLS_TPT)/tpt_JFree/jfreechart/run_dir/lib/jfreechart.jar
JUNIT38JAR		=$(CLIENT)/tools/junit38.jar

STYLEREPORT		=/vobs/tpt/tpt_StyleReport/StyleReportPro7.0/lib/xreport_pro.jar

TPT_JASPERREPORTS	=$(TOOLS_TPT)/jasperreports/run_dir/jasperreports.jar
TPT_ITEXT		=$(TOOLS_TPT)/iText/run_dir/itext.jar
TPT_COMMONS_BEANUTILS	=$(TOOLS_TPT)/commons-beanutils/run_dir/commons-beanutils.jar
TPT_COMMONS_COLLECTIONS	=$(TOOLS_TPT)/commons-collections/run_dir/commons-collections.jar
TPT_COMMONS_DIGESTER	=$(TOOLS_TPT)/commons-digester/run_dir/commons-digester.jar
TPT_COMMONS_LOGGING	=$(TOOLS_TPT)/commons-logging/run_dir/commons-logging.jar

SQL_PARSER		=$(TOOLS_TPT)/sql92_parser/run_dir/sql92_parser.jar


# OLD INFRA TOOLS
# TOOLS = /vobs/dte/tools
# JWSDP_HOME =$(XML_PARSER_HOME)/JWSDP-1.3
# XML_PARSER_HOME     =$(TOOLS)/XMLParser

ACTIVEMQJAR     	=$(TOOLS)/activemq/activemq-5.1/activemq-5.1.$(JAR_EXT)
ANT_HOME        	=$(JWSDP_HOME)/apache-ant
CONCURJAR 		=$(TOOLS)/java/classes/concurrency.$(JAR_EXT)
FSCONJAR	        =$(TOOLS)/java/jars/fscontext.$(JAR_EXT)
IAKJAR 			=$(TOOLS)/iaik/jars/iaik_jce_full.$(JAR_EXT)
JAF 			=$(TOOLS)/java/jars/activation.$(JAR_EXT)
JAKARTAORO 		=$(TOOLS)/jakarta/jakarta-oro-2.0.6.$(JAR_EXT)
JAKARTAREGEXP  		=$(TOOLS)/jakarta/jakarta-regexp-1.2.$(JAR_EXT)
JAVAMAIL 		=$(TOOLS)/java/jars/mail.$(JAR_EXT)
JAWJAR 			=$(TOOLS)/java/jars/jawall.$(JAR_EXT)
JDBCZIP   		=$(INFRAVERITY)/$(JARPATH)/ojdbc14.jar
JDKUPDJAR 		=$(TOOLS)/iaik/jars/jdk11x_update.$(JAR_EXT)

JETTY 			=$(TOOLS)/Jetty/$(JETTYVERSION)/lib/org.mortbay.jetty.$(JAR_EXT)
JETTYJAVAX 		=$(TOOLS)/Jetty/$(JETTYVERSION)/lib/javax.servlet.$(JAR_EXT)
JETTYVERSION 		=Jetty-4.2.14

JGLCLASSES 		=$(TOOLS)/jgl/classes
JGLJAR 			=$(TOOLS)/jgl/jars/jgl.$(JAR_EXT)
JHJAR 			=$(TOOLS)/java/jars/jhall.jar
JMSJAR			=$(TOOLS)/java/jms1.0.2b/lib/jms.$(JAR_EXT)
JNDIJAR 		=$(TOOLS)/java/jars/jndi.$(JAR_EXT)
JUNIT32JAR 		=$(TOOLS)/junit/junit32/junit32.$(JAR_EXT)
JUNIT37JAR 		=$(TOOLS)/java/jars/junit37.$(JAR_EXT)
JUNITJAR 		=$(TOOLS)/junit/classes/junit.$(JAR_EXT)
JUNITJAR2 		=$(TOOLS)/java/jars/junit.$(JAR_EXT)
LDAPJAR  		=$(TOOLS)/netscape/jars/ldap.$(JAR_EXT)
LDAPJDKJAR 		=$(TOOLS)/netscape/jars/ldapjdk.$(JAR_EXT)
NBIOJAR 		=$(TOOLS)/NBIO/nbio-release/seda/jars/nbio.$(JAR_EXT)
PUJAR 			=$(TOOLS)/java/jars/providerutil.$(JAR_EXT)
SMARTSOCKJAR 		=$(TOOLS)/talarian/$(SMARTSOCKJAR_VERSION)/lib/ss.$(JAR_EXT)
STYLEREPORT  		=$(TOOLS)/StyleReport/Pro3.5.3/xreport_pro.$(JAR_EXT)
SWINGJAR		=$(TOOLS)/java/jars/swingall.$(JAR_EXT)


JAXB_API_JAR  		=$(JAXB_HOME)/lib/jaxb-api.$(JAR_EXT)
JAXB_HOME    		=$(JWSDP_HOME)/jaxb
JAXB_IMPL_JAR  		=$(JAXB_HOME)/lib/jaxb-impl.$(JAR_EXT)
JAXB_LIBS_JAR  		=$(JAXB_HOME)/lib/jaxb-libs.$(JAR_EXT)
JAXB_XJC_JAR 		=$(JAXB_HOME)/lib/jaxb-xjc.$(JAR_EXT)
JWSDP_HOME  		=$(XML_PARSER_HOME)/JWSDP-1.3
JWSDP_JAX_JAR 		=$(JWSDP_HOME)/jwsdp-shared/lib/jax-qname.$(JAR_EXT)
JWSDP_NAMESPACE_JAR 	=$(JWSDP_HOME)/jwsdp-shared/lib/namespace.$(JAR_EXT)
JWSDP_RELAXNG_JAR 	=$(JWSDP_HOME)/jwsdp-shared/lib/relaxngDatatype.$(JAR_EXT)
JWSDP_XSD_JAR 		=$(JWSDP_HOME)/jwsdp-shared/lib/xsdlib.$(JAR_EXT)

XALAN_HOME 		=$(XML_PARSER_HOME)/Apache_Xalan-j_2_4_1
XALAN_XML_API 		=$(XALAN_HOME)/bin/xml-apis.$(JAR_EXT)
XERCES_IMPL 		=$(XML_PARSER_HOME)/Apache_Xerces_2.2.1/xerces-2_2_1/xmlParserAPIs.$(JAR_EXT)
XML4JAR 		=$(TOOLS)/java/jars/xml4j.$(JAR_EXT)
XML_PARSER_API 		=$(XML_PARSER_HOME)/Apache_Xerces_2.2.1/xerces-2_2_1/xercesImpl.$(JAR_EXT)
XML_PARSER_HOME 	=$(TOOLS)/XMLParser

