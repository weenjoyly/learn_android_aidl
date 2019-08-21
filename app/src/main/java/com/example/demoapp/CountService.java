package com.example.demoapp;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

public class CountService extends Service {
    RemoteCallbackList<ICallback> callbackRemoteCallbackList;

    @Override
    public IBinder onBind(Intent intent) {
        if (callbackRemoteCallbackList == null) {
            callbackRemoteCallbackList = new RemoteCallbackList<>();
        }
        return new IMyAidlInterface.Stub() {
            @Override
            public void count(final int a, final int b) throws RemoteException {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //模拟异步请求
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        call(String.valueOf(a + b));
                    }
                }).start();
            }

            @Override
            public void register(ICallback call) throws RemoteException {
                callbackRemoteCallbackList.register(call);
            }

            @Override
            public void unregister(ICallback call) throws RemoteException {
                callbackRemoteCallbackList.unregister(call);
            }
        };
    }

    private void call(String s) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (int i = 0; i < callbackRemoteCallbackList.getRegisteredCallbackCount(); i++) {
                try {
                    callbackRemoteCallbackList.getRegisteredCallbackItem(i).call(s);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
