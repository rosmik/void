package com.letsbecreative.yoin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
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
        MainActivity parent = null;
        public static final String ARG_SECTION_NUMBER = "section_number";
        public User()
        {
        	
        }
	        

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	parent = ((MainActivity) getActivity());
            View rootView = inflater.inflate(R.layout.contacts, container, false);
            expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
            if(listAdapter == null){
            	prepareListData();
            	listAdapter = new ExpandableListAdapter(rootView.getContext(), listDataHeader, listDataChild);
            }
            
   
            // setting list adapter
            expListView.setAdapter(listAdapter);
            
            registerForContextMenu(expListView);
       
                        
            return rootView;
        }
        private void prepareListData() {
            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<String, List<String>>();
     
            // Adding child data
            listDataHeader.add("Henrik");
            listDataHeader.add("Malte");
            listDataHeader.add("Nisse");
     
            // Adding child data
            List<String> top250 = new ArrayList<String>();
            top250.add("hnkryden@gmail.com");
            top250.add("0708132759");
            top250.add("Linköping");
     
            List<String> nowShowing = new ArrayList<String>();
            nowShowing.add("mortiz@gmail.com");
            nowShowing.add("0708821123312");
            nowShowing.add("Linköping");
     
            List<String> comingSoon = new ArrayList<String>();
            comingSoon.add("sfdsdfdsf@sdffds");
            comingSoon.add("708486045645546");
            comingSoon.add("Linköping");
     
            listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
            listDataChild.put(listDataHeader.get(1), nowShowing);
            listDataChild.put(listDataHeader.get(2), comingSoon);
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
            menu.setHeaderTitle(listDataHeader.get((int) info.id));
            Log.d("User",String.valueOf(info.id));
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