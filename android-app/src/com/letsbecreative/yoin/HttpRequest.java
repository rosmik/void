package com.letsbecreative.yoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

class HttpRequest extends AsyncTask<String, Object, String>{


	private String requestType = null;
	public RequestListener requestListener;
	//DatabaseHandler databaseHandler;

	HttpRequest(String type, Activity activity){
		super();
		requestType = type;
		requestListener = (RequestListener) activity;
		//databaseHandler = new DatabaseHandler(requestListener.inheritGetBaseContext(),"yoinDatabase", null , 2);

	}

	public interface RequestListener {
		public Card getPersonalCard();
		public void setPersonalCard(Card resultCard);
		//public Context inheritGetBaseContext();
		public void addPersonalCard(Card personalCard);
		public void alertSaveFailed();
		public void alertContactFailed();
		public boolean checkNewCardCallback();
		//public void requestHttpPost(String jsonString);
		//public Bitmap getQRCode();
		public void callback(Card card);
	}

	@Override
	protected String doInBackground(String... uri) {
		HttpResponse response;
		HttpClient httpclient = new DefaultHttpClient();
		String responseString = null;

		if (requestType == "POST"){
			HttpPost httpPost = new HttpPost(uri[0]);			
			httpPost.setHeader("Content-type", "application/json;charset=utf-8");
			try {
				httpPost.setEntity(new StringEntity(uri[1], HTTP.UTF_8));
				Log.d("json",uri[1]);
				Log.d("Header",httpPost.getFirstHeader("Content-type").toString());
				Log.d("requestHttp", "Trying to send http Post");
				response = httpclient.execute(httpPost);
				Log.d("requestHttp", "sent a http Post");
				StatusLine statusLine = response.getStatusLine();
				if(statusLine.getStatusCode() == HttpStatus.SC_OK){
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					responseString = out.toString();
				} else{
					//Closes the connection.
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
				//TODO Handle problems..
				e.printStackTrace();
			} catch (IOException e) {
				//TODO Handle problems..
				e.printStackTrace();
			}
		}else if (requestType == "GET"){
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
				e.printStackTrace();
			} catch (IOException e) {
				//TODO Handle problems..
				e.printStackTrace();
			}
		}
		else {
			Log.d("Yoin","Not a valid requestType, cant execute command");
		}
		return responseString;
	}

	@Override	
	protected void onPostExecute(String result){
		if (requestType == "GET"){
			if (result != null){
                Card card;
                Log.d("Yoin","searchContactResult: " + result);
                try{
                        card = new Card(result);
                }catch (JSONException e){
                        Log.e("Card", "Construct from json string failed while parsing string");
                        requestListener.alertContactFailed();
                        return;
                }
                if(requestListener.checkNewCardCallback()){
                        requestListener.callback(card);
                }
                else {
                        Log.e("QRFragment", "No callback set for returning Card.");
                        throw new RuntimeException();
                }
        }else{
                Log.e("searchContactResult", "Did not find this contact.");
                requestListener.alertContactFailed();
        }
			
			Log.e("Yoin","result from httpget: " + result);
		} else if(requestType == "POST"){
			if (result != null){
				Log.d("Yoin", "searchContactResult: "+result);
				//addedAddress.setText("Get your card at: http://79.136.89.243/get/" + result);
				Card personalCard = requestListener.getPersonalCard();
				personalCard.id = result;
				requestListener.addPersonalCard(personalCard);
			}else{
				Log.e("Yoin","searchContactResult: Failed saving user-data" );
				requestListener.alertSaveFailed();
			}
		}
	}
}
