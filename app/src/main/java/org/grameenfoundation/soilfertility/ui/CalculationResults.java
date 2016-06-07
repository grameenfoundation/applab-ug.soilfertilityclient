package org.grameenfoundation.soilfertility.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import org.grameenfoundation.soilfertility.R;
import org.grameenfoundation.soilfertility.dataaccess.DatabaseHelper;
import org.grameenfoundation.soilfertility.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;

/**
 * Copyright (c) 2013 AppLab, Grameen Foundation
 * Created by: David
 * <p/>
 * this is and acivity class that displays calculation results once the answers are obtained
 * The results to be displayed are passed as an Calc object from extras
 */
public class CalculationResults extends SherlockFragmentActivity {
    private Calc results;
    private TableLayout table_ratios;
    private TableLayout table_total_fertilizer;
    private TableLayout table_expected_effects;

    private TableLayout table_crops, table_fertilizers;

    private TextView lbl_total_net_returns_on_investiment;
    private TextView lbl_input_amount_available;
    private int new_row_id_multiplier = 1;
    private final DecimalFormat formatter = new DecimalFormat("#,###");
    private final String LOG_TAG = getClass().getSimpleName();
    private DatabaseHelper databaseHelper = null;

    //Redefine basing on the user's units
    private TextView header_rate;
    private TextView header_fertilizer_total;
    private TextView header_effects_per_acre;
    private TextView textView1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculation_results);

        //Set the right units based on the User's unit selection
        header_rate = (TextView) findViewById(R.id.header_rate);
        header_fertilizer_total= (TextView) findViewById(R.id.header_fertilizer_total);
        header_effects_per_acre= (TextView) findViewById(R.id.header_effects_per_acre);
        textView1= (TextView) findViewById(R.id.textView1);

        table_ratios = (TableLayout) findViewById(R.id.table_crop_feertilzer_ration);
        table_total_fertilizer = (TableLayout) findViewById(R.id.table_fertilizer_totals);
        table_expected_effects = (TableLayout) findViewById(R.id.table_expected_effects);

        table_crops = (TableLayout) findViewById(R.id.table_crops);
        table_fertilizers = (TableLayout) findViewById(R.id.table_fertilisers);

        lbl_total_net_returns_on_investiment = (TextView) findViewById(R.id.lbl_total_net_returns_on_investiment);
        lbl_input_amount_available = (TextView) findViewById(R.id.lbl_input_amount_available);
        results = (Calc) getIntent().getSerializableExtra("result");
        if (results != null) {
            populateUnitsTitles();

            //populate tables
            populateCropFertilzerRatiosTable();
            populateCropTotalFertilzersTable();
            populateExpectedEffectsTable();
            //display net returns
            long net_returns = Math.round(results.getTotNetReturns());
            lbl_total_net_returns_on_investiment.setText(formatter.format(net_returns));

            populateCropsTable();
            populateFertilizersTable();
            lbl_input_amount_available.setText("Amount available:  " + formatter.format(results.getAmtAvailable()));
        }
    }

    private void populateUnitsTitles() {
        try {

            header_rate.setText("Application Rate - Kg/" + results.getUnits().replace('s', ' '));
            header_fertilizer_total.setText("Total fertilizer needed in an " + results.getUnits().replace('s', ' '));
            header_effects_per_acre.setText("Expected Average Effects per " + results.getUnits().replace('s', ' '));
            textView1.setText("Land Size (" + results.getUnits() + ")");
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    /**
     * render crop fertilizer application rate in table layout
     * the passed calculation results have the application rate in kg/Hactare
     * of fertilizers for each crop selected. this rate is converted to kg/acre
     * before display
     */
    private void populateCropFertilzerRatiosTable() {
        try {
            LayoutInflater inflater = getLayoutInflater();

            for (CalcCropFertilizerRatio ratio : results.getCropFerts()) {
                //create a new row for ratios
                TableRow new_row = (TableRow) inflater.inflate(R.layout.new_resultrow_crop_fertilzer_ratio, table_ratios, false);
                new_row.setId(new_row_id_multiplier++);
                //column crop
                TextView txt_crop = (TextView) new_row.findViewById(R.id.result_ratio_crop_row);
                txt_crop.setId(new_row_id_multiplier++);
                txt_crop.setText(ratio.getCrop().getName());
                //column fertilizer
                TextView txt_fertlizer = (TextView) new_row.findViewById(R.id.result_ratio_fertilizer_row);
                txt_fertlizer.setId(new_row_id_multiplier++);
                txt_fertlizer.setText(getFertilizerShortName(ratio.getFert().getName()));
                //column ratio
                TextView txt_ratio = (TextView) new_row.findViewById(R.id.result_ratio_rate_row);
                txt_ratio.setId(new_row_id_multiplier++);
                //Double landRation = getLandRatioOfCrop(crops, ratio.getCrop(), totalLandSize);
                //Double ration = round(ratio.getAmt() * landRation / FragmentNewCalculation.ACRE, 1);
                Double ration = round(getFertilizerRatioForCrop(results, ratio), 1);
                txt_ratio.setText(ration.toString());
                //add row if ratio is not zero
                if (ratio.getAmt() > 0) {
                    table_ratios.addView(new_row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * render total fertilizer figures in table layout
     * the calculation object passed to this activity as an extra
     * has these values. Here we just add table rows to the UI table
     */
    private void populateCropTotalFertilzersTable() {
        try {
            LayoutInflater inflater = getLayoutInflater();

            for (CalcFertilizer calcFertilizer : results.getCalcFertilizers()) {
                //create a new row for fertilizers
                TableRow new_row = (TableRow) inflater.inflate(R.layout.new_resultrow_total_fertilizers, table_total_fertilizer, false);
                new_row.setId(new_row_id_multiplier++);
                //column calcFertilizer
                TextView txt_fertilizer = (TextView) new_row.findViewById(R.id.results_row_total_fertilzer_name_row);
                txt_fertilizer.setId(new_row_id_multiplier++);
                txt_fertilizer.setText(getFertilizerShortName(calcFertilizer.getFertilizer().getName()));
                //column amount-need
                TextView txt_amount = (TextView) new_row.findViewById(R.id.results_row_total_fertilzer_value_row);
                txt_amount.setId(new_row_id_multiplier++);

                Double total = 0.0;
                 if (results.getUnits().equals("Hectares")) {
                    total = round(calcFertilizer.getTotalRequired() / FragmentNewCalculation.ACRE, 1);
                 }
                else { total = round(calcFertilizer.getTotalRequired(), 1);}

                txt_amount.setText(total.toString());
                //add row
                table_total_fertilizer.addView(new_row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * for each crop selected, there is an expected yield increase per hactare
     * and net returns per hactare. here, we render these values upon conversion to
     * per acre basing on the chosen units
     */
    private void populateExpectedEffectsTable() {
        try {
            LayoutInflater inflater = getLayoutInflater();

            for (CalcCrop calcCrop : results.getCalcCrops()) {
                //create a new row for ratios
                TableRow new_row = (TableRow) inflater.inflate(R.layout.new_resultrow_effects, table_expected_effects, false);
                new_row.setId(new_row_id_multiplier++);
                //column calcCrop
                TextView txt_crop = (TextView) new_row.findViewById(R.id.resultrow_effects_crop_column);
                txt_crop.setId(new_row_id_multiplier++);
                txt_crop.setText(calcCrop.getCrop().getName());
                //column yield
                TextView txt_yield = (TextView) new_row.findViewById(R.id.resultrow_effects_yield_column);
                txt_yield.setId(new_row_id_multiplier++);

                long yield = 0;
                if (results.getUnits().equals("Hectares")) {
                    yield = Math.round(calcCrop.getYieldIncrease() / FragmentNewCalculation.ACRE);
                }else { yield = Math.round(calcCrop.getYieldIncrease());}

                txt_yield.setText(formatter.format(yield));
                //column returns
                TextView txt_returns = (TextView) new_row.findViewById(R.id.resultrow_effects_returns_column);
                txt_returns.setId(new_row_id_multiplier++);

                long retunrns = 0;
                if (results.getUnits().equals("Hectares")) {
                    retunrns = Math.round(calcCrop.getNetReturns() / FragmentNewCalculation.ACRE);
                }else {retunrns = Math.round(calcCrop.getNetReturns());}

                txt_returns.setText(formatter.format(retunrns));
                //add row
                table_expected_effects.addView(new_row);
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * displays the user's input showing the land size and expected crop profit
     * for each of the selected crops
     */
    private void populateCropsTable(){
        try {
            LayoutInflater inflater = getLayoutInflater();

            for (CalcCrop calcCrop : results.getCalcCrops()) {
                //create a new row for ratios
                TableRow new_row = (TableRow) inflater.inflate(R.layout.new_resultrow_crop, table_crops, false);
                //new_row.setId(new_row_id_multiplier++);

                //column calcCrop
                TextView txt_crop_name = (TextView) new_row.findViewById(R.id.crop_name);
                txt_crop_name.setId(new_row_id_multiplier++);
                txt_crop_name.setText(calcCrop.getCrop().getName());

                //column land_size
                TextView txt_area = (TextView) new_row.findViewById(R.id.crop_area);
                txt_area.setId(new_row_id_multiplier++);
                // long area = Math.round(calcCrop.getArea());
                // txt_area.setText(formatter.format(area));
                if (results.getUnits().equals("Hectares")) {
                    txt_area.setText(truncate(calcCrop.getAreaHectares(), 2).toString());
                }
                else
                {  txt_area.setText(truncate(calcCrop.getArea(), 2).toString());}
                //column price
                TextView txt_price = (TextView) new_row.findViewById(R.id.crop_price);
                txt_price.setId(new_row_id_multiplier++);
                // long price = Math.round(calcCrop.getProfit());
                // txt_price.setText(formatter.format(price));
                txt_price.setText(calcCrop.getProfit().toString());

                //add row
                table_crops.addView(new_row);
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * displays user's input showing the available price/50kg bag of
     * each of the selected fertilizer
     */
    private void populateFertilizersTable(){
        try {
            LayoutInflater inflater = getLayoutInflater();

            for (CalcFertilizer cf : results.getCalcFertilizers()) {
                //create a new row for ratios
                TableRow new_row = (TableRow) inflater.inflate(R.layout.new_resultrow_fertilizer, table_fertilizers, false);
                //new_row.setId(new_row_id_multiplier++);

                //column calcFertilizer
                TextView txt_name = (TextView) new_row.findViewById(R.id.fertilizer_name);
                txt_name.setId(new_row_id_multiplier++);
                txt_name.setText(cf.getFertilizer().getName());

                //column price
                TextView txt_price = (TextView) new_row.findViewById(R.id.fertilizer_price_per_50_kgs);
                txt_price.setId(new_row_id_multiplier++);
                txt_price.setText(formatter.format(cf.getPrice()));

                //add row
                table_fertilizers.addView(new_row);
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * searches for a given calc-crop and returns the land ration for the crop
     * it represents
     *
     * @param crops         a list of crops details
     * @param crop          a crop whose ration is required
     * @param totalSize     the total land size to have the ratio against
     * @return              land ratio for the given crop
     */
    public static Double getLandRatioOfCrop(Collection<CalcCrop> crops, Crop crop, Double totalSize){
        for (CalcCrop calcCrop : crops){
            if(calcCrop.getCrop().getName().equals(crop.getName())){
                return calcCrop.getLandRatio(totalSize);
            }
        }
        return null;
    }

    public static Double getFertilizerRatioForCrop(Calc results, CalcCropFertilizerRatio crop) {
        Double totalLandSize = results.getTotalLandSize();
        Collection<CalcCrop> crops = results.getCalcCrops();

        Double landRatio = 0d;
        //get this crop's land ratio
        for (CalcCrop calcCrop : crops){
            if(calcCrop.getCrop().getName().equals(crop.getCrop().getName())){
                landRatio = calcCrop.getLandRatio(totalLandSize);
                break;
            }
        }
        //multiply land ratio by total fertilizer to get how much fertilizer the crop needs
        //But first, get the fertilizer
        CalcFertilizer fertilizer = null;
        for (CalcFertilizer calcFertilizer : results.getCalcFertilizers()) {
            if(calcFertilizer.getFertilizer().getName().equals(crop.getFert().getName())){
                fertilizer = calcFertilizer;
                break;
            }
        }
        if(fertilizer != null) {
            if (results.getUnits().equals("Hectares")) {
            return  (fertilizer.getTotalRequired() * landRatio) / FragmentNewCalculation.ACRE;
            }else {
                return  (fertilizer.getTotalRequired() * landRatio);
            }
        }
        return 0d;
    }

    /**
     * custom rounding method
     * @param value     figure to round
     * @param places    how many decimal places
     * @return          rounded value
     */
    public static Double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * truncates a figure
     * @param value      figure to undergo truncation
     * @param places     decimal places to be returned
     * @return           truncated value
     */
    public static Double truncate(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = (long) value;
        return (double) tmp / factor;
    }

    /**
     * gets the helper from the manager
     *
     * @return a databasehelper instance
     */
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getBaseContext(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    /**
     * get a short name representing the fertilizer. This is normally
     * an abbreviation at the end of the long name
     *
     * @param name full name for the fertilizer
     * @return short name for the fertilizer
     */
    private String getFertilizerShortName(String name) {
        if (name.split(" ").length > 1) {
            return name.substring(name.lastIndexOf(" ") + 1);
        } else {
            return name;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
