include /vobs/dte/sbtcommon/build/defs/client.mk
include $(CLIENT)/build/client.mk
include $(CLIENT_SCRIPTS_DEP)


COMMENT="generated script"

all: $(CLIENT_SCRIPTS_OUT)

$(CLIENT_SCRIPTS_OUT): $(CLIENT_BUILDCASXML_KSH) $(CLIENT_BUILDSACASXML_KSH)
	@rm -f $(CLIENT_SCRIPTS_OUT)
	@touch $(CLIENT_SCRIPTS_OUT)	

$(CLIENT_BUILDCASXML_KSH): $(CLIENT_SCRIPTS_CAS_DEPENDS)
	@$(MAKE) clean1
	@$(MAKE) stage_install1
	@$(CLIENT_BUILDCASXMLDIR)/genbuildcasxml.ksh

$(CLIENT_BUILDSACASXML_KSH): $(CLIENT_SCRIPTS_SACAS_DEPENDS)
	@$(MAKE) clean2
	@$(MAKE) stage_install2
	@$(CLIENT_BUILDSACASXMLDIR)/genbuildsacasxml.ksh

stage_install1:
	@/usr/atria/bin/cleartool lsco -cview $(CLIENT_BUILDCASXML_KSH) | grep $(CLIENT_BUILDCASXML_KSH) >/dev/null 2>&1; \
	if [ $$? -ne 0 ]; then \
		/usr/atria/bin/cleartool co -unres  -c $(COMMENT) $(CLIENT_BUILDCASXML_KSH) ;\
	fi

stage_install2:
	@/usr/atria/bin/cleartool lsco -cview $(CLIENT_BUILDSACASXML_KSH) | grep $(CLIENT_BUILDSACASXML_KSH) >/dev/null 2>&1; \
	if [ $$? -ne 0 ]; then \
		/usr/atria/bin/cleartool co -unres  -c $(COMMENT) $(CLIENT_BUILDSACASXML_KSH) ;\
	fi

install: install1 install2

install1:
	@/usr/atria/bin/cleartool lsco -cview $(CLIENT_BUILDCASXML_KSH) | grep $(CLIENT_BUILDCASXML_KSH) >/dev/null 2>&1; \
	if [ $$? -eq 0 ]; then \
		/usr/atria/bin/cleartool ci -nc -identical $(CLIENT_BUILDCASXML_KSH); \
	fi

install2:
	@/usr/atria/bin/cleartool lsco -cview $(CLIENT_BUILDSACASXML_KSH) | grep $(CLIENT_BUILDSACASXML_KSH) >/dev/null 2>&1; \
	if [ $$? -eq 0 ]; then \
		/usr/atria/bin/cleartool ci -nc -identical $(CLIENT_BUILDSACASXML_KSH); \
	fi

uninstall: uninstall1 uninstall2

uninstall1:
	@/usr/atria/bin/cleartool lsco -cview $(CLIENT_BUILDCASXML_KSH) | grep $(CLIENT_BUILDCASXML_KSH) >/dev/null 2>&1; \
	if [ $$? -eq 0 ]; then \
		/usr/atria/bin/cleartool unco -rm $(CLIENT_BUILDCASXML_KSH); \
	fi

uninstall2:
	@/usr/atria/bin/cleartool lsco -cview $(CLIENT_BUILDSACASXML_KSH) | grep $(CLIENT_BUILDSACASXML_KSH) >/dev/null 2>&1; \
	if [ $$? -eq 0 ]; then \
		/usr/atria/bin/cleartool unco -rm $(CLIENT_BUILDSACASXML_KSH); \
	fi

clean: clean1 clean2
	rm -f $(CLIENT_SCRIPTS_OUT)

clean1: uninstall1
clean2: uninstall2

setversion:
	#do nothing

