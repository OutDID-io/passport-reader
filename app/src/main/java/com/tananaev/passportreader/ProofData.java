package com.tananaev.passportreader;

import android.util.Log;

import net.sf.scuba.util.Hex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class ProofData {
    public static final int BYTES_PER_STRING = 15;

    private byte[] mrz;
    private byte[] encapContent;
    private byte[] ec;
    private byte[] signature;
    private byte[] dscRsaMod;
    private final byte[] decryptedSigHead = Hex.hexStringToBytes("3031300d060960864801650304020105000420");

    final String TAG = "ProofData";

    public void setMrz(byte[] mrz) {
        this.mrz = mrz;
    }

    public void setSignature(byte[] sig) {
        this.signature = sig;
    }

    public void setDscRsaMod(byte[] dscRsaMod) {
        this.dscRsaMod = dscRsaMod;
    }

    public void setEc(byte[] ec) {
        this.ec = ec;
    }

    public void setEncapContent(byte[] encapContent) {
        this.encapContent = encapContent;
    }

    public void verify() {
        assert this.mrz.length == 93;

        assert this.encapContent.length == 219;

        assert this.ec.length == 74;

        assert this.signature.length <= 512;

        assert this.dscRsaMod.length <= 512;
    }

    public String exportJson() {
        JSONObject res = new JSONObject();
        try {
            res.put("mrz", new JSONArray(toBinaryArray(this.mrz)))
                    .put("encapContent", new JSONArray(toBinaryArray(this.encapContent)))
                    .put("ec", new JSONArray(toBinaryArray(this.ec)))
                    .put("decryptedSigHead", new JSONArray(toBinaryArray(this.decryptedSigHead)))
                    .put("sig", new JSONArray(toStringHexArray(this.signature, BYTES_PER_STRING)))
                    .put("mod", new JSONArray(toStringHexArray(this.dscRsaMod, BYTES_PER_STRING)));

            return res.toString();
        } catch (JSONException e) {
            // Panic!
            Log.e(TAG, e.toString());
            return null;
        }
    }

    private String[] toStringHexArray(byte[] a, int bytesPerString) {
        String[] res = new String[(int) Math.ceil((double) a.length / bytesPerString)];
        for(int i=0; i<res.length; i++) {
            int length = bytesPerString;
            if(i == res.length-1) {
                length = a.length % bytesPerString;
            }
            StringBuilder sb = new StringBuilder();
            String hex = Hex.bytesToHexString(a, Math.max(a.length - (i+1)*bytesPerString, 0), length); // Need to effectively reverse all bytes
            sb.append(hex);
            res[i] = "0x" + sb.toString();
        }

        return res;
    }

    private byte[] toBinaryArray(byte[] a) {
        byte[] res = new byte[a.length*8];

        for(int i=0; i<a.length; i++) {
            byte[] numBits = toBinaryArray(a[i]);
            for(int j=0; j<numBits.length; j++) {
                res[i*8 + j] = numBits[j];
            }
        }

        return res;
    }

    private byte[] toBinaryArray(byte a) {
        byte[] res = new byte[8];
        for (int i = 0; i < res.length; i++) {
            res[res.length-1 - i] = ((a & (1 << i)) != 0 ? (byte) 1 : (byte) 0);
        }
        return res;
    }
}
