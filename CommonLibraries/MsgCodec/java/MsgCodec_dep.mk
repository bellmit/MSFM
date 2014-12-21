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
MSG_CODEC_CLASSPATH=$(MSG_CODEC_COMMON_CLASS):$(FFJAR):$(FFIMPLJAR):$(FFPERSISTJAR):$(MSIMPJAR):$(JUNITJAR):$(CONCURJAR):$(JUNIT37JAR):$(OBJWAVEJAR):$(OMGBASECLASSESJAR):$(OMGSERVICECLASSESJAR):$(ROJAR):$(CMJAR):$(IVIDLCLASSESJAR):$(CUJAR)



#######################################################################
# The dependencies for this component.  This is intended to be used 
# in the dependency list in Makefile targets.
# 
#######################################################################
MSG_CODEC_DEPENDS=$(filter %.jar,$(subst :, ,$(SERVER_COMMON_CLASSPATH)))




