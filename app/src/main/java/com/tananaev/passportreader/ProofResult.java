package com.tananaev.passportreader;

public class ProofResult {
    public final String proof, inputs, error;

    public ProofResult(String proof, String inputs, String error) {
        this.proof = proof;
        this.inputs = inputs;
        this.error = error;
    }
}
