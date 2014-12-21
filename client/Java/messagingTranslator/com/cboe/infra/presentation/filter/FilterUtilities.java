package com.cboe.infra.presentation.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FilterUtilities
{

    /**
     * Returns a new collection contains the elements in c that pass
     * the filter f.  Collection c remains unmodified.
     */

    public static Collection filterCollection( Collection c, Filter f )
    {
        // we need to synchronize on the collection we're passed in
	    // case someone is modifying the collection.
	    // note that this only gives us some comfort if the
	    // collection we're passed is Synchronized
	    Collection rv = null;
	    synchronized (c)
	    {
			rv = new ArrayList();
			Iterator i = c.iterator();
			while( i.hasNext() )
			{
				Object o = i.next();
				if( f.accept( o ) )
				{
					rv.add( o );
				}
			}
	    }
        return rv;
    }


//    public static void filterTree(CBOETreeTableRow startingPoint, Filter f)
//    {
//        List rowChildren = startingPoint.getChildrenList();
//        for (int j = 0; j < rowChildren.size();++j)
//        {
//            CBOETreeTableRow kid = (CBOETreeTableRow) rowChildren.get(j);
//            filterTree(kid, f);
//        }
//        if (! f.accept( startingPoint ) )
//        {
//            startingPoint.setRowVisible(false);
//        }
//        else
//        {
//            startingPoint.setRowVisible(true);
//        }
//    }

}
