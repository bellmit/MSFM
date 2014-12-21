@echo off

setlocal

set CLASSPATH=.;..\tools\velocity-1.3.1.jar;..\tools\velocity-dep-1.3.1.jar

javac dv_velocity.java

java dv_velocity DV_CollectionHelper.vtl                                        > ..\Java\impls\com\cboe\client\util\CollectionHelper.java

java dv_velocity DV_junitMultipleValuesMap.vtl                                  > ..\Java\impls\com\cboe\client\util\junit\junitMultipleValuesMap.java

java dv_velocity DV_XYMap.vtl Int Int                                           > ..\Java\impls\com\cboe\client\util\collections\IntIntMap.java
java dv_velocity DV_XYMap.vtl Int Long                                          > ..\Java\impls\com\cboe\client\util\collections\IntLongMap.java
java dv_velocity DV_XYMap.vtl Int Object                                        > ..\Java\impls\com\cboe\client\util\collections\IntObjectMap.java
java dv_velocity DV_XYMap.vtl Int String                                        > ..\Java\impls\com\cboe\client\util\collections\IntStringMap.java
java dv_velocity DV_XYMap.vtl Object Int                                        > ..\Java\impls\com\cboe\client\util\collections\ObjectIntMap.java
java dv_velocity DV_XYMap.vtl Object Object                                     > ..\Java\impls\com\cboe\client\util\collections\ObjectObjectMap.java
java dv_velocity DV_XYMap.vtl String Object                                     > ..\Java\impls\com\cboe\client\util\collections\StringObjectMap.java

java dv_velocity DV_XYMultipleValuesMap.vtl Int Int                             > ..\Java\impls\com\cboe\client\util\collections\IntIntMultipleValuesMap.java
java dv_velocity DV_XYMultipleValuesMap.vtl Int Object                          > ..\Java\impls\com\cboe\client\util\collections\IntObjectMultipleValuesMap.java
java dv_velocity DV_XYMultipleValuesMap.vtl Int String                          > ..\Java\impls\com\cboe\client\util\collections\IntStringMultipleValuesMap.java
java dv_velocity DV_XYMultipleValuesMap.vtl Object Int                          > ..\Java\impls\com\cboe\client\util\collections\ObjectIntMultipleValuesMap.java
java dv_velocity DV_XYMultipleValuesMap.vtl Object Object                       > ..\Java\impls\com\cboe\client\util\collections\ObjectObjectMultipleValuesMap.java

java dv_velocity DV_XYArrayHolder.vtl Int Int                                   > ..\Java\impls\com\cboe\client\util\collections\IntIntArrayHolder.java
java dv_velocity DV_XYArrayHolderIF.vtl Int Int                                 > ..\Java\impls\com\cboe\client\util\collections\IntIntArrayHolderIF.java
java dv_velocity DV_XYArrayHolder.vtl Int Long                                  > ..\Java\impls\com\cboe\client\util\collections\IntLongArrayHolder.java
java dv_velocity DV_XYArrayHolderIF.vtl Int Long                                > ..\Java\impls\com\cboe\client\util\collections\IntLongArrayHolderIF.java
java dv_velocity DV_XYArrayHolder.vtl Long Int                                  > ..\Java\impls\com\cboe\client\util\collections\LongIntArrayHolder.java
java dv_velocity DV_XYArrayHolderIF.vtl Long Int                                > ..\Java\impls\com\cboe\client\util\collections\LongIntArrayHolderIF.java
java dv_velocity DV_XYArrayHolder.vtl Int Object                                > ..\Java\impls\com\cboe\client\util\collections\IntObjectArrayHolder.java
java dv_velocity DV_XYArrayHolderIF.vtl Int Object                              > ..\Java\impls\com\cboe\client\util\collections\IntObjectArrayHolderIF.java
java dv_velocity DV_XYArrayHolder.vtl Int String                                > ..\Java\impls\com\cboe\client\util\collections\IntStringArrayHolder.java
java dv_velocity DV_XYArrayHolderIF.vtl Int String                              > ..\Java\impls\com\cboe\client\util\collections\IntStringArrayHolderIF.java
java dv_velocity DV_XYArrayHolder.vtl Object Int                                > ..\Java\impls\com\cboe\client\util\collections\ObjectIntArrayHolder.java
java dv_velocity DV_XYArrayHolderIF.vtl Object Int                              > ..\Java\impls\com\cboe\client\util\collections\ObjectIntArrayHolderIF.java
java dv_velocity DV_XYArrayHolder.vtl Object Object                             > ..\Java\impls\com\cboe\client\util\collections\ObjectObjectArrayHolder.java
java dv_velocity DV_XYArrayHolderIF.vtl Object Object                           > ..\Java\impls\com\cboe\client\util\collections\ObjectObjectArrayHolderIF.java
java dv_velocity DV_XYArrayHolder.vtl String Object                             > ..\Java\impls\com\cboe\client\util\collections\StringObjectArrayHolder.java
java dv_velocity DV_XYArrayHolderIF.vtl String Object                           > ..\Java\impls\com\cboe\client\util\collections\StringObjectArrayHolderIF.java

java dv_velocity DV_XArrayHolder.vtl Int                                        > ..\Java\impls\com\cboe\client\util\collections\IntArrayHolder.java
java dv_velocity DV_XArrayHolderIF.vtl Int                                      > ..\Java\impls\com\cboe\client\util\collections\IntArrayHolderIF.java
java dv_velocity DV_XArrayHolder.vtl Long                                       > ..\Java\impls\com\cboe\client\util\collections\LongArrayHolder.java
java dv_velocity DV_XArrayHolderIF.vtl Long                                     > ..\Java\impls\com\cboe\client\util\collections\LongArrayHolderIF.java
java dv_velocity DV_XArrayHolder.vtl Object                                     > ..\Java\impls\com\cboe\client\util\collections\ObjectArrayHolder.java
java dv_velocity DV_XArrayHolderIF.vtl Object                                   > ..\Java\impls\com\cboe\client\util\collections\ObjectArrayHolderIF.java
java dv_velocity DV_XArrayHolder.vtl String                                     > ..\Java\impls\com\cboe\client\util\collections\StringArrayHolder.java
java dv_velocity DV_XArrayHolderIF.vtl String                                   > ..\Java\impls\com\cboe\client\util\collections\StringArrayHolderIF.java

java dv_velocity DV_XYKeyValuePolicy.vtl Int Int                                > ..\Java\impls\com\cboe\client\util\collections\IntIntKeyValuePolicy.java
java dv_velocity DV_XYKeyValuePolicyIF.vtl Int Int                              > ..\Java\impls\com\cboe\client\util\collections\IntIntKeyValuePolicyIF.java
java dv_velocity DV_XYKeyValuePolicy.vtl Int Long                               > ..\Java\impls\com\cboe\client\util\collections\IntLongKeyValuePolicy.java
java dv_velocity DV_XYKeyValuePolicyIF.vtl Int Long                             > ..\Java\impls\com\cboe\client\util\collections\IntLongKeyValuePolicyIF.java
java dv_velocity DV_XYKeyValuePolicy.vtl Int Object                             > ..\Java\impls\com\cboe\client\util\collections\IntObjectKeyValuePolicy.java
java dv_velocity DV_XYKeyValuePolicyIF.vtl Int Object                           > ..\Java\impls\com\cboe\client\util\collections\IntObjectKeyValuePolicyIF.java
java dv_velocity DV_XYKeyValuePolicy.vtl Int String                             > ..\Java\impls\com\cboe\client\util\collections\IntStringKeyValuePolicy.java
java dv_velocity DV_XYKeyValuePolicyIF.vtl Int String                           > ..\Java\impls\com\cboe\client\util\collections\IntStringKeyValuePolicyIF.java
java dv_velocity DV_XYKeyValuePolicy.vtl Long Int                               > ..\Java\impls\com\cboe\client\util\collections\LongIntKeyValuePolicy.java
java dv_velocity DV_XYKeyValuePolicyIF.vtl Long Int                             > ..\Java\impls\com\cboe\client\util\collections\LongIntKeyValuePolicyIF.java
java dv_velocity DV_XYKeyValuePolicy.vtl Object Int                             > ..\Java\impls\com\cboe\client\util\collections\ObjectIntKeyValuePolicy.java
java dv_velocity DV_XYKeyValuePolicyIF.vtl Object Int                           > ..\Java\impls\com\cboe\client\util\collections\ObjectIntKeyValuePolicyIF.java
java dv_velocity DV_XYKeyValuePolicy.vtl Object Object                          > ..\Java\impls\com\cboe\client\util\collections\ObjectObjectKeyValuePolicy.java
java dv_velocity DV_XYKeyValuePolicyIF.vtl Object Object                        > ..\Java\impls\com\cboe\client\util\collections\ObjectObjectKeyValuePolicyIF.java

java dv_velocity DV_XKeyPolicy.vtl Int                                          > ..\Java\impls\com\cboe\client\util\collections\IntKeyPolicy.java
java dv_velocity DV_XKeyPolicyIF.vtl Int                                        > ..\Java\impls\com\cboe\client\util\collections\IntKeyPolicyIF.java
java dv_velocity DV_XKeyPolicy.vtl Long                                         > ..\Java\impls\com\cboe\client\util\collections\LongKeyPolicy.java
java dv_velocity DV_XKeyPolicyIF.vtl Long                                       > ..\Java\impls\com\cboe\client\util\collections\LongKeyPolicyIF.java
java dv_velocity DV_XKeyPolicy.vtl Object                                       > ..\Java\impls\com\cboe\client\util\collections\ObjectKeyPolicy.java
java dv_velocity DV_XKeyPolicyIF.vtl Object                                     > ..\Java\impls\com\cboe\client\util\collections\ObjectKeyPolicyIF.java
java dv_velocity DV_XKeyPolicy.vtl String                                       > ..\Java\impls\com\cboe\client\util\collections\StringKeyPolicy.java
java dv_velocity DV_XKeyPolicyIF.vtl String                                     > ..\Java\impls\com\cboe\client\util\collections\StringKeyPolicyIF.java

java dv_velocity DV_XReferenceCountMap.vtl Int                                  > ..\Java\impls\com\cboe\client\util\collections\IntReferenceCountMap.java
java dv_velocity DV_XReferenceCountMap.vtl Long                                 > ..\Java\impls\com\cboe\client\util\collections\LongReferenceCountMap.java
java dv_velocity DV_XReferenceCountMap.vtl Object                               > ..\Java\impls\com\cboe\client\util\collections\ObjectReferenceCountMap.java
java dv_velocity DV_XReferenceCountMap.vtl String                               > ..\Java\impls\com\cboe\client\util\collections\StringReferenceCountMap.java
java dv_velocity DV_XReferenceCountMap.vtl Comparable                           > ..\Java\impls\com\cboe\client\util\collections\ComparableReferenceCountMap.java

java dv_velocity DV_XYVisitor.vtl Int                                           > ..\Java\impls\com\cboe\client\util\collections\IntVisitorIF.java
java dv_velocity DV_XYVisitor.vtl Long                                          > ..\Java\impls\com\cboe\client\util\collections\LongVisitorIF.java
java dv_velocity DV_XYVisitor.vtl Object                                        > ..\Java\impls\com\cboe\client\util\collections\ObjectVisitorIF.java

java dv_velocity DV_XYVisitor.vtl Int Int                                       > ..\Java\impls\com\cboe\client\util\collections\IntIntVisitorIF.java
java dv_velocity DV_XYVisitor.vtl Int Long                                      > ..\Java\impls\com\cboe\client\util\collections\IntLongVisitorIF.java
java dv_velocity DV_XYVisitor.vtl Long Int                                      > ..\Java\impls\com\cboe\client\util\collections\LongIntVisitorIF.java
java dv_velocity DV_XYVisitor.vtl Int Object                                    > ..\Java\impls\com\cboe\client\util\collections\IntObjectVisitorIF.java
java dv_velocity DV_XYVisitor.vtl Object Object                                 > ..\Java\impls\com\cboe\client\util\collections\ObjectObjectVisitorIF.java
java dv_velocity DV_XYVisitor.vtl Object Int                                    > ..\Java\impls\com\cboe\client\util\collections\ObjectIntVisitorIF.java


:end

endlocal