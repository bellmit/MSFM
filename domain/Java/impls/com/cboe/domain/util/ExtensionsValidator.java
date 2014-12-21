package com.cboe.domain.util;

/**
 * Interface required for user supplied validators
 *
 * The validator is used to validate both the key and the value
 *
 * Users do not have to provide a separate validator
 *
 * The user can choose to not validate values by simply returning true
 * from the <code>isValidValue()</code> operation.
 *
 */
public interface ExtensionsValidator {

    /**
      * Validate an extensions field key
      *
      * @param key <code>String</code> key to be validated
      * @return true if valid key, false otherwise
      */
      public boolean isValidKey(String key);

     /**
      * Validate an extensions field value (and key depending on implementation)
      *
      * @param key <code>String</code> key associated with value
      * @param value <code>String</code> value to be validated
      * @return true if valid value, false otherwise
      */
      public boolean isValidValue(String key, String value);

}
