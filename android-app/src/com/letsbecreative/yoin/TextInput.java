package com.letsbecreative.yoin;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
public class TextInput extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	TextView addedAddress;

	public TextInput() 
	{       }
	public String identityNumber;
	public ImageView QRView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//View rootView = inflater.inflate(R.layout.user_input, container, false);
		//return rootView;
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.user_input,
				container, false);

		final EditText firstName = (EditText) layout.findViewById(R.id.first_name);
		final EditText lastName = (EditText) layout.findViewById(R.id.last_name);
		final EditText mail = (EditText) layout.findViewById(R.id.mail);
		final EditText phone = (EditText) layout.findViewById(R.id.phone);
		final EditText linkedin = (EditText) layout.findViewById(R.id.linkedin);
		addedAddress = (TextView) layout.findViewById(R.id.addedCardAddress);


		final Button saveButton = (Button) layout.findViewById(R.id.save_button);
		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//TODO Handle when you should be able to save (all req. fields filled)
				Card cardToSend = new Card(firstName.getText().toString(), lastName.getText().toString());
				cardToSend.addEntry("mail", mail.getText().toString());
				cardToSend.addEntry("phone", phone.getText().toString());
				cardToSend.addEntry("linkedin", linkedin.getText().toString());
				Log.d("JSONData", cardToSend.toString());
				requestHttpPost("http://79.136.89.243/add",cardToSend.toString());
			}
		});

		QRView = (ImageView) layout.findViewById(R.id.qr_view);

		return layout;
	}

	public void requestHttpPost(String fileUrl, String jsonString){

		AsyncTask<String, Object, String> task = new AsyncTask<String, Object, String>() {

			@Override
			protected String doInBackground(String... uri) {
				HttpResponse response;
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(uri[0]);
				String responseString = null;
				//httpPost.setHeader("Accept", "application/json");
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
				} catch (IOException e) {
					//TODO Handle problems..
				}
				return responseString;
			}
			@Override
			protected void onPostExecute(String result){
				if (result != null){
					Log.d("searchContactResult", result);
					Toast.makeText(getActivity(), "Saved your data!: " + result, Toast.LENGTH_LONG).show();
					addedAddress.setText("Get your card at: http://http://79.136.89.243/get/" + result);
					identityNumber = result;
					generateQR();

				}else{
					Log.e("searchContactResult", "Failed saving user-data" );
					Toast contactToast= Toast.makeText(getActivity(), "Failed to save your data", Toast.LENGTH_SHORT);
					contactToast.show();
				}
			}

		};
		String[] dataArray;
		dataArray = new String[2];
		dataArray[0] = fileUrl;
		dataArray[1] = jsonString;
		task.execute(dataArray);
	}
	private void generateQR(){


		int size = 200;// Cubic size of QR code
		Bitmap bitmapQR = null; // Initialize bitmap, only one is necessary for each user, can be updated
		//		ImageView QRView = (ImageView) layout.findViewById(R.id.qr_view);
		//		final ImageView QRView;
		com.google.zxing.Writer QRwriter = new QRCodeWriter();// creates a writer object
		try{
			BitMatrix matrix = QRwriter.encode(identityNumber, BarcodeFormat.QR_CODE,size, size); // creates a matrix from the given string to specified format
			bitmapQR = Bitmap.createBitmap(size, size, Config.ARGB_4444); // Setup for bitmap, each pixel is stored in one byte.hic
			for (int i = 0; i < size; i++){
				for (int j = 0; j < size; j++){
					bitmapQR.setPixel(i, j, matrix.get(i, j) ? Color.BLACK: Color.WHITE);// loops through bitmap and matrix to create the bitmap graphics
				}
			}
		}
		catch (WriterException generateFail) {
			generateFail.printStackTrace();
		}
		if (bitmapQR != null){
			
			QRView.setImageBitmap(bitmapQR);

		}
	}
}