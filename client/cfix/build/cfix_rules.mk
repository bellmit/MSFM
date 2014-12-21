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
include $(CFIX_INTERFACES_DEP)
include $(CFIX_IMPLS_DEP)
include $(CFIX_INTERCEPTORS_DEP)
include $(CFIX_TEST_DEP)


ALL_CFIX_BUILD_COMPONENTS=$(CFIX_INTERFACES_JAR)\
                          $(CFIX_IMPLS_JAR) \
                          $(CFIX_TEST_JAR) \
                          $(CFIX_INTERCEPTORS_JAR)


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
.PHONY: cfix $(ALL_CFIX_BUILD_COMPONENTS)

all:: cfix

all_targets:: cfix_targets

cfix: $(ALL_CFIX_BUILD_COMPONENTS)

cfix_targets: cfix_interfaces cfix_impls cfix_interceptors cfix_test

$(CFIX_INTERFACES_JAR): $(CFIX_INTERFACES_DEPENDS)
	@cd $(CFIX_INTERFACES_JAVA); $(MAKE) $(CFIX_INTERFACES_JAR)

cfix_interfaces:
	@cd $(CFIX_INTERFACES_JAVA); $(MAKE) $(ACTION)

$(CFIX_IMPLS_JAR): $(CFIX_IMPLS_DEPENDS)
	@cd $(CFIX_IMPLS_JAVA); $(MAKE) $(CFIX_IMPLS_JAR)

cfix_impls:
	@cd $(CFIX_IMPLS_JAVA); $(MAKE) $(ACTION)

$(CFIX_INTERCEPTORS_JAR): $(CFIX_INTERCEPTORS_DEPENDS)
	@cd $(CFIX_INTERCEPTORS_JAVA); $(MAKE) $(CFIX_INTERCEPTORS_JAR)

cfix_interceptors:
	@cd $(CFIX_INTERCEPTORS_JAVA); $(MAKE) $(ACTION)

$(CFIX_TEST_JAR): $(CFIX_TEST_DEPENDS)
	@cd $(CFIX_TEST_JAVA); $(MAKE) $(CFIX_TEST_JAR)

cfix_test:
	@cd $(CFIX_TEST_JAVA); $(MAKE) $(ACTION)


