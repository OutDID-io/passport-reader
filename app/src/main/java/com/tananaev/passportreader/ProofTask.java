package com.tananaev.passportreader;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

public class ProofTask implements Runnable {
    public static final String TAG = "MainActivity";

    private final AssetManager mgr;
    private final ProofResultCallback cb;
    public ProofTask(AssetManager mgr, ProofResultCallback cb) {
        this.mgr = mgr;
        this.cb = cb;
    }

    @Override
    public void run() {
        RapidSnark rs = new RapidSnark();
        byte[] proof = new byte[1024], publicInputs = new byte[2048], error = new byte[1024];
        boolean res = rs.prove(this.mgr, "circuit_final.zkey", "w.wtns", proof, publicInputs, error);

        this.cb.onComplete(new ProofResult(new String(proof), new String(publicInputs), new String(error)));
    }
}

