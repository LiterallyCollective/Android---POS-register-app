package com.refresh.printerhelper;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.refresh.pos.R;
import com.refresh.printerhelper.utils.AidlUtil;
import com.refresh.printerhelper.utils.BluetoothUtil;
import com.refresh.printerhelper.utils.BytesUtil;
import com.refresh.printerhelper.utils.ESCUtil;

import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.LoadingDialog;
import woyou.aidlservice.jiuiv5.ICallback;

/**
 * Created by Administrator on 2017/5/27.
 */

public class AllActivity extends BaseActivity implements View.OnClickListener {
    boolean mark = false;
    LoadingDialog mDialog;
    byte[] temp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);
        setMyTitle(R.string.all_title);
        setBack();

        findViewById(R.id.multi_one).setOnClickListener(this);
        findViewById(R.id.multi_two).setOnClickListener(this);
        findViewById(R.id.multi_three).setOnClickListener(this);
        findViewById(R.id.multi_four).setOnClickListener(this);
        findViewById(R.id.multi_five).setOnClickListener(this);
        findViewById(R.id.multi_six).setOnClickListener(this);

        findViewById(R.id.multi_buffer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!baseApp.isAidl()){
                    Toast.makeText(AllActivity.this, R.string.toast_1, Toast.LENGTH_LONG).show();
                    return;
                }else{
                    if(mark){
                        mark = false;
                        v.setBackgroundColor(getResources().getColor(R.color.text));
                        ((TextView)v).setText(R.string.enter_work);
                    }else{
                        mark = true;
                        v.setBackgroundColor(getResources().getColor(R.color.gray));
                        ((TextView)v).setText(R.string.exit_work);

                    }

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        byte[] rv = null;
        switch (v.getId()){
            case R.id.multi_one:
                rv = BytesUtil.getBaiduTestBytes();
                break;
            case R.id.multi_two:
                rv = BytesUtil.getMeituanBill();
                break;
            case R.id.multi_three:
                rv = BytesUtil.getErlmoData();
                break;
            case R.id.multi_four:
                rv = BytesUtil.getKoubeiData();
                break;
            case R.id.multi_five:
                rv = ESCUtil.printBitmap(BytesUtil.initBlackBlock(384));
                break;
            case R.id.multi_six:
                rv = ESCUtil.printBitmap(BytesUtil.initBlackBlock(800,384));
                break;
        }

        if(mark){
            sendDataThread(rv);
        }else{
            sendData(rv);
        }
    }

    ICallback mICallback = new ICallback.Stub() {
        @Override
        public void onRunResult(boolean isSuccess) throws RemoteException {

        }

        @Override
        public void onReturnString(String result) throws RemoteException {

        }

        @Override
        public void onRaiseException(int code, String msg) throws RemoteException {

        }

        @Override
        public void onPrintResult(int code, String msg) throws RemoteException {
            if(code == 0){
                mDialog.cancel();
            }else{
                AidlUtil.getInstance().sendRawDatabyBuffer(temp, mICallback);
            }
        }
    };

    private void sendDataThread(byte[] send){
        mDialog = DialogCreater.createLoadingDialog(this, getResources().getString(R.string.printing));
        mDialog.show();
        temp = send;
        AidlUtil.getInstance().sendRawDatabyBuffer(temp, mICallback);
    }


    private void sendData(final byte[] send){
        if(baseApp.isAidl()){
            AidlUtil.getInstance().sendRawData(send);
        }else{
            BluetoothUtil.sendData(send);
        }
    }
}
