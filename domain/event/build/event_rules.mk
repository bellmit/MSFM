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
include $(EVENT_INTERFACES_DEP)
include $(EVENT_IMPLS_DEP)
include $(EVENT_IDL_DEP)
include $(EVENT_TEST_DEP)

ALL_EVENT_BUILD_COMPONENTS=$(EVENT_IMPLS_JAR) \
                            $(EVENT_INTERFACES_JAR) \
                            $(EVENT_TEST_JAR) \
                            $(EVENT_IDL_JAR)


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
.PHONY: event $(ALL_EVENT_BUILD_COMPONENTS)

all:: event

all_targets:: event_targets

event: $(ALL_EVENT_BUILD_COMPONENTS)

event_targets: event_interfaces event_impls event_idl event_test

$(EVENT_INTERFACES_JAR): $(EVENT_INTERFACES_DEPENDS)
	@cd $(EVENT_INTERFACES_JAVA); $(MAKE) $(EVENT_INTERFACES_JAR)

event_interfaces:
	@cd $(EVENT_INTERFACES_JAVA); $(MAKE) $(ACTION)

$(EVENT_IMPLS_JAR): $(EVENT_IMPLS_DEPENDS)
	@cd $(EVENT_IMPLS_JAVA); $(MAKE) $(EVENT_IMPLS_JAR)

event_impls:
	@cd $(EVENT_IMPLS_JAVA); $(MAKE) $(ACTION)

$(EVENT_TEST_JAR): $(EVENT_TEST_DEPENDS)
	@cd $(EVENT_TEST_JAVA); $(MAKE) $(EVENT_TEST_JAR)

event_test:
	@cd $(EVENT_TEST_JAVA); $(MAKE) $(ACTION)

$(EVENT_IDL_JAR): $(EVENT_IDL_DEPENDS)
	@cd $(EVENT_IDL); $(MAKE) $(EVENT_IDL_JAR)

event_idl:
	@cd $(EVENT_IDL); $(MAKE) $(ACTION)

