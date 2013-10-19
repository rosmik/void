package com.letsbecreative.yoin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper{
	protected String databaseName;

	public DatabaseHandler(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		databaseName = name;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String databaseTable= "CREATE TABLE " + databaseName + "("+
				"jsonString "+" TEXT"+")";
		db.execSQL(databaseTable);
		// TODO Auto-generated method stub		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//delete table
		db.execSQL("DROP TABLE IF EXISTS " + databaseName);
		//how to add a new table?
	}
	 public void addContact(Card card) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("jsonString", card.toString());
		db.insert(databaseName,null,values);		
		db.close();
	}


	public void getContacts(Vector<Card> cardVector) {
		try {
			if(cardVector == null){
				cardVector = new Vector<Card>();
			}
			cardVector.clear();

			// Select All Query
			String selectQuery = "SELECT * FROM " + databaseName;

			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Card card=new Card(cursor.getString(0));
					cardVector.add(card);
				} 
				while (cursor.moveToNext());
			}
			// return contact list
			cursor.close();
			db.close();
			return ;
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("all_contact", "" + e);
		}

		return ;
	}
}