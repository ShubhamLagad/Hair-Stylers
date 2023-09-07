package com.subhdroid.hairstylers.Parlour.ParlourMenuFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.subhdroid.hairstylers.R;

import java.util.ArrayList;


public class ParlourReport extends Fragment {

    BarChart weekBarChart,monthBarChart,yearBarChart;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parlour_report, container, false);
        weekBarChart = view.findViewById(R.id.weekBarChart);
        monthBarChart = view.findViewById(R.id.monthBarChart);
        yearBarChart = view.findViewById(R.id.yearBarChart);

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for(int i=0;i<7;i++){
            float value = (float)(i*10.0);
            BarEntry barentry = new BarEntry(i,value);
            barEntries.add(barentry);
        }


        BarDataSet barDataSet  = new BarDataSet(barEntries,"Customers");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setDrawValues(false);

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Mon");
        xAxisLabel.add("Tue");
        xAxisLabel.add("Wed");
        xAxisLabel.add("Thu");
        xAxisLabel.add("Fri");
        xAxisLabel.add("Sat");
        xAxisLabel.add("Sun");

//        XAxis xAxis = weekBarChart.getXAxis();
//        xAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return xAxisLabel.get((int) value);
//
//            }
//        });

        weekBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

        weekBarChart.setData(new BarData(barDataSet));
        weekBarChart.animateY(3000);
        weekBarChart.getDescription().setText("Weekly Customer chart");
        weekBarChart.getDescription().setTextColor(R.color.icon_color);

        monthBarChart.setData(new BarData(barDataSet));
        monthBarChart.animateY(4000);
        monthBarChart.getDescription().setText("Monthly Customer chart");
        monthBarChart.getDescription().setTextColor(R.color.icon_color);

        yearBarChart.setData(new BarData(barDataSet));
        yearBarChart.animateY(5000);
        yearBarChart.getDescription().setText("Yearly Customer chart");
        yearBarChart.getDescription().setTextColor(R.color.icon_color);

        return view;
    }
}