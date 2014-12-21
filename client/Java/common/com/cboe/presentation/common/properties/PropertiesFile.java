//
// -----------------------------------------------------------------------------------
// Source file: PropertiesFile.java
//
// PACKAGE: com.cboe.presentation.common.properties;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.properties;

import java.io.IOException;
import java.util.*;

/**
 * This interface defines the contract to allow the manipulation of the data in a
 * properties file.
 */
public interface PropertiesFile
{
    /**
     * Writes out the current data to a file, overwriting the original file.
     * @exception IOException if an error occurs while writing.
     */
    public void save() throws IOException;

    /**
     * Writes out the current data to a file.
     * @param fileName to create
     * @exception IOException if an error occurs while writing.
     */
    public void save(String fileName) throws IOException;

    /**
     * Allows the reading of the entire properties file.
     * @return Map The Map containing the Map's for each section.
     * Will be empty if no sections exist.
     */
    public Map getAll();

    /**
     * Allows the reading of an entire section of properties file.
     * @param sectionName within the properties file (without []'s)
     * @return Map The Map containing the keys and values as strings
     * in that section, null if the section does not exist.
     */
    public Map getSection(String sectionName);

    /**
     * Allows the reading of a single value of data from any section within the
     * properties file.
     * @param sectionName within the properties file (without []'s)
     * @param key whose value is to be found within that section
     * @return String The string containing the value of the key, null if the
     * key does not exist.
     */
    public String getValue(String sectionName, String key);

    /**
     * Creates a new section in the properties file.  If the section already exists or
     * the section is null or "" then no action is taken. Remember to save()
     * when ready.
     * @param sectionName (without []'s) to be created.
     */
    public void addSection(String sectionName);

    /**
     * Assigns a value to a key in a section of the properties file. Any method calls
     * where the section or key are null or "" will be ignored. Remember to
     * save() when ready.
     * @param sectionName to add to. If the section does not
     * exist, it will be created.
     * @param key to add.  If the key does not exist, it will
     * be created.
     * @param value to assign to the key.  This value can be
     * "".
     * @return String The value of any key that is overwritten in this operation.
     */
    public String addValue(String sectionName, String key, String value);

    /**
     * Clears the entire properties file. Remember to save() when ready.
     * @return Map The Map containing the Map's for each section.
     */
    public Map removeAll();

    /**
     * Removes a section (and all it's keys) from the properties file. Remember to
     * save() when ready.
     * @param sectionName to be removed (without []'s)
     * @return Map Any section removed as a Map.  Null if nothing is
     * removed.
     */
    public Map removeSection(String sectionName);

    /**
     * Removes a key (and it's corresponding value) from the properties file. Remember
     * to save() when ready.
     * @param sectionName that the key and value is in.( without []'s)
     * @param key to be removed.
     * @return String The value of any value that is removed.  Null if nothing
     * is removed.
     */
    public String removeValue(String sectionName, String key);
}