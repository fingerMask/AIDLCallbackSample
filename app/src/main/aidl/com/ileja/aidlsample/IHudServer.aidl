// IHudServer.aidl
package com.ileja.aidlsample;

import com.ileja.aidlsample.IHudCallback;

// Declare any non-default types here with import statements

interface IHudServer {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void sendMessage(String message);

    void registerCallback(IHudCallback cb);

    void unregisterCallback(IHudCallback cb);
}
