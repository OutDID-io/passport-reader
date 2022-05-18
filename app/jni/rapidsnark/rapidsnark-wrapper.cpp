//
// Created by Zvezdin on 15/05/2022.
//
#include <jni.h>
#include <prover.h>

#include <android/log.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#define APPNAME "MainActivity"

const void debug_print_file(AAsset *file) {
    const void* contents = AAsset_getBuffer(file);

    //Print first witness bytes for debug
    const char* chars = (char*) contents;
    for(int i=0; i<10; i++) {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Byte %d %d", i, *(chars+i) );
    }

    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Size %d", AAsset_getLength64(file));
}

const void debug_write_data(void* p, const unsigned long size) {
    char* chars = (char*) p;
    for(long i=0; i<size; i++) {
        *(chars+i) = (char) i;
    }
}

extern "C" jboolean Java_com_tananaev_passportreader_RapidSnark_prove (

        JNIEnv *env,        /* interface pointer */

        jobject obj,        /* "this" pointer */
        jobject jassetManager,

        jstring zkey_file,

        jstring witness_file, jbyteArray proof, jbyteArray public_inputs, jbyteArray error)

{
    const char* zkey_file_c = env->GetStringUTFChars(zkey_file, 0);
    const char* witness_file_c = env->GetStringUTFChars(witness_file, 0);

    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "%s", witness_file_c);

    AAssetManager *mg = AAssetManager_fromJava(env, jassetManager);

    AAsset *witness_asset = AAssetManager_open(mg, witness_file_c, AASSET_MODE_BUFFER);
    AAsset *zkey_asset = AAssetManager_open(mg, zkey_file_c, AASSET_MODE_BUFFER);
    const void* witness_contents = AAsset_getBuffer(witness_asset);
    const unsigned long witness_size = AAsset_getLength64(witness_asset);
    const void* zkey_contents = AAsset_getBuffer(zkey_asset);
    const unsigned long zkey_size = AAsset_getLength64(zkey_asset);

    jbyte* proof_contents = env->GetByteArrayElements(proof, 0);
    const unsigned long proof_size = env->GetArrayLength(proof);

    jbyte* inputs_contents = env->GetByteArrayElements(public_inputs, 0);
    const unsigned long inputs_size = env->GetArrayLength(public_inputs);

    jbyte* error_contents = env->GetByteArrayElements(error, 0);
    const unsigned long error_size = env->GetArrayLength(error);

    debug_print_file(witness_asset);
    debug_print_file(zkey_asset);

    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Proof starting");
    int res = groth16_prover(zkey_contents, zkey_size, witness_contents, witness_size, (char*) proof_contents, proof_size, (char*) inputs_contents, inputs_size, (char*) error_contents, error_size);
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Proof result: %d", res);

    env->ReleaseByteArrayElements(proof, proof_contents, 0); //Copies changes to proof_contents back to JVM
    env->ReleaseByteArrayElements(public_inputs, inputs_contents, 0);
    env->ReleaseByteArrayElements(error, error_contents, 0);
    AAsset_close(witness_asset);
    AAsset_close(zkey_asset);

    return (bool) res;
}
