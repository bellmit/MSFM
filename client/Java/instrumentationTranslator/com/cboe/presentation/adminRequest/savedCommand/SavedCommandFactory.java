//
// -----------------------------------------------------------------------------------
// Source file: SavedCommandFactory.java
//
// PACKAGE: com.cboe.presentation.adminRequest.savedCommand
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.adminRequest.savedCommand;

import java.util.*;

import com.cboe.idl.cmiErrorCodes.NotFoundCodes;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.domain.Delimeter;
import com.cboe.interfaces.instrumentation.adminRequest.savedCommand.FavoriteCommand;
import com.cboe.interfaces.instrumentation.adminRequest.savedCommand.PersistentCommand;
import com.cboe.interfaces.instrumentation.adminRequest.savedCommand.SavedCommandStorageListener;
import com.cboe.interfaces.presentation.common.storage.Storage;

import com.cboe.util.ExceptionBuilder;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.storage.AbstractStorageAccessor;
import com.cboe.presentation.common.storage.StorageManagerFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.threading.APIWorkerImpl;
import com.cboe.presentation.threading.GUIWorkerImpl;

/**
 * Provides an implementation of the Adapter design pattern between a Factory of
 * FavoriteCommand's and PersistentCommand's and the persistence of them using the Storage
 * framework of the translator classes. This class is responsible for caching and storing
 * all the FavoriteCommand and PersistentCommand objects. It is implemented as a Singleton.
 */
public class SavedCommandFactory extends AbstractStorageAccessor
{
    protected static final String SAVED_NAME_LIST_PROPERTY_KEY =
            "SavedCommands" + Delimeter.PROPERTY_DELIMETER;

    protected static final String FAVORITE_NAME_LIST_PROPERTY_KEY =
            "Favorites" + Delimeter.PROPERTY_DELIMETER;

    protected static final int MAXIMUM_FAVORITES_PER_PROPERTY = 10;

    protected static final String LOCAL_FILENAME_PROPERTY_KEY = "Local.ARCommFavFile";
    protected static final String REMOTE_FILENAME_PROPERTY_KEY = "Remote.ARCommFavFile";
    protected static final String DEFAULT_FILE_NAME = "shmGlobalStorage" +
                                                      System.getProperty("file.separator")
                                                      + "ARCommandFavorites.properties";

    protected final Object saveLockObject = new Object();

    private Map<String, PersistentCommand> persistentByName;
    private List<FavoriteCommand> allFavorites;
    private Map<String, List<FavoriteCommand>> favoritesByOrb;
    private Map<String, List<PersistentCommand>> persistentByOrb;

    private static SavedCommandFactory factoryInstance;

    private static final FavoriteCommand[] EMPTY_FAVORITE_SEQUENCE = new FavoriteCommand[0];
    private static final PersistentCommand[] EMPTY_PERSISTENT_SEQUENCE = new PersistentCommand[0];

    private final List<SavedCommandStorageListener> listeners =
            new ArrayList<SavedCommandStorageListener>(5);

    protected SavedCommandFactory()
    {
    }

    public static synchronized SavedCommandFactory getInstance()
    {
        if(factoryInstance == null)
        {
            factoryInstance = new SavedCommandFactory();
        }
        return factoryInstance;
    }

    /**
     * Creates a FavoriteCommand instance from the data provided.
     * @param fullCommandName the fully qualified name of the command to save
     * @param orbName to save favorite for
     * @return instance of FavoriteCommand. You should not attempt to cast this down, as
     * there is not guarantee that the implementation type will remain the same.
     */
    public FavoriteCommand createFavoriteCommand(String fullCommandName, String orbName)
    {
        return new FavoriteCommandImpl(fullCommandName, orbName);
    }

    /**
     * Creates a PersistentCommand instance from the data provided.
     * @param name that this PersistentCommand will be saved as
     * @param description that this PersistentCommand will be saved with
     * @param fullCommandName the fully qualified name of the command to save
     * @param orbNames to save with
     * @param argumentValues to save with
     * @param timeoutMillis milliseconds to wait for each ORB to respond
     * @return instance of PersistentCommand. You should not attempt to cast this down, as
     * there is not guarantee that the implementation type will remain the same.
     */
    public PersistentCommand createPersistentCommand(String name, String description,
                                                     String fullCommandName, String[] orbNames,
                                                     String[] argumentValues, int timeoutMillis)
    {
        return new PersistentCommandImpl(name, description, fullCommandName,
                                         orbNames, argumentValues, timeoutMillis);
    }

    /**
     * Adds a listener for events from this factory
     * @param listener to add
     * @see SavedCommandStorageListener
     */
    public void addListener(SavedCommandStorageListener listener)
    {
        synchronized(listeners)
        {
            if(!listeners.contains(listener))
            {
                listeners.add(listener);
            }
        }
    }

    /**
     * Removes a listener for events from this factory
     * @param listener to remove
     * @see SavedCommandStorageListener
     */
    public void removeListener(SavedCommandStorageListener listener)
    {
        synchronized(listeners)
        {
            listeners.remove(listener);
        }
    }

    /**
     * Gets all the FavoriteCommand's that have currently been saved.
     * @return array of FavoriteCommand objects that have been saved. May return a zero-length
     * array. Will not return null.
     */
    public synchronized FavoriteCommand[] getAllFavoriteCommands()
    {
        return allFavorites.toArray(new FavoriteCommand[allFavorites.size()]);
    }

    /**
     * Gets all the FavoriteCommand's that have currently been saved, for the given orbName
     * @param orbName to get FavoriteCommand's for
     * @return array of FavoriteCommand objects that have been saved for the orbName.
     * May return a zero-length array. Will not return null.
     */
    public synchronized FavoriteCommand[] getFavoriteCommands(String orbName)
    {
        FavoriteCommand[] result;
        List<FavoriteCommand> favoritesForOrb = favoritesByOrb.get(orbName);
        if(favoritesForOrb != null)
        {
            result = favoritesForOrb.toArray(new FavoriteCommand[favoritesForOrb.size()]);
        }
        else
        {
            result = EMPTY_FAVORITE_SEQUENCE;
        }
        return result;
    }

    /**
     * Determines if there are any FavoriteCommand's that have been saved for the passed orbName.
     * @param orbName to find FavoriteCommand's for
     * @return true if at least one exists for orbName, false otherwise.
     */
    public synchronized boolean containsFavoriteCommands(String orbName)
    {
        return favoritesByOrb.containsKey(orbName);
    }

    /**
     * Adds a new FavoriteCommand to this factory, adds it to the cache, then saves the
     * persistence store. The saving of the persistence store will be done on a background thread.
     * This method will return before the storage source has been saved.
     * @param command to add and save
     */
    public synchronized void saveFavoriteCommand(FavoriteCommand command)
    {
        if(!allFavorites.contains(command))
        {
            addFavoriteCommand(command);
            fireSavedFavoriteCommandEvent(command);
            saveFavorites();
        }
    }

    /**
     * Removes a FavoriteCommand from this factory, removes it from the cache, then saves the
     * persistence store. The saving of the persistence store will be done on a background thread.
     * This method will return before the storage source has been saved.
     * @param command to remove
     */
    public synchronized void removeFavoriteCommand(FavoriteCommand command)
    {
        if(allFavorites.remove(command))
        {
            List<FavoriteCommand> favoritesForOrb = favoritesByOrb.get(command.getOrbName());
            if(favoritesForOrb != null)
            {
                favoritesForOrb.remove(command);
                if(favoritesForOrb.isEmpty())
                {
                    favoritesByOrb.remove(command.getOrbName());
                }
            }
            fireRemovedFavoriteCommandEvent(command);
            saveFavorites();
        }
    }

    /**
     * Gets all the PersistentCommand's that have currently been saved.
     * @return array of PersistentCommand objects that have been saved. May return a zero-length
     * array. Will not return null.
     */
    public synchronized PersistentCommand[] getAllPersistentCommands()
    {
        Collection<PersistentCommand> values = persistentByName.values();
        return values.toArray(new PersistentCommand[values.size()]);
    }

    /**
     * Gets the user-entered names of all the PersistentCommand's that have currently been saved.
     * @return array of names of all the PersistentCommand objects that have been saved.
     * May return a zero-length array. Will not return null.
     */
    public synchronized String[] getAllPersistentCommandNames()
    {
        Set<String> keys = persistentByName.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    /**
     * Gets a specific PersistentCommand named with passed name.
     * @param name of PersistentCommand to get
     * @return PersistentCommand found with passed name. Will not return null.
     * @throws NotFoundException will be thrown if PersistentCommand with name does not exist
     */
    public synchronized PersistentCommand getPersistentCommand(String name)
            throws NotFoundException
    {
        if(!persistentByName.containsKey(name))
        {
            throw ExceptionBuilder.notFoundException("PersistentCommand with name: " + name +
                                                     " does not exist.",
                                                     NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }
        return persistentByName.get(name);
    }

    /**
     * Gets all the PersistentCommand's that have currently been saved, for the given orbName
     * @param orbName to get PersistentCommand's for
     * @return array of PersistentCommand objects that have been saved for the orbName.
     * May return a zero-length array. Will not return null.
     */
    public synchronized PersistentCommand[] getPersistentCommands(String orbName)
    {
        PersistentCommand[] result;
        List<PersistentCommand> peristentForOrb = persistentByOrb.get(orbName);
        if(peristentForOrb != null)
        {
            result = peristentForOrb.toArray(new PersistentCommand[peristentForOrb.size()]);
        }
        else
        {
            result = EMPTY_PERSISTENT_SEQUENCE;
        }
        return result;
    }

    /**
     * Adds a new PersistentCommand to this factory, adds it to the cache, then saves the
     * persistence store. The saving of the persistence store will be done on a background thread.
     * This method will return before the storage source has been saved.
     * @param command to add and save
     */
    public synchronized void savePersistentCommand(PersistentCommand command)
    {
        if(persistentByName.containsKey(command.getName()))
        {
            removePersistentCommand(command.getName());
        }

        addPersistentCommand(command);
        fireSavedPersistentCommandEvent(command);
        savePersistent();
    }

    /**
     * Removes a PersistentCommand from this factory, removes it from the cache, then saves the
     * persistence store. The saving of the persistence store will be done on a background thread.
     * This method will return before the storage source has been saved.
     * @param command to remove
     */
    public synchronized void removePersistentCommand(PersistentCommand command)
    {
        removePersistentCommand(command.getName());
    }

    /**
     * Removes a PersistentCommand from this factory, removes it from the cache, then saves the
     * persistence store. The saving of the persistence store will be done on a background thread.
     * This method will return before the storage source has been saved.
     * @param name of PersistentCommand to remove
     */
    public synchronized void removePersistentCommand(String name)
    {
        PersistentCommand removedCommand = persistentByName.remove(name);
        if(removedCommand != null)
        {
            String[] orbNames = removedCommand.getAllOrbNames();
            for(String orbName : orbNames)
            {
                List<PersistentCommand> persistentForOrb = persistentByOrb.get(orbName);
                if(persistentForOrb != null)
                {
                    persistentForOrb.remove(removedCommand);
                    if(persistentForOrb.isEmpty())
                    {
                        persistentByOrb.remove(orbName);
                    }
                }
            }
            fireRemovedPersistentCommandEvent(removedCommand);
            savePersistent();
        }
    }

    protected String getDefaultFileName()
    {
        return DEFAULT_FILE_NAME;
    }

    protected String getLocalFileNamePropertyKey()
    {
        return LOCAL_FILENAME_PROPERTY_KEY;
    }

    protected String getRemoteFileNamePropertyKey()
    {
        return REMOTE_FILENAME_PROPERTY_KEY;
    }

    protected Storage getRemoteStorage()
    {
        return StorageManagerFactory.getStorageManager().getARCommandFavoriteStorage();
    }

    @Override
    protected synchronized void initializeCache()
    {
        super.initializeCache();

        persistentByName = new HashMap<String, PersistentCommand>(40);
        allFavorites = new ArrayList<FavoriteCommand>(50);
        favoritesByOrb = new HashMap<String, List<FavoriteCommand>>(30);
        persistentByOrb = new HashMap<String, List<PersistentCommand>>(30);

        loadFavoriteCommands();
        loadPersistentCommands();
    }

    @Override
    protected void save()
    {
        saveFavorites();
        savePersistent();
    }

    protected synchronized void addFavoriteCommand(FavoriteCommand command)
    {
        allFavorites.add(command);
        List<FavoriteCommand> favoritesForOrb = favoritesByOrb.get(command.getOrbName());
        if(favoritesForOrb == null)
        {
            favoritesForOrb = new ArrayList<FavoriteCommand>(10);
            favoritesByOrb.put(command.getOrbName(), favoritesForOrb);
        }
        favoritesForOrb.add(command);
    }

    protected void loadFavoriteCommands()
    {
        int counterForPropertyKey = 0;
        String tokenizedFavorites = cache.getProperty((FAVORITE_NAME_LIST_PROPERTY_KEY +
                                                       counterForPropertyKey));
        while(tokenizedFavorites != null)
        {
            if(tokenizedFavorites.length() > 0)
            {
                StringTokenizer tokenizer =
                        new StringTokenizer(tokenizedFavorites,
                                            Character.toString(Delimeter.PROPERTY_DELIMETER),
                                            false);
                while(tokenizer.hasMoreTokens())
                {
                    String encodedFavoriteContents = tokenizer.nextToken();
                    try
                    {
                        FavoriteCommand newFavorite = new FavoriteCommandImpl(encodedFavoriteContents);
                        addFavoriteCommand(newFavorite);
                    }
                    catch(DataValidationException e)
                    {
                        String msg = "Could not load Favorite Command from storage. Values:" +
                                     encodedFavoriteContents;
                        DefaultExceptionHandlerHome.find().process(e, msg);
                    }
                }
            }

            counterForPropertyKey++;
            tokenizedFavorites = cache.getProperty((FAVORITE_NAME_LIST_PROPERTY_KEY +
                                                    counterForPropertyKey));
        }
    }

    protected synchronized void addPersistentCommand(PersistentCommand command)
    {
        persistentByName.put(command.getName(), command);
        String[] orbNames = command.getAllOrbNames();
        for(String orbName : orbNames)
        {
            List<PersistentCommand> persistentForOrb = persistentByOrb.get(orbName);
            if(persistentForOrb == null)
            {
                persistentForOrb = new ArrayList<PersistentCommand>(10);
                persistentByOrb.put(orbName, persistentForOrb);
            }
            persistentForOrb.add(command);
        }
    }

    protected void loadPersistentCommands()
    {
        String tokenizedNames = cache.getProperty(SAVED_NAME_LIST_PROPERTY_KEY);
        if(tokenizedNames != null && tokenizedNames.length() > 0)
        {
            StringTokenizer tokenizer =
                    new StringTokenizer(tokenizedNames,
                                        Character.toString(Delimeter.PROPERTY_DELIMETER),
                                        false);
            while(tokenizer.hasMoreTokens())
            {
                String name = tokenizer.nextToken();
                String encodedPersistentContents = cache.getProperty(name);

                try
                {
                    PersistentCommand newPersistent =
                            new PersistentCommandImpl(name, encodedPersistentContents);
                    addPersistentCommand(newPersistent);
                }
                catch(DataValidationException e)
                {
                    String msg = "Could not load Persistent Command from storage. Values:" +
                                 encodedPersistentContents;
                    DefaultExceptionHandlerHome.find().process(e, msg);
                }
            }
        }
    }

    protected synchronized void saveFavorites()
    {
        final FavoriteCommand[] localFavoritesForSave =
                allFavorites.toArray(new FavoriteCommand[allFavorites.size()]);

        //noinspection ProhibitedExceptionDeclared
        GUIWorkerImpl worker = new GUIWorkerImpl(saveLockObject)
        {
            private FavoriteCommand[] favoritesForSave = localFavoritesForSave;

            @SuppressWarnings({"NonPrivateFieldAccessedInSynchronizedContext"})
            @Override
            public void execute() throws Exception
            {
                //first remove old ones
                int counterForPropertyKey = 0;
                Object removedItem = cache.remove((FAVORITE_NAME_LIST_PROPERTY_KEY +
                                                   counterForPropertyKey));
                while(removedItem != null)
                {
                    counterForPropertyKey++;
                    removedItem = cache.remove((FAVORITE_NAME_LIST_PROPERTY_KEY +
                                                counterForPropertyKey));
                }

                //now save all current ones
                counterForPropertyKey = 0;
                int countPerPropertyKey = 0;
                StringBuilder favValues = new StringBuilder(65 * MAXIMUM_FAVORITES_PER_PROPERTY);
                for(FavoriteCommand favoriteCommand : favoritesForSave)
                {
                    favValues.append(favoriteCommand.encodeValues());
                    favValues.append(Delimeter.PROPERTY_DELIMETER);
                    countPerPropertyKey++;
                    if(countPerPropertyKey == MAXIMUM_FAVORITES_PER_PROPERTY)
                    {
                        cache.setProperty((FAVORITE_NAME_LIST_PROPERTY_KEY +
                                           counterForPropertyKey),
                                          favValues.toString());
                        counterForPropertyKey++;
                        countPerPropertyKey = 0;
                        favValues = new StringBuilder(60 * MAXIMUM_FAVORITES_PER_PROPERTY);
                    }
                }
                if(favValues.length() > 0)
                {
                    cache.setProperty((FAVORITE_NAME_LIST_PROPERTY_KEY +
                                       counterForPropertyKey),
                                      favValues.toString());
                }

                superSave();
            }

            @Override
            public void handleException(Exception e)
            {
                //noinspection NonPrivateFieldAccessedInSynchronizedContext
                exceptionThrown = true;
                DefaultExceptionHandlerHome.find().process(e, "Could not save Favorite Commands.");
            }

            @Override
            public boolean isCleanUpEnabled()
            {
                return false;
            }
            @Override
            public boolean isInitializeEnabled()
            {
                return false;
            }
            @Override
            public boolean isProcessDataEnabled()
            {
                return false;
            }
        };

        APIWorkerImpl.run(worker);
    }

    protected synchronized void savePersistent()
    {
        Collection<PersistentCommand> values = persistentByName.values();
        final PersistentCommand[] localPersistentsForSave =
                values.toArray(new PersistentCommand[values.size()]);

        //noinspection ProhibitedExceptionDeclared
        GUIWorkerImpl worker = new GUIWorkerImpl(saveLockObject)
        {
            private PersistentCommand[] persistentsForSave = localPersistentsForSave;

            @SuppressWarnings({"NonPrivateFieldAccessedInSynchronizedContext"})
            @Override
            public void execute() throws Exception
            {
                //first remove old ones
                String tokenizedNamesToRemove = (String) cache.remove(SAVED_NAME_LIST_PROPERTY_KEY);
                if(tokenizedNamesToRemove != null && tokenizedNamesToRemove.length() > 0)
                {
                    StringTokenizer tokenizer =
                            new StringTokenizer(tokenizedNamesToRemove,
                                                Character.toString(Delimeter.PROPERTY_DELIMETER),
                                                false);
                    while(tokenizer.hasMoreTokens())
                    {
                        String name = tokenizer.nextToken();
                        cache.remove(name);
                    }
                }

                //now save all current ones
                StringBuilder allNames = new StringBuilder(15 * persistentsForSave.length);
                for(PersistentCommand persistentCommand : persistentsForSave)
                {
                    String name = persistentCommand.getName();
                    allNames.append(name).append(Delimeter.PROPERTY_DELIMETER);
                    cache.setProperty(name, persistentCommand.encodeValues());
                }
                if(allNames.length() > 0)
                {
                    cache.setProperty(SAVED_NAME_LIST_PROPERTY_KEY, allNames.toString());
                }

                superSave();
            }

            @Override
            public void handleException(Exception e)
            {
                //noinspection NonPrivateFieldAccessedInSynchronizedContext
                exceptionThrown = true;
                DefaultExceptionHandlerHome.find().process(e, "Could not save Persistent Commands.");
            }

            @Override
            public boolean isCleanUpEnabled()
            {
                return false;
            }

            @Override
            public boolean isInitializeEnabled()
            {
                return false;
            }

            @Override
            public boolean isProcessDataEnabled()
            {
                return false;
            }
        };

        APIWorkerImpl.run(worker);
    }

    protected void superSave()
    {
        super.save();
    }

    protected void fireSavedFavoriteCommandEvent(final FavoriteCommand command)
    {
        final SavedCommandStorageListener[] localListeners;
        synchronized(listeners)
        {
            localListeners = listeners.toArray(new SavedCommandStorageListener[listeners.size()]);
        }

        Runnable runner = new Runnable()
        {
            public void run()
            {
                for(SavedCommandStorageListener listener : localListeners)
                {
                    //noinspection CatchGenericClass
                    try
                    {
                        listener.favoriteCommandSaved(command);
                    }
                    catch(Exception e)
                    {
                        GUILoggerHome.find().exception(
                                "Exception Sending Event from SavedCommandFactory.", e);
                    }
                }
            }
        };
        Thread thread = new Thread(runner);
        thread.start();
    }

    protected void fireRemovedFavoriteCommandEvent(final FavoriteCommand command)
    {
        final SavedCommandStorageListener[] localListeners;
        synchronized(listeners)
        {
            localListeners = listeners.toArray(new SavedCommandStorageListener[listeners.size()]);
        }

        Runnable runner = new Runnable()
        {
            public void run()
            {
                for(SavedCommandStorageListener listener : localListeners)
                {
                    //noinspection CatchGenericClass
                    try
                    {
                        listener.favoriteCommandRemoved(command);
                    }
                    catch(Exception e)
                    {
                        GUILoggerHome.find().exception(
                                "Exception Sending Event from SavedCommandFactory.", e);
                    }
                }
            }
        };
        Thread thread = new Thread(runner);
        thread.start();
    }

    protected void fireSavedPersistentCommandEvent(final PersistentCommand command)
    {
        final SavedCommandStorageListener[] localListeners;
        synchronized(listeners)
        {
            localListeners = listeners.toArray(new SavedCommandStorageListener[listeners.size()]);
        }

        Runnable runner = new Runnable()
        {
            public void run()
            {
                for(SavedCommandStorageListener listener : localListeners)
                {
                    //noinspection CatchGenericClass
                    try
                    {
                        listener.persistentCommandSaved(command);
                    }
                    catch(Exception e)
                    {
                        GUILoggerHome.find().exception(
                                "Exception Sending Event from SavedCommandFactory.", e);
                    }
                }
            }
        };
        Thread thread = new Thread(runner);
        thread.start();
    }

    protected void fireRemovedPersistentCommandEvent(final PersistentCommand command)
    {
        final SavedCommandStorageListener[] localListeners;
        synchronized(listeners)
        {
            localListeners = listeners.toArray(new SavedCommandStorageListener[listeners.size()]);
        }

        Runnable runner = new Runnable()
        {
            public void run()
            {
                for(SavedCommandStorageListener listener : localListeners)
                {
                    //noinspection CatchGenericClass
                    try
                    {
                        listener.persistentCommandRemoved(command);
                    }
                    catch(Exception e)
                    {
                        GUILoggerHome.find().exception(
                                "Exception Sending Event from SavedCommandFactory.", e);
                    }
                }
            }
        };
        Thread thread = new Thread(runner);
        thread.start();
    }

    @SuppressWarnings({"UseOfSystemOutOrSystemErr", "OverlyLongMethod"})
    public static void main(String[] args)
    {
//        SavedCommandFactory factory = new SavedCommandFactory()
//        {
//            protected void initializeCache()
//            {
//                cache = new Properties();
//
//                persistentByName = new HashMap<String, PersistentCommand>(40);
//                allFavorites = new ArrayList<FavoriteCommand>(50);
//                favoritesByOrb = new HashMap<String, List<FavoriteCommand>>(30);
//                persistentByOrb = new HashMap<String, List<PersistentCommand>>(30);
//
//                try
//                {
//                    FileInputStream is = new FileInputStream("ARTest.properties");
//                    cache.load(is);
//                    is.close();
//                }
//                catch(FileNotFoundException e)
//                {
//                    System.out.println("e = " + e);
//                }
//                catch(IOException e)
//                {
//                    System.out.println("e = " + e);
//                }
//
//                loadFavoriteCommands();
//                loadPersistentCommands();
//            }
//            protected void superSave()
//            {
//                try
//                {
//                    FileOutputStream os = new FileOutputStream("ARTest.properties");
//                    cache.store(os, "ARCommandsSaved");
//                    os.close();
//                }
//                catch(FileNotFoundException e)
//                {
//                    System.out.println("e = " + e);
//                }
//                catch(IOException e)
//                {
//                    System.out.println("e = " + e);
//                }
//            }
//        };
//
//        FavoriteCommand fav = factory.createFavoriteCommand("runSomething", "ics01ics0002");
//        factory.saveFavoriteCommand(fav);
//        fav = factory.createFavoriteCommand("runSomethingElse", "someOrb");
//        factory.saveFavoriteCommand(fav);
//        fav = factory.createFavoriteCommand("runSomethingElse2", "someOrb2");
//        factory.saveFavoriteCommand(fav);
//
//        FavoriteCommand[] allFavs = factory.getAllFavoriteCommands();
//        System.out.println(Arrays.toString(allFavs));
//
//        System.out.println(factory.containsFavoriteCommands("someOrb"));
//        System.out.println(factory.containsFavoriteCommands("notFoundOrg"));
//
//        allFavs = factory.getFavoriteCommands("someOrb2");
//        System.out.println(Arrays.toString(allFavs));
//
//        for(FavoriteCommand favoriteCommand : allFavs)
//        {
//            factory.removeFavoriteCommand(favoriteCommand);
//        }
//
//        String[] orbNames = {"ics01ics0002", "bc01prodbc96", "cas01cas2006"};
//        String[] orbNames2 = {"bc01prodbc96", "cas01cas2006"};
//        String[] argumentValues = {"true", "5", null, "valueA"};
//
//        PersistentCommand pers =
//                factory.createPersistentCommand("MyCommand", "The Description", "runSomething",
//                                                orbNames, argumentValues);
//        factory.savePersistentCommand(pers);
//        pers = factory.createPersistentCommand("MyCommand2", "The Description2", "runSomethingElse",
//                                               orbNames2, argumentValues);
//        factory.savePersistentCommand(pers);
//        pers = factory.createPersistentCommand("MyCommand3", "The Description3", "runSomething3",
//                                               orbNames, argumentValues);
//        factory.savePersistentCommand(pers);
//
//        PersistentCommand[] allPers = factory.getAllPersistentCommands();
//        System.out.println(Arrays.toString(allPers));
//
//        String[] allPersNames = factory.getAllPersistentCommandNames();
//        System.out.println(Arrays.toString(allPersNames));
//
//        pers = factory.getPersistentCommand("MyCommand");
//        System.out.println("pers = " + pers);
//
//        allPers = factory.getPersistentCommands("ics01ics0002");
//        System.out.println(Arrays.toString(allPers));
//
//        factory.removePersistentCommand(pers);
//
//        allPers = factory.getAllPersistentCommands();
//        System.out.println(Arrays.toString(allPers));
//
//        allPersNames = factory.getAllPersistentCommandNames();
//        System.out.println(Arrays.toString(allPersNames));
//
//        factory.removePersistentCommand("MyCommand3");
//
//        allPers = factory.getAllPersistentCommands();
//        System.out.println(Arrays.toString(allPers));
//
//        allPersNames = factory.getAllPersistentCommandNames();
//        System.out.println(Arrays.toString(allPersNames));
//
//        factory.save();
//
//        try
//        {
//            Thread.sleep(10000);
//        }
//        catch(InterruptedException e)
//        {
//            System.out.println("e = " + e);
//        }
        System.exit(0);
    }
}
