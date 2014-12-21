export VOB=/sbt/prod/infra/v2run_dir/classes


export MYPATH=$VOB/domain_interfaces.jar:$MYPATH
export MYPATH=$VOB/client_translator.jar:$MYPATH
export MYPATH=$VOB/client_interfaces.jar:$MYPATH
export MYPATH=$VOB/server_interfaces.jar:$MYPATH
export MYPATH=$VOB/ffimpl.jar:$MYPATH
export MYPATH=$VOB/client_interceptors.jar:$MYPATH
export MYPATH=$VOB/cfe_impls.jar:$MYPATH
export MYPATH=$VOB/cfe_interceptors.jar:$MYPATH
export MYPATH=$VOB/cfIDLclasses.jar:$MYPATH
export MYPATH=$VOB/cfe_interfaces.jar:$MYPATH
export MYPATH=$VOB/domain_impls.jar:$MYPATH
export MYPATH=$VOB/client_impls.jar:$MYPATH
export MYPATH=$VOB/server_impls.jar:$MYPATH
export MYPATH=$VOB/clientIDL.jar:$MYPATH
export MYPATH=$VOB/serverIDL.jar:$MYPATH
export MYPATH=$VOB/Utility.jar:$MYPATH
export MYPATH=$VOB/infrastructure.jar:$MYPATH
export MYPATH=$VOB/saclient_interfaces.jar:$MYPATH
export MYPATH=$VOB/saclient_impls.jar:$MYPATH
export MYPATH=/sbt/prod/infra/run_dir/tmp:$VOB/FoundationFramework.jar:$MYPATH

export CLASSPATH=$MYPATH:$CLASSPATH
