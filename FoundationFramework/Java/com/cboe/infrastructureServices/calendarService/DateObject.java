package com.cboe.infrastructureServices.calendarService;

public class DateObject{

	public int _year;
	public int _month;
	public int _date;

	public DateObject(int year, int month, int date)
	{
		this._year = year;
		this._month = month;
		this._date = date;
	}

	public boolean equals(Object object){
		if(!(object instanceof DateObject))
			return false;
		else
			return (this.hashCode() == object.hashCode());
	}

	public int hashCode(){
		return (_year * 10^4) + (_month * 10^2 ) + (_date);
	}

	public String toString(){
		return "DateObject" + hashCode();
	}
}	
