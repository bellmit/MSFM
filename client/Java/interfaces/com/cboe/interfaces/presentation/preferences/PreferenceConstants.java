package com.cboe.interfaces.presentation.preferences;

import java.awt.event.KeyEvent;

public interface PreferenceConstants
{
    char DELIMITER = KeyEvent.VK_END;

    String PROPERTIES_SECTION_NAME = "Preferences";
	String PREFERENCE_SERVICES = "PreferenceServices";
	String PREFERENCES_FILE = "PreferencesFile";
	String REMOTE_FILE = "RemoteFile";
	String BACKUP_FILE = "BackupFile";

	String BUSINESS_SECTION = "Business-Preferences";
    String BUSINESS_PREFERENCES_LOCATION_KEY = "BusinessLocalFile";
    String REMOTE_BUSINESS_PREFERENCES_LOCATION_KEY = "Remote.BusinessPreferences.File";

    String GUI_SECTION = "GUI-Preferences";
    String GUI_PREFERENCES_LOCATION_KEY = "GUILocalFile";
    String REMOTE_GUI_PREFERENCES_LOCATION_KEY = "Remote.GUIPreferences.File";

    String EOP_GUI_SECTION = "EOPGUI-Preferences";

    char VERSION_REPLACEMENT_CHAR = '-';
    String DEFAULT_PATH_SEPARATOR = ".";

    String REMOTE_SAVE_ALLOWED_KEY = "SaveAllowed";
    String CURRENT_VERSION_PREFERENCE_NAME = "CurrVersion";
    String VERSIONS_PREFERENCE_NAME = "AllVersions";
    String CONVERT_PREFERENCES_AUTOMATICALLY_PROPERTY = "ConvertPreferencesAutomatically";
}
