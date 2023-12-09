package com.usinacucau.horasparadas;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class InstallationIdProvider {
    private static final String PREFS_NAME = "InstallationPrefs";
    private static final String INSTALLATION_ID = "InstallationId";

    public static String getInstallationId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String installationId = preferences.getString(INSTALLATION_ID, null);

        if (installationId == null) {
            installationId = generateInstallationId();
            preferences.edit().putString(INSTALLATION_ID, installationId).apply();
        }


        String installationId1 = installationId;
        return installationId;
    }

    private static String generateInstallationId() {
        return UUID.randomUUID().toString();
    }
}

