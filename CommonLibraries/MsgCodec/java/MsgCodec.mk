#######################################################################
# All macros defined in this file should be globally unique across
# all release and build components
#
# This makefile makes reference to macros defined elsewhere.  Please
# define the following when including this file
#   SERVER_JAVA = the directory location for the java files
#   SERVER_CLASSES = the directory location for the class files
#   SERVER_REL = the directory location for the jar files
#
#######################################################################

#######################################################################
# Defines specific jars and locations for this build component 
# 
#######################################################################
MSG_CODEC_JAVA        =/vobs/dte/CommonLibraries/MsgCodec/java
MSG_CODEC_CLASS       =/vobs/dte/CommonLibraries/MsgCodec/classes/java
MSG_CODEC_JARNAME     =MsgCodec
MSG_CODEC_JAR         =/vobs/dte/CommonLibraries/MsgCodec/release/$(MSG_CODEC_JARNAME).$(JAR_EXT)
MSG_CODEC_VERSIONSRC  =$(MSG_CODEC_JAVA)/$(MSG_CODEC_JARNAME).$(VERSION_EXT)

ifdef BUILDCHECKOUT
    MSG_CODEC_BUILD_JAR = $(MSG_CODEC_JAR)
else
    MSG_CODEC_BUILD_JAR = $(MSG_CODEC_CLASS)/$(MSG_CODEC_JARNAME).$(JAR_EXT)
endif
