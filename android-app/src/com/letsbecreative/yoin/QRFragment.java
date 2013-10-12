package com.letsbecreative.yoin;

import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;
import com.welcu.android.zxingfragmentlib.BarCodeScannerFragment;
//import com.welcu.android.zxingfragmentlib.BarCodeScannerFragment.IResultCallback;

public class QRFragment extends BarCodeScannerFragment {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setmCallBack(new IResultCallback() {
            @Override
            public void result(Result lastResult) {
                Toast.makeText(getActivity(), "Scan: " + lastResult.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
