package com.letsbecreative.yoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;

import com.google.zxing.Result;
import com.letsbecreative.yoin.MainActivity.IQRCallback;
import com.welcu.android.zxingfragmentlib.BarCodeScannerFragment;
//import com.welcu.android.zxingfragmentlib.BarCodeScannerFragment.IResultCallback;

public class QRFragment extends BarCodeScannerFragment {

	public QRListener listener;
	//IQRCallback newCardCallback;
	
	public interface QRListener{
		public void setNewCardCallback(IQRCallback callback);
		public void requestHttpGet(String result);
		
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setmCallBack(new IResultCallback() {
            @Override
            public void result(Result lastResult) {
                Toast.makeText(getActivity(), "Scan: " + lastResult.toString(), Toast.LENGTH_LONG).show();
                listener.requestHttpGet(/*"http://79.136.89.243/get/" + */ lastResult.toString());
            }
        });
    }
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		listener = (QRListener) activity;
	}



}
