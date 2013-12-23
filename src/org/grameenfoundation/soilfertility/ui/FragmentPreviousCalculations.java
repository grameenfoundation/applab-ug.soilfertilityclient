package org.grameenfoundation.soilfertility.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import org.grameenfoundation.soilfertility.R;

/**
 * Copyright (c) 2013 AppLab, Grameen Foundation
 * Created by: David
 * <p/>
 * This class contains the UI elements for the "previous calculations" tab
 */
public class FragmentPreviousCalculations extends SherlockFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_previous_calculations, container, false);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

    @Override
    public void onPause() {
        //This should run when the fragment goes to backstack
        super.onPause();
    }

    @Override
    public void onResume() {
        //This should run when the fragment returns from backstack
        super.onResume();
    }
}
