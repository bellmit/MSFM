package com.cboe.interfaces.domain.session;

import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.NotFoundException;

/**
 * A home for session element products.
 *
 * @author John Wickberg
 */
public interface SessionElementProductHome {

    /**
     * Name of home for foundation framework.
     */
    public static final String HOME_NAME = "SessionElementProductHome";

    /**
     * Creates a new instance.
     *
     * @param sessionClass parent of new instance
     * @param productKey product key of new instance
     * @return created instance
     * @exception TransactionFailedException if instance cannot be created
     */
    SessionElementProduct create(SessionElementClass sessionClass, int productKey)
        throws TransactionFailedException;
}

