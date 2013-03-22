
package com.hurray.landlord.utils;

import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class ResAvatarUtil {

    private static final String TAG = "ResAvatarUtil";

    private static final String MANIFEST = "manifest.xml";

    private static final String TYPENAME = "drawable";

    private static final int BUFF_SIZE = 512;

    private static final int FRAME_INTERVAL = 150;

    private static final int[] FRAME_INTERVAL_EYES = {
            500, 50, 125, 50, 2000
    };

    public static String getAvatarManifest() {
        return PathUtil.getResAvatarPath() + File.separator + MANIFEST;
    }

    public static File getAvatarManifestFile() {
        return new File(getAvatarManifest());
    }

    public static String getResAvatarBase(boolean isMan) {
        return getSex(isMan) + "base";
    }

    public static BitmapDrawable getResAvatarBaseDrawable(Resources res, boolean isMan) {
        return new BitmapDrawable(res, getResAvatarBase(isMan));
    }

    public static Drawable getResAvatarBaseDrawable(Resources res, boolean isMan, String packageName) {

        String name = getResAvatarBase(isMan);

        int id = res.getIdentifier(name, TYPENAME, packageName);

        return res.getDrawable(id);
    }

    public static String getResAvatarHand(boolean isMan) {
        return getSex(isMan) + "hand";
    }


    public static Drawable getResAvatarHandDrawable(Resources res, boolean isMan, String packageName) {
        return res.getDrawable(res.getIdentifier(getResAvatarHand(isMan),
                TYPENAME, packageName));
    }

    public static String getResAvatarClothes(boolean isMan, int clothesId) {
        return getSex(isMan) + "clothes" + clothesId;
    }

    public static File getResAvatarClothesFile(boolean isMan, int clothesId) {
        return new File(getResAvatarClothes(isMan, clothesId));
    }

    public static Drawable getResAvatarClothesDrawable(Resources res, boolean isMan,
            int clothesId, String packageName) {
        int id = res.getIdentifier(getResAvatarClothes(isMan, clothesId), TYPENAME,
                packageName);
        if(id!=0){
            return res.getDrawable(id);
        }
        return null;
    }

    public static String getResAvatarHair(boolean isMan, int hairId) {
        return getSex(isMan) + "hair" + hairId;
    }

    public static File getResAvatarHairFile(boolean isMan, int hairId) {
        return new File(getResAvatarHair(isMan, hairId));
    }

    public static Drawable getResAvatarHairDrawable(Resources res, boolean isMan,
            int hairId, String packageName) {
        int id = res.getIdentifier(getResAvatarHair(isMan, hairId), TYPENAME,
                packageName);
        Drawable d = null;
             d = res.getDrawable(id);
        return d;
    }

    public static ArrayList<String> getResAvatarEmotion(boolean isMan, int emotionId) {
        ArrayList<String> fileNameList = new ArrayList<String>();
        String prefix = getResAvatarEmotionPrefix(isMan, emotionId);

        int i = 0;
        File file = new File(prefix + i);
        while (file.exists()) {
            fileNameList.add(prefix + i);
            LogUtil.d(TAG, "add fileName=" + file.getPath());
            i++;
            file = new File(prefix + i);
        }
        return fileNameList;
    }

    public static boolean isEmotionExist(boolean isMan, int emotionId, Resources res,
            String packageName) {
        String prefix = getResAvatarEmotionPrefix(isMan, emotionId);
        return res.getDrawable(res.getIdentifier(prefix + 0, TYPENAME, packageName)).isVisible();
    }

    public static ArrayList<File> getResAvatarEmotionFiles(boolean isMan, int emotionId) {
        ArrayList<File> fileList = new ArrayList<File>();
        String prefix = getResAvatarEmotionPrefix(isMan, emotionId);

        int i = 0;
        File file = new File(prefix + i);
        while (file.exists()) {
            fileList.add(file);
            LogUtil.d(TAG, "add file=" + file.getPath());
            i++;
            file = new File(prefix + i);
        }
        return fileList;
    }

    public static ArrayList<Drawable> getResAvatarEmotionDrawables(Resources res,
            boolean isMan, int emotionId, String packageName) {
        ArrayList<Drawable> drawableList = new ArrayList<Drawable>();
        String prefix = getResAvatarEmotionPrefix(isMan, emotionId);
        Drawable d = null;
        if (emotionId != 0) {

            d = res.getDrawable(res.getIdentifier(prefix + 0, TYPENAME, packageName));

            drawableList.add(d);
            d = res.getDrawable(res.getIdentifier(prefix + 1, TYPENAME, packageName));
            drawableList.add(d);

        } else {
            d = res.getDrawable(res.getIdentifier(prefix + 0, TYPENAME, packageName));
            drawableList.add(d);
            d = res.getDrawable(res.getIdentifier(prefix + 1, TYPENAME, packageName));
            drawableList.add(d);
            d = res.getDrawable(res.getIdentifier(prefix + 2, TYPENAME, packageName));
            drawableList.add(d);
            d = res.getDrawable(res.getIdentifier(prefix + 1, TYPENAME, packageName));
            drawableList.add(d);
            d = res.getDrawable(res.getIdentifier(prefix + 0, TYPENAME, packageName));
            drawableList.add(d);
        }

        return drawableList;
    }

    public static AnimationDrawable getResAvatarEmotionAnimDrawable(Resources res, boolean isMan,
            int emotionId, String packageName) {
        ArrayList<Drawable> drawables = getResAvatarEmotionDrawables(res, isMan, emotionId,
                packageName);
        AnimationDrawable mAnimDrawable = null;
        if (drawables.size() > 0) {
            mAnimDrawable = new AnimationDrawable();
            Iterator<Drawable> it = drawables.iterator();
            int i = 0;
            while (it.hasNext()) {
                mAnimDrawable.addFrame(it.next(), FRAME_INTERVAL_EYES[i]);
                i++;
            }
            mAnimDrawable.setOneShot(false);
        }
        return mAnimDrawable;
    }

    private static String getResAvatarEmotionPrefix(boolean isMan, int emotionId) {
        return getSex(isMan) + "emotion" + emotionId + "_";
    }

    private static String getSex(boolean isMan) {
        if (isMan) {
            return "nan_";
        } else {
            return "nv_";
        }
    }

    public static void inputstreamTofile(InputStream is, File file) throws IOException {
        OutputStream os = new FileOutputStream(file);
        int bytesRead = 0;
        byte[] buffer = new byte[BUFF_SIZE];
        while ((bytesRead = is.read(buffer, 0, BUFF_SIZE)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        is.close();
    }

}
