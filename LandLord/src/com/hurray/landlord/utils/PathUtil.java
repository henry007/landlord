
package com.hurray.landlord.utils;

import java.io.File;

public class PathUtil {

    private static final String PATH_ROOT = SdcardUtil.getRootPath() + File.separator + ".hylandlord";

    private static final String PATH_APK_UPDATE = PATH_ROOT + File.separator + "apk_update";
    
    private static final String PATH_RES = PATH_ROOT + File.separator + "res";

    private static final String PATH_ZIP = PATH_ROOT + File.separator + "zip";

    private static final String PATH_AVATAR = PATH_RES + File.separator + "avatar";

    public static String getApkUpdatePath() {
        if (SdcardUtil.isSdcardExist()) {
            new File(PATH_APK_UPDATE).mkdirs();
            return PATH_APK_UPDATE;
        }

        return null;
    }

    public static String getResPath() {
        return PATH_RES;
    }

    public static String getZipPath() {
        return PATH_ZIP;
    }

    public static String getResAvatarPath() {
        return PATH_AVATAR;
    }

    public static boolean ensurePathExist() {
        boolean success = true;
        File f = new File(PATH_AVATAR);
        if (!f.mkdirs()) {
            success = false;
        }
        f = new File(PATH_ZIP);
        if (!f.mkdirs()) {
            success = false;
        }
        return success;
    }

}
