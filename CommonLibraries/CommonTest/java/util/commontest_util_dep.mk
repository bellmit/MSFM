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


#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
COMMONTEST_UTIL_CLASSPATH=$(COMMONTEST_UTIL_CLASSES):$(JUNIT_TPT)

#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
COMMONTEST_UTIL_DEPENDS=$(filter %.jar,$(subst :, ,$(COMMONTEST_UTIL_CLASSPATH)))

