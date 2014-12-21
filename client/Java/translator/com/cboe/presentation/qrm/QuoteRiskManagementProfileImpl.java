package com.cboe.presentation.qrm;

import com.cboe.domain.util.ReflectiveStructBuilder;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiProduct.ProductDescriptionStruct;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.ReportingClass;
import com.cboe.interfaces.presentation.qrm.QuoteRiskManagementProfile;
import com.cboe.interfaces.presentation.qrm.UserQuoteRiskManagementProfile;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.product.ProductHelper;
import org.omg.CORBA.UserException;

import java.util.Observable;

/**
 *  This class wraps <code>QuoteRiskManagementProfileStruct<code>, provides various getters and setters,
 *  implements <code>save()</code> and <code>remove()</code>  methods.
 */
public class QuoteRiskManagementProfileImpl extends Observable implements QuoteRiskManagementProfile
{
    private ProductClass productClass;
    private boolean modified = false;
    private String classString = null;
    public static final transient String DEFAULT_PROFILE = "DEFAULT";
    private String userId = null;
    private QuoteRiskManagementProfileStruct profileStruct;

    /**
     * QuoteRiskManagementProfile constructor comment.
     */
    public QuoteRiskManagementProfileImpl()
    {
        super();
    }

    /**
     * Constructs a new profile for the passed ProductClass
     * @param productClass to create new profile for
     */
    public QuoteRiskManagementProfileImpl(ProductClass productClass)
    {
        this();

        profileStruct = new QuoteRiskManagementProfileStruct();
        profileStruct.classKey = productClass.getClassKey();

        setQuoteRiskManagementEnabled(false);
        setTimeWindow(0);
        setVolumeThreshold(0);
        setModified(true);
    }

    /**
     * Constructs this profile based on an existing struct
     * @param profileStruct to initiate with
     */
    public QuoteRiskManagementProfileImpl(QuoteRiskManagementProfileStruct profileStruct)
    {
        this();
        setProfileStruct(profileStruct);
    }

    public boolean isAllSelectedProductClass()
    {
        return isAllSelected();
    }

    public boolean isDefaultProductClass()
    {
        return isDefaultProfile();
    }

    /**
     * Gets the profile struct this profile is for.
     * @return QuoteRiskManagementProfileStruct
     */
    public QuoteRiskManagementProfileStruct getProfileStruct()
    {
        return profileStruct;
    }

    /**
     * Must check first if this profile represents the default. If so, this method returns null.
     * @deprecated Use public getters to get struct contents always
     */
    public ClassStruct getClassStruct()
    {
        ClassStruct classStruct = null;
        if(getProductClass() != null)
        {
            classStruct = getProductClass().getClassStruct();
        }

        return classStruct;
    }

    /**
     * Gets the product class that this profile is for. Must check first if this profile represents
     * the default. If so, this method returns null.
     */
    public ProductClass getProductClass()
    {
        if(productClass == null && getProfileStruct() != null)
        {
            try
            {
                productClass = ProductHelper.getProductClassCheckInvalid(getClassKey());
            }
            catch(UserException e)
            {
                DefaultExceptionHandlerHome.find().process(e, "Could not find Product Class By Key : " + getClassKey());
            }
        }

        return productClass;
    }

    /**
     * Gets the class key from the profile
     * @return class key of class profile is for
     */
    public int getClassKey()
    {
        int classKey = -1;

        if(getProfileStruct() != null)
        {
            classKey = getProfileStruct().classKey;
        }
        return classKey;
    }

    /**
     * Gets the class symbol of the product class for this profile formatted as a string.
     * @return formatted string
     */
    public String getClassSymbol()
    {
        if(classString == null && getProductClass() != null)
        {
            setClassString(getProductClass().toString());
        }

        return classString;
    }

    /**
     * return true if the ProductClass is designated as a Test Class
     */
    public boolean isTestClass()
    {
        return getProductClass().isTestClass();
    }

    /**
     * Gets the product type of the ProductClass. Must check first if this profile represents
     * the default. If so, this method will return -1.
     */
    public short getProductType()
    {
        short type = -1;
        if(getProductClass() != null)
        {
            type = getProductClass().getProductType();
        }

        return type;
    }

    /**
     * Gets the underlying product from the ProductClass. Must check first if this profile represents
     * the default. If so, this method returns null.
     */
    public Product getUnderlyingProduct()
    {
        Product product = null;
        if(getProductClass() != null)
        {
            product = getProductClass().getUnderlyingProduct();
        }

        return product;
    }

    /**
     * Gets the listing state of the ProductClass. Must check first if this profile represents
     * the default. If so, this method will return -1.
     */
    public short getListingState()
    {
        short listingState = -1;
        if(getProductClass() != null)
        {
            listingState = getProductClass().getListingState();
        }

        return listingState;
    }

    /**
     * Gets the created time of the ProductClass. Must check first if this profile represents
     * the default. If so, this method will return null.
     */
    public DateTimeStruct getCreatedTime()
    {
        DateTimeStruct dateTime = null;
        if(getProductClass() != null)
        {
            dateTime = getProductClass().getCreatedTime();
        }

        return dateTime;
    }

    /**
     * Gets the last modified time of the ProductClass. Must check first if this profile represents
     * the default. If so, this method will return null.
     */
    public DateTimeStruct getLastModifiedTime()
    {
        DateTimeStruct dateTime = null;
        if(getProductClass() != null)
        {
            dateTime = getProductClass().getLastModifiedTime();
        }

        return dateTime;
    }

    /**
     * Gets the activation time of the ProductClass. Must check first if this profile represents
     * the default. If so, this method will return null.
     */
    public DateStruct getActivationDate()
    {
        DateStruct date = null;
        if(getProductClass() != null)
        {
            date = getProductClass().getActivationDate();
        }

        return date;
    }

    /**
     * Gets the inactivation time of the ProductClass. Must check first if this profile represents
     * the default. If so, this method will return null.
     */
    public DateStruct getInactivationDate()
    {
        DateStruct date = null;
        if(getProductClass() != null)
        {
            date = getProductClass().getInactivationDate();
        }

        return date;
    }

    /**
     * Gets the primary exchange of the ProductClass. Must check first if this profile represents
     * the default. If so, this method will return null.
     */
    public String getPrimaryExchange()
    {
        String primaryExchange = null;
        if(getProductClass() != null)
        {
            primaryExchange = getProductClass().getPrimaryExchange();
        }

        return primaryExchange;
    }

    /**
     * Gets the EPW structs of the ProductClass. Must check first if this profile represents
     * the default. If so, this method will return a zero length array.
     */
    public EPWStruct[] getEPWValues()
    {
        EPWStruct[] structs = new EPWStruct[0];
        if(getProductClass() != null)
        {
            structs = getProductClass().getEPWValues();
        }

        return structs;
    }

    /**
     * Gets the EPW fast market multiplier of the ProductClass. Must check first if this profile represents
     * the default. If so, this method will return a -1.
     */
    public double getEPWFastMarketMultiplier()
    {
        double multiplier = -1;
        if(getProductClass() != null)
        {
            multiplier = getProductClass().getEPWFastMarketMultiplier();
        }

        return multiplier;
    }

    /**
     * Gets the ProductDescriptionStruct of the ProductClass. Must check first if this profile represents
     * the default. If so, this method will return a null.
     */
    public ProductDescriptionStruct getProductDescription()
    {
        ProductDescriptionStruct description = null;
        if(getProductClass() != null)
        {
            description = getProductClass().getProductDescription();
        }

        return description;
    }

    /**
     * Gets all the reporting classes for this product class
     * @return an array of reporting classes
     */
    public ReportingClass[] getReportingClasses()
    {
        ReportingClass[] reportingClasses = new ReportingClass[0];

        if(getProductClass() != null)
        {
            reportingClasses = getProductClass().getReportingClasses();
        }
        return reportingClasses;
    }

    /**
     * Determines if this profile represents the default profile
     * @return boolean True if default, false otherwise
     */
    public boolean isDefaultProfile()
    {
        boolean isDefault = false;

        if(getProductClass() != null)
        {
            isDefault = getProductClass().isDefaultProductClass();
        }

        return isDefault;
    }

    /**
     * Gets the volume threshold of this profile
     * @return volume threshold
     */
    public int getVolumeThreshold()
    {
        int threshold = 0;
        if(getProfileStruct() != null)
        {
            threshold = getProfileStruct().volumeThreshold;
        }

        return threshold;
    }

    /**
     * Sets the volume threshold of this profile
     * @param newThreshold to set
     */
    public void setVolumeThreshold(int newThreshold)
    {
        if(getProfileStruct() != null)
        {
            getProfileStruct().volumeThreshold = newThreshold;
        }
    }

    /**
     * Sets the time window for this profile
     * @param newTimeWindow to set
     */
    public void setTimeWindow(int newTimeWindow)
    {
        if(getProfileStruct() != null)
        {
            getProfileStruct().timeWindow = newTimeWindow;
        }
    }

    /**
     * Gets the time window for this profile
     * @return time window
     */
    public int getTimeWindow()
    {
        int time = 0;
        if(getProfileStruct() != null)
        {
            time = getProfileStruct().timeWindow;
        }

        return time;
    }

    /**
     * Determines if the profile is quote risk management enabled
     * @return boolean
     */
    public boolean isQuoteRiskManagementEnabled()
    {
        boolean enabled = false;

        if(getProfileStruct() != null)
        {
            enabled = getProfileStruct().quoteRiskManagementEnabled;
        }

        return enabled;
    }

    /**
     * Sets whether quote risk management is enabled for this profile
     * @param enabled
     */
    public void setQuoteRiskManagementEnabled(boolean enabled)
    {
        if(getProfileStruct() != null)
        {
            getProfileStruct().quoteRiskManagementEnabled = enabled;
        }
    }

    /**
     * Determines if this profile has been modified.
     * @return True if modified, false otherwise.
     */
    public boolean isModified()
    {
        return modified;
    }

    /**
     * Sets whether this profile has been modified.
     * @param modified True if modified, false if NOT.
     */
    public void setModified(boolean modified)
    {
        if (modified != this.modified)
        {
            this.modified = modified;
            if (this.modified)
            {
                setChanged();
                notifyObservers(null);
                clearChanged();
            }
        }
    }

    /**
     * Gets the user id of this profile
     * @return user id
     */
    public String getUserId()
    {
        return userId;
    }

    /**
     * Sets the user id of this profile
     * @param userId
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    /**
     * Returns the string representation of this profile
     */
    public String toString()
    {
        return getClassSymbol();
    }

    /**
     * Removes this profile from the collection of profiles.
     */
    public void remove() throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException, DataValidationException
    {
        GUIUserTradingParametersAPIHome.find().removeQuoteRiskProfile(getUserId(), getClassKey());
    }

    /**
     * Saves this profile to the collection of profiles.
     */
    public void save() throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException, DataValidationException
    {
        if(isModified())
        {
            GUIUserTradingParametersAPIHome.find().setQuoteRiskProfile(getUserId(), getProfileStruct());
            setModified(false);
        }
    }

    /**
     * Clones this profile
     */
    public Object clone()
    {
        Object newProfile = null;
        try
        {
            newProfile = super.clone();
        }
        catch(CloneNotSupportedException e)
        {
            DefaultExceptionHandlerHome.find().process(e,"Could not clone QuoteRiskManagementProfile Object: " + (this.productClass == null ? this.toString() : this.productClass.toString()));
        }

        return newProfile;
    }

    /**
     * Clones this profile for another (different) User
     */
    public QuoteRiskManagementProfileImpl cloneForAnotherUser(UserQuoteRiskManagementProfile newUserQrmProfile, String userId)
    {
        QuoteRiskManagementProfileStruct newQRMStruct = (QuoteRiskManagementProfileStruct)ReflectiveStructBuilder.newStruct(QuoteRiskManagementProfileStruct.class);
        newQRMStruct.quoteRiskManagementEnabled = this.getProfileStruct().quoteRiskManagementEnabled;
        newQRMStruct.timeWindow = this.getProfileStruct().timeWindow;
        newQRMStruct.volumeThreshold = this.getProfileStruct().volumeThreshold;
        newQRMStruct.classKey = this.getProfileStruct().classKey;
        QuoteRiskManagementProfileImpl newProfile = new QuoteRiskManagementProfileImpl(newQRMStruct);
        newProfile.modified = true;          // must avoid side-effects of setter method setModified()
        newProfile.setUserId(userId);
        // Observable initial settings
        newProfile.addObserver(newUserQrmProfile);

        return newProfile;
    }

    public boolean isAllSelected()
    {
        return false;
    }

    public Object getKey()
    {
        return getClassKey();
    }

    /**
     * Determines if this ProductClass is invalid, either it has been marked inactive or has been removed from the
     * system, but some data structures still reference the classkey
     */
    public boolean isValid()
    {
        return getProductClass().isValid();
    }

    public String getPost()
    {
        return getProductClass().getPost();
    }

    public String getStation()
    {
        return getProductClass().getStation();
    }


    /**
     * Sets the product class veriable
     * @param productClass to set
     */
    protected void setProductClass(ProductClass productClass)
    {
        this.productClass = productClass;
    }

    /**
     * Sets the profile struct this profile should represent
     * @param newProfile QuoteRiskManagementProfileStruct
     */
    protected void setProfileStruct(QuoteRiskManagementProfileStruct newProfile)
    {
        this.profileStruct = newProfile;
    }

    /**
     * Sets the class String from formatting for quick use later
     * @param classString to set
     */
    private void setClassString(String classString)
    {
        this.classString = classString;
    }
}
