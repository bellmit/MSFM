#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
#######################################################################

#######################################################################
# include external component definitions
# These are references to components other than the current component
#
#######################################################################
include $(TOOLS)/build/tools.mk
include $(INFRAVERITY)/build/InfraVerity.mk


#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
COMMON_CLASSPATH=$(COMMON_CLASSES):$(TOOLS_BUILD_JAR):$(CONCURJAR):$(JAWJAR):$(JAKARTAREGEXP):$(INFRAVERITY_IDL_BUILD_JAR):$(JUNIT47JAR):$(MOCKITO)

#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
COMMON_DEPENDS=$(filter %.jar,$(subst :, ,$(COMMON_CLASSPATH)))

