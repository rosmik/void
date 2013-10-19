package com.letsbecreative.yoin;

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
    	ExpandableListAdapter listAdapter = null;
        ExpandableListView expListView = null;
        public static final String ARG_SECTION_NUMBER = "section_number";
        private MainActivity parent;
        
        public User()
        {
        	super();
        }
	        

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	
            View rootView = inflater.inflate(R.layout.contacts, container, false);
            expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
            
            this.parent = (MainActivity) getActivity();            
            if(this.parent == null) throw new RuntimeException();
            if(listAdapter == null){
            	listAdapter = new ExpandableListAdapter(rootView.getContext(), parent.cardVector);
            }
            
   
            // setting list adapter
            expListView.setAdapter(listAdapter);
            return rootView;
        }        
    }