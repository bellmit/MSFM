package com.cboe.domain.util.fixUtil;

import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import java.util.StringTokenizer;

/**
 * Author: beniwalv
 * Date: Aug 5, 2004
 * Time: 1:55:50 PM
 */

public class FixUtilExchangeAcronymMapper {
  ExchangeAcronymStruct exchangeAcronymStruct = new ExchangeAcronymStruct("", "");
  public final static String CONFIGURED_SECURITY_EXCHANGE = "configuredSecurityExchange";
  static final String defaultExchange = getDefaultExchange();

  /**
   *
   */
  public FixUtilExchangeAcronymMapper(String exchangeAcronymString) {
    mapStringToStruct(exchangeAcronymString);
  }

  public static String getDefaultExchange(){
  String configuredExchange = System.getProperty(CONFIGURED_SECURITY_EXCHANGE);
      if (configuredExchange == null || configuredExchange.equals("")) {
          Log.alarm(new StringBuilder(40)
                  .append("No security exchange configured !").toString());
      } else {
          Log.information(new StringBuilder(55)
                  .append("Configured security exchange setting is : ").append(configuredExchange)
                  .toString());
          Log.information(new StringBuilder(70)
                  .append("Mapping security exchange from configuration setting to FIX String")
                  .toString());
      }
      if ( configuredExchange.equals("CBOE2") || configuredExchange.equals("C2") ) {
           configuredExchange = "CBOE2";
          Log.information(new StringBuilder(50)
                  .append("The FixUtilExchangeAcronymnMapper is set as : ").append(configuredExchange)
                  .toString());
      } else {
          configuredExchange = "CBOE";
          Log.information(new StringBuilder(50)
                  .append("The FixUtilExchangeAcronymnMapper is set as : ").append(configuredExchange)
                  .toString());
      }
      return configuredExchange;
  }

  /**
   *
   */
  public FixUtilExchangeAcronymMapper(ExchangeAcronymStruct aExchangeAcronymStruct) {
    exchangeAcronymStruct = aExchangeAcronymStruct;
  }

  /**
   * Maps the FIX string to an ExchangeAcronymStruct
   */
  private void mapStringToStruct(String exchangeAcronymString) {
    if(exchangeAcronymString == null || exchangeAcronymString.length() == 0) {
      return;
    }

    StringTokenizer tokenizer = new StringTokenizer(exchangeAcronymString, ":");

    int tokenCount = tokenizer.countTokens();

    if(tokenCount == 1) {
      exchangeAcronymStruct.exchange = defaultExchange;
      exchangeAcronymStruct.acronym = exchangeAcronymString;
    }
    else if(tokenCount == 2) {
      exchangeAcronymStruct.exchange = tokenizer.nextToken();
      exchangeAcronymStruct.acronym = tokenizer.nextToken();
    }
  }

  /**
   * Get the cmi struct from the values in the FIX string;
   */
  public ExchangeAcronymStruct getCMIExchangeAcronymStruct() {
    return exchangeAcronymStruct;
  }

  /**
   * Get the FIX string from the values in the struct.
   */
  public String getFIXExchangeAcronymString() {
    if(exchangeAcronymStruct == null) {
      return null;
    }
    else if(exchangeAcronymStruct.exchange == null && exchangeAcronymStruct.acronym == null) {
      return null;
    }
    else if(exchangeAcronymStruct.exchange.length() == 0 && exchangeAcronymStruct.acronym.length() == 0) {
      return null;
    }

    return exchangeAcronymStruct.exchange + ":" + exchangeAcronymStruct.acronym;
  }
}


