package com.letsbecreative.yoin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.os.Bundle;
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
        @Override
        public boolean onContextItemSelected(MenuItem item) {
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo)item.getMenuInfo();

            switch (item.getItemId()) {
            case 0:
            	// TODO Export contact to phone
            	Log.d("User", "0 pressed");
            	return true;
            case 1:
            	// TODO Remove contact from cardset 
            	return true;
            default:
                return super.onContextItemSelected(item);
            }
            
        }
    }