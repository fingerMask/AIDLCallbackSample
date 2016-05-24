package com.ileja.aidlsample;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

public class DataService extends Service {

    private RemoteCallbackList<IHudCallback> callbackList = new RemoteCallbackList<>();
    private static final int MSG_CALL = 0x01;


    public DataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return remoteBinder;
    }

    /**
     * 分发消息
     */
    private void dispatchProcessingCallback(final String message) {

        final int N = callbackList.beginBroadcast();
        try {
            for(int i = 0; i < N; i++) {
                IHudCallback cb = callbackList.getBroadcastItem(i);
                if(cb == null){
                    continue;
                }

                cb.onMessage(message);
            }
        } catch (RemoteException e) {
        }
        callbackList.finishBroadcast();
    }

    @Override
    public void onDestroy() {
        mHandler.removeMessages(0);
        callbackList.kill();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    private IHudServer.Stub remoteBinder = new IHudServer.Stub() {
        @Override
        public void sendMessage(String message) throws RemoteException {

            Message msg = mHandler.obtainMessage(MSG_CALL);
            msg.obj = hanleMessage(message);
            mHandler.sendMessage(msg);
        }

        @Override
        public void registerCallback(IHudCallback cb) throws RemoteException {
            if(cb != null){
                callbackList.register(cb);
            }
        }

        @Override
        public void unregisterCallback(IHudCallback cb) throws RemoteException {
            if(cb != null){
                callbackList.unregister(cb);
            }
        }

        private String hanleMessage(String message){
            return message+" is handled!!!";
        }
    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            dispatchProcessingCallback((String)msg.obj);
            super.handleMessage(msg);
        }
    };
}
