package com.general.files;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class GeneralFunctionsDummy {

    GeneralFunctions generalFunc;

    public boolean isCameraStoragePermissionGranted(Context a) {

        generalFunc = new GeneralFunctions(a);
        int var1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            var1 = ContextCompat.checkSelfPermission(a, "android.permission.READ_MEDIA_IMAGES");
        } else {
            var1 = ContextCompat.checkSelfPermission(a, "android.permission.WRITE_EXTERNAL_STORAGE");
        }
        int var2 = ContextCompat.checkSelfPermission(a, "android.permission.CAMERA");
        if (var1 == 0 && var2 == 0) {
            return true;
        } else {
            if (!(a instanceof Activity)) {
                System.out.println("Context must be an instance of an activity.");
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    boolean var3 = ActivityCompat.shouldShowRequestPermissionRationale((Activity)a, "android.permission.READ_MEDIA_IMAGES") && ActivityCompat.shouldShowRequestPermissionRationale((Activity)a, "android.permission.CAMERA");
                    if (var3) {
                        generalFunc.storeData("CAMERA_PERMISSION_ASKED", "Yes");
                        ActivityCompat.requestPermissions((Activity)a, new String[]{"android.permission.CAMERA", "android.permission.READ_MEDIA_IMAGES"}, 51);
                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("We need camera and storage permission to perform necessary task. Please permit mentioned permissions through Settings screen.", "LBL_NOTIFY_PERMISSION_CAMERA"), generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"), (var1x) -> {
                            if (var1x == 1) {
                                generalFunc.openSettings(false);
                            }

                        });
                    }
                } else {
                    boolean var3 = ActivityCompat.shouldShowRequestPermissionRationale((Activity)a, "android.permission.WRITE_EXTERNAL_STORAGE") && ActivityCompat.shouldShowRequestPermissionRationale((Activity)a, "android.permission.CAMERA");
                    if (var3) {
                        generalFunc.storeData("CAMERA_PERMISSION_ASKED", "Yes");
                        ActivityCompat.requestPermissions((Activity)a, new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"}, 51);
                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("We need camera and storage permission to perform necessary task. Please permit mentioned permissions through Settings screen.", "LBL_NOTIFY_PERMISSION_CAMERA"), generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"), (var1x) -> {
                            if (var1x == 1) {
                                generalFunc.openSettings(false);
                            }

                        });
                    }
                }
            }

            return false;
        }
    }
}
