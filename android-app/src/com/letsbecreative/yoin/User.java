package com.letsbecreative.yoin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.letsbecreative.yoin.ExpandableListAdapter;



public class User extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
    	ExpandableListAdapter listAdapter;
        ExpandableListView expListView;
        List<String> listDataHeader;
        HashMap<String, List<String>> listDataChild;
        public static final String ARG_SECTION_NUMBER = "section_number";
        public User()
        {
        	
        }
	        

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.contacts, container, false);
   /*       	String myStringArray[]={"Henrik","Micke","Malte","Limpan","Plogen","Nisse"};
            ListView choiceListView = (ListView)rootView.findViewById(R.id.choice_list);
	      	ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(),android.R.layout.simple_dropdown_item_1line, myStringArray);
	      	choiceListView.setAdapter(adapter);
    */
            expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
            
            prepareListData();
            listAdapter = new ExpandableListAdapter(rootView.getContext(), listDataHeader, listDataChild);
            
   
            // setting list adapter
            expListView.setAdapter(listAdapter);
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
    
        
    }