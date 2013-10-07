package com.letsbecreative.yoin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentTab1 extends Fragment {
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
        final TextView text = (TextView) rootView.findViewById(R.id.section_label);
        text.setText("Hello Motherflucker");
        return rootView;
	}
}
