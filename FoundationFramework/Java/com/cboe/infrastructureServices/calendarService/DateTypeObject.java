package com.cboe.infrastructureServices.calendarService;

public class DateTypeObject{

        public DateObject dateObj;
        public int type;

        public DateTypeObject(DateObject dObj, int thisType)
        {
                this.dateObj = dObj;
                this.type = thisType;
        }

        public boolean equals(Object object)
        {
           if(!(object instanceof DateTypeObject))
           {
              return false;
           }
           else
           {
              if (this.dateObj.hashCode() == ((DateTypeObject)(object)).dateObj.hashCode())
              {
                 if (this.type == ((DateTypeObject)(object)).type)
                 {
                    return true;
                 }
              }
           }
           return false;
        }

        public String toString()
        {
           return "DateObject:" + this.dateObj.hashCode() + " and Type: " + this.type;
        }
}

