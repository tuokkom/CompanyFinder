package com.tuokko.companyfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CompanyListActivity extends AppCompatActivity {
    private static final String CLASS_NAME = "CompanyListActivity";

    private int mDay;
    private int mMonth;
    private int mYear;

    private SimpleAdapter mAdapter;
    private ArrayList<Map<String, String>> mCompanies = new ArrayList<>();

    private ArrayList<JSONObject> mCompanyObjects = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_list);

        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setDisplayShowHomeEnabled(true);
        }

        mCompanyObjects = new ArrayList<>();

        Intent intent = getIntent();
        mDay = intent.getIntExtra("day", 0);
        mMonth = intent.getIntExtra("month", 0);
        mYear = intent.getIntExtra("year", 0);

        final TextView dateText = findViewById(R.id.currentlyChosenDate);
        dateText.setText(getResources().getString(R.string.companies_registered_at) + mDay + "-" + mMonth + "-" + mYear);

        setupCompanyListView();
        getCompanies();
    }

    /**
     * Change the date button clicked handler
     *
     * @param view View
     */
    public void onChooseAnotherDate(View view) {
        finish();
    }

    /**
     * Setup of the ListView containing the company names and business ids
     */
    private void setupCompanyListView() {
        final ListView companies = findViewById(R.id.companyListView);
        mAdapter = new SimpleAdapter(this,
                mCompanies, android.R.layout.simple_list_item_2,
                new String[] {Constants.NAME, Constants.BUSINESS_ID},
                new int[] {android.R.id.text1, android.R.id.text2});
        companies.setAdapter(mAdapter);
        companies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showCompanyDetails(i);
            }
        });

    }

    /**
     * Make a GET request to avoindata.fi to get all the companies founded at a given date
     */
    private void getCompanies() {
        final String METHOD_NAME = "getCompanies";

        final TextView responseText = findViewById(R.id.responseData);
        responseText.setText(getResources().getString(R.string.loading_companies));

        YtjRequest requestUrl = new YtjRequest();
        requestUrl.setDate(mDay, mMonth, mYear);
        Log.d(CLASS_NAME, METHOD_NAME, "Request url is: " + requestUrl.getRequestUrl());

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = requestUrl.getRequestUrl();

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handleResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(CLASS_NAME, METHOD_NAME, "Error: " + error.toString());
                responseText.setText(getResources().getString(R.string.getting_companies_failed));
            }
        });

        queue.add(request);
    }

    /**
     * Handle the response to the GET request to avoindata.fi
     *
     * @param response Response content
     */
    private void handleResponse(String response) {
        final String METHOD_NAME = "handleResponse";

        final TextView responseText = findViewById(R.id.responseData);
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray companyArray = jsonResponse.optJSONArray(Constants.KEY_RESULTS);
            for (int i = 0; i < companyArray.length(); i++) {
                JSONObject companyObject = (JSONObject) companyArray.get(i);
                mCompanyObjects.add(companyObject);
                addCompanyToListView(companyObject);
            }
        } catch (JSONException e) {
            responseText.setText(getResources().getString(R.string.getting_companies_failed));
            e.printStackTrace();
        }

        responseText.setVisibility(View.INVISIBLE);
        showSortBySpinner();
        //responseText.setText("Response is: "+ response.substring(0,500));
    }

    /**
     * Adds a company to the ListView. The name and the business id of the company are shown
     *
     * @param companyObject Object containing at least the name and the business id of the company
     */
    private void addCompanyToListView(JSONObject companyObject) {
        final String METHOD_NAME = "addCompanyToListView";
        Log.d(CLASS_NAME, METHOD_NAME, "Company object: " + companyObject.toString());

        try {
            Map<String, String> companyMap = new HashMap<String, String>(2);
            companyMap.put("name", companyObject.getString("name"));
            companyMap.put("businessId", companyObject.getString("businessId"));

            mCompanies.add(companyMap);
            Log.d(CLASS_NAME, METHOD_NAME, "Name of the company: " + companyObject.getString("name"));
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setup the spinner that has the sorting options
     */
    private void showSortBySpinner() {
        final String METHOD_NAME = "showSortBySpinner";
        Log.d(CLASS_NAME, METHOD_NAME, "Setup of the Sort-by spinner");

        Spinner sortBySpinner = findViewById(R.id.sortBySpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(spinnerAdapter);
        sortBySpinner.setVisibility(View.VISIBLE);

        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String METHOD_NAME = "onItemSelected";
                Log.d(CLASS_NAME, METHOD_NAME, "New item selected, id: " + id);
                sortCompanies((int) id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                final String METHOD_NAME = "onNothingSelected";
                Log.d(CLASS_NAME, METHOD_NAME, "Nothing is selected");
            }
        });
    }

    /**
     * Sort the companies based on their Names or on their Business IDs
     *
     * @param id
     */
    private void sortCompanies(int id) {
        final String METHOD_NAME = "sortCompanies";
        switch (id) {
            case 1:
                Log.d(CLASS_NAME, METHOD_NAME, "Sorting results by Name");
                mCompanies.sort(new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> o1, Map<String, String> o2) {
                        String firstVal = o1.get(Constants.NAME);
                        String secondVal = o2.get(Constants.NAME);
                        return firstVal.compareTo(secondVal);
                    }
                });
                mAdapter.notifyDataSetChanged();
                break;
            case 2:
                Log.d(CLASS_NAME, METHOD_NAME, "Sorting results by Business ID");
                mCompanies.sort(new Comparator<Map<String, String>>() {
                    @Override
                    public int compare(Map<String, String> o1, Map<String, String> o2) {
                        String firstVal = o1.get(Constants.BUSINESS_ID);
                        String secondVal = o2.get(Constants.BUSINESS_ID);
                        return firstVal.compareTo(secondVal);
                    }
                });
                mAdapter.notifyDataSetChanged();
                break;
            default:
                Log.d(CLASS_NAME, METHOD_NAME, "Unknown sort id");
        }
    }

    /**
     * Show more detailed information about the company
     *
     * @param i The index of the company
     */
    private void showCompanyDetails(int i) {
        final String METHOD_NAME = "showCompanyDetails";
        Log.d(CLASS_NAME, METHOD_NAME, "A company clicked, name: " + mCompanies.get(i).get(Constants.NAME));

        String businessId = mCompanies.get(i).get(Constants.BUSINESS_ID);
        for (JSONObject company : mCompanyObjects) {
            try {
                if (company.getString(Constants.BUSINESS_ID).equals(businessId)) {
                    Log.d(CLASS_NAME, METHOD_NAME, "Company object: " + company.toString());
                    String detailsUrl = company.getString(Constants.DETAILS_URL);
                    Intent intent = new Intent(this, CompanyDetailsActivity.class);
                    intent.putExtra(Constants.DETAILS_URL, detailsUrl);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
