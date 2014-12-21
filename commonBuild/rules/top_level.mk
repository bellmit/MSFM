# All *_rules.mk files included in a Makefile should define double colon rules for all and all_targets.
# Double colon rules will be concatenated together and executed as a single rule.  Therefore simply
# including an "all" rule in a top level makefile will build that component
#
# all  - defines how to build all DOs
# all_targets - executes an ACTION across all builds


all:: ACTION=all
all::
	@echo "Build finished successfully"

sall:: all
	$(MAKE) ACTION=sall generic
	@echo "Build finished successfully"

install:: ACTION=install
install:: all_targets
	@echo "install finished successfully"

uninstall:: ACTION=uninstall
uninstall:: all_targets
	@echo "uninstall finished successfully"

clean:: ACTION=clean
clean:: all_targets
	@echo "clean finished successfully"

generic:: all_targets
	@echo "generic finished successfully"

