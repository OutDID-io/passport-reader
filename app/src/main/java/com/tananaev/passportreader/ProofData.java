package com.tananaev.passportreader;

import android.util.Base64;
import android.util.Log;

import net.sf.scuba.util.Hex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class ProofData {
    public static final int BYTES_PER_STRING = 15;
    public static final int MAX_LDS_LENGTH = 256;
    public static final int MAX_ATTRS_LENGTH = 128;
    public static final int MRZ_LENGTH = 93;
    public static final int MAX_RSA_LENGTH = 512;

    private byte[] mrz;
    private byte[] lds;
    private byte[] attrs;
    private byte[] sig;
    private byte[] mod;
    private int ldsLen;
    private int attrsLen;
    // The decryptedSigHead should be removed if further unused
    private final byte[] decryptedSigHead = Hex.hexStringToBytes("3031300d060960864801650304020105000420");

    final String TAG = "ProofData";

    public void setMrz(byte[] mrz) {
        assert mrz.length == MRZ_LENGTH;
        this.mrz = mrz;
    }

    public void setSig(byte[] sig) {
        assert sig.length <= MAX_RSA_LENGTH;
        this.sig = sig;
    }

    public void setMod(byte[] mod) {
        assert mod.length <= MAX_RSA_LENGTH;
        this.mod = mod;
    }

    public void setAttrs(byte[] attrs) {
        assert attrs.length <= MAX_ATTRS_LENGTH;
        this.attrsLen = attrs.length*8;
        this.attrs = Arrays.copyOf(attrs, MAX_ATTRS_LENGTH); // Pad with zeros up to that length
    }

    public void setLds(byte[] lds) {
        assert lds.length <= MAX_LDS_LENGTH;
        this.ldsLen = lds.length*8;
        this.lds = Arrays.copyOf(lds, MAX_LDS_LENGTH);
    }

    public String exportJson() {
        JSONObject res = new JSONObject();
        try {
            res.put("mrz", new JSONArray(toBinaryArray(this.mrz)))
                    .put("lds", new JSONArray(toBinaryArray(this.lds)))
                    .put("ldsLen", this.ldsLen)
                    .put("attrs", new JSONArray(toBinaryArray(this.attrs)))
                    .put("attrsLen", this.attrsLen)
                    .put("sig", new JSONArray(toStringHexArray(this.sig, BYTES_PER_STRING)))
                    .put("mod", new JSONArray(toStringHexArray(this.mod, BYTES_PER_STRING)));

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
