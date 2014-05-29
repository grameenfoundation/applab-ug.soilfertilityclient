package org.grameenfoundation.soilfertility.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockListFragment;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import org.grameenfoundation.soilfertility.R;
import org.grameenfoundation.soilfertility.dataaccess.DatabaseHelper;
import org.grameenfoundation.soilfertility.model.Calc;
import org.grameenfoundation.soilfertility.model.CalcAdapter;
import org.grameenfoundation.soilfertility.model.LocalCalculator;

import java.sql.SQLException;
import java.util.List;

/**
 * Copyright (c) 2013 AppLab, Grameen Foundation
 * Created by: David
 * <p/>
 * This class contains the UI elements for the "previous calculations" tab
 */
public class FragmentPreviousCalculations extends SherlockListFragment implements AdapterView.OnItemClickListener {

    private DatabaseHelper databaseHelper = null;
    private final String LOG_TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            List<Calc> items = getHelper().getCalculationsDataDao().queryBuilder().orderBy("dateCreated", false).query();
            CalcAdapter adapter = new CalcAdapter(getSherlockActivity(), R.layout.list_item_calculations, items);
            setListAdapter(adapter);
            //return super.onCreateView(inflater, container, savedInstanceState);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Database exception", e);
        }
        return inflater.inflate(R.layout.fragment_previous_calculations, container, false);
    }

    /**
     * gets the helper from the manager
     *
     * @return a databasehelper instance
     */
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getSherlockActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        try {
            Calc detail = (Calc) v.getTag();
            if (detail != null) {
                detail = getHelper().getCalculationsDataDao().queryForSameId(detail);
                if(detail != null && !detail.isSolved()){
                    //was saved without being solved. Solve
                    String url = getSherlockActivity().getSharedPreferences("preferences.xml",
                            Context.MODE_MULTI_PROCESS).getString("url", FragmentNewCalculation.DEFAULT_URL);
                    LocalCalculator calculator = new LocalCalculator(getSherlockActivity(), url);
                    calculator.execute(detail);
                }
                else {
                    Intent resultsIntent = new Intent(getSherlockActivity(), CalculationResults.class);
                    resultsIntent.putExtra("result", detail);
                    getSherlockActivity().startActivity(resultsIntent);
                }
            }
        } catch (SQLException e) {
            Toast.makeText(getSherlockActivity(), "error retrieving record", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Database exception", e);
        } catch (Exception e) {
            Toast.makeText(getSherlockActivity(), "error displaying record", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "ItemClick exception", e);
        }
    }
}
