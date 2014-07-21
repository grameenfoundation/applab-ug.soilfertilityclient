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
    private Button btnAddFertilizer;
    private Button btnCalculate;

    private EditText txt_farmer_name;
    private TableLayout table_crops;
    private TableLayout table_fertilizers;
    private View view;

    private List<Crop> lst_crops;
    private List<Crop> lst_selected_crops = new ArrayList<Crop>();

    private List<Fertilizer> lst_fertilizers;
    private List<Fertilizer> lst_selected_fertilizers = new ArrayList<Fertilizer>();

    private List lst_new_croplistbox_ids;
    private List lst_new_crop_landsize_ids;
    private List lst_new_crop_profit_ids;
    private List lst_new_fertilizer_listbox_ids;
    private List lst_new_fertilizer_price_ids;

    private int id_count_crops = 1;
    private int id_count_fertilizers = 1;
    private int id_count_crop_rows = 2000;
    private int id_count_fertilizers_rows = 3000;

    public static final Double HACTARE = 0.404686;
    public static final Double ACRE = 2.47105;
    public static final String DEFAULT_URL = "http://212.88.100.70:8888/Service1.asmx/Optimize";
    // private static final Double ACRE = 2.47105;

    private DatabaseHelper databaseHelper = null;
    private final String LOG_TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_new_calculation, container, false);

        setRetainInstance(true);
        Spinner lstbox_crops = (Spinner) view.findViewById(R.id.cmb_crops);
        Spinner lstbox_fertilzers = (Spinner) view.findViewById(R.id.cmb_fertilzers);

        btnAddNewCrop = (Button) view.findViewById(R.id.btn_add_row);
        btnAddFertilizer = (Button) view.findViewById(R.id.btn_add_fertilizer);

        Button btnRemoveFertilizer = (Button) view.findViewById(R.id.btn_dynamic_remove_fertilizer);
        Button btnRemoveCrop = (Button) view.findViewById(R.id.btn_dynamic_remove_crop_row);

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
            lstbox_crops.setSelection(0);

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
                btnAddCropClicked();
            }
        });

        lstbox_crops.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //re-populate dropdowns
                boolean selectedChanged = selectedCropsChanged();
                if (selectedChanged) {
                    populateCropDropDowns();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        btnRemoveCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRemoveCropRowClicked(v);
                populateCropDropDowns();
            }
        });

        //=========================================================================

        lstbox_fertilzers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //re-populate dropdowns
                boolean selectedChanged = selectedFertilizersChanged();
                if (selectedChanged) {
                    // updateSelectedFertilisers();
                    populateFertilizerDropDowns();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
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
                btnRemoveFertilizerClicked(v);
                populateFertilizerDropDowns();
            }
        });

        //=========================================================================

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
            String url = getSherlockActivity().getSharedPreferences("preferences.xml",
                    Context.MODE_MULTI_PROCESS).getString("url", DEFAULT_URL);
            calculator = new LocalCalculator(view.getContext(), url);
            Calc details = new Calc();
            List<CalcCrop> calcCrops = new ArrayList<CalcCrop>();
            List<CalcFertilizer> calcFertilizers = new ArrayList<CalcFertilizer>();

            // fill in details from UI - imei and farmer name
            TelephonyManager telephonyManager = (TelephonyManager) getSherlockActivity()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            details.setImei(telephonyManager.getDeviceId());
            details.setFarmerName(txt_farmer_name.getText().toString());

            // get dynamically added controls and add other calcCrops here

            for (int x = 1; x < table_crops.getChildCount(); x++) {
                TableRow row = (TableRow) table_crops.getChildAt(x);
                Spinner lstbox_of_crop = (Spinner) row.getChildAt(0);
                CheckedTextView selectedCrop = (CheckedTextView) lstbox_of_crop.getChildAt(0);
                EditText txt_of_land = (EditText) row.getChildAt(1);
                EditText txt_of_profit = (EditText) row.getChildAt(2);

                if (txt_of_land.getText().toString().matches(""))
                    throw new ValidationException("land size is empty!");

                if (txt_of_profit.getText().toString().matches(""))
                    throw new ValidationException("Profit/Kg is empty!");

                //populate next crop
                CalcCrop next_Calc_crop = new CalcCrop(details);
                next_Calc_crop.setCrop(getHelper().getCropDataDao().queryForSameId(new Crop(selectedCrop.getText().toString())));
                next_Calc_crop.setArea(changeAcresToHectares(Double.parseDouble(txt_of_land.getText().toString())));
                next_Calc_crop.setProfit(Double.parseDouble(txt_of_profit.getText().toString()));

                for (CalcCrop cc : calcCrops)
                    if (cc.getCrop().getName().equalsIgnoreCase(next_Calc_crop.getCrop().getName()))
                        throw new ValidationException("Crop " + next_Calc_crop.getCrop().getName() + " is repeated!");

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

                for (CalcFertilizer cf : calcFertilizers)
                    if (cf.getFertilizer().getName().equalsIgnoreCase(next_Calc_fertilizer.getFertilizer().getName()))
                        throw new ValidationException("Fertilizer " + next_Calc_fertilizer.getFertilizer().getName() + " is repeated!");

                calcFertilizers.add(next_Calc_fertilizer);
            }
            details.setCalcFertilizers(calcFertilizers);

            //amount available for expenditure
            EditText txt_amount_available = (EditText) view.findViewById(R.id.txt_amount_available);
            if (txt_amount_available.getText().toString().matches("")) {
                throw new ValidationException("Amount available is empty!");
            }
            details.setAmtAvailable(Double.parseDouble(txt_amount_available.getText().toString()));

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
     * onclick listener implementation for removing a fertilizer row
     */
    private void btnRemoveFertilizerClicked(View v) {

        if (table_fertilizers.getChildCount() > 2) {

            //obtain the row
            TableRow row = (TableRow) v.getParent();

            try {

                int rowIndex = table_fertilizers.indexOfChild(row);

                // delete row from layout and refresh
                table_fertilizers.removeViewAt(rowIndex);
                table_fertilizers.invalidate();
                table_fertilizers.requestLayout();
            } catch (Exception e) {
                Toast.makeText(getSherlockActivity(), "failed to read data", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Database exception", e);
            }
        }

    }

    /**
     * onlick listener implementation for add new fertilizer
     */
    private void btnAddFertilizerClicked() {

        List<Fertilizer> availablefertilizers = getAvailableFertilizers(null);

        if (availablefertilizers != null && availablefertilizers.size() > 0) {
            //add row and fill in controls
            int new_lstbox_id_multiplier = id_count_fertilizers++;
            int new_row_id_multiplier = id_count_fertilizers_rows++;
            //inflate the new row and pullout inner controls
            LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
            TableRow new_row = (TableRow) inflater.inflate(R.layout.new_row_fertilizer_layout, table_fertilizers, false);
            new_row.setId(new_row_id_multiplier);

            Spinner lstbox_new_fertilizer = (Spinner) new_row.findViewById(R.id.cmb_fertilzers_row);
            lstbox_new_fertilizer.setTag(availablefertilizers.get(0).getName());
            lstbox_new_fertilizer.setId(new_lstbox_id_multiplier);

            EditText txt_new_price = (EditText) new_row.findViewById(R.id.figure_price_per_bag_row);
            txt_new_price.setId(new_lstbox_id_multiplier + 100);

            Button button = (Button) new_row.findViewById(R.id.btn_dynamic_remove_fertilizer);

            //modify ids of inner controls and keep them for reference
            lst_new_fertilizer_listbox_ids.add(lstbox_new_fertilizer.getId());
            lst_new_fertilizer_price_ids.add(txt_new_price.getId());
            //populate items into listbox
            ArrayAdapter adapter = new ArrayAdapter(getSherlockActivity(),
                    android.R.layout.simple_spinner_dropdown_item, availablefertilizers);
            lstbox_new_fertilizer.setAdapter(adapter);
            //add row and refresh ui
            table_fertilizers.addView(new_row);
            table_fertilizers.invalidate();
            table_fertilizers.requestLayout();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnRemoveFertilizerClicked(v);
                    populateFertilizerDropDowns();
                }
            });

            lstbox_new_fertilizer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    //re-populate dropdowns
                    boolean selectedChanged = selectedFertilizersChanged();
                    if (selectedChanged)
                        populateFertilizerDropDowns();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            scrollToBottom();
        }
    }

    private Boolean selectedFertilizersChanged() {
        try {
            List<Fertilizer> selectedfertilizers = getSelectedFertilizers();

            // boolean newFertilizerSelected = false;
            for (Fertilizer f : selectedfertilizers)
                if (!lst_selected_fertilizers.contains(f)) {
                    return true;
                }

            // boolean oldFertilizerDe_Selected = false;
            for (Fertilizer f : lst_selected_fertilizers)
                if (!selectedfertilizers.contains(f)) {
                    return true;
                }

            // if (newFertilizerSelected || oldFertilizerDe_Selected)
            // return true;

        } catch (Exception e) {
            Toast.makeText(getSherlockActivity(), "failed to process data", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, " exception", e);
        }

        return false;
    }

    private void updateSelectedFertilisers() {
        try {
            if (selectedFertilizersChanged()) {
                List<Fertilizer> selectedfertilizers = getSelectedFertilizers();
                lst_selected_fertilizers = selectedfertilizers;
            }
        } catch (Exception e) {
            Toast.makeText(getSherlockActivity(), "failed to process data", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, " exception", e);
        }
    }

    private void populateFertilizerDropDowns() {

        updateSelectedFertilisers();

        try {
            for (int x = 1; x < table_fertilizers.getChildCount(); x++) {
                TableRow row = (TableRow) table_fertilizers.getChildAt(x);
                Spinner lstbox_next_fertilizer = (Spinner) row.getChildAt(0);
                CheckedTextView selectedFertilizer = (CheckedTextView) lstbox_next_fertilizer.getChildAt(0);

                if (selectedFertilizer == null)
                    continue;

                // fertilizer
                Fertilizer fertilizer = getHelper().getFertilizerDataDao().queryForSameId(new Fertilizer(selectedFertilizer.getText().toString()));

                List<Fertilizer> availablefertilizers = getAvailableFertilizers(fertilizer);
                // availablefertilizers.add(0, fertilizer);

                // populate items into listbox
                ArrayAdapter adapter = new ArrayAdapter(getSherlockActivity(), android.R.layout.simple_spinner_dropdown_item, availablefertilizers);
                lstbox_next_fertilizer.setAdapter(adapter);

                // availablefertilizers.remove(0);

            }
        } catch (Exception e) {
            Toast.makeText(getSherlockActivity(), "failed to process data", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, " exception", e);
        }
    }

    private List<Fertilizer> getAvailableFertilizers(Fertilizer currentfertilizer) {

        List<Fertilizer> availableFertilizers = new ArrayList<Fertilizer>();
        try {

            for (Fertilizer f : lst_fertilizers) {
                if (!lst_selected_fertilizers.contains(f))
                    availableFertilizers.add(f);
            }

            if (currentfertilizer != null && !availableFertilizers.contains(currentfertilizer))
                availableFertilizers.add(0, currentfertilizer);

        } catch (Exception e) {
            Toast.makeText(getSherlockActivity(), "failed to process data", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, " exception", e);
        }

        return availableFertilizers;
    }

    private List<Fertilizer> getSelectedFertilizers() throws SQLException {

        List<Fertilizer> fertilizers = new ArrayList<Fertilizer>();

        for (int x = 1; x < table_fertilizers.getChildCount(); x++) {
            TableRow row = (TableRow) table_fertilizers.getChildAt(x);
            Spinner lstbox_next_fertilizer = (Spinner) row.getChildAt(0);
            CheckedTextView selectedFertilizer = (CheckedTextView) lstbox_next_fertilizer.getChildAt(0);

            //fertilizer
            CharSequence name_field = selectedFertilizer == null ? null : selectedFertilizer.getText();
            if(name_field != null) {
                String name = name_field.toString();
                Fertilizer fertilizer = getHelper().getFertilizerDataDao().queryForSameId(new Fertilizer(name));
                fertilizers.add(fertilizer);
            }
        }

        return fertilizers;
    }

    //=====================================================================================

    /**
     * onlick listener implementation for removing given row
     */
    private void btnRemoveCropRowClicked(View v) {

        //well, we always have to leave a crop row
        if (table_crops.getChildCount() > 2) {
            try {
                //obtain the row
                TableRow row = (TableRow) v.getParent();

                int rowIndex = table_crops.indexOfChild(row);

                //delete row from layout and refresh
                table_crops.removeViewAt(rowIndex);
                table_crops.invalidate();
                table_crops.requestLayout();
            } catch (Exception e) {
                Toast.makeText(getSherlockActivity(), "failed to read data", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Database exception", e);
            }
        }
    }

    /**
     * onlick listener implementation for add new crop row
     */
    private void btnAddCropClicked() {

        List<Crop> availablecrops = getAvailableCrops(null);

        if (availablecrops != null && availablecrops.size() > 0) {
            //add row and fill in controls
            int row_id_multiplier = id_count_crops++;
            //inflate the new row and pullout inner controls
            LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
            TableRow new_row = (TableRow) inflater.inflate(R.layout.new_row_crop_layout, table_crops, false);
            new_row.setId(id_count_crop_rows++);

            Spinner lstbox_new_crop = (Spinner) new_row.findViewById(R.id.cmb_crops);
            lstbox_new_crop.setTag(availablecrops.get(0).getName());
            lstbox_new_crop.setId(row_id_multiplier);

            EditText txt_new_landsize = (EditText) new_row.findViewById(R.id.figure_landsize);
            EditText txt_new_profit = (EditText) new_row.findViewById(R.id.figure_profit);

            Button button = (Button) new_row.findViewById(R.id.btn_dynamic_remove_crop_row);

            txt_new_landsize.setId(row_id_multiplier + 100);
            txt_new_profit.setId(row_id_multiplier + 1000);
            button.setId(row_id_multiplier + 2000);
            //modify ids of inner controls and keep them for reference
            lst_new_croplistbox_ids.add(lstbox_new_crop.getId());
            lst_new_crop_landsize_ids.add(txt_new_landsize.getId());
            lst_new_crop_profit_ids.add(txt_new_profit.getId());

            //populate items into listbox
            ArrayAdapter adapter = new ArrayAdapter(getSherlockActivity(),
                    android.R.layout.simple_spinner_dropdown_item, availablecrops);
            lstbox_new_crop.setAdapter(adapter);
            //add row and refresh ui
            table_crops.addView(new_row);
            table_crops.invalidate();
            table_crops.requestLayout();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnRemoveCropRowClicked(v);
                    populateCropDropDowns();
                }
            });

            lstbox_new_crop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    //re-populate dropdowns
                    boolean selectedChanged = selectedCropsChanged();
                    if (selectedChanged)
                        populateCropDropDowns();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            scrollDown();
        }
    }

    private Boolean selectedCropsChanged() {
        try {
            List<Crop> selectedCrops = getSelectedCrops();

            for (Crop c : selectedCrops)
                if (!lst_selected_crops.contains(c)) {
                    return true;
                }

            // boolean oldFertilizerDe_Selected = false;
            for (Crop c : lst_selected_crops)
                if (!selectedCrops.contains(c)) {
                    return true;
                }

            // if (newFertilizerSelected || oldFertilizerDe_Selected)
            // return true;

        } catch (Exception e) {
            Toast.makeText(getSherlockActivity(), "failed to process data", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, " exception", e);
        }

        return false;
    }

    private void updateSelectedCrops() {
        try {
            if (selectedCropsChanged()) {
                List<Crop> selectedcrops = getSelectedCrops();
                lst_selected_crops = selectedcrops;
            }
        } catch (Exception e) {
            Toast.makeText(getSherlockActivity(), "failed to process data", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, " exception", e);
        }
    }

    private void populateCropDropDowns() {

        updateSelectedCrops();

        try {
            for (int x = 1; x < table_crops.getChildCount(); x++) {
                TableRow row = (TableRow) table_crops.getChildAt(x);
                Spinner lstbox_next_crop = (Spinner) row.getChildAt(0);
                CheckedTextView selectedCrop = (CheckedTextView) lstbox_next_crop.getChildAt(0);

                if (selectedCrop == null)
                    continue;

                // crop
                Crop crop = getHelper().getCropDataDao().queryForSameId(new Crop(selectedCrop.getText().toString()));

                List<Crop> availablecrops = getAvailableCrops(crop);

                // populate items into listbox
                ArrayAdapter adapter = new ArrayAdapter(getSherlockActivity(), android.R.layout.simple_spinner_dropdown_item, availablecrops);
                lstbox_next_crop.setAdapter(adapter);
            }
        } catch (Exception e) {
            Toast.makeText(getSherlockActivity(), "failed to process data", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, " exception", e);
        }
    }

    private List<Crop> getAvailableCrops(Crop currentcrop) {

        List<Crop> availableCrops = new ArrayList<Crop>();
        try {

            for (Crop c : lst_crops) {
                if (!lst_selected_crops.contains(c))
                    availableCrops.add(c);
            }

            if (currentcrop != null && !availableCrops.contains(currentcrop))
                availableCrops.add(0, currentcrop);

        } catch (Exception e) {
            Toast.makeText(getSherlockActivity(), "failed to process data", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, " exception", e);
        }

        return availableCrops;
    }

    private List<Crop> getSelectedCrops() throws SQLException {

        List<Crop> crops = new ArrayList<Crop>();

        for (int x = 1; x < table_crops.getChildCount(); x++) {
            TableRow row = (TableRow) table_crops.getChildAt(x);
            Spinner lstbox_next_crop = (Spinner) row.getChildAt(0);
            CheckedTextView selectedCrop = (CheckedTextView) lstbox_next_crop.getChildAt(0);

            // crop
            CharSequence name_field = selectedCrop == null ? null : selectedCrop.getText();
            if(name_field != null) {
                String name = name_field.toString();
                Crop crop = getHelper().getCropDataDao().queryForSameId(new Crop(name));
                crops.add(crop);
            }
        }

        return crops;
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
        // return acres / ACRE;
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
