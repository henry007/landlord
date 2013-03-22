
package com.hurray.landlord.res;

import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ManifestUtil;
import com.hurray.landlord.utils.PathUtil;
import com.hurray.landlord.utils.ResAvatarUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ResUpdateChecker implements ResultCode {

    private static final String TAG = "ResUpdateChecker";

    private static final String ASSETS_RES_FILE = "avatar.zip";

    private ResUpdateCheckerListener mListener;

    private int mAssetsResVer;
    private int mNetResVer;
    private int mSdcardResVer;

    private int mResultCode;
    private String mUri;

    public void setResUpdateCheckerListener(ResUpdateCheckerListener l) {
        mListener = l;
    }

    public void check() {
        new Thread() {
            public void run() {
                doCheck();
            }
        }.start();
    }

    private void doCheck() {

        getVersions();

        if (mNetResVer > mAssetsResVer) {
            // 网络
            if (mNetResVer > mSdcardResVer) {
                // 升级到网络
                mResultCode = NEED_UPDATE_FROM_NET_TO_SDCARD;
            } else {
                // 不必升级
                mResultCode = NEED_UPDATE_NONE;
            }
        } else { // 本地
            if (mAssetsResVer > mSdcardResVer) {
                // 升级到APK
                mResultCode = NEED_UPDATE_FROM_ASSETS_TO_SDCARD;
                mUri = ASSETS_RES_FILE;
                mListener.onCheckResult(0, mUri);
                return;
            } else {
                // 不必升级
                mResultCode = NEED_UPDATE_NONE;
            }
        }

        mListener.onCheckResult(mResultCode, mUri);
    }

    private void getVersions() {
        mAssetsResVer = ManifestUtil.getAssetsResVersion();
        mSdcardResVer = getSdcardAvatarVer();
        mNetResVer = 0;
    }

    private int getSdcardAvatarVer() {
        int ver = 0;

        File file = ResAvatarUtil.getAvatarManifestFile();
        if (file.exists()) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                SAXParser parser = factory.newSAXParser();
                ManifestHandler mh = new ManifestHandler();
                parser.parse(file, mh);
                ver = mh.getVersion();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ver;
    }

    private static class ManifestHandler extends DefaultHandler {

        private int mVersion;

        private boolean isAllFilesExist;

        public ManifestHandler() {
            super();
            mVersion = 0;
            isAllFilesExist = true;
        }

        public int getVersion() {
            if (isAllFilesExist) {
                return mVersion;
            }

            return 0;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            super.startElement(uri, localName, qName, attributes);

            if (localName.compareTo("manifest") == 0) {
                String version = attributes.getValue("version");
                LogUtil.d(TAG, "version=" + version);
                try {
                    mVersion = Integer.valueOf(version);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (qName.compareTo("file") == 0) {
                String name = attributes.getValue("name");
                LogUtil.d(TAG, "name=" + name);

                String fullName = PathUtil.getResAvatarPath() + File.separator + name;
                if (!new File(fullName).exists()) {
                    isAllFilesExist = false;
                }

            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
        }

    }

}
