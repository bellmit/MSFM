VERTXTPLACEHOLDER=Implementation-Version
SPECTXTPLACEHOLDER=Specification-Version

DATE=`date`
VERSIONCOMMENT="version number set by jar version release on $(DATE)"
VERSIONLOCKEDMESSAGE="Version file was locked, waiting 8 seconds to try again, press Ctrl-C to cancel."

#####################
#increment version number
define increment-version
	echo "Incrementing version number in version resource "$$VERSIONSRC; \
    if [ "$(IMPL_VERSION)" != "" ]; then \
        /usr/bin/perl -i -p -e "s/$(VERTXTPLACEHOLDER).*/$(VERTXTPLACEHOLDER)=$(IMPL_VERSION).0/" $$VERSIONTOINC; \
    else \
        /usr/bin/perl -i -p -e 's/$(VERTXTPLACEHOLDER)=(.*)\.([0-9]+)$$/$(VERTXTPLACEHOLDER)=$$1.$${\($$2+1)}/' $$VERSIONTOINC; \
    fi; \
    if [ "$(SPEC_VERSION)" != "" ]; then \
        /usr/bin/perl -i -p -e "s/$(SPECTXTPLACEHOLDER).*/$(SPECTXTPLACEHOLDER)=$(SPEC_VERSION)/" $$VERSIONTOINC; \
    fi
endef

####################
#checkout and increment the version resource
define build-version
  if [ "$$VERSIONSRC" != "" ]; then \
    if [ -r $$VERSIONSRC ]; then \
      if [ ! -d $$CLASSESOUT/$(VERSION_DEST) ]; then \
        mkdir -p $$CLASSESOUT/$(VERSION_DEST); \
      fi; \
      cp $$VERSIONSRC $$CLASSESOUT/$(VERSION_DEST) ; \
      VERSIONTOINC=$$CLASSESOUT/$(VERSION_DEST)/`basename $$VERSIONSRC`; \
      chmod 666 $$VERSIONTOINC; \
      $(increment-version); \
    fi; \
  fi
endef

####################

####################
#checks in the version resource
define install-version
  if [ "$$VERSIONSRC" != "" ]; then \
      BUILD_VERSION=$$CLASSESOUT/$(VERSION_DEST)/`basename $$VERSIONSRC`; \
      if [ -f $$BUILD_VERSION ]; then \
        /usr/bin/diff $$BUILD_VERSION $$VERSIONSRC > /dev/null 2>&1;\
        if [ $$? -ne 0 ]; then \
            echo "Checking in version resource "$$VERSIONSRC; \
	        /usr/atria/bin/cleartool co -nc $$VERSIONSRC ; \
	        /usr/atria/bin/cleartool ci -rm -from $$BUILD_VERSION -nc $$VERSIONSRC ; \
        fi; \
      fi; \
  fi
endef
####################

####################
#checks in the version resource
define uninstall-version
  if [ "$$VERSIONSRC" != "" ]; then \
    /usr/atria/bin/cleartool lsco -cview $$VERSIONSRC | $(GREP) $$VERSIONSRC >/dev/null 2>&1; \
    if [ $$? -eq 0 ]; then \
      if [ $$CLEANING -eq 1 ]; then \
		echo "Uninstalling version resource "$$VERSIONSRC; \
        /usr/atria/bin/cleartool unco -rm $$VERSIONSRC; \
      else \
        /usr/bin/rm -f $$VERSIONSRC; \
        CURVER=`/usr/atria/bin/cleartool ls $$VERSIONSRC | /usr/bin/nawk '{print $$3}'`; \
        LATESTVER=`echo $$CURVER | sed "s/[0-9]*$$/LATEST/"`; \
        /usr/atria/bin/cleartool find $$VERSIONSRC@@ -version "version($$CURVER)&& version($$LATESTVER)" -print | grep $$VERSIONSRC > /dev/null 2>&1; \
        if [ $$? -eq 1 ]; then \
		  echo "Uninstalling version resource "$$VERSIONSRC; \
          /usr/atria/bin/cleartool unco -rm $$VERSIONSRC; \
        fi; \
      fi; \
	fi;\
  fi
endef
####################

####################
#implementation adapted from ../scripts/updateVersion.ksh
define set-version
  if [ "$$VERSIONSRC" != "" ]; then \
    /usr/atria/bin/cleartool lsco -cview $$VERSIONSRC | grep $$VERSIONSRC >/dev/null 2>&1; \
    if [ $$? -ne 0 ]; then \
        /usr/atria/bin/cleartool co -res -c "set version number from build" $$VERSIONSRC; \
    fi; \
    /usr/bin/perl -i -p -e "s/$(VERTXTPLACEHOLDER).*/$(VERTXTPLACEHOLDER)=$$IMPL_VERSION.0/" $$VERSIONSRC; \
  fi
endef
####################

setversion::
	@VERSION=$(IMPL_VERSION);\
	VERSIONSRC=$(VERSIONSRC); \
	$(set-version)

