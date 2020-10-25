package com.tuokko.companyfinder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CompanyDetailsActivity extends AppCompatActivity {
    final static String CLASS_NAME = "CompanyDetailsActivity";

    private SimpleAdapter mAdapter;
    private ArrayList<Map<String, String>> mDetailsList = new ArrayList<Map<String, String>>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_details);

        setupDetailsListView();
        getCompanyDetails();
    }

    /**
     * Handle the Return button clicked event
     *
     * @param v
     */
    public void onReturnToCompanyList(View v) {
        Log.d(CLASS_NAME, "onReturnToCompanyList", "Return button clicked");
        finish();
    }

    /**
     * Setup the ListView containing the details of the company
     */
    private void setupDetailsListView() {
        final String METHOD_NAME = "setupDetailsListView";
        Log.d(CLASS_NAME, METHOD_NAME, "Detail ListView setup");

        final ListView details = (ListView) findViewById(R.id.detailsListView);
        mAdapter = new SimpleAdapter(this,
                mDetailsList, android.R.layout.simple_list_item_2,
                new String[] {Constants.DETAIL_KEY, Constants.DETAIL_VALUE},
                new int[] {android.R.id.text1, android.R.id.text2});
        details.setAdapter(mAdapter);
        details.setVisibility(View.VISIBLE);
    }

    /**
     * Send a new GET request to avoindata.fi to get more details about the company
     */
    private void getCompanyDetails() {
        final String METHOD_NAME = "getCompanyDetails";
        final TextView infoText = (TextView) findViewById(R.id.companyName);
        infoText.setText(getResources().getString(R.string.loading_details));
        infoText.setTextSize(20);

        String url = getIntent().getStringExtra(Constants.DETAILS_URL);
        // Add a 's' after http to make a secure request
        url = url.substring(0, 4) + "s" + url.substring(4);
        Log.d(CLASS_NAME, METHOD_NAME, "Details url: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handleResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                infoText.setText(getResources().getString(R.string.getting_details_failed));
                error.printStackTrace();
            }
        });
        queue.add(request);
    }

    /**
     * Handle the response from avoinddata.fi
     *
     * @param response Contains the whole response from avoindata.fi
     */
    private void handleResponse(String response) {
        final String METHOD_NAME = "handleResponse";
        try {
            JSONObject responseData = new JSONObject(response);
            JSONArray responseArray = responseData.getJSONArray(Constants.KEY_RESULTS);
            Log.d(CLASS_NAME, METHOD_NAME, "Response: " + responseArray.toString());
            addDetails(responseArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set all the content shown on the company details page
     *
     * @param detailArray Contains all the details of the company obtained from avoindata.fi
     */
    private void addDetails(JSONArray detailArray) {
        final String METHOD_NAME = "addDetails";

        try {
            JSONObject detailObject = (JSONObject) detailArray.get(0);

            // Set the name of the company
            String name = detailObject.getString(Constants.NAME);
            TextView nameView = (TextView) findViewById(R.id.companyName);
            nameView.setText(name);
            nameView.setTextSize(22);

            // Set the business ID
            Map<String, String> businessidMap = new HashMap<>(2);
            String businessId = detailObject.getString(Constants.BUSINESS_ID);
            businessidMap.put(Constants.DETAIL_KEY, "Business ID");
            businessidMap.put(Constants.DETAIL_VALUE, businessId);
            mDetailsList.add(businessidMap);

            // Set the date of registration
            Map<String, String> dateMap = new HashMap<>(2);
            String date = detailObject.getString(Constants.REGISTRATION_DATE);
            dateMap.put(Constants.DETAIL_KEY, "Registration date");
            dateMap.put(Constants.DETAIL_VALUE, date);
            mDetailsList.add(dateMap);

            // Set the form of the company
            Map<String, String> companyFormMap = new HashMap<>(2);
            String companyForm = detailObject.getString(Constants.COMPANY_FORM);
            companyFormMap.put(Constants.DETAIL_KEY, "Company Form");
            companyFormMap.put(Constants.DETAIL_VALUE, companyForm);
            mDetailsList.add(companyFormMap);

            JSONObject addressObject = (JSONObject) detailObject.getJSONArray(Constants.ADDRESSES).get(0);

            // Set the city
            Log.d(CLASS_NAME, METHOD_NAME, "Company address is: " + addressObject);
            Map<String, String> cityMap = new HashMap<>(2);
            String city = addressObject.getString(Constants.CITY);
            if (city.length() > 1) {
                city = city.substring(0,1).toUpperCase() + city.substring(1).toLowerCase();
            }
            cityMap.put(Constants.DETAIL_KEY, "City");
            cityMap.put(Constants.DETAIL_VALUE, city);
            mDetailsList.add(cityMap);

            // Set the postal code
            Map<String, String> postCodeMap = new HashMap<>(2);
            String postCode = addressObject.getString(Constants.POST_CODE);
            postCodeMap.put(Constants.DETAIL_KEY, "Postal Code");
            postCodeMap.put(Constants.DETAIL_VALUE, postCode);
            mDetailsList.add(postCodeMap);

            // Set the street address
            Map<String, String> streetMap = new HashMap<>(2);
            String street = addressObject.getString(Constants.STREET);
            streetMap.put(Constants.DETAIL_KEY, "Street");
            streetMap.put(Constants.DETAIL_VALUE, street);
            mDetailsList.add(streetMap);

            // Set the business line in all the available languages
            JSONArray businessLineArray = detailObject.getJSONArray(Constants.BUSINESS_LINES);
            for (int i = 0; i < businessLineArray.length(); i++) {
                JSONObject businessLineObject = (JSONObject) businessLineArray.get(i);
                Map<String, String> businessLineMap = new HashMap<>(2);
                String businessLine = businessLineObject.getString(Constants.NAME);
                businessLineMap.put(Constants.DETAIL_KEY, "Business Line " + (i+1));
                businessLineMap.put(Constants.DETAIL_VALUE, businessLine);
                mDetailsList.add(businessLineMap);
            }

            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
