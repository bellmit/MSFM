#include "com_cboe_util_UniqueNumberGenerator.h"


JNIEXPORT jint JNICALL Java_com_cboe_util_UniqueNumberGenerator_getProcessID
  (JNIEnv *env, jobject object)
{
	return (jint) getpid();
}	
