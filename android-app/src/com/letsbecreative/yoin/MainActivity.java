package com.letsbecreative.yoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.Collections;

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

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ComponentName;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;

public class MainActivity extends FragmentActivity implements
ActionBar.TabListener, PersonalTab.CardListener, HttpRequest.RequestListener, QRFragment.QRListener {

	// Constants used for fragment numbering
	static final int SCANNER = 0;
	static final int YOU = 1;
	static final int CONTACTS = 2;

	// Variables used in personaltab
	public String getAddress = "http://79.136.89.243/get/";
	public String postAddress = "http://79.136.89.243/add";
	public Card personalCard = null;
	
	//Variables used in QR-fragment
	IQRCallback newCardCallback;
	
	public interface IQRCallback{
		public void callback(Card card);
	}


	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	/**
	 *  The tabfragments
	 */
	PersonalTab personalTab = new PersonalTab();
	User cardListTab = new User();
	QRFragment qrReaderTab = new QRFragment();

	DatabaseHandler databaseHandler;
	Vector<Card> cardVector = new Vector<Card>();

	private void qrCallback(final Card card){
		this.runOnUiThread(new Runnable(){
			public void run(){
				Toast.makeText(getApplicationContext(), "Callback called with card: " + card.toString(), Toast.LENGTH_LONG).show();
				addCard(card);
				mViewPager.setCurrentItem(CONTACTS);
			}
		});
	}

	public void addCard(Card card){
		this.cardVector.add(card);
		Collections.sort((List<Card>)cardVector);
		cardListTab.listAdapter.notifyDataSetChanged();
		this.databaseHandler.addContact(card);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setNewCardCallback(new IQRCallback(){
			public void callback(Card card){
				qrCallback(card	);
			}
		});

		databaseHandler = new DatabaseHandler(getBaseContext(), "yoinDatabase", null , 2);
		databaseHandler.getContacts(cardVector);

		if (personalCard == null){
			setPersonalCard(databaseHandler.getPersonalCard());
		}

		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD); // NAVIGATION_MODE_STANDARD hides the tabs
		actionBar.setDisplayShowTitleEnabled(false);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				updateNavColor(position); // Update color of navigator items when swiping to another fragment
			}
		});


		// Set the default fragment to show. Showing the middle fragmet 
		mViewPager.setCurrentItem(YOU);

		// Set up the navigation buttons in the bottom
		// Scanner-button
		final Button navScanButton = (Button) findViewById(R.id.nav_scanner);
		navScanButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Change to Scanner fragment
				mViewPager.setCurrentItem(SCANNER,true);
				updateNavColor(SCANNER);
			}
		});

		// You-button
		final Button navYouButton = (Button) findViewById(R.id.nav_you);
		navYouButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Change to the "You" fragment
				mViewPager.setCurrentItem(YOU,true);
				updateNavColor(YOU);
			}
		});

		// Contacts-button
		final Button navContactsButton = (Button) findViewById(R.id.nav_contacts);
		navContactsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Change to Contacts fragment
				mViewPager.setCurrentItem(CONTACTS,true);
				updateNavColor(CONTACTS);
			}
		});



	}

	protected void updateNavColor(int i) {
		Button navScanButton = (Button) findViewById(R.id.nav_scanner);
		Button navYouButton = (Button) findViewById(R.id.nav_you);
		Button navContactsButton = (Button) findViewById(R.id.nav_contacts);

		switch(i){
		case SCANNER:
			navScanButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_nav_on,0,0);
			navYouButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_nav_off,0,0);
			navContactsButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_nav_off,0,0);
			navScanButton.setTextColor(Color.BLUE);
			navYouButton.setTextColor(Color.WHITE);
			navContactsButton.setTextColor(Color.WHITE);
			break;
		case YOU:
			navScanButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_nav_off,0,0);
			navYouButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_nav_on,0,0);
			navContactsButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_nav_off,0,0);
			navScanButton.setTextColor(Color.WHITE);
			navYouButton.setTextColor(Color.BLUE);
			navContactsButton.setTextColor(Color.WHITE);
			break;
		case CONTACTS:
			navScanButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_nav_off,0,0);
			navYouButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_nav_off,0,0);
			navContactsButton.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_nav_on,0,0);
			navScanButton.setTextColor(Color.WHITE);
			navYouButton.setTextColor(Color.WHITE);
			navContactsButton.setTextColor(Color.BLUE);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("onCreateOptionsMenu", "in optionsmenu");

		// Inflate the options menu from XML
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search, menu);

		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		if(searchManager == null) {
			throw(new RuntimeException());
		}
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		if(searchView == null) {
			throw(new RuntimeException());
		}
		// Assumes current activity is the searchable activity
		searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName("com.letsbecreative.yoin", "com.letsbecreative.yoin.SearchActivity")));
		searchView.setIconifiedByDefault(true); 

		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return the different fragmenttabs
			switch (position) {
			case SCANNER:
				return qrReaderTab;
			case YOU:
				return personalTab;
			case CONTACTS:
				return cardListTab;
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case SCANNER:
				return getString(R.string.title_section1).toUpperCase(l);
			case YOU:
				return getString(R.string.title_section2).toUpperCase(l);
			case CONTACTS:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	
	//Functions used in PersonalTab
	@Override
	public void requestHttpPost(String jsonString) {

		String[] dataArray =  new String[2]; 
		dataArray[0] = postAddress;
		dataArray[1] = jsonString;
		new HttpRequest("POST",this).execute(dataArray);
	}

	private void showPersonalCard(){
		this.runOnUiThread(new Runnable(){
			public void run(){		
				personalTab.showPersonalCard();
			}
		});
	}
	
	@Override
	public Card getPersonalCard() {
		return personalCard;
	}
	
	@Override
	public void setPersonalCard(Card personalCard) {
		this.personalCard = personalCard;
	}

	@Override
	public Bitmap getQRCode() {

		int size = 200;// Cubic size of QR code
		Bitmap bitmapQR = null; // Initialize bitmap, only one is necessary for each user, can be updated
		String qrInformation = getAddress + personalCard.id;
		Log.d("MainActivity", qrInformation);
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
		return bitmapQR;
	}

	
	
	//functions used in HttpRequest Class
//	@Override
//	public Context inheritGetBaseContext() {
//		return getBaseContext();
//	}

	@Override
	public boolean checkNewCardCallback() {
		if (newCardCallback != null){
			return true;
		}
		return false;
	}
	
	@Override
	public void callback(Card card) {
		newCardCallback.callback(card);
	}
	
	@Override
	public void addPersonalCard(Card resultCard) {
		databaseHandler.addPersonalCard(resultCard);
		personalCard = resultCard;
		showPersonalCard();
		Toast.makeText(getApplicationContext(), "Saved your data " + personalCard.firstName + "!", Toast.LENGTH_LONG).show();
	}

	@Override
	public void alertSaveFailed() {
		Toast.makeText(getApplicationContext(),"Unable to save your data. Check your internet connection!", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void alertContactFailed() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(),"Unable to find data. Check your internet connection!", Toast.LENGTH_SHORT).show();
		
	}
	
	//Functions used in QR-fragment
	public void setNewCardCallback(IQRCallback callback){
		newCardCallback = callback;
	}
	
	@Override
	public void requestHttpGet(String result){
		String[] dataArray =  new String[1]; 
		dataArray[0] = result;
		new HttpRequest("GET",this).execute(dataArray);
	}
	
}
