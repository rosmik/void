package com.letsbecreative.yoin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;



public class User extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
		
    	ExpandableListAdapter listAdapter = null;
        ExpandableListView expListView = null;
        List<String> listDataHeader = null;
        HashMap<String, List<String>> listDataChild = null;
        public static final String ARG_SECTION_NUMBER = "section_number";
        private MainActivity parent;
        
        public User()
        {
        	super();
        }
	        

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	parent = ((MainActivity) getActivity());
            View rootView = inflater.inflate(R.layout.contacts, container, false);
            expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
            this.parent = (MainActivity) getActivity();            
            if(this.parent == null) throw new RuntimeException();
            if(listAdapter == null){
            	listAdapter = new ExpandableListAdapter(rootView.getContext(), parent.cardVector);
            }
            
   
            // setting list adapter
            expListView.setAdapter(listAdapter);
            
            registerForContextMenu(expListView);
       
                        
            return rootView;
        }
       
        
        public void addCard(Card card){
        	String fullName = card.getFirstName() + " " + card.getLastName();
        	ArrayList<String> l = new ArrayList<String>();
        	Iterator<Map.Entry<String,String>> it = card.getEntryIterator();
        	while(it.hasNext()){
        		Map.Entry<String, String> entry = it.next();
        		l.add(entry.getKey() + ": " + entry.getValue());
        	}
        	listDataHeader.add(0, fullName);
        	listDataChild.put(fullName, l);
        	expListView.expandGroup(0);
        }
    
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        	
          if (v.getId()==R.id.lvExp) {
            ExpandableListView.ExpandableListContextMenuInfo info =
                    (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
            menu.setHeaderTitle(listAdapter.getGroup((int) info.id).toString());
            
            String[] menuItems = getResources().getStringArray(R.array.context_menu);
            for (int i = 0; i<menuItems.length; i++) {
              menu.add(Menu.NONE, i, i, menuItems[i]);
            }
          }
        }
        
        public void saveInput(){
        	
        }
        
       public void addContact(Card c){
        	ArrayList<ContentProviderOperation> op_list = new ArrayList<ContentProviderOperation>(); 
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI) 
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null) 
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null) 
                //.withValue(RawContacts.AGGREGATION_MODE, RawContacts.AGGREGATION_MODE_DEFAULT) 
                .build()); 

         // first and last names 
              op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI) 
          .withValueBackReference(Data.RAW_CONTACT_ID, 0) 
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE) 
                .withValue(StructuredName.GIVEN_NAME, c.firstName) 
                .withValue(StructuredName.FAMILY_NAME, c.lastName) 
                .build()); 

//              op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI) 
//                      .withValueBackReference(Data.RAW_CONTACT_ID, 0) 
//                      .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                      .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, c.getEntry("phone"))
//                      .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, Phone.TYPE_CUSTOM)
//                      .build());
//              op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI) 
//                      .withValueBackReference(Data.RAW_CONTACT_ID, 0)
//
//              .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
//              .withValue(ContactsContract.CommonDataKinds.Email.DATA, c.getEntry("email"))
//              .withValue(ContactsContract.CommonDataKinds.Email.TYPE, Email.TYPE_CUSTOM)
//              .build());

         try{ 
          ContentProviderResult[] results = getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, op_list); 
         }catch(Exception e){ 
          e.printStackTrace(); 
         } 
        }
        
        @Override
        public boolean onContextItemSelected(MenuItem item) {
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo)item.getMenuInfo();

            switch (item.getItemId()) {
            case 0:
            	// TODO Export contact to phone
            	addContact((Card)listAdapter.getCard((int)info.id));
            	return true;
            case 1:
            	// TODO Remove contact from cardset 
            	return true;
            default:
                return super.onContextItemSelected(item);
            }
            
        }
    }