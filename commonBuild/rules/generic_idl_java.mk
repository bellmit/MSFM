
###########################################################
# To use this include you must define the following macros
#
# IDLDIR - the location of the source to build
# IC_SWITCHES - the switches to pass to the idl compiler
# ALL_IDL_FILES - the list of .idl files 
#
# You must also define any macros specified by the 
#   $(COMMONBUILD)/rules/generic_java.mk
#
#
###########################################################


define  generate-modules
    $(clean-modules)
	mkdir -p $(JAVADIR)/modules
    COUNT=`ls $(JAVADIR)/*.out/*.mod 2>/dev/null | wc -l`; \
    if [ $$COUNT -ne 0 ]; then \
        for i in `cat $(JAVADIR)/*.out/*.mod | sort -u`;            \
        do                                                          \
            DIR="$(JAVADIR)/modules/"`echo $$i | sed "s/\./\//g"`;  \
            mkdir -p $$DIR;                                         \
                                                                    \
            MODULE=`echo $$i | sed "s/.*[.]//g"`;                   \
            CLASS=_$${MODULE}Module;                                \
            FILE=$$DIR/$${CLASS}.java;                              \
                                                                    \
            echo "package $$i;" > $$FILE;                           \
            echo "\tpublic class $$CLASS {}" >> $$FILE;                \
        done                                                        \
    fi

    touch $(MODULES_OUT)
endef

define clean-modules
    rm -f $(MODULES_OUT)
    rm -Rf $(JAVADIR)/modules
endef

ifdef MESSAGINGSYSTEM_IDLCOMPILER_JAR
    MYIDLCLASSPATH=$(MESSAGINGSYSTEM_IDLCOMPILER_JAR):$(JGLCLASSES)
else
    #for backward compatibility
    MYIDLCLASSPATH=$(IDLCOMJAR):$(JGLCLASSES)
endif

IDLCOMPILER=$(JAVA) -DORB.IRLocal=true -DIDLCompiler.PreProcessor.MaxCyclicCount=999 -cp $(MYIDLCLASSPATH) com.cboe.IDLCompiler.IDLParser $(IC_SWITCHES) -Z mod


ALL_OUTPUT_IDX_FILES=$(addprefix $(JAVADIR)/, $(ALL_IDL_FILES))
ALL_OUTPUT_IDX_DIRS=$(addsuffix .out, $(ALL_OUTPUT_IDX_FILES))



# Includes defines for other functionality
include $(GENERIC_JAVA_MK) 

#Redefine this value
JAVA_DIRS=$(ALL_OUTPUT_IDX_DIRS) $(JAVADIR)/modules

MODULES_OUT=$(JAVADIR)/modules.out


gen_clean::
	@$(clean-java)
	@$(clean-modules)


#in addition to the compilation in the generic_java, we need to create a dependency on the IDL output files
$(JAVA_COMPILE):: $(ALL_OUTPUT_IDX_FILES) $(MODULES_OUT)


$(MODULES_OUT):: $(ALL_OUTPUT_IDX_FILES) $(BUILD_ALL)
	@$(generate-modules)

#This will compile the IDL
$(JAVADIR)/%.ida : %.idl $(BUILD_ALL)
	@echo "Building $< ..."
	@rm -f $@
	@rm -Rf $@.out
	@$(IDLCOMPILER) -O $@.out -Async $<
	@touch $@

#This will compile the IDL
$(JAVADIR)/%.idn : %.idl $(BUILD_ALL)
	@echo "Building $< ..."
	@rm -f $@
	@rm -Rf $@.out
	@$(IDLCOMPILER) -O $@.out $<
	@touch $@


