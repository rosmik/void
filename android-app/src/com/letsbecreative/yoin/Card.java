package com.letsbecreative.yoin;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;
import org.json.JSONException;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Parcelable,Comparable<Card>{
	protected String firstName;
	protected String lastName;
	protected String id;
	protected Map<String, String> entries = new TreeMap<String,String>();

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

	public Card(Parcel inParcel){
		super();
		this.firstName = inParcel.readString();
		this.lastName = inParcel.readString();
		this.id = inParcel.readString();
		//this.entries = inParcel.readMap(entries);
		Bundle bundle = new Bundle();
		bundle = inParcel.readBundle();
		try{
		this.entries = (Map<String,String>)bundle.getSerializable("HashMap");
		}catch(ClassCastException e){
			e.printStackTrace();
			throw new RuntimeException();
		}
		
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
	
	public Card(Card oldCard){
		this.id = oldCard.id;
		this.firstName = oldCard.firstName;
		this.lastName = oldCard.lastName;
		
		Iterator<Map.Entry<String,String>> it = oldCard.entries.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, String> entry = it.next();
			this.entries.put(new String(entry.getKey()), new String(entry.getKey()));
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
	public String getIndexed(int pos){
		Iterator<Map.Entry<String,String>> it = entries.entrySet().iterator();
		for(int i = 0; i < pos; ++i){
			it.next();
		}
		Map.Entry<String,String> entry = it.next();
		return entry.getKey() + ": " + entry.getValue();
	}
	public int countEntries(){
		return this.entries.size();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeString(firstName);
		out.writeString(lastName);
		out.writeString(id);
		//out.writeMap(entries);
		out.writeMap(entries);
		Bundle bundle = new Bundle();
		bundle.putSerializable("HashMap", (Serializable) entries);
		out.writeBundle(bundle);
	}

	@Override
	public int compareTo(Card another) {
		int res = firstName.compareTo(another.firstName);
		if(res==0){
			res = lastName.compareTo(another.lastName);
		}
		if(res==0){
			res = id.compareTo(another.id);
		}
		return res;
	}
}
