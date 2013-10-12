package com.letsbecreative.yoin;

import org.json.JSONObject;
import org.json.JSONException;

import android.util.Log;

public class Card {

	public Card(String firstName, String lastName, String phone, String id) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.id = id;
	}

	public Card(String firstName, String lastName, String phone) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
	}

	public Card(String jsonString) {
		super();
		
		try{
			JSONObject json = new JSONObject(jsonString);
			this.firstName = json.optString("firstName");
			this.lastName = json.optString("lastName");
			this.phone = json.optString("phone");
			this.id = json.optString("_id");
		}catch (JSONException e){
			Log.e("Card", "Construct from json string failed while parsing string");
			return;
		}
	}
	
	protected String firstName;
	protected String lastName;
	protected String phone;
	protected String id;
	
	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		try{
			json.put("firstName", firstName);
			json.put("lastName", lastName);
			json.put("phone", phone);
			return json.toString();
		}catch(JSONException e){
			return "JSON creation failed.";
		}
		
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
