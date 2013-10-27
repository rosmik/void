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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
public class PersonalTab extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	//TextView addedAddress;
	public ImageView QRView;
	public TextView firstName_t;
	public TextView lastName_t;
	public TextView name_t;
	public TextView mail_t;
	public TextView phone_t;
	public TextView linkedin_t;

	public PersonalTab() 
	{       }
	CardListener personalCardListener;
	
	//OnPersonalCardListener mPersonalCard
	
	 // Container Activity must implement this interface
    public interface CardListener {
        public Card getPersonalCard();
        public void setPersonalCard(Card personalCard);
        public void requestHttpPost(String jsonString);
        public Bitmap getQRCode();
    }
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            personalCardListener = (CardListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CardListener");
        }
    }
    
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null){
			Log.d("Yoin","savedInstance is not empty");
			personalCardListener.setPersonalCard((Card)savedInstanceState.getParcelable("card"));
			//identityNumber = savedInstanceState.getString("id");
			Log.d("restoring personal data", personalCardListener.getPersonalCard().toString());
		}
		
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.user_input,
				container, false);

		//addedAddress = (TextView) layout.findViewById(R.id.addedCardAddress);

		final Button saveButton = (Button) layout.findViewById(R.id.update_user_button);
		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Dialog updateDialog = createUpdateDialog();
				updateDialog.show();

			}
		});

		QRView = (ImageView) layout.findViewById(R.id.qr_view);
		name_t = (TextView) layout.findViewById(R.id.name_t);
		mail_t = (TextView) layout.findViewById(R.id.mail_t);
		phone_t = (TextView) layout.findViewById(R.id.phone_t);
		linkedin_t = (TextView) layout.findViewById(R.id.linkedin_t);
		
		if (personalCardListener.getPersonalCard() != null){
			showPersonalCard();
		}

		return layout;
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedUserState){
		super.onSaveInstanceState(savedUserState);
		savedUserState.putParcelable("card", ((MainActivity)getActivity()).personalCard);
		//savedUserState.putString("id", identityNumber);
		//savedUserState.putAll(getArguments());
	}
	

	
	
	
	private void generateQR(){
		
		int size = 200;// Cubic size of QR code
		Bitmap bitmapQR = null; // Initialize bitmap, only one is necessary for each user, can be updated
		String qrInformation = "http://79.136.89.243/get/" + personalCardListener.getPersonalCard().id;
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
	
	public Dialog createUpdateDialog(){
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.update_personal_data, null);
		
		AlertDialog.Builder updateDialogBuilder = new AlertDialog.Builder(getActivity());
		
		updateDialogBuilder
		.setView(view);
		final EditText firstName = (EditText) view.findViewById(R.id.first_name);
		final EditText lastName = (EditText) view.findViewById(R.id.last_name);
		final EditText mail = (EditText) view.findViewById(R.id.mail);
		final EditText phone = (EditText) view.findViewById(R.id.phone);
		final EditText linkedin = (EditText) view.findViewById(R.id.linkedin);
		if (personalCardListener.getPersonalCard() != null){
			firstName.setText(personalCardListener.getPersonalCard().firstName);
			lastName.setText(personalCardListener.getPersonalCard().lastName);
			mail.setText(personalCardListener.getPersonalCard().getEntry("mail"));
			phone.setText(personalCardListener.getPersonalCard().getEntry("phone"));
			linkedin.setText(personalCardListener.getPersonalCard().getEntry("linkedin"));
	
		};
		
		updateDialogBuilder
		.setNegativeButton("Cancel", 
				new DialogInterface.OnClickListener() {
								
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						
					}
				})
		.setPositiveButton("Update", 
				new DialogInterface.OnClickListener() {
								
					@Override
					public void onClick(DialogInterface dialog, int which) {
					Card pCard = new Card(firstName.getText().toString(), lastName.getText().toString());
						pCard.addEntry("mail", mail.getText().toString());
						pCard.addEntry("phone", phone.getText().toString());
						pCard.addEntry("linkedin", linkedin.getText().toString());
						personalCardListener.setPersonalCard(pCard);
						personalCardListener.requestHttpPost(pCard.toString());
						//Log.d("JSONData", personalCardListener.getPersonalCard().toString());

						dialog.dismiss();
						
					}
				});
		return updateDialogBuilder.create();
	}
	
	public void showPersonalCard(){
		generateQR();
		QRView.setVisibility(View.VISIBLE);
		name_t.setText(personalCardListener.getPersonalCard().firstName + " " + personalCardListener.getPersonalCard().lastName);
		name_t.setVisibility(View.VISIBLE);
		mail_t.setText(personalCardListener.getPersonalCard().getEntry("mail"));
		mail_t.setVisibility(View.VISIBLE);
		phone_t.setText(personalCardListener.getPersonalCard().getEntry("phone"));
		phone_t.setVisibility(View.VISIBLE);
		linkedin_t.setText(personalCardListener.getPersonalCard().getEntry("linkedin"));
		linkedin_t.setVisibility(View.VISIBLE);
		QRView.setVisibility(View.VISIBLE);
	}
}