#
# To use this makefile you must specify the following options on the command line
#  DB_USER=<userid>
#  DB_PASSWORD=<password>
#  DB_CONNECT=<db_connect>
#  For example:
#    clearmake -C gnu DB_USER=tradeeng DB_PASSWORD=tradeeng DB_CONNECT=ATGSVR1_DB1 all
# OPTIONAL :DIR_NAME = directory name you want exported files to go.
# By default it will go to $(RUN_DIR)/sql/data/test/exp_data
# If DI_NAME sepcified, it will go to $(RUN_DIR)/sql/data/test/exp_data$DIR_NAME
#
#  Use the imp_test_data and exp_test_data for importing and exporting test data.
#  
#


TEST_DATA_EXT=dmp

SQL_DIR=.
TEST_DATA_DIR=$(SQL_DIR)/data/test
EXP_TEST_DATA_DIR=$(TEST_DATA_DIR)/exp_data$(DIR_NAME)


include $(SQL_DIR)/domain_sql.mk


DDL_COMPILE=sqlplus -s $(DB_USER)/$(DB_PASSWORD)@$(DB_CONNECT)


IMP=$$ORACLE_HOME/bin/imp $(DB_USER)/$(DB_PASSWORD)@$(DB_CONNECT) IGNORE=Y 

EXP=$$ORACLE_HOME/bin/exp $(DB_USER)/$(DB_PASSWORD)@$(DB_CONNECT) GRANTS=N INDEXES=N ROWS=Y CONSTRAINTS=N TRIGGERS=N

default_target:
	@echo "you must specify a target: all, imp_test_data, exp_test_data"

all: tables
	@echo "Done."

tables: 
	@FILE=/tmp/db_build.$$$$;\
	cat $(TABLE_DDL_FILES) > $$FILE;\
	echo "\nquit" >> $$FILE;\
	$(DDL_COMPILE) @$$FILE;\
	rm -f $$FILE



imp_test_data: 
	@for i in $(TEST_DATA);\
	do \
		if [ -f $(TEST_DATA_DIR)/$$i.$(TEST_DATA_EXT) ];\
		then \
			$(IMP) TABLES=$$i FILE=$(TEST_DATA_DIR)/$$i.$(TEST_DATA_EXT); \
		else \
			echo Warning - test data file not found: $(TEST_DATA_DIR)/$$i.$(TEST_DATA_EXT) - skipping this file; \
		fi \
	done


update_config:
	@$(DDL_COMPILE) @updateConfigurationGroup.sql $(PREFIX)	prefix

prepare_config:
	@$(DDL_COMPILE) @updateConfigurationGroup.sql prefix $(PREFIX)	

exp_test_data: 
	@if [ ! -d $(EXP_TEST_DATA_DIR) ]; then\
		mkdir $(EXP_TEST_DATA_DIR);\
	fi;\
	for i in $(TEST_DATA);\
	do\
		$(EXP) TABLES=$$i FILE=$(EXP_TEST_DATA_DIR)/$$i.$(TEST_DATA_EXT);\
	done

ci_test_data:
	@for i in $(TEST_DATA);\
	do\
		/usr/atria/bin/cleartool co -nc $(TEST_DATA_DIR)/$$i.$(TEST_DATA_EXT);\
		mv $(EXP_TEST_DATA_DIR)/$$i.$(TEST_DATA_EXT) $(TEST_DATA_DIR)/$$i.$(TEST_DATA_EXT);\
		/usr/atria/bin/cleartool ci -c "checkin by the sql:Makefile:ci_test_data rule."  $(TEST_DATA_DIR)/$$i.$(TEST_DATA_EXT);\
	done


