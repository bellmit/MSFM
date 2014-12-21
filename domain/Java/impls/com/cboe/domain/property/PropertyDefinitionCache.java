package com.cboe.domain.property;
//
// -----------------------------------------------------------------------------------
// Source file: PropertyDefinitionCache
//
// PACKAGE: com.cboe.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import java.util.*;

import com.cboe.idl.property.PropertyGroupStruct;

import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.domain.property.PropertyDefinition;
import com.cboe.interfaces.domain.property.PropertyDefinitionGroup;
import com.cboe.interfaces.domain.property.PropertyGroup;
import com.cboe.interfaces.domain.property.PropertyServiceFacade;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 *  Cache for the property definitions.  This class is a singleton. 
 *  Nothing is ever removed from the cache.
 */
public class PropertyDefinitionCache implements EventChannelListener
{
    protected        HashMap                 categoryDefinitions;
    private   static PropertyDefinitionCache instance;
    private          boolean                 isLoading = false;

    /**
     *  Private constructor to enforce the singleton pattern.
     *
     */
    private PropertyDefinitionCache()
    {
        categoryDefinitions = new HashMap();
    }

    /**
     *  Get the category definitions for a group.
     *
     *  @param category the category to get the definitions for
     *  @return The map of definitions, keyed by definition name
     */
    public PropertyDefinitionGroup getCategoryDefinitions(String category)
    {
//        PropertyDefinitionGroup definitionGroup = (PropertyDefinitionGroup) categoryDefinitions.get(category);      TODO Shawn uncomment when ClassCastException is fixed
        // TODO Shawn temp begin ?? remove later ??
        Object def = categoryDefinitions.get(category);

        PropertyDefinitionGroup definitionGroup = null;
        if (def instanceof PropertyDefinitionGroup)
        {
//            System.out.println(">>>> Shawn PropertyDefinitionCache.getCategoryDefinitions def is instanceof PropertyDefinitionGroup");
            definitionGroup = (PropertyDefinitionGroup) def;
        }
        else if (def instanceof PropertyGroup)
        {
//            System.out.println("<<<< Shawn PropertyDefinitionCache.getCategoryDefinitions def is instanceof PropertyGroup");
            PropertyGroup group = (PropertyGroup) def;
            try
            {
                convertPropertyGroupToDefinitionGroup(group);
                definitionGroup = (PropertyDefinitionGroup) categoryDefinitions.get(category);
            }
            catch(NotFoundException nfe)
            {
                if(Log.isDebugOn())
                {
                    Log.debug("PropertyDefinitionCache:getCategoryDefinitions - No defintions found for category = " + category);
                }
            }
            catch(Exception e)
            {
//                Log.exception("???? Shawn PropertyDefinitionCache.getCategoryDefinitions: Could not convert property group to property definition group.", e);
            }
        }
        // TODO Shawn temp end ??

        if (definitionGroup == null)               
        {
            loadCategory(category);
            definitionGroup = (PropertyDefinitionGroup) categoryDefinitions.get(category);
        }

        return definitionGroup;
    }

    /**
     *  Implements the Singleton pattern.
     *
     *  @return the instance for this class.
     */
    public static PropertyDefinitionCache getInstance()
    {
        if (instance == null)
        {
            instance = new PropertyDefinitionCache();
        }
        return instance;
    }

    /**
     *  Get a property defintion.  Load the category of definitions, if it is not
     *  loaded.
     *
     *  @param category Category to search for a definition in.
     *  @param definitionName name of the definition.
     *  @return The defintion.  Null if one does not exist in that category/name combo.
     */
    public PropertyDefinition getPropertyDefinition(String category, String definitionName)
    {
        PropertyDefinitionGroup group = getCategoryDefinitions(category);
        PropertyDefinition definition = null;

        if (group != null)
        {
            definition = group.getDefinition(definitionName);
        }
        return definition;
    }

    /**
     *  Got an event from the event channel, handle it.
     *
     */
    public void channelUpdate(ChannelEvent event)
    {
        final ChannelKey aChannelKey = (ChannelKey)event.getChannel();
        final Object eventData = event.getEventData();
        

        // Check for remove
        if (aChannelKey.channelType == ChannelType.REMOVE_PROPERTY)
        {
            String key = (String) eventData;
            if (key.equals(PropertyDefinition.PROPERTY_DEFINITION_KEY))
            {
                acceptPropertyRemove((String)aChannelKey.key,key);
            }
        }
        // Check for update
        else if (aChannelKey.channelType == ChannelType.UPDATE_PROPERTY)
        {
            PropertyGroupStruct struct = (PropertyGroupStruct) eventData;
            if (struct.propertyKey.equals(PropertyDefinition.PROPERTY_DEFINITION_KEY))
            {
                PropertyServicePropertyGroup group = PropertyFactory.createPropertyGroup(struct);

                // TODO Shawn temp begin ??
                try
                {
                    convertPropertyGroupToDefinitionGroup(group);
                }
                catch (Exception e)
                {
//                    Log.exception("???? Shawn PropertyDefinitionCache.channelUpdate: Could not convert property group to property definition group.", e);
                }
                // TODO Shawn temp end ??

                acceptPropertyUpdate(group);
            }
        }
    }

    /**
     *  Accept events for a property group changing.  This means that a set of
     *  category definitions changed.  Load the new set to replace the old set.
     *
     *  @param group The group that changed.
     */
    public void acceptPropertyUpdate(PropertyServicePropertyGroup group)
    {
        // Double check the key first
        if (group.getKey().equals(PropertyDefinition.PROPERTY_DEFINITION_KEY))
        {
            synchronized(categoryDefinitions)
            {
                categoryDefinitions.remove(group.getCategory());
                categoryDefinitions.put(group.getCategory(),group);
            }
        }
    }

    /**
     *  Accept events for a property being removed.  This means that a set of
     *  category definitions has been totally removed.  This is bizare, but go 
     *  ahead and remove them from the cache.
     *
     *  @param category The category the group was in.
     *  @param key The key for the group that was removed.
     */
    public void acceptPropertyRemove(String category, String key)
    {
        // Double check the key first
        if (key.equals(PropertyDefinition.PROPERTY_DEFINITION_KEY))
        {
            synchronized(categoryDefinitions)
            {
                categoryDefinitions.remove(category);
            }
        }
    }


    /**
     *  Load a category into the definition map.
     *
     *  @param category The category to load.
     */
    protected void loadCategory(String category)
    {
        // Load the category
        try
        {
            PropertyServiceFacade facade = PropertyServiceFacadeHome.find();
            PropertyGroup group;
            try
            {
                group = facade.getPropertyGroup(category, PropertyDefinition.PROPERTY_DEFINITION_KEY);
            }
            catch( NotFoundException nfe )
            {
            	if(Log.isDebugOn())
                {
                    Log.debug("PropertyDefinitionCache:loadCategory - No defintions found for category = " + category );
                }
                
                group = null;
            }

            convertPropertyGroupToDefinitionGroup(group);
        }
        catch (Exception nfe)
        {
            Log.exception("Could not load property category for definitions.", nfe);
        }
    }

    protected void convertPropertyGroupToDefinitionGroup(PropertyGroup group) throws Exception // TODO SysExc, etc ??
    {
        if(group != null)
        {
            // Convert the property group to a property definition group
            PropertyDefinitionGroup definitionGroup = PropertyFactory.createPropertyDefinitionGroup(group);
            // add the group to the Map
            synchronized(categoryDefinitions)
            {
                categoryDefinitions.put(group.getCategory(), definitionGroup);
            }
            // Subscribe to changes to the category
            getPropertyServiceFacade().subscribe(group.getCategory(), this);
        }
    }

    /**
     *  Get the facade for the PropertyService
     */
    protected PropertyServiceFacade getPropertyServiceFacade()
    {
        return PropertyServiceFacadeHome.find();
    }
}
