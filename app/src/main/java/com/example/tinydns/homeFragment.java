package com.example.tinydns;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class homeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String ServerURL = "https://172.17.0.2:5000";
    private static final String TAG = "tinyDNS homeFragment";
    private String mParam1;
    private String mParam2;
    private BarChart chart;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public homeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Request and building the bar chart.
        chart = view.findViewById(R.id.chart);
        setupBarChart();
        //TextView chartView = view.findViewById(R.id.viewChart);
        tinyHttpRequest bHttp = new tinyHttpRequest(ServerURL);
        Thread httpCall_chart = new Thread(new Runnable() {
            @Override
            public void run() {
                String newresponse = bHttp.makeHttpRequest("/charts");
                Log.d(TAG, "home chart" + newresponse);
                // Convert response
                List<BarEntry> data = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(newresponse);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        int value = jsonArray.getJSONArray(i).getInt(1);
                        data.add(new BarEntry(i, value));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Perform UI updates on the main thread
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "add chart to ui ");
                        addDataToChart(data);

                    }
                });
            }
        });
        httpCall_chart.start();
        // Request and build the table.
        TableLayout tableLayout = view.findViewById(R.id.homeLayout);
        tinyHttpRequest tHttp = new tinyHttpRequest(ServerURL);
        Thread httpCall = new Thread(new Runnable() {
            @Override
            public void run() {
                String newresponse = tHttp.makeHttpRequest("/home_row");
                Log.d(TAG, "home table" + newresponse);
                // Parse the String into a List<List<String>>
                Type type = new TypeToken<List<List<String>>>() {}.getType();
                List<List<String>> tContent = new Gson().fromJson(newresponse, type);
                // Perform UI updates on the main thread
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "response in run ");
                        for (List<String> row : tContent) {
                            TableRow tableRow = (TableRow) LayoutInflater.from(requireContext()).inflate(R.layout.queries_row_layout, null);
                            for (String value : row) {
                                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(requireContext()).inflate(R.layout.queries_cell_layout, null);
                                TextView textView = linearLayout.findViewById(R.id.textViewCell);
                                textView.setText(value);
                                textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.queries_cell_border));
                                tableRow.addView(linearLayout);
                            }
                            tableLayout.addView(tableRow);
                        }
                    }
                });
            }
        });
        httpCall.start();
    }
    private void setupBarChart() {
        // Konfiguriere das Balkendiagramm
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);

        // Konfiguriere die Achsen
        chart.getXAxis().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisRight().setEnabled(false);

        // Deaktiviere das Zoomen und Scrollen
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setDragEnabled(false);

        // Aktiviere Animationen
        chart.animateY(1000);
    }

    private void addDataToChart(List<BarEntry> entries) {
        BarDataSet dataSet = new BarDataSet(entries, "Request amount");
        dataSet.setColor(Color.argb(51, 255, 99, 132));
        dataSet.setBarBorderColor(Color.argb(255,255,99,132));
        dataSet.setBarBorderWidth(1f);
        BarData data = new BarData(dataSet);
        chart.setData(data);
        chart.invalidate(); // Aktualisiere das Diagramm
    }
}