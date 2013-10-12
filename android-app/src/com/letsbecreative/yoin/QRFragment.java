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

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;

import com.google.zxing.Result;
import com.welcu.android.zxingfragmentlib.BarCodeScannerFragment;
//import com.welcu.android.zxingfragmentlib.BarCodeScannerFragment.IResultCallback;

public class QRFragment extends BarCodeScannerFragment {
	
	Activity parentActivity;
	MainActivity.IQRCallback newCardCallback;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setmCallBack(new IResultCallback() {
            @Override
            public void result(Result lastResult) {
                Toast.makeText(getActivity(), "Scan: " + lastResult.toString(), Toast.LENGTH_LONG).show();
                requestHttp(/*"http://79.136.89.243/get/" + */ lastResult.toString());
            }
        });
    }
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		parentActivity = activity;
	}
	
	public void setNewCardCallback(MainActivity.IQRCallback callback){
		newCardCallback = callback;
	}
	
	public void requestHttp(String fileUrl){

		AsyncTask<String, Object, String> task = new AsyncTask<String, Object, String>() {

			@Override
			protected String doInBackground(String... uri) {

				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response;
				String responseString = null;
				try {
					Log.d("requestHttp", "Trying to send http get");
					response = httpclient.execute(new HttpGet(uri[0]));
					Log.d("requestHttp", "sent a http get");
					StatusLine statusLine = response.getStatusLine();
					if(statusLine.getStatusCode() == HttpStatus.SC_OK){
						Log.d("requestHttp", "Got 200 OK");
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						response.getEntity().writeTo(out);
						out.close();
						responseString = out.toString();
					} else{
						//Closes the connection.
						Log.e("requestHttp", "Status code != 200 OK");
						response.getEntity().getContent().close();
						throw new IOException(statusLine.getReasonPhrase());
					}
				} catch (ClientProtocolException e) {
					//TODO Handle problems..
				} catch (IOException e) {
					//TODO Handle problems..
				}
				return responseString;
			}
			@Override
			protected void onPostExecute(String result){
				if (result != null){
					Log.d("searchContactResult", result);
					Toast.makeText(getActivity(), "Result from http-get: " + result, Toast.LENGTH_LONG).show();
					Card card = new Card(result);
					newCardCallback.callback(card);
					// TODO save the data some way
				}else{
					Log.e("searchContactResult", "Did not find this contact.");
					Toast contactToast= Toast.makeText(getActivity(), "Did not find this contact.", Toast.LENGTH_SHORT);
					contactToast.show();
				}
				
			}

		};
		task.execute(fileUrl);
	}
}
