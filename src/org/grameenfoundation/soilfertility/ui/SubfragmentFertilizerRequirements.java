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
 *
 * This class supports UI interaction when entering variables for calculation
 */
public class SubfragmentFertilizerRequirements extends SherlockFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subfragment_fertilizer_requirements, container, false);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }
}
