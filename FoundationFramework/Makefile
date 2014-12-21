include $(BUILD_ENV_ROOT)/build/make/environment.mk

SUBDIRS		= COMPONENT sub dirs

CC_OPTIONS	= optimize_fast inline_x rtti except dyn_target
CC_SYS_LIBS	+= dyn_sockets dynload libc_old_iostream dyn_libc

# has to be here to keep from trying to build 
SOURCES 	=

include $(BUILD_ENV_ROOT)/build/make/targets.mk
