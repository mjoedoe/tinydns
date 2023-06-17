package com.example.tinydns;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link vtStatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class vtStatsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String VT_API_KEY = "apikey";
    private static final String ServerURL = "https://www.virustotal.com";
    private static final String TAG = "tinyDNS vtStatsFragment";
    private String mParam1;
    private String mParam2;

    public vtStatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment vtStatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static vtStatsFragment newInstance(String param1, String param2) {
        vtStatsFragment fragment = new vtStatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_vt_stats, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: starting ");
        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
        // Http Connection instance.
        tinyHttpRequest tHttp = new tinyHttpRequest(ServerURL);



        Thread httpCall = new Thread(new Runnable() {
            @Override
            public void run() {
                String newresponse = tHttp.makeHttpRequest("/api/v3/users/" + VT_API_KEY + "/overall_quotas");
                Log.d(TAG, "response vt " + newresponse);
                // Parse the String into a json.
                JSONObject vt_json = null;
                String hour_used = "";
                String hour_avail = "";
                String day_used = "";
                String day_avail = "";
                String month_used = "";
                String month_avail = "";
                try {
                    vt_json = new JSONObject(newresponse).getJSONObject("data");
                    day_used = vt_json.getJSONObject("api_requests_daily").getJSONObject("user").getString("used");
                    day_avail = vt_json.getJSONObject("api_requests_daily").getJSONObject("user").getString("allowed");
                    hour_avail = vt_json.getJSONObject("api_requests_hourly").getJSONObject("user").getString("allowed");
                    hour_used = vt_json.getJSONObject("api_requests_hourly").getJSONObject("user").getString("used");
                    month_used = vt_json.getJSONObject("api_requests_monthly").getJSONObject("user").getString("used");
                    month_avail = vt_json.getJSONObject("api_requests_monthly").getJSONObject("user").getString("allowed");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Type type = new TypeToken<List<List<String>>>() {}.getType();
                //List<List<String>> tContent = new Gson().fromJson(newresponse, type);
                // Perform UI updates on the main thread
                JSONObject finalVt_json = vt_json;
                String finalHour_avail = hour_avail;
                String finalHour_used = hour_used;
                String finalDay_used = day_used;
                String finalDay_avail = day_avail;
                String finalMonth_used = month_used;
                String finalMonth_avail = month_avail;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalVt_json == null) {
                            Log.d(TAG, "vtstats json is null ");
                            return;
                        }

                        TextView ha = (TextView) view.findViewById(R.id.requests_hour_available);
                        ha.setText(finalHour_avail);
                        TextView hu = (TextView) view.findViewById(R.id.requests_hour_used);
                        hu.setText(finalHour_used);
                        TextView du = (TextView) view.findViewById(R.id.requests_today_used);
                        du.setText(finalDay_used);
                        TextView da = (TextView) view.findViewById(R.id.requests_today_available);
                        da.setText(finalDay_avail);
                        TextView mu = (TextView) view.findViewById(R.id.requests_month_used);
                        mu.setText(finalMonth_used);
                        TextView ma = (TextView) view.findViewById(R.id.requests_month_available);
                        ma.setText(finalMonth_avail);
/*                        for (List<String> row : tContent) {
                            TextView textView = linearLayout.findViewById(R.id.textViewCell);
                            textView.setText(value);
                            TableRow tableRow = (TableRow) LayoutInflater.from(requireContext()).inflate(R.layout.queries_row_layout, null);
                            for (String value : row) {
                                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(requireContext()).inflate(R.layout.queries_cell_layout, null);
                                TextView textView = linearLayout.findViewById(R.id.textViewCell);
                                textView.setText(value);
                                textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.queries_cell_border));
                                tableRow.addView(linearLayout);
                            }
                            tableLayout.addView(tableRow);
                        }*/
                    }
                });
            }
        });
        httpCall.start();
    }
}