package com.cboe.presentation.qrm;

//------------------------------------------------------------------------------------------------------------------
// FILE:    UserQuoteRiskManagementProfileImpl.java
//
// PACKAGE: com.cboe.presentation.commonBusiness
//
//-------------------------------------------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
//
//-------------------------------------------------------------------------------------------------------------------


// Imports
import org.omg.CORBA.UserException;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

// java packages
import java.util.*;
import java.beans.PropertyChangeListener;

// local packages
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.qrm.UserQuoteRiskManagementProfile;
import com.cboe.interfaces.presentation.qrm.QuoteRiskManagementProfile;
import com.cboe.exceptions.*;

/**
 *  This class represents User Quote Risk Management Profile.
 *
 *
 *  @author Alex Brazhnichenko
 *  Creation date (3/2/00 4:47:12 PM)
 *  @version 03/03/2000
 */

public class UserQuoteRiskManagementProfileImpl implements UserQuoteRiskManagementProfile {
  private boolean QRMEnabled;
  private QuoteRiskManagementProfile defaultProfile;
  private Map<QuoteRiskManagementProfile, QuoteRiskManagementProfile> removedProfiles;
  private Map<QuoteRiskManagementProfile, QuoteRiskManagementProfile> addedUpdatedProfiles;
  private List<QuoteRiskManagementProfile> currentProfiles = new ArrayList<QuoteRiskManagementProfile>(10);
  private boolean modified = false;
  private java.lang.String userId;
  private boolean initialized = false;
  private final String Category = this.getClass().getName();
  protected java.beans.PropertyChangeSupport propertyChange;

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTION

    /**
     * UserQuoteRiskManagementProfile constructor comment.
     */
    public UserQuoteRiskManagementProfileImpl() {
      super();
      this.addedUpdatedProfiles = new HashMap<QuoteRiskManagementProfile, QuoteRiskManagementProfile>();
      this.removedProfiles = new HashMap<QuoteRiskManagementProfile, QuoteRiskManagementProfile>();
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/2/00 4:52:30 PM)
     * @param profileStruct com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct
     */
    public UserQuoteRiskManagementProfileImpl(UserQuoteRiskManagementProfileStruct profileStruct)
    {
      this();
      setQuoteRiskManagementProfileStruct(profileStruct);
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/8/00 2:54:22 PM)
     * @param userId java.lang.String
     */
    public UserQuoteRiskManagementProfileImpl(String userId)
    {
      this();
      setUserId(userId);
    }

///////////////////////////////////////////////////////////////////////////////
// INTERFACE IMPLEMENTATION

    public void update(Observable o, Object arg)
    {
        //One of the QRM Profiles has been modified
        QuoteRiskManagementProfile qrmProfile = (QuoteRiskManagementProfile) o;
        this.addedUpdatedProfiles.put(qrmProfile, qrmProfile);
        checkModified();
        firePropertyChange(QRM_PROFILE_ADDED_UPDATED_EVENT, qrmProfile, null);
    }

///////////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS

    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
      getPropertyChange().addPropertyChangeListener(listener);
    }
    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void addPropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
      getPropertyChange().addPropertyChangeListener(propertyName, listener);
    }
    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
      getPropertyChange().firePropertyChange(evt);
    }
    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
      getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }
    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
      getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }
    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
      getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/2/00 4:49:51 PM)
     * @return com.cboe.presentation.commonBusiness.QuoteRiskManagementProfile
     */
    public QuoteRiskManagementProfile getDefaultProfile() {
      return defaultProfile;
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/3/00 11:14:14 AM)
     * @return com.cboe.presentation.commonBusiness.QuoteRiskManagementProfile[]
     */
    public QuoteRiskManagementProfile[] getQRMProfiles()
    {
      QuoteRiskManagementProfile[] profiles = new QuoteRiskManagementProfile[0];
      profiles = currentProfiles.toArray(profiles);

      return profiles;
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/3/00 11:40:38 AM)
     * @return com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct[]
     */
    public QuoteRiskManagementProfileStruct[] getQRMProfileStructs()
    {
      QuoteRiskManagementProfileStruct[] profileStructs = null;
      QuoteRiskManagementProfile[] profiles = getQRMProfiles();

      if ( profiles != null )
      {
        profileStructs = new QuoteRiskManagementProfileStruct[profiles.length];
        for (int i = 0; i < profiles.length; i++)
        {
          profileStructs[i] = profiles[i].getProfileStruct();
        }
      }

      return profileStructs;
    }

    /**
     * Insert the method's description here.
     * Creation date: (3/8/00 2:18:16 PM)
     * @return java.lang.String
     */
    public java.lang.String getUserId() {
      return userId;
    }
    /**
     * The hasListeners method was generated to support the propertyChange field.
     */
    public synchronized boolean hasListeners(String propertyName) {
      return getPropertyChange().hasListeners(propertyName);
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/7/00 3:31:17 PM)
     */
    public void initialize()
    {
      if (getUserId() != null)
      {
        try
        {
          UserQuoteRiskManagementProfileStruct profile = GUIUserTradingParametersAPIHome.find().getAllQuoteRiskProfiles(getUserId());
          setQuoteRiskManagementProfileStruct(profile);
        }
        catch( UserException e )
        {
           DefaultExceptionHandlerHome.find().process( e );
        }
      }
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/10/00 3:53:40 PM)
     * @return boolean
     */
    public boolean isInitialized() {
      return initialized;
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/6/00 4:24:42 PM)
     * @return boolean
     */
    public boolean isModified()
    {
      return modified;
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/2/00 4:49:03 PM)
     * @return boolean
     */
    public boolean isQRMEnabled() {
      return QRMEnabled;
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/3/00 2:47:57 PM)
     * @param element ProductClass
     */
    public void put(ProductClass element)
    {
        if ( element != null )
        {
            QuoteRiskManagementProfileImpl prof = (QuoteRiskManagementProfileImpl) element;

            if(!currentProfiles.contains(prof))
            {
                prof.addObserver(this);

                currentProfiles.add(prof);

                if ( isInitialized() )
                {
                    this.addedUpdatedProfiles.put(prof, prof);
                    this.removedProfiles.remove(prof);
                    checkModified();

                    GUILoggerHome.find().debug(Category+".put()",GUILoggerBusinessProperty.QRM,"UserQuoteRiskManagementProfile.put firePropertyChange(QRM_PROFILE_ADDED_UPDATED_EVENT, element '" + prof.getProductClass().toString() + "', null)");
                    firePropertyChange(QRM_PROFILE_ADDED_UPDATED_EVENT, element, null);
                }
            }
        }
    }
    /**
     * putMerge - put an element in the collection, but first removing any element matching its product class
     *                                              also handle default profile separately
     * Creation date: (12/20/06)
     * @param element ProductClass
     */
    public void putMerge(ProductClass element)
    {
        if ( element != null )
        {
            QuoteRiskManagementProfileImpl prof = (QuoteRiskManagementProfileImpl) element;
            if (prof.isDefaultProfile())
            {
                this.replaceDefaultProfile(prof);
            }
            else
            {
                int index = this.contains(prof);
                if(index >= 0)
                {
                    QuoteRiskManagementProfileImpl qrmProfile = (QuoteRiskManagementProfileImpl) currentProfiles.get(index);
                    remove(qrmProfile);
                }
                put(element);
            }
        }
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/3/00 2:47:57 PM)
     * @param element ProductClass
     */
    public void remove(ProductClass element)
    {
        if ( !((QuoteRiskManagementProfile)element).isDefaultProfile() )
        {
            QuoteRiskManagementProfileImpl prof = (QuoteRiskManagementProfileImpl) element;
            prof.deleteObserver(this);

            currentProfiles.remove(prof);

            if ( isInitialized() )
            {
                this.addedUpdatedProfiles.remove(prof);
                this.removedProfiles.put(prof, prof);
                checkModified();

                GUILoggerHome.find().debug(Category+".remove()",GUILoggerBusinessProperty.QRM,"UserQuoteRiskManagementProfile.remove firePropertyChange(QRM_PROFILE_REMOVED_EVENT, '" + prof.getProductClass().toString() + "', null)");
                firePropertyChange(QRM_PROFILE_REMOVED_EVENT, prof, null);
            }
        }
    }
    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
      getPropertyChange().removePropertyChangeListener(listener);
    }
    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
      getPropertyChange().removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Insert the method's description here.
     * Creation date: (3/3/00 2:33:37 PM)
     */
    public void save() throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException, DataValidationException
    {
        GUILoggerHome.find().debug(Category+".save()",GUILoggerBusinessProperty.QRM,"UserQuoteRiskManagementProfile.save:");
//        try
//        {
            if ( modified )
            {
                GUIUserTradingParametersAPIHome.find().setQuoteRiskManagementEnabledStatus(getUserId(),isQRMEnabled());
                GUILoggerHome.find().debug(Category,GUILoggerBusinessProperty.QRM,"UserQuoteRiskManagementProfile.save: after saveStatus, isQRMEnabled() = " + Boolean.toString(isQRMEnabled()) + " for UserID: " + getUserId());
            }

            //delete profiles marked for removal
            Object[] removedArray = this.removedProfiles.values().toArray();
            for (int i = 0; i < removedArray.length; i++)
            {
                QuoteRiskManagementProfile profile = (QuoteRiskManagementProfile)removedArray[i];
                profile.setUserId(getUserId());
                profile.remove();
            }
            GUILoggerHome.find().debug(Category+".save()",GUILoggerBusinessProperty.QRM,"UserQuoteRiskManagementProfile.save: after remove: "+ removedArray.length + " profiles removed.");

            Object[] addedUpdatedArray = this.addedUpdatedProfiles.values().toArray();
            for (int i = 0; i < addedUpdatedArray.length; i++)
            {
                QuoteRiskManagementProfile profile = (QuoteRiskManagementProfile)addedUpdatedArray[i];
                profile.setUserId(getUserId());
                profile.save();
            }
            GUILoggerHome.find().debug(Category+".save()",GUILoggerBusinessProperty.QRM,"UserQuoteRiskManagementProfile.save: after save/add: "+ addedUpdatedArray.length + " profiles saved.");
            clearProfileUpdates();
            setModified(false);
//        }
//        catch( UserException e )
//        {
//           DefaultExceptionHandlerHome.find().process( e );
//        }

    }
    /**
     * Insert the method's description here.
     * Creation date: (3/2/00 4:49:51 PM)
     * @param newDefaultProfile com.cboe.presentation.commonBusiness.QuoteRiskManagementProfile
     */
    public void setDefaultProfile(QuoteRiskManagementProfile newDefaultProfile) {
      defaultProfile = newDefaultProfile;
      setQRMProfile(defaultProfile);
    }
    /**
     * replaceDefaultProfile - replace the default qrm profile for a merge or overwrite copy operation
     * Creation date: (3/2/00 4:49:51 PM)
     * @param newDefaultProfile com.cboe.presentation.commonBusiness.QuoteRiskManagementProfile
     */
    public void replaceDefaultProfile(QuoteRiskManagementProfile newDefaultProfile)
    {
        defaultProfile = newDefaultProfile;
        for (int i= 0; i < currentProfiles.size(); ++i)
        {
            QuoteRiskManagementProfile qrmProfile = currentProfiles.get(i);
            if (qrmProfile.isDefaultProfile())
            {
                currentProfiles.remove(i);
                currentProfiles.add(i, newDefaultProfile);
            }
        }
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/10/00 3:53:40 PM)
     * @param newInitialized boolean
     */
    public void setInitialized(boolean newInitialized) {
      initialized = newInitialized;
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/6/00 4:24:42 PM)
     * @param newModified boolean
     */
    public void setModified(boolean newModified) {
      modified = newModified;
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/2/00 4:49:03 PM)
     * @param newQRMEnabled boolean
     */
    public void setQRMEnabled(boolean newQRMEnabled)
    {
      QRMEnabled = newQRMEnabled;
      if ( isInitialized() )
      {
        GUILoggerHome.find().debug(Category+".setQRMEnabled()",GUILoggerBusinessProperty.QRM,"UserQuoteRiskManagementProfile.setQRMEnabled firePropertyChange(QRM_PROFILE_ADDED_UPDATED_EVENT, profile, profile)");
        firePropertyChange(USER_QRM_PROFILE_ENABLED_EVENT, null, this);
      }
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/8/00 2:18:16 PM)
     * @param newUserId java.lang.String
     */
    public void setUserId(java.lang.String newUserId) {
      userId = newUserId;
    }

    /**
      Clear the list of added and removed profiles
    */
    public void clearProfileUpdates()
    {
        this.addedUpdatedProfiles.clear();
        this.removedProfiles.clear();
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/6/00 1:11:57 PM)
     * @param profile com.cboe.presentation.commonBusiness.QuoteRiskManagementProfile
     */
    public void setQRMProfile(QuoteRiskManagementProfile profile)
    {
      put(profile);
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/3/00 11:13:30 AM)
     * @param profiles com.cboe.presentation.commonBusiness.QuoteRiskManagementProfile[]
     */
    public void setQRMProfiles(QuoteRiskManagementProfile[] profiles)
    {
        for(int i = 0; i < profiles.length; i++)
        {
            put(profiles[i]);
        }
    }

    /**
     * mergeQRMProfiles
     * @param profiles com.cboe.presentation.commonBusiness.QuoteRiskManagementProfile[]
     */
    public void mergeQRMProfiles(QuoteRiskManagementProfile[] profiles)
    {
        PropertyChangeListener[] listeners = removeAndSavePCListeners();

        for(int i = 0; i < profiles.length; i++)
        {
            putMerge(profiles[i]);
        }

        restorePCListeners(listeners);
    }

    /**
     * replaceQRMProfiles
     * @param profiles com.cboe.presentation.commonBusiness.QuoteRiskManagementProfile[]
     */
    public void replaceQRMProfiles(QuoteRiskManagementProfile[] profiles)
    {
        PropertyChangeListener[] listeners = removeAndSavePCListeners();

        removedProfiles.clear();
        for (QuoteRiskManagementProfile aCurrentProfile : currentProfiles)
        {
            if (!aCurrentProfile.isDefaultProfile())
            {
                removedProfiles.put(aCurrentProfile, aCurrentProfile);
            }
        }
        currentProfiles.clear();
        addedUpdatedProfiles.clear();
        setInitialized(true);

        for(int i = 0; i < profiles.length; i++)
        {
            if (profiles[i].isDefaultProfile())
            {
                defaultProfile = profiles[i];
                GUILoggerHome.find().debug(Category+".setQuoteRiskManagementProfileStruct()",GUILoggerBusinessProperty.QRM,"setQuoteRiskManagementProfileStruct: after setDefaultProfile");
                break;
            }
        }
        this.setQRMProfiles(profiles);
        checkModified();
        GUILoggerHome.find().debug(Category+".setQuoteRiskManagementProfileStruct()",GUILoggerBusinessProperty.QRM,"setQuoteRiskManagementProfileStruct: after setQRMProfileStructs, length="+profiles.length);

        restorePCListeners(listeners);
    }
///////////////////////////////////////////////////////////////////////////////
// PROTECTED METHODS

    /**
     * Accessor for the propertyChange field.
     */
    protected java.beans.PropertyChangeSupport getPropertyChange() {
      if (propertyChange == null) {
        propertyChange = new java.beans.PropertyChangeSupport(this);
      };
      return propertyChange;
    }

    protected void checkModified()
    {
        setModified( (this.addedUpdatedProfiles.size() > 0) || (this.removedProfiles.size() > 0) );
    }

    /**
     * Insert the method's description here.
     * Creation date: (3/3/00 11:50:56 AM)
     * @param profileStructs com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct[]
     */
    protected void setQRMProfileStructs(QuoteRiskManagementProfileStruct[] profileStructs)
    {
      if ( profileStructs != null )
      {
        int profileCount = profileStructs.length;
        QuoteRiskManagementProfile[] profiles = new QuoteRiskManagementProfile[profileCount];

        for (int i = 0; i < profileCount; i++)
        {
          profiles[i] = new QuoteRiskManagementProfileImpl(profileStructs[i]);
        }

        setQRMProfiles(profiles);
      }
    }
    /**
     * Insert the method's description here.
     * Creation date: (3/2/00 4:57:22 PM)
     * @param profileStruct com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct
     */
    protected void setQuoteRiskManagementProfileStruct(UserQuoteRiskManagementProfileStruct profileStruct)
    {
      if ( profileStruct != null )
      {
        setQRMEnabled(profileStruct.globalQuoteRiskManagementEnabled);

        currentProfiles.clear();
        removedProfiles.clear();
        addedUpdatedProfiles.clear();

        setDefaultProfile(new QuoteRiskManagementProfileImpl(profileStruct.defaultQuoteRiskProfile));
        GUILoggerHome.find().debug(Category+".setQuoteRiskManagementProfileStruct()",GUILoggerBusinessProperty.QRM,"setQuoteRiskManagementProfileStruct: after setDefaultProfile");
        setQRMProfileStructs(profileStruct.quoteRiskProfiles);
        GUILoggerHome.find().debug(Category+".setQuoteRiskManagementProfileStruct()",GUILoggerBusinessProperty.QRM,"setQuoteRiskManagementProfileStruct: after setQRMProfileStructs, length="+profileStruct.quoteRiskProfiles.length);

        setInitialized(true);
      }
    }

    protected int contains(QuoteRiskManagementProfileImpl profile)
    {
        int result = -1;
        for (QuoteRiskManagementProfile aCurrentProfile : currentProfiles)
        {
            if (aCurrentProfile.getClassKey() == profile.getClassKey())
            {
                result = currentProfiles.indexOf(aCurrentProfile);
                break;
            }
        }
        return result;
    }

    private void restorePCListeners(PropertyChangeListener[] listeners)
    {
        for (int i = 0; i < listeners.length; ++i)
        {
            this.addPropertyChangeListener(listeners[i]);
        }
    }

    private PropertyChangeListener[] removeAndSavePCListeners()
    {
        PropertyChangeListener[] listeners = this.getPropertyChange().getPropertyChangeListeners();
        for (int i = 0; i < listeners.length; ++i)
        {
            this.getPropertyChange().removePropertyChangeListener(listeners[i]);
        }
        return listeners;
    }

}
