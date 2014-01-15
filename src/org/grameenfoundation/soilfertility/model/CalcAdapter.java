package org.grameenfoundation.soilfertility.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.grameenfoundation.soilfertility.R;

import java.util.List;

/**
 * Copyright (c) 2014 AppLab, Grameen Foundation
 * Created by: David
 * Adapter for listing previosu calculations in a list view
 */
public class CalcAdapter extends ArrayAdapter<Calc> {

    private List<Calc> items;

    public CalcAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CalcAdapter(Context context, int resource, List<Calc> items){
        super(context, resource, items);
        this.items = items;
    }

    public View getView(int position, View view, ViewGroup parent){
        View v = view;
        if(v == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.list_item_calculations, null);
        }
        Calc calc = items.get(position);
        if(calc != null){
            TextView t_farmername = (TextView) v.findViewById(R.id.lbl_farmer_name);
            TextView t_datecreated = (TextView) v.findViewById(R.id.lbl_date_created);
            if(t_farmername != null){
                t_farmername.setText(calc.getFarmerName());
            }
            if(t_datecreated != null){
                t_datecreated.setText(Calc.dateFormat.format(calc.getDateCreated()));
            }
        }
        v.setTag(calc);
        return v;
    }
}
