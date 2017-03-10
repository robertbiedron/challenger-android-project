package com.alobha.challenger.ui.start;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alobha.challenger.R;

/**
 * Created by mrNRG on 13.06.2016.
 */
public class TutorialFragment extends Fragment {
    private static final String RESOURCE_ID = "resource_id";
    private int mResource;

    public static Fragment newInstance(int mResource){
        Fragment f = new TutorialFragment();
        Bundle args = new Bundle(1);
        args.putInt(RESOURCE_ID, mResource);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResource = getArguments().getInt(RESOURCE_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView view = (ImageView) inflater.inflate(R.layout.fragment_tutorial, container, false);
        view.setImageResource(mResource);
        return view;
    }
}