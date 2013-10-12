package com.letsbecreative.yoin;

import org.json.JSONObject;
import org.json.JSONException;

public class Card {

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
