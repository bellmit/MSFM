#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#
#######################################################################

#######################################################################
# include external component definitions
#
#######################################################################

#######################################################################
# Defines the build classpath for this build component.
# Only those jars that are needed to build this component should be 
# referenced.
# 
#######################################################################
CMI_IDL_CLASSPATH =$(CMI_IDL_CLASS):$(OMGBASE_JAR):$(OMGSERVICES_JAR)



#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
CMI_IDL_DEPENDS=$(filter %.jar,$(subst :, ,$(CMI_IDL_CLASSPATH)))


