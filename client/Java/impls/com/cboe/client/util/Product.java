package com.cboe.client.util;

import com.cboe.domain.util.fixUtil.FixUtilDateHelper;
import com.cboe.domain.util.fixUtil.FixUtilPriceHelper;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiConstants.OptionTypes;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Piyush Patel
 *         Date: April 1, 2010
 *         Time: 10:10:10 AM
 */
public class Product
{

    // OSI flag - should only be set for FIX
    // (no other xml should have the property)
      private static boolean isOSI;
      private static String osiSessions = null;
      static {
          try {
              osiSessions = System.getProperty("OSIsessions");
              if ((osiSessions != null) && (osiSessions.length() > 0)) {
                  isOSI = true;
              } else {
                  isOSI = false;
              }
          } catch (Exception e) {
              Log.exception("OSI initialization exception", e);
              isOSI = false ;
          }
          Log.information("OSI:" + isOSI);
      }




    protected String productSymbol = "";
    protected int hashCode = 0;

    public Product(String productSymbol)
    {
        this.productSymbol = productSymbol;
        this.hashCode = productSymbol.hashCode();
    }
    public int hashCode()
    {
        return hashCode;
    }

    public boolean equals(Object o)
    {
        return productSymbol.equals(o.toString());
    }

    public String toString()
    {
        return productSymbol;
    }

    static class OptionProduct extends Product
    {
        String reportingClass;
        String maturityMonthYear;
        String maturityDay;
        double exercisePrice;
        int optionType;

        OptionProduct(String reportingClass, String maturityMonthYear, String maturityDay, double exercisePrice, int optionType)
        {
            super("");
            this.reportingClass = reportingClass;
            this.maturityMonthYear = maturityMonthYear;
            this.maturityDay = maturityDay;
            this.exercisePrice = exercisePrice;
            this.optionType = optionType;
            this.hashCode = getHashCode();
        }

        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            OptionProduct that = (OptionProduct) o;

            if (Double.compare(that.exercisePrice, exercisePrice) != 0)
            {
                return false;
            }
            if (optionType != that.optionType)
            {
                return false;
            }
            if (reportingClass!=null?!reportingClass.equals(that.reportingClass):that.reportingClass!=null)
            {
                return false;
            }
            if (isOSI && !maturityDay.equals(that.maturityDay))
            {
                return false;
            }
            if (!maturityMonthYear.equals(that.maturityMonthYear))
            {
                return false;
            }

            return true;
        }

        public int getHashCode()
        {
            int result = super.hashCode();
            long temp;
            result = 31 * result + reportingClass.hashCode();
            result = 31 * result + maturityMonthYear.hashCode();
            result = 31 * result + (isOSI ? maturityDay.hashCode() : 0);
            temp = exercisePrice != +0.0d ? Double.doubleToLongBits(exercisePrice) : 0L;
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + optionType;
            return result;
        }

        public int hashCode()
        {
            return hashCode;
        }

        public String toString()
        {
            StringBuilder sb = new StringBuilder(100);
            sb.append("OPTION ");
            sb.append(super.toString());
            sb.append(" RC:");
            sb.append(reportingClass);
            sb.append(" MaturityYearMonth:");
            sb.append(maturityMonthYear);
            sb.append(" MaturityDay:");
            sb.append(maturityDay);
            sb.append(" ExercisePrice:");
            sb.append(exercisePrice);
            sb.append(" OptionType:");
            sb.append(optionType);
            sb.append(" isOSI:");
            sb.append(isOSI);

            return sb.toString();
        }
    }

    ;

    static class CommonStockorIndexProduct extends Product
    {

        public CommonStockorIndexProduct(String productSymbol)
        {
            super(productSymbol);
        }

    }

    ;


    static class FutureProduct extends Product
    {
        String maturityMonthYear;
        String reportingClass;

        FutureProduct(String reportingClass, String maturityMonthYear)
        {
            super("");
            this.reportingClass = reportingClass;
            this.maturityMonthYear = maturityMonthYear;
        }

        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            FutureProduct that = (FutureProduct) o;
            if (reportingClass!=null?!reportingClass.equals(that.reportingClass):that.reportingClass!=null)
            {
                return false;
            }
            if (!maturityMonthYear.equals(that.maturityMonthYear))
            {
                return false;
            }

            return true;
        }

        public int hashCode()
        {
            int result = super.hashCode();
            result = 31 * result + (reportingClass!=null?reportingClass.hashCode():0);
            result = 31 * result + (maturityMonthYear!=null?maturityMonthYear.hashCode():0);
            return result;
        }

        public String toString()
        {
            StringBuilder sb = new StringBuilder(100);
            sb.append("FUTURE ");
            sb.append(super.toString());
            sb.append(" RC:");
            sb.append(reportingClass);
            sb.append(" MaturityYearMonth:");
            sb.append(maturityMonthYear);
            return sb.toString();
        }
    }

    public static Product createProduct(ProductStruct productStruct)
    {
        return createProduct(productStruct.productName);
    }
    public static Product createProduct(ProductNameStruct productNameStruct)
    {

        if (productNameStruct.productSymbol != null && productNameStruct.productSymbol.length() > 0)
        {
            return new CommonStockorIndexProduct(productNameStruct.productSymbol);
        }
        else if (productNameStruct.optionType == OptionTypes.CALL || productNameStruct.optionType == OptionTypes.PUT )
        {
            String maturityYYYYMM = FixUtilDateHelper.dateStructToYYYYMM(productNameStruct.expirationDate);
            return new OptionProduct(productNameStruct.reportingClass, maturityYYYYMM, String.valueOf(productNameStruct.expirationDate.day), FixUtilPriceHelper.priceStructToDouble(productNameStruct.exercisePrice), productNameStruct.optionType);
        }
        else
        {
            String maturityYYYYMM = FixUtilDateHelper.dateStructToYYYYMM(productNameStruct.expirationDate);
            return new FutureProduct(productNameStruct.reportingClass, maturityYYYYMM);
        }

    }



    public static Product createOptionProduct(String reportingClass, String maturityMonthYear, String maturityDay, double exercisePrice, int optionType)
    {
        return new OptionProduct(reportingClass, maturityMonthYear, maturityDay, exercisePrice, optionType);
    }

    public static Product createFutureProduct(String reportingClass, String maturityMonthYear)
    {
        return new FutureProduct(reportingClass,maturityMonthYear);
    }

    public static Product createCommonStockorIndexProduct(String productSymbol)
    {
        return new CommonStockorIndexProduct(productSymbol);
    }


}