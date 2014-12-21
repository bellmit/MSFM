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
COMMONFACILITIES_CLASSPATH=$(COMMONFACILITIES_CLASS):$(XMLPARSER):$(JGLCLASSES):$(FFJAR):$(JUNITJAR):$(JUNIT37JAR):$(IVIDLCLASSESJAR)


#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
COMMONFACILITIES_DEPENDS=$(filter %.jar,$(subst :, ,$(COMMONFACILITIES_CLASSPATH)))

