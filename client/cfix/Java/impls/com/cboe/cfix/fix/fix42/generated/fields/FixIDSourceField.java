package com.cboe.cfix.fix.fix42.generated.fields;

/**
 * FixIDSourceField.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * This GENERATED file implements FIX Protocol's Field Tag [22] (known as IDSource).
 *
 * WARNING: This entire file is completely generated by XSLT stylesheets from CboeFIX42.xml file.
 *
 */

import com.cboe.cfix.interfaces.*;

public abstract class FixIDSourceField implements FixFieldIF
{   
    public static final int    TagID         =  22;
    public static final String TagIDAsString = "22";
    public static final char[] TagIDAsChars  = {'2','2'};
    public static final String TagName       = "IDSource";
    public static final String TagFixType    = "STRING";

    public static final String  Cusip                                    = "1";
    public static final String  string_Cusip                             = Cusip;
    public static final char[]  chars_Cusip                              = {'1'};
    public static final String  tagged_Cusip                             = TagIDAsString + EQUALS + string_Cusip + SOH;
    public static final char[]  taggedchars_Cusip                        = {'2','2', EQUALSchar, '1', SOHchar};
    public static final String  text_Cusip                               = "Cusip";
    public static final String  Sedol                                    = "2";
    public static final String  string_Sedol                             = Sedol;
    public static final char[]  chars_Sedol                              = {'2'};
    public static final String  tagged_Sedol                             = TagIDAsString + EQUALS + string_Sedol + SOH;
    public static final char[]  taggedchars_Sedol                        = {'2','2', EQUALSchar, '2', SOHchar};
    public static final String  text_Sedol                               = "Sedol";
    public static final String  Quik                                     = "3";
    public static final String  string_Quik                              = Quik;
    public static final char[]  chars_Quik                               = {'3'};
    public static final String  tagged_Quik                              = TagIDAsString + EQUALS + string_Quik + SOH;
    public static final char[]  taggedchars_Quik                         = {'2','2', EQUALSchar, '3', SOHchar};
    public static final String  text_Quik                                = "Quik";
    public static final String  IsinNumber                               = "4";
    public static final String  string_IsinNumber                        = IsinNumber;
    public static final char[]  chars_IsinNumber                         = {'4'};
    public static final String  tagged_IsinNumber                        = TagIDAsString + EQUALS + string_IsinNumber + SOH;
    public static final char[]  taggedchars_IsinNumber                   = {'2','2', EQUALSchar, '4', SOHchar};
    public static final String  text_IsinNumber                          = "IsinNumber";
    public static final String  RicCode                                  = "5";
    public static final String  string_RicCode                           = RicCode;
    public static final char[]  chars_RicCode                            = {'5'};
    public static final String  tagged_RicCode                           = TagIDAsString + EQUALS + string_RicCode + SOH;
    public static final char[]  taggedchars_RicCode                      = {'2','2', EQUALSchar, '5', SOHchar};
    public static final String  text_RicCode                             = "RicCode";
    public static final String  IsoCurrencyCode                          = "6";
    public static final String  string_IsoCurrencyCode                   = IsoCurrencyCode;
    public static final char[]  chars_IsoCurrencyCode                    = {'6'};
    public static final String  tagged_IsoCurrencyCode                   = TagIDAsString + EQUALS + string_IsoCurrencyCode + SOH;
    public static final char[]  taggedchars_IsoCurrencyCode              = {'2','2', EQUALSchar, '6', SOHchar};
    public static final String  text_IsoCurrencyCode                     = "IsoCurrencyCode";
    public static final String  IsoCountryCode                           = "7";
    public static final String  string_IsoCountryCode                    = IsoCountryCode;
    public static final char[]  chars_IsoCountryCode                     = {'7'};
    public static final String  tagged_IsoCountryCode                    = TagIDAsString + EQUALS + string_IsoCountryCode + SOH;
    public static final char[]  taggedchars_IsoCountryCode               = {'2','2', EQUALSchar, '7', SOHchar};
    public static final String  text_IsoCountryCode                      = "IsoCountryCode";
    public static final String  ExchangeSymbol                           = "8";
    public static final String  string_ExchangeSymbol                    = ExchangeSymbol;
    public static final char[]  chars_ExchangeSymbol                     = {'8'};
    public static final String  tagged_ExchangeSymbol                    = TagIDAsString + EQUALS + string_ExchangeSymbol + SOH;
    public static final char[]  taggedchars_ExchangeSymbol               = {'2','2', EQUALSchar, '8', SOHchar};
    public static final String  text_ExchangeSymbol                      = "ExchangeSymbol";
    public static final String  ConsolidatedTapeAssociation              = "9";
    public static final String  string_ConsolidatedTapeAssociation       = ConsolidatedTapeAssociation;
    public static final char[]  chars_ConsolidatedTapeAssociation        = {'9'};
    public static final String  tagged_ConsolidatedTapeAssociation       = TagIDAsString + EQUALS + string_ConsolidatedTapeAssociation + SOH;
    public static final char[]  taggedchars_ConsolidatedTapeAssociation  = {'2','2', EQUALSchar, '9', SOHchar};
    public static final String  text_ConsolidatedTapeAssociation         = "ConsolidatedTapeAssociation";

    private static FixIDSourceField flyweightCusip;
    public static final FixIDSourceField flyweightCusip()
    {
        if (flyweightCusip == null)
        {
            synchronized(FixIDSourceField.class)
            {
                if (flyweightCusip == null)
                {
                    flyweightCusip  = new FixIDSourceField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_Cusip;}
                     public String  getValueDescription()             {return text_Cusip;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_Cusip).append("|").append(text_Cusip).append("]").toString();}
                     public boolean isCusip()                         {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_Cusip);}
                    };
                }
            }
        }

        return flyweightCusip;
    }

    private static FixIDSourceField flyweightSedol;
    public static final FixIDSourceField flyweightSedol()
    {
        if (flyweightSedol == null)
        {
            synchronized(FixIDSourceField.class)
            {
                if (flyweightSedol == null)
                {
                    flyweightSedol  = new FixIDSourceField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_Sedol;}
                     public String  getValueDescription()             {return text_Sedol;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_Sedol).append("|").append(text_Sedol).append("]").toString();}
                     public boolean isSedol()                         {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_Sedol);}
                    };
                }
            }
        }

        return flyweightSedol;
    }

    private static FixIDSourceField flyweightQuik;
    public static final FixIDSourceField flyweightQuik()
    {
        if (flyweightQuik == null)
        {
            synchronized(FixIDSourceField.class)
            {
                if (flyweightQuik == null)
                {
                    flyweightQuik  = new FixIDSourceField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_Quik;}
                     public String  getValueDescription()             {return text_Quik;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_Quik).append("|").append(text_Quik).append("]").toString();}
                     public boolean isQuik()                          {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_Quik);}
                    };
                }
            }
        }

        return flyweightQuik;
    }

    private static FixIDSourceField flyweightIsinNumber;
    public static final FixIDSourceField flyweightIsinNumber()
    {
        if (flyweightIsinNumber == null)
        {
            synchronized(FixIDSourceField.class)
            {
                if (flyweightIsinNumber == null)
                {
                    flyweightIsinNumber  = new FixIDSourceField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_IsinNumber;}
                     public String  getValueDescription()             {return text_IsinNumber;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_IsinNumber).append("|").append(text_IsinNumber).append("]").toString();}
                     public boolean isIsinNumber()                    {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_IsinNumber);}
                    };
                }
            }
        }

        return flyweightIsinNumber;
    }

    private static FixIDSourceField flyweightRicCode;
    public static final FixIDSourceField flyweightRicCode()
    {
        if (flyweightRicCode == null)
        {
            synchronized(FixIDSourceField.class)
            {
                if (flyweightRicCode == null)
                {
                    flyweightRicCode  = new FixIDSourceField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_RicCode;}
                     public String  getValueDescription()             {return text_RicCode;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_RicCode).append("|").append(text_RicCode).append("]").toString();}
                     public boolean isRicCode()                       {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_RicCode);}
                    };
                }
            }
        }

        return flyweightRicCode;
    }

    private static FixIDSourceField flyweightIsoCurrencyCode;
    public static final FixIDSourceField flyweightIsoCurrencyCode()
    {
        if (flyweightIsoCurrencyCode == null)
        {
            synchronized(FixIDSourceField.class)
            {
                if (flyweightIsoCurrencyCode == null)
                {
                    flyweightIsoCurrencyCode  = new FixIDSourceField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_IsoCurrencyCode;}
                     public String  getValueDescription()             {return text_IsoCurrencyCode;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_IsoCurrencyCode).append("|").append(text_IsoCurrencyCode).append("]").toString();}
                     public boolean isIsoCurrencyCode()               {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_IsoCurrencyCode);}
                    };
                }
            }
        }

        return flyweightIsoCurrencyCode;
    }

    private static FixIDSourceField flyweightIsoCountryCode;
    public static final FixIDSourceField flyweightIsoCountryCode()
    {
        if (flyweightIsoCountryCode == null)
        {
            synchronized(FixIDSourceField.class)
            {
                if (flyweightIsoCountryCode == null)
                {
                    flyweightIsoCountryCode  = new FixIDSourceField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_IsoCountryCode;}
                     public String  getValueDescription()             {return text_IsoCountryCode;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_IsoCountryCode).append("|").append(text_IsoCountryCode).append("]").toString();}
                     public boolean isIsoCountryCode()                {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_IsoCountryCode);}
                    };
                }
            }
        }

        return flyweightIsoCountryCode;
    }

    private static FixIDSourceField flyweightExchangeSymbol;
    public static final FixIDSourceField flyweightExchangeSymbol()
    {
        if (flyweightExchangeSymbol == null)
        {
            synchronized(FixIDSourceField.class)
            {
                if (flyweightExchangeSymbol == null)
                {
                    flyweightExchangeSymbol  = new FixIDSourceField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_ExchangeSymbol;}
                     public String  getValueDescription()             {return text_ExchangeSymbol;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_ExchangeSymbol).append("|").append(text_ExchangeSymbol).append("]").toString();}
                     public boolean isExchangeSymbol()                {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_ExchangeSymbol);}
                    };
                }
            }
        }

        return flyweightExchangeSymbol;
    }

    private static FixIDSourceField flyweightConsolidatedTapeAssociation;
    public static final FixIDSourceField flyweightConsolidatedTapeAssociation()
    {
        if (flyweightConsolidatedTapeAssociation == null)
        {
            synchronized(FixIDSourceField.class)
            {
                if (flyweightConsolidatedTapeAssociation == null)
                {
                    flyweightConsolidatedTapeAssociation  = new FixIDSourceField()
                    {public boolean hasValue()                        {return true;}
                     public String  getValue()                        {return string_ConsolidatedTapeAssociation;}
                     public String  getValueDescription()             {return text_ConsolidatedTapeAssociation;}
                     public String  toString()                        {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(string_ConsolidatedTapeAssociation).append("|").append(text_ConsolidatedTapeAssociation).append("]").toString();}
                     public boolean isConsolidatedTapeAssociation()   {return true;}
                     public void    accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(taggedchars_ConsolidatedTapeAssociation);}
                    };
                }
            }
        }

        return flyweightConsolidatedTapeAssociation;
    }


    public boolean isCusip()                         {return false;}
    public boolean isSedol()                         {return false;}
    public boolean isQuik()                          {return false;}
    public boolean isIsinNumber()                    {return false;}
    public boolean isRicCode()                       {return false;}
    public boolean isIsoCurrencyCode()               {return false;}
    public boolean isIsoCountryCode()                {return false;}
    public boolean isExchangeSymbol()                {return false;}
    public boolean isConsolidatedTapeAssociation()   {return false;}

    public final int    getTag()                                      {return TagID;}
    public final String getTagAsString()                              {return TagIDAsString;}
    public final char[] getTagAsChars()                               {return TagIDAsChars;}
    public final String getTagName()                                  {return TagName;}
    public       String getValueDescription()                         {return getValue();}
    public       void   accept(FixMessageBuilderIF fixMessageBuilder) {fixMessageBuilder.append(TagIDAsChars, getValue());}
    public       String toString()                                    {return new StringBuilder(64).append(TagName).append("{").append(TagIDAsString).append("} [").append(getValue()).append(']').toString();}

    public static FixIDSourceField create(char[] array, int offset, int length)
    {
        return create(new String(array, offset, length));
    }

    public static FixIDSourceField create(final String paramString)
    {                                                                                               
        if (Cusip.equals(paramString))                 return flyweightCusip();
        if (Sedol.equals(paramString))                 return flyweightSedol();
        if (Quik.equals(paramString))                  return flyweightQuik();
        if (IsinNumber.equals(paramString))            return flyweightIsinNumber();
        if (RicCode.equals(paramString))               return flyweightRicCode();
        if (IsoCurrencyCode.equals(paramString))       return flyweightIsoCurrencyCode();
        if (IsoCountryCode.equals(paramString))        return flyweightIsoCountryCode();
        if (ExchangeSymbol.equals(paramString))        return flyweightExchangeSymbol();
        if (ConsolidatedTapeAssociation.equals(paramString)) return flyweightConsolidatedTapeAssociation();
        return new FixIDSourceField()
        {       public String  valueString = paramString;
                public boolean hasValue()                        {return valueString != null;}
                public String  getValue()                        {return valueString;}
                public String  getValueDescription()             {return valueString;}
        };
    }                                                                                               
}