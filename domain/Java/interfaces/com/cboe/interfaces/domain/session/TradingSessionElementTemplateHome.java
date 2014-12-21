package com.cboe.interfaces.domain.session;

import com.cboe.idl.session.TradingSessionElementTemplateStruct;
import com.cboe.idl.session.TemplateClassStruct;
import com.cboe.idl.session.TradingSessionElementTemplateStructV2;
import com.cboe.idl.product.ProductClassStruct;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.NotFoundException;

/**
 * A home for trading session element templates.
 *
 * @author John Wickberg
 */
public interface TradingSessionElementTemplateHome {

    /**
     * Name of the home for foundation framework.
     */
    public static final String HOME_NAME = "TradingSessionElementTemplateHome";

    /**
     * Builds class mapping for templates.
     * 
     * @param productClasses structs defining active product classes
     */
    void buildClassMapping(ProductClassStruct[] productClasses);
    
    /**
     * Creates a new template.
     *
     * @param newTemplate struct defining new template
     * @return created template
     * @exception TransactionFailedException if template cannot be created
     */
    TradingSessionElementTemplate create(TradingSession session, TradingSessionElementTemplateStruct newTemplate)
        throws DataValidationException, TransactionFailedException;

    /**
     * Finds all defined templates.
     *
     * @return all defined templates.
     */
    TradingSessionElementTemplate[] findAll();

    /**
     * Finds template with given name.
     *
     * @param templateName name of template
     * @return template having requested name
     * @exception NotFoundException if template is not found
     */
    TradingSessionElementTemplate findByName(String templateName) throws NotFoundException;

    /**
     * Finds all active templates for a session.
     *
     * @param sessionName name of the session.
     * @return active templates for the session
     */
    TradingSessionElementTemplate[] findActiveBySession(String sessionName);

    /**
     * Finds all templates for a session.
     *
     * @param sessionName name of the session.
     * @return templates defined for the session
     */
    TradingSessionElementTemplate[] findBySession(String sessionName);

    /**
     * Tests to see if templates are assigned to a session.
     */
    boolean isSessionInUse(String sessionName);
    
    /**
     * Removes template.
     *
     * @param templateName name of template to be removed.
     */
    void removeTemplate(String templateName) throws DataValidationException, TransactionFailedException;

    /**
     * Converts session element template to a struct.
     *
     * @param template template to be converted
     * @return struct corresponding to template
     */
    TradingSessionElementTemplateStruct toStruct(TradingSessionElementTemplate template);

    /**
     * Converts session element templates to a structs.
     *
     * @param templates templates to be converted
     * @return structs corresponding to templates
     */
    TradingSessionElementTemplateStruct[] toStructs(TradingSessionElementTemplate[] templates);

    /**
     * Updates a session element template from a struct.
     *
     * @param newValues struct containing updated values for a template
     */
    void updateTemplate(TradingSessionElementTemplateStruct newValues)
        throws NotFoundException, DataValidationException, TransactionFailedException;

    TradingSessionElementTemplateStructV2 toStructV2(TradingSessionElementTemplate template);
	TradingSessionElementTemplateStructV2[] toStructsV2(TradingSessionElementTemplate[] templates);

	TradingSessionElementTemplate create(TradingSession parentSession,TradingSessionElementTemplateStructV2 templateStruct) throws DataValidationException, TransactionFailedException;

	void updateTemplate(TradingSessionElementTemplateStructV2 templateStruct)throws NotFoundException, DataValidationException, TransactionFailedException;
}
