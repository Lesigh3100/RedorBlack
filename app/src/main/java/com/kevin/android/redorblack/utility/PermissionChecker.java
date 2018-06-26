package com.kevin.android.redorblack.utility;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.kevin.android.redorblack.MainActivity;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;

public class PermissionChecker {
    private final String TAG = "PermissionChecker";
   private final int MY_PERMISSIONS_GRANTED = 124455;
   private MainActivity mainActivity;

    public PermissionChecker(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    // a string array of all the required permissions
    private static final String[] appPermissions = {
            CAMERA, RECORD_AUDIO, MODIFY_AUDIO_SETTINGS, READ_PHONE_STATE, ACCESS_NETWORK_STATE
    };

    // returns a string of all the permissions that still need to be granted
    private String[] allPermissions(String[] needed) {
        ArrayList<String> results = new ArrayList<>();
        for (String permission : needed) {
            if (!permissionGranted(permission)) {
                results.add(permission);
            }
        }
        return (results.toArray(new String[results.size()]));
    }

    // returns true if the single permission is granted
    private boolean permissionGranted(String permission) {
        return (ContextCompat.checkSelfPermission(mainActivity, permission)) == PackageManager.PERMISSION_GRANTED;
    }

    // returns true if all permissions are granted
    public boolean allPermissionsGranted() {
        return (permissionGranted(RECORD_AUDIO) && permissionGranted(MODIFY_AUDIO_SETTINGS) &&
                permissionGranted(READ_PHONE_STATE) && permissionGranted(ACCESS_NETWORK_STATE) && permissionGranted(CAMERA));
    }

    // checks if every necessary permission is granted & asks for any that are not yet granted
    public void checkAllPermissions() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(mainActivity, allPermissions(appPermissions), MY_PERMISSIONS_GRANTED);
        }
    }
}
