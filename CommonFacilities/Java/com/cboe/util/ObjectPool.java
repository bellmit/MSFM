package com.cboe.util;

/**
 * Object Pool usage requirements:
 * 
 * Class of the object to be pooled, (see com.cboe.businessServices.orderBookService.OrderBookPriceItem)
 * 
 * 1.	Class variable statically initialized to desired ObjectPool using the ObjectPoolHome.
 * A configuration file may have to be set up for the 'create' method.
 * 
 * eg. private static ObjectPool myPool = ObjectPoolHome.getHome().create( new UserClass() );
 * 
 * 2.	Implement the Copyable interface.
 * 
 * 3.	getInstance() static method that delegates to checkOut().
 * 
 * 4.	returnInstance() static method that delegates to checkIn().
 * 
 * 5.   Added 
 * 
 * The calling object would just use the two static methods to get and return an instance of the object.
 *
 * @version 0.31
 * @author Kevin Park
 */
public interface ObjectPool<T extends Copyable> {

    public static final String TRACE_MODE = "trace";

    public static final String LOG_OVERFLOW = "overflow";
    /**
     * @author Kevin Park
     * @param returnedObject java.lang.Object
     */
    void checkIn(T returnedObject);
    /**
     * @author Kevin Park
     * @return java.lang.Object
     */
    T checkOut();

    /**
     * @return
     */
    public  boolean isLogOverflow();

    /**
     * @param p_logOverflow
     */
    public  void setLogOverflow(boolean p_logOverflow);

    /**
     * @return
     */
    public  boolean isTraceMode();

    /**
     * @param p_traceMode
     */
    public  void setTraceMode(boolean p_traceMode);
    
    public int getMaxNumSegments();

    public void setMaxNumSegments(int p_maxNumSegments);
    
    public void undoObjectPooling(StringBuffer returnValue);
    
    public void addMoreSegments(StringBuffer returnValue, int numberOfSegmentsToBeAdded);
    
}
