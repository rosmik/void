package com.letsbecreative.yoin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONException;

public class Card {
	protected String firstName;
	protected String lastName;
	protected String id;
	protected Map<String, String> entries = new HashMap<String,String>();

	public Card(String firstName, String lastName) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Card(String firstName, String lastName, String id) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = id;
	}


	public Card(String jsonString) throws JSONException{
		super();

		Iterator<?> it;
		JSONObject json = new JSONObject(jsonString);
		
		this.firstName = json.getString("firstName");
		json.remove("firstName");
		this.lastName = json.getString("lastName");
		json.remove("lastName");
		this.id = json.getString("_id");
		json.remove("_id");
		it = json.keys();
		while(it.hasNext()){
			String key = (String) it.next();
			addEntry(key, json.getString(key));
		}
	}
	
	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		try{
			Iterator<Map.Entry<String, String>> it = entries.entrySet().iterator();
			json.put("firstName", firstName);
			json.put("lastName", lastName);
			json.put("_id", id);
			while(it.hasNext()){
				Map.Entry<String, String> entry = it.next();
				json.put(entry.getKey(), entry.getValue());
			}
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Iterator<Map.Entry<String, String>> getEntryIterator(){
		return entries.entrySet().iterator();
	}
	public void addEntry(String key, String value){
		entries.put(key, value);
	}
	public String getEntry(String key){
		return entries.get(key);
	}
}
