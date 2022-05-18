package com.tananaev.passportreader;

import android.content.res.AssetManager;

public class RapidSnark {
    native boolean prove(AssetManager jAssetManager, String zkeyFile, String witnessFile, byte[] proof, byte[] publicInputs, byte[] error);

    static {
        System.loadLibrary("rapidsnark-wrapper");
    }
}
