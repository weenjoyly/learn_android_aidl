// IMyAidlInterface.aidl
package com.example.demoapp;
import com.example.demoapp.ICallback;
// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void count(int a, int b);
    void register(ICallback call);
    void unregister(ICallback call);
}
