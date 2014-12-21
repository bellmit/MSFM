#######################################################################
# This files defines the rules for building all build components
# in this release component
#
# It is intended that this file be included in two other makes, one
# for the release component and one for the entire system.
#######################################################################

###########################################################
# Include each build component defintion
#
###########################################################
include $(CMI)/build/cmi.mk
include $(CMI_IDL_DEP)

ALL_CMI_BUILD_COMPONENTS=$(CMI_IDL_JAR)

############################################################
# The following are the build rules that will build this
# entire release component.
#
# These are all PHONY rules since each build component has
# the real rules for building the component.  This exists
# so that all build components can be built from a single
# makefile.
#
############################################################
.PHONY: cmi $(ALL_CMI_BUILD_COMPONENTS)

all:: cmi

all_targets:: cmi_targets

cmi: $(ALL_CMI_BUILD_COMPONENTS)

cmi_targets: cmi_idl

$(CMI_IDL_JAR): $(CMI_IDL_DEPENDS)
	@cd $(CMI_IDL); $(MAKE) $(CMI_IDL_BUILD_JAR) $(CMI_IDL_JAVA_BUILD_JAR)

cmi_idl:
	@cd $(CMI_IDL); $(MAKE) $(ACTION)



