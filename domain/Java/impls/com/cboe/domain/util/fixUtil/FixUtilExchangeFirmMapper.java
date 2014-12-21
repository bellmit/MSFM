package com.cboe.domain.util.fixUtil;

import java.util.StringTokenizer;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Author: beniwalv
 * Date: Aug 5, 2004
 * Time: 11:09:27 AM
 */
public class FixUtilExchangeFirmMapper {
    ExchangeFirmStruct exchangeFirmStruct;

    private static final ExchangeFirmStruct EMPTY_XCHNG_FIRM = new ExchangeFirmStruct("", "");
    public final static String CONFIGURED_SECURITY_EXCHANGE = "configuredSecurityExchange";
    static final String defaultExchange = getDefaultExchange();

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
                    .append("The FixUtilExchangeFirmMapper is set as : ").append(configuredExchange)
                    .toString());
        } else {
            configuredExchange = "CBOE";
            Log.information(new StringBuilder(50)
                    .append("The FixUtilExchangeFirmMapper is set as : ").append(configuredExchange)
                    .toString());
        }
        return configuredExchange;
    }


  /**
   *
   */
  public FixUtilExchangeFirmMapper(String exchangeFirmString) {
    mapStringToStruct(exchangeFirmString);
  }

  /**
   *
   */
  public FixUtilExchangeFirmMapper(ExchangeFirmStruct aExchangeFirmStruct) {
    exchangeFirmStruct = aExchangeFirmStruct;
  }

  /**
   * Maps the FIX string to an ExchangeFirmStruct
   */
  private void mapStringToStruct(String exchangeFirmString) {
    exchangeFirmStruct = EMPTY_XCHNG_FIRM;
    if(exchangeFirmString == null || exchangeFirmString.length() == 0) {
      return;
    }

    StringTokenizer tokenizer = new StringTokenizer(exchangeFirmString, ":");

    int tokenCount = tokenizer.countTokens();

    String exchange, firm;
    if (tokenCount == 1) {
        exchange = defaultExchange;
        firm     = exchangeFirmString;
    } else if (tokenCount == 2) {
        exchange = tokenizer.nextToken();
        firm     = tokenizer.nextToken();
    } else {
        return;
    }

    exchangeFirmStruct = new ExchangeFirmStruct(exchange, firm);
  }

  /**
   * Get the cmi struct from the values in the FIX string;
   */
  public ExchangeFirmStruct getCMIExchangeFirmStruct() {
    return exchangeFirmStruct;
  }

  /**
   * Get the FIX string from the values in the struct.
   */
  public String getFIXExchangeFirmString() {
    if(exchangeFirmStruct == null) {
      return null;
    }
    else if(exchangeFirmStruct.exchange == null && exchangeFirmStruct.firmNumber == null) {
      return null;
    }
    else if(exchangeFirmStruct.exchange.length() == 0 && exchangeFirmStruct.firmNumber.length() == 0) {
      return null;
    }

    StringBuilder result = new StringBuilder(exchangeFirmStruct.exchange.length()+exchangeFirmStruct.firmNumber.length()+1);
    result.append(exchangeFirmStruct.exchange).append(':').append(exchangeFirmStruct.firmNumber);
    return result.toString();
  }
}
