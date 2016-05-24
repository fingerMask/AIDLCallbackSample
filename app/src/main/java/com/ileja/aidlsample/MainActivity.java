package com.ileja.aidlsample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private IHudServer hudServer;

    @Bind(R.id.tvMessage)
    TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void connect(){
        Intent intent = new Intent(MainActivity.this,DataService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @OnClick(R.id.btnClick)
    void onClick(){
        if(hudServer != null){
            try {
                hudServer.sendMessage("this is rxjava sample");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            hudServer = IHudServer.Stub.asInterface(service);
            if(hudServer != null){
                try{
                    hudServer.registerCallback(callback);
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            hudServer = null;
        }
    };

    private IHudCallback.Stub callback = new IHudCallback.Stub() {
        @Override
        public void onMessage(String message) throws RemoteException {
            Observable.just(message)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            tvMessage.setText(s);
                        }
                    });
        }
    };
}
