@echo off

setlocal

REM AUTHOR: DMITRY VOLPYANSKY

set CLASSPATH=../../tools/saxon7.jar;../../tools/saxon7-jdom.jar

set OUTPUT_ROOT_DIRECTORY="/client/cfix/Java/impls"

java net.sf.saxon.Transform CboeFIX42.xml Fix_Class_Field_Main.xsl IMPLS_DIRECTORY=%OUTPUT_ROOT_DIRECTORY%
java net.sf.saxon.Transform CboeFIX42.xml Fix_Class_FixHelper_Main.xsl IMPLS_DIRECTORY=%OUTPUT_ROOT_DIRECTORY%
java net.sf.saxon.Transform CboeFIX42.xml Fix_Class_Message_Main.xsl IMPLS_DIRECTORY=%OUTPUT_ROOT_DIRECTORY%
java net.sf.saxon.Transform CboeFIX42.xml Fix_Class_MessageFactory_Main.xsl IMPLS_DIRECTORY=%OUTPUT_ROOT_DIRECTORY%
REM java net.sf.saxon.Transform CboeFIX42.xml Fix_Perl_Decoder.xsl

endlocal
