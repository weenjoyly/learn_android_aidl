package com.example.demoapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText mASumEditText;
    EditText mBSumEditText;
    Button mRunBtn;
    TextView mResultTextView;
    IMyAidlInterface myAidlInterface;
    ServiceConnection mServiceConnection;
    ICallback mResultCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initParam();
    }

    private void initView() {
        mASumEditText = findViewById(R.id.a);
        mBSumEditText = findViewById(R.id.b);
        mRunBtn = findViewById(R.id.run);
        mResultTextView = findViewById(R.id.result);
    }

    private void bindService() {
        bindService(new Intent(this, CountService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initParam() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myAidlInterface = IMyAidlInterface.Stub.asInterface(service);
                try {
                    myAidlInterface.register(mResultCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                myAidlInterface = null;
            }
        };
        mRunBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myAidlInterface != null) {
                    Integer a, b;
                    try {
                        a = Integer.valueOf(mASumEditText.getText().toString());
                        b = Integer.valueOf(mBSumEditText.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "请输入数字", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        myAidlInterface.count(a, b);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mResultCallback = new ICallback.Stub() {
            @Override
            public void call(final String data) throws RemoteException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mResultTextView.setText(data);
                    }
                });
            }
        };
    }


    public void onStop() {
        super.onStop();
        unbindService(mServiceConnection);
    }

    public void onResume() {
        super.onResume();
        bindService();
    }
}
