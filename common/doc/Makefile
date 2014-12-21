include $(BUILD_ENV_ROOT)/build/make/environment.mk
include .sniffdir/macros.incl
-include .sniffdir/vpath.incl

TOOLS = ospace
PRODUCTS = common_1_0 ace_os_abstract_4_2

SUBDIRS		= $(SNIFF_SUBDIRS)
INCLUDE_DIRS	= $(SNIFF_INCLUDE)
DEFINES		= PLUGENG_LIBRARY

CC_OPTIONS	= optimize_fast inline_x rtti except dyn_target
CC_SYS_LIBS	+= dyn_sockets dynload libc_old_iostream dyn_libc

SOURCES	= $(SNIFF_Implementation_FILES)

include $(BUILD_ENV_ROOT)/build/make/targets.mk


#Need some standard fixups to hide from the user the fact that the
# different platforms have different file extension for the targets
# that SNiFF+ supports
ifneq ($SNIFF_LINK_TARGET,)
$(SNIFF_LINK_TARGET) : \
	target_directories \
	$(PROD_BIN_DIR)/$(SNIFF_LINK_TARGET)$(cc_g_bin_ext)

endif

ifneq ($SNIFF_RELINK_TARGET,)
$(SNIFF_RELINK_TARGET) : \
	target_directories \
	$(PROD_DYNLIB_DIR)/$(cc_g_lib_pfx)$(SNIFF_RELINK_TARGET)$(cc_g_dynlib_ext)

endif

ifneq ($SNIFF_LIB_TARGET,)
$(SNIFF_LIB_TARGET) : \
	target_directories \
	$(PROD_LIB_DIR)/$(cc_g_lib_pfx)$(SNIFF_LINK_TARGET)$(cc_g_lib_ext)

endif

