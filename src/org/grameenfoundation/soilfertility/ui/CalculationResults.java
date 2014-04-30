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
import org.grameenfoundation.soilfertility.model.Calc;
import org.grameenfoundation.soilfertility.model.CalcCrop;
import org.grameenfoundation.soilfertility.model.CalcCropFertilizerRatio;
import org.grameenfoundation.soilfertility.model.CalcFertilizer;

import java.text.DecimalFormat;

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
    private int new_row_id_multiplier = 1;
    private final DecimalFormat formatter = new DecimalFormat("#,###");
    private final String LOG_TAG = getClass().getSimpleName();
    private DatabaseHelper databaseHelper = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculation_results);

        table_ratios = (TableLayout) findViewById(R.id.table_crop_feertilzer_ration);
        table_total_fertilizer = (TableLayout) findViewById(R.id.table_fertilizer_totals);
        table_expected_effects = (TableLayout) findViewById(R.id.table_expected_effects);

        table_crops = (TableLayout) findViewById(R.id.table_crops);
        table_fertilizers = (TableLayout) findViewById(R.id.table_fertilisers);

        lbl_total_net_returns_on_investiment = (TextView) findViewById(R.id.lbl_total_net_returns_on_investiment);
        results = (Calc) getIntent().getSerializableExtra("result");
        if (results != null) {
            //populate tables
            populateCropFertilzerRatiosTable();
            populateCropTotalFertilzersTable();
            populateExpectedEffectsTable();
            //display net returns
            long net_returns = Math.round(results.getTotNetReturns());
            lbl_total_net_returns_on_investiment.setText(formatter.format(net_returns));

            populateCropsTable();

            populateFertilizersTable();

        }


    }

    /**
     * render crop fertilizer ratios in table layout
     */
    private void populateCropFertilzerRatiosTable() {
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
                long ration = Math.round(ratio.getAmt());
                txt_ratio.setText(formatter.format(ration));
                //add row if ratio is not zero
                if (ratio.getAmt() > 0) {
                    table_ratios.addView(new_row);
                }
            }
    }

    /**
     * render total fertilizer figures in table layout
     */
    private void populateCropTotalFertilzersTable() {
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
                long total = Math.round(calcFertilizer.getTotalRequired());
                txt_amount.setText(formatter.format(total));
                //add row
                table_total_fertilizer.addView(new_row);
            }
    }

    /**
     * render expected total returns in table layout
     */
    private void populateExpectedEffectsTable() {
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
                long yield = Math.round(calcCrop.getYieldIncrease());
                txt_yield.setText(formatter.format(yield));
                //column returns
                TextView txt_returns = (TextView) new_row.findViewById(R.id.resultrow_effects_returns_column);
                txt_returns.setId(new_row_id_multiplier++);
                long retunrns = Math.round(calcCrop.getNetReturns());
                txt_returns.setText(formatter.format(retunrns));
                //add row
                table_expected_effects.addView(new_row);
            }
            //table_ratios.invalidate();
            //table_ratios.requestLayout();
    }

    private void populateCropsTable(){
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
                txt_area.setText(calcCrop.getArea().toString());

                //column price
                TextView txt_price = (TextView) new_row.findViewById(R.id.crop_price);
                txt_price.setId(new_row_id_multiplier++);
                // long price = Math.round(calcCrop.getProfit());
                // txt_price.setText(formatter.format(price));
                txt_price.setText(calcCrop.getProfit().toString());

                //add row
                table_crops.addView(new_row);
            }
    }

    private void populateFertilizersTable(){
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
            TextView txt_price = (TextView) new_row.findViewById(R.id.crop_price);
            txt_price.setId(new_row_id_multiplier++);
            txt_price.setText(cf.getPrice());

            //add row
            table_fertilizers.addView(new_row);
        }
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
