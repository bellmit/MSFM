
# AUTHOR: DMITRY VOLPYANSKY

export CLASSPATH=../../tools/saxon7.jar:../../tools/saxon7-jdom.jar

export OUTPUT_ROOT_DIRECTORY="/vobs/dte/client/cfix/Java/impls"

export JAVA_HOME=/usr/java
JAVA=$JAVA_HOME/bin/java

$JAVA net.sf.saxon.Transform CboeFIX42.xml Fix_Class_Field_Main.xsl IMPLS_DIRECTORY=$OUTPUT_ROOT_DIRECTORY
$JAVA net.sf.saxon.Transform CboeFIX42.xml Fix_Class_FixHelper_Main.xsl IMPLS_DIRECTORY=$OUTPUT_ROOT_DIRECTORY
$JAVA net.sf.saxon.Transform CboeFIX42.xml Fix_Class_Message_Main.xsl IMPLS_DIRECTORY=$OUTPUT_ROOT_DIRECTORY
$JAVA net.sf.saxon.Transform CboeFIX42.xml Fix_Class_MessageFactory_Main.xsl IMPLS_DIRECTORY=$OUTPUT_ROOT_DIRECTORY
# $JAVA net.sf.saxon.Transform CboeFIX42.xml Fix_Perl_Decoder.xsl

