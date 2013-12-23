package org.grameenfoundation.soilfertility.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.grameenfoundation.soilfertility.R;
import org.grameenfoundation.soilfertility.model.Crop;
import org.grameenfoundation.soilfertility.model.CropFertilizerRatio;
import org.grameenfoundation.soilfertility.model.Fertilizer;
import org.grameenfoundation.soilfertility.model.Optimizer;

import java.text.DecimalFormat;

/**
 * Copyright (c) 2013 AppLab, Grameen Foundation
 * Created by: David
 * <p/>
 * this is and acivity class that displays calculation results once the answers are obtained
 * The results to be displayed are passed as an Optimizer object from extras
 */
public class CalculationResults extends SherlockFragmentActivity {
    private Optimizer results;
    private TableLayout table_ratios;
    private TableLayout table_total_fertilizer;
    private TableLayout table_expected_effects;
    private TextView lbl_total_net_returns_on_investiment;
    private int new_row_id_multiplier = 1;
    private final DecimalFormat formatter = new DecimalFormat("#,###");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculation_results);

        table_ratios = (TableLayout) findViewById(R.id.table_crop_feertilzer_ration);
        table_total_fertilizer = (TableLayout) findViewById(R.id.table_fertilizer_totals);
        table_expected_effects = (TableLayout) findViewById(R.id.table_expected_effects);
        lbl_total_net_returns_on_investiment = (TextView) findViewById(R.id.lbl_total_net_returns_on_investiment);
        results = (Optimizer) getIntent().getSerializableExtra("result");

        //populate tables
        populateCropFertilzerRatiosTable();
        populateCropTotalFertilzersTable();
        populateExpectedEffectsTable();
        //display net returns
        long net_returns = Math.round(results.getTotNetReturns());
        lbl_total_net_returns_on_investiment.setText(formatter.format(net_returns));
    }

    /**
     * render crop fertilizer ratios in table layout
     */
    private void populateCropFertilzerRatiosTable() {
        if (results != null) {
            LayoutInflater inflater = getLayoutInflater();

            for (CropFertilizerRatio ratio : results.getCropFerts()) {
                //create a new row for ratios
                TableRow new_row = (TableRow) inflater.inflate(R.layout.new_resultrow_crop_fertilzer_ratio, table_ratios, false);
                new_row.setId(new_row_id_multiplier++);
                //column crop
                TextView txt_crop = (TextView) new_row.findViewById(R.id.result_ratio_crop_row);
                txt_crop.setId(new_row_id_multiplier++);
                txt_crop.setText(ratio.getCrop());
                //column fertilizer
                TextView txt_fertlizer = (TextView) new_row.findViewById(R.id.result_ratio_fertilizer_row);
                txt_fertlizer.setId(new_row_id_multiplier++);
                txt_fertlizer.setText(ratio.getFert());
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
            //table_ratios.invalidate();
            //table_ratios.requestLayout();
        }
    }

    /**
     * render total fertilizer figures in table layout
     */
    private void populateCropTotalFertilzersTable() {
        if (results != null) {
            LayoutInflater inflater = getLayoutInflater();

            for (Fertilizer fertilizer : results.getFertilizers()) {
                //create a new row for fertilizers
                TableRow new_row = (TableRow) inflater.inflate(R.layout.new_resultrow_total_fertilizers, table_total_fertilizer, false);
                new_row.setId(new_row_id_multiplier++);
                //column fertilizer
                TextView txt_fertilizer = (TextView) new_row.findViewById(R.id.results_row_total_fertilzer_name_row);
                txt_fertilizer.setId(new_row_id_multiplier++);
                txt_fertilizer.setText(fertilizer.getName());
                //column amount-need
                TextView txt_amount = (TextView) new_row.findViewById(R.id.results_row_total_fertilzer_value_row);
                txt_amount.setId(new_row_id_multiplier++);
                long total = Math.round(fertilizer.getTotalRequired());
                txt_amount.setText(formatter.format(total));
                //add row
                table_total_fertilizer.addView(new_row);
            }
            //table_ratios.invalidate();
            //table_ratios.requestLayout();
        }
    }

    /**
     * render expected total returns in table layout
     */
    private void populateExpectedEffectsTable() {
        if (results != null) {
            LayoutInflater inflater = getLayoutInflater();

            for (Crop crop : results.getCrops()) {
                //create a new row for ratios
                TableRow new_row = (TableRow) inflater.inflate(R.layout.new_resultrow_effects, table_expected_effects, false);
                new_row.setId(new_row_id_multiplier++);
                //column crop
                TextView txt_crop = (TextView) new_row.findViewById(R.id.resultrow_effects_crop_column);
                txt_crop.setId(new_row_id_multiplier++);
                txt_crop.setText(crop.getName());
                //column yield
                TextView txt_yield = (TextView) new_row.findViewById(R.id.resultrow_effects_yield_column);
                txt_yield.setId(new_row_id_multiplier++);
                long yield = Math.round(crop.getYieldIncrease());
                txt_yield.setText(formatter.format(yield));
                //column returns
                TextView txt_returns = (TextView) new_row.findViewById(R.id.resultrow_effects_returns_column);
                txt_returns.setId(new_row_id_multiplier++);
                long retunrns = Math.round(crop.getNetReturns());
                txt_returns.setText(formatter.format(retunrns));
                //add row
                table_expected_effects.addView(new_row);
            }
            //table_ratios.invalidate();
            //table_ratios.requestLayout();
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
