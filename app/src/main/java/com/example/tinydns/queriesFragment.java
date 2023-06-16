package com.example.tinydns;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.*;
import com.google.gson.Gson;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link queriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class queriesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String ServerURL = "https://172.17.0.2:5000";
    private static final String TAG = "tinyDNS queriesFragment";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public queriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment queriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static queriesFragment newInstance(String param1, String param2) {
        queriesFragment fragment = new queriesFragment();
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
        View view = inflater.inflate(R.layout.fragment_queries, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: starting ");
        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
        // Http Connection instance.
        tinyHttpRequest tHttp = new tinyHttpRequest(ServerURL);

//        // Create a ListeningExecutorService
//        ListeningExecutorService executorService = MoreExecutors.newDirectExecutorService();
//
//        // Create a SettableFuture
//        SettableFuture<String> futureResult = SettableFuture.create();
//
//        // Execute the task in a separate thread
//        executorService.submit(() -> {
//            // Perform the task
//
//            String result = tHttp.makeHttpRequest("/queries_raw");
//            Log.d(TAG, "onViewCreated: inside submit" + result);
//            // Set the result on the future
//            futureResult.set(result);
//        });
//        Futures.addCallback(futureResult, new FutureCallback<String>() {
//            @Override
//            public void onSuccess(@Nullable String result) {
//                // Convert from String
//                // Parse the String into a List<List<String>>
//                Type type = new TypeToken<List<List<String>>>() {}.getType();
//                List<List<String>> tContent = new Gson().fromJson(result, type);
//                Log.d(TAG, "onSuccess: " + tContent);
//                // Handle the result
//                System.out.println("Result: " + result);
//                for (List<String> row : tContent) {
//                    TableRow tableRow = (TableRow) LayoutInflater.from(requireContext()).inflate(R.layout.queries_row_layout, null);
//                    for (String value : row) {
//                        TextView textView = (TextView) LayoutInflater.from(requireContext()).inflate(R.layout.queries_cell_layout, null);
//                        textView.setText(value);
//                        textView.setPadding(8, 8, 8, 8);
//                        tableRow.addView(textView);
//                    }
//                    tableLayout.addView(tableRow);
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                // Handle the failure
//                throwable.printStackTrace();
//            }
//        }, executorService);

        Thread httpCall = new Thread(new Runnable() {
            @Override
            public void run() {
                String newresponse = tHttp.makeHttpRequest("/queries_raw");
                Log.d(TAG, "response chart " + newresponse);
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
}

