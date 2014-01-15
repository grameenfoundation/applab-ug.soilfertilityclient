package org.grameenfoundation.soilfertility.ui;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragment;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import org.grameenfoundation.soilfertility.R;
import org.grameenfoundation.soilfertility.dataaccess.DatabaseHelper;
import org.grameenfoundation.soilfertility.model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2013 AppLab, Grameen Foundation
 * Created by: David
 * <p/>
 * This class contains the UI elements for the "new calculations" tab
 */
public class FragmentNewCalculation extends SherlockFragment {

    private LocalCalculator calculator;
    private FragmentTabHost mTabHost;
    private Button btnAddNewCrop;
    private Button btnRemoveLastRow;
    private Button btnAddFertilizer;
    private Button btnRemoveFertilizer;
    private Button btnCalculate;
    private EditText txt_farmer_name;
    private TableLayout table_crops;
    private TableLayout table_fertilizers;
    private Spinner lstbox_crops;
    private Spinner lstbox_fertilzers;
    private List<Crop> lst_crops;
    private List<Fertilizer> lst_fertilizers;
    private List lst_new_croplistbox_ids;
    private List lst_new_crop_landsize_ids;
    private List lst_new_crop_profit_ids;
    private List lst_new_fertilizer_listbox_ids;
    private List lst_new_fertilizer_price_ids;
    private int id_count_crops = 1;
    private int id_count_fertilizers = 1;
    private int id_count_crop_rows = 2000;
    private int id_count_fertilizers_rows = 3000;
    private static final Double HACTARE = 0.404686;
    private static final Double ACRE = 2.47105;
    private DatabaseHelper databaseHelper = null;
    private final String LOG_TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_new_calculation, container, false);
        setRetainInstance(true);
        lstbox_crops = (Spinner) view.findViewById(R.id.cmb_crops);
        lstbox_fertilzers = (Spinner) view.findViewById(R.id.cmb_fertilzers);
        btnAddNewCrop = (Button) view.findViewById(R.id.btn_add_row);
        btnRemoveLastRow = (Button) view.findViewById(R.id.btn_remove_row);
        btnAddFertilizer = (Button) view.findViewById(R.id.btn_add_fertilizer);
        btnRemoveFertilizer = (Button) view.findViewById(R.id.btn_remove_fertilizer);
        btnCalculate = (Button) view.findViewById(R.id.btn_calculate);
        table_crops = (TableLayout) view.findViewById(R.id.table_crops);
        table_fertilizers = (TableLayout) view.findViewById(R.id.table_fertilizers);
        txt_farmer_name = (EditText) view.findViewById(R.id.txt_name);
        lst_new_croplistbox_ids = new ArrayList();
        lst_new_crop_landsize_ids = new ArrayList();
        lst_new_crop_profit_ids = new ArrayList();
        lst_new_fertilizer_listbox_ids = new ArrayList();
        lst_new_fertilizer_price_ids = new ArrayList();

        try {
            //we will get these items from a local database
            lst_crops = getHelper().getCropDataDao().queryForAll();
            ArrayAdapter adapter = new ArrayAdapter(getSherlockActivity(), android.R.layout.simple_spinner_dropdown_item, lst_crops);
            lstbox_crops.setAdapter(adapter);

            lst_fertilizers = getHelper().getFertilizerDataDao().queryForAll();
            ArrayAdapter adapterFertilizers = new ArrayAdapter(getSherlockActivity(), android.R.layout.simple_spinner_dropdown_item, lst_fertilizers);
            lstbox_fertilzers.setAdapter(adapterFertilizers);
        } catch (SQLException e) {
            Toast.makeText(getSherlockActivity(), "failed to read data", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Database exception", e);
        }
        btnAddNewCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddNewCropClicked();
            }
        });
        btnRemoveLastRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRemoveLastRowClicked();
            }
        });
        btnAddFertilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddFertilizerClicked();
            }
        });
        btnRemoveFertilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRemoveFertilizerClicked();
            }
        });
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCalculateClicked(view);
            }
        });

        return view;
    }


    /**
     * onclick listener implementation for beginning the calculation
     * picks all information provided from controls and passes it to
     * an instance of the calculator for processing
     */
    private void btnCalculateClicked(View view) {
        try {
            validate();
            String url = getSherlockActivity().getSharedPreferences("preferences.xml", Context.MODE_MULTI_PROCESS).getString("url","http://74.208.213.214:8888/Service1.asmx/Optimize");
            calculator = new LocalCalculator(view.getContext(), url);
            Calc details = new Calc();
            List<CalcCrop> calcCrops = new ArrayList<CalcCrop>();
            List<CalcFertilizer> calcFertilizers = new ArrayList<CalcFertilizer>();
            //fill in details from UI
            //imei and farmer name
            TelephonyManager telephonyManager = (TelephonyManager) getSherlockActivity()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            details.setImei(telephonyManager.getDeviceId());
            details.setFarmerName(txt_farmer_name.getText().toString());

            /*
            get dynamically added controls and add other calcCrops
            here
             */
            for (int x = 1; x < table_crops.getChildCount(); x++) {
                TableRow row = (TableRow) table_crops.getChildAt(x);
                Spinner lstbox_of_crop = (Spinner) row.getChildAt(0);
                CheckedTextView selectedCrop = (CheckedTextView) lstbox_of_crop.getChildAt(0);
                EditText txt_of_land = (EditText) row.getChildAt(1);
                EditText txt_of_profit = (EditText) row.getChildAt(2);
                if (txt_of_land.getText().toString().matches("")) {
                    throw new ValidationException("land size is empty!");
                }
                if (txt_of_profit.getText().toString().matches("")) {
                    throw new ValidationException("Profit/Kg is empty!");
                }
                //populate next crop
                CalcCrop next_Calc_crop = new CalcCrop(details);
                next_Calc_crop.setCrop(getHelper().getCropDataDao().queryForSameId(new Crop(selectedCrop.getText().toString())));
                next_Calc_crop.setArea(changeAcresToHectares(Double.parseDouble(txt_of_land.getText().toString())));
                next_Calc_crop.setProfit(Integer.parseInt(txt_of_profit.getText().toString()));
                calcCrops.add(next_Calc_crop);
            }
            details.setCalcCrops(calcCrops);
             /*
            get dynamically added controls and add other calcFertilizers
            here
             */
            for (int x = 1; x < table_fertilizers.getChildCount(); x++) {
                TableRow row = (TableRow) table_fertilizers.getChildAt(x);
                Spinner lstbox_next_fertilizer = (Spinner) row.getChildAt(0);
                CheckedTextView selectedFertilizer = (CheckedTextView) lstbox_next_fertilizer.getChildAt(0);
                EditText txt_next_price_per_bag = (EditText) row.getChildAt(1);
                if (txt_next_price_per_bag.getText().toString().matches("")) {
                    throw new ValidationException("Price/50kg bag is empty!");
                }
                //populate next fertilizer
                CalcFertilizer next_Calc_fertilizer = new CalcFertilizer(details);
                next_Calc_fertilizer.setFertilizer(getHelper().getFertilizerDataDao().queryForSameId(new Fertilizer(selectedFertilizer.getText().toString())));
                next_Calc_fertilizer.setPrice(Integer.parseInt(txt_next_price_per_bag.getText().toString()));
                calcFertilizers.add(next_Calc_fertilizer);
            }
            details.setCalcFertilizers(calcFertilizers);

            //amount available for expenditure
            EditText txt_amount_available = (EditText) view.findViewById(R.id.txt_amount_available);
            if (txt_amount_available.getText().toString().matches("")) {
                throw new ValidationException("Amount available is empty!");
            }
            details.setAmtAvailable(Double.parseDouble(txt_amount_available.getText().toString()));
//            try {
//                getHelper().getCalculationsDataDao().create(details);
//            } catch (SQLException e) {
//                Toast.makeText(getSherlockActivity(), "failed to log calculation", Toast.LENGTH_SHORT);
//                Log.e(LOG_TAG, "Database exception", e);
//            }
            calculator.execute(details);
        } catch (ValidationException e) {
            Toast.makeText(getSherlockActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error", e);
            Toast.makeText(getSherlockActivity(), "failed to process your request, try again later", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * checks wheather all required values are provided
     *
     * @throws <code>ValidatioException</code>
     *          when some fields are not provided
     */
    private void validate() throws ValidationException {
        String message = "";
        try {
            if (txt_farmer_name.getText().toString().matches("")) {
                message = "farmer name is empty!";
            }
        } finally {
            if (!message.equals("")) {
                throw new ValidationException(message);
            }
        }
    }

    /**
     * onclick listener implementation for removing last
     * fertilizer row
     */
    private void btnRemoveFertilizerClicked() {
        if (table_fertilizers.getChildCount() <= 2) {
            //well, we always have to leave a row
            return;
        }
        try {
            //obtain the row
            TableRow row = (TableRow) table_fertilizers.getChildAt(table_fertilizers.getChildCount() - 1);
            //recover the fertilizer that was eliminated from the listboxes upon selection
            Spinner lstbox_last_fertilizer = (Spinner) row.findViewById(--id_count_fertilizers);
            String recovered_item = (String) lstbox_last_fertilizer.getTag();
            lst_fertilizers.add(getHelper().getFertilizerDataDao().queryForSameId(new Fertilizer(recovered_item)));
            //delete row from layout and refresh
            table_fertilizers.removeViewAt(table_fertilizers.getChildCount() - 1);
            table_fertilizers.invalidate();
            table_fertilizers.requestLayout();
        } catch (SQLException e) {
            Toast.makeText(getSherlockActivity(), "failed to read data", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Database exception", e);
        }
    }

    /**
     * onlick listener implementation for add new fertilizer
     */
    private void btnAddFertilizerClicked() {
        if (lstbox_fertilzers.getSelectedItem() == null || lst_fertilizers.size() <= 1) {
            //all fertilizers have been exhausted, no need for an extra listbox
            return;
        }
        String selected_fertilizer = lstbox_fertilzers.getSelectedItem().toString();
        if (!removeFertilizer(selected_fertilizer) || selected_fertilizer == null || selected_fertilizer == "") {
            //failed to remove already selected item,
            //i expect this to happen only when there are no items in listbox
            return;
        } else {
            //add row and fill in controls
            int new_lstbox_id_multiplier = id_count_fertilizers++;
            int new_row_id_multiplier = id_count_fertilizers_rows++;
            //inflate the new row and pullout inner controls
            LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
            TableRow new_row = (TableRow) inflater.inflate(R.layout.new_row_fertilizer_layout, table_fertilizers, false);
            new_row.setId(new_row_id_multiplier);
            Spinner lstbox_new_fertilizer = (Spinner) new_row.findViewById(R.id.cmb_fertilzers_row);
            lstbox_new_fertilizer.setTag(selected_fertilizer);
            lstbox_new_fertilizer.setId(new_lstbox_id_multiplier);
            EditText txt_new_price = (EditText) new_row.findViewById(R.id.figure_price_per_bag_row);
            txt_new_price.setId(new_lstbox_id_multiplier + 100);
            //modify ids of inner controls and keep them for reference
            lst_new_fertilizer_listbox_ids.add(lstbox_new_fertilizer.getId());
            lst_new_fertilizer_price_ids.add(txt_new_price.getId());
            //populate items into listbox
            ArrayAdapter adapter = new ArrayAdapter(getSherlockActivity(), android.R.layout.simple_spinner_dropdown_item, lst_fertilizers);
            lstbox_new_fertilizer.setAdapter(adapter);
            //add row and refresh ui
            table_fertilizers.addView(new_row);
            table_fertilizers.invalidate();
            table_fertilizers.requestLayout();

            scrollToBottom();
        }
    }

    /**
     * onlick listener implementation for removing last row
     */
    private void btnRemoveLastRowClicked() {
        if (table_crops.getChildCount() <= 2) {
            //well, we always have to leave a row
            return;
        }
        try {
            //obtain the row
            TableRow row = (TableRow) table_crops.getChildAt(table_crops.getChildCount() - 1);
            //recover the crop that was eliminated from the listboxes upon selection
            Spinner lstbox_last_crop = (Spinner) row.findViewById(--id_count_crops);
            String recovered_item = (String) lstbox_last_crop.getTag();
            lst_crops.add(getHelper().getCropDataDao().queryForSameId(new Crop(recovered_item)));
            //delete row from layout and refresh
            table_crops.removeViewAt(table_crops.getChildCount() - 1);
            table_crops.invalidate();
            table_crops.requestLayout();
        } catch (SQLException e) {
            Toast.makeText(getSherlockActivity(), "failed to read data", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Database exception", e);
        }
    }

    /**
     * onlick listener implementation for add new crop row
     */
    private void btnAddNewCropClicked() {
        if (lstbox_crops.getSelectedItem() == null || lst_crops.size() <= 1) {
            //all crops have been exhausted, no need for an extra listbox
            return;
        }
        String selected_crop = lstbox_crops.getSelectedItem().toString();
        if (!removeCrop(selected_crop) || selected_crop == null || selected_crop == "") {
            //failed to remove already selected item,
            //i expect this to happen only when there are no items in listbox
            return;
        } else {
            //add row and fill in controls
            int row_id_multiplier = id_count_crops++;
            //inflate the new row and pullout inner controls
            LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
            TableRow new_row = (TableRow) inflater.inflate(R.layout.new_row_crop_layout, table_crops, false);
            new_row.setId(id_count_crop_rows++);
            Spinner lstbox_new_crop = (Spinner) new_row.findViewById(R.id.cmb_crops);
            lstbox_new_crop.setTag(selected_crop);
            lstbox_new_crop.setId(row_id_multiplier);
            EditText txt_new_landsize = (EditText) new_row.findViewById(R.id.figure_landsize);
            EditText txt_new_profit = (EditText) new_row.findViewById(R.id.figure_profit);
            txt_new_landsize.setId(row_id_multiplier + 100);
            txt_new_profit.setId(row_id_multiplier + 1000);
            //modify ids of inner controls and keep them for reference
            lst_new_croplistbox_ids.add(lstbox_new_crop.getId());
            lst_new_crop_landsize_ids.add(txt_new_landsize.getId());
            lst_new_crop_profit_ids.add(txt_new_profit.getId());
            //populate items into listbox
            ArrayAdapter adapter = new ArrayAdapter(getSherlockActivity(), android.R.layout.simple_spinner_dropdown_item, lst_crops);
            lstbox_new_crop.setAdapter(adapter);
            //add row and refresh ui
            table_crops.addView(new_row);
            table_crops.invalidate();
            table_crops.requestLayout();

            scrollDown();
        }
    }

    private boolean removeCrop(String cropName) {
        for (Crop crop : lst_crops) {
            if (crop.getName().equals(cropName)) {
                lst_crops.remove(crop);
                return true;
            }
        }
        return false;
    }

    private boolean removeFertilizer(String fertilizerName) {
        for (Fertilizer fertilizer : lst_fertilizers) {
            if (fertilizer.getName().equals(fertilizerName)) {
                lst_fertilizers.remove(fertilizer);
                return true;
            }
        }
        return false;
    }

    /**
     * this will help ensure the newly added controls are displayed
     */
    private void scrollDown() {
        final ScrollView scrolview = (ScrollView) getSherlockActivity().findViewById(R.id.scroll_view);
        scrolview.post(new Runnable() {
            public void run() {
                if (Build.VERSION.SDK_INT >= 11) { //API level 8 (Froyo - Ideos) wont have the getY method
                    scrolview.scrollTo(0, (int) btnAddNewCrop.getY() - 100);
                }
            }
        });
    }

    /**
     * converts area from Acres to Hectares
     *
     * @param acres the acres to be converted
     * @return hectares in the provided acres
     */
    private Double changeAcresToHectares(Double acres) {
        return acres * HACTARE;
    }

    /**
     * converts area from Hectares to Acres
     *
     * @param hectares the hectares to be converted
     * @return acres in tje provided hectares
     */
    public static Double changeHectaresToAcres(Double hectares) {
        return hectares * ACRE;
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

    /**
     * this will help ensure the controls at the bottom are display
     * when the view extends
     */
    private void scrollToBottom() {
        final ScrollView scrolview = (ScrollView) getSherlockActivity().findViewById(R.id.scroll_view);
        scrolview.post(new Runnable() {
            public void run() {
                scrolview.scrollTo(0, scrolview.getBottom());
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

    @Override
    public void onPause() {
        //This runs when the fragment goes to backstack
        super.onPause();

    }

    @Override
    public void onResume() {
        //This runs when the fragment returns from backstack
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
}
