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
import android.os.Parcel;
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
	//TextView addedAddress;
	public ImageView QRView;
	public TextView name_t;
	public TextView mail_t;
	public TextView phone_t;
	public TextView linkedin_t;

	public TextInput() 
	{       }
	//public String identityNumber = null;
	public String getAddress = "http://79.136.89.243/get/";
	
	public Card personalCard = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null){
			Log.d("Yoin","savedInstance is not empty");
			personalCard = savedInstanceState.getParcelable("card");
			//identityNumber = savedInstanceState.getString("id");
			Log.d("restoring personal data", personalCard.toString());
		}
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.user_input,
				container, false);

		final EditText firstName = (EditText) layout.findViewById(R.id.first_name);
		final EditText lastName = (EditText) layout.findViewById(R.id.last_name);
		final EditText mail = (EditText) layout.findViewById(R.id.mail);
		final EditText phone = (EditText) layout.findViewById(R.id.phone);
		final EditText linkedin = (EditText) layout.findViewById(R.id.linkedin);
		//addedAddress = (TextView) layout.findViewById(R.id.addedCardAddress);


		final Button saveButton = (Button) layout.findViewById(R.id.save_button);
		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//TODO Handle when you should be able to save (all req. fields filled)
				personalCard = new Card(firstName.getText().toString(), lastName.getText().toString());
				personalCard.addEntry("mail", mail.getText().toString());
				personalCard.addEntry("phone", phone.getText().toString());
				personalCard.addEntry("linkedin", linkedin.getText().toString());
				Log.d("JSONData", personalCard.toString());
				requestHttpPost("http://79.136.89.243/add", personalCard.toString());
			}
		});

		QRView = (ImageView) layout.findViewById(R.id.qr_view);
		name_t = (TextView) layout.findViewById(R.id.name_t);
		mail_t = (TextView) layout.findViewById(R.id.mail_t);
		phone_t = (TextView) layout.findViewById(R.id.phone_t);
		linkedin_t = (TextView) layout.findViewById(R.id.linkedin_t);
		
		if (personalCard != null){
			showPersonalCard();
		}

		return layout;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedUserState){
		super.onSaveInstanceState(savedUserState);
		savedUserState.putParcelable("card", personalCard);
		//savedUserState.putString("id", identityNumber);
		//savedUserState.putAll(getArguments());
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
					//TODO Handle problems..s
				}
				return responseString;
			}
			@Override
			protected void onPostExecute(String result){
				if (result != null){
					Log.d("searchContactResult", result);
					Toast.makeText(getActivity(), "Saved your data!: " + result, Toast.LENGTH_LONG).show();
					//addedAddress.setText("Get your card at: http://79.136.89.243/get/" + result);
					personalCard.id = result;
					showPersonalCard();
					QRView.setVisibility(View.VISIBLE);

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
		String qrInformation = getAddress + personalCard.id;
		Log.d("qrinformation", qrInformation);
		com.google.zxing.Writer QRwriter = new QRCodeWriter();// creates a writer object
		try{
			BitMatrix matrix = QRwriter.encode(qrInformation, BarcodeFormat.QR_CODE,size, size); // creates a matrix from the given string to specified format
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
	
	private void showPersonalCard(){
		generateQR();
		QRView.setVisibility(View.VISIBLE);
		name_t.setText(personalCard.firstName + " " + personalCard.lastName);
		name_t.setVisibility(View.VISIBLE);
		mail_t.setText(personalCard.getEntry("mail"));
		mail_t.setVisibility(View.VISIBLE);
		phone_t.setText(personalCard.getEntry("phone"));
		phone_t.setVisibility(View.VISIBLE);
		linkedin_t.setText(personalCard.getEntry("linkedin"));
		linkedin_t.setVisibility(View.VISIBLE);
	}
}