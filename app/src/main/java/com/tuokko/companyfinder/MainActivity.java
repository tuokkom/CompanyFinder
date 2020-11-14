package com.tuokko.companyfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Disable future dates
        DatePicker datePicker = findViewById(R.id.datePicker);
        datePicker.setMaxDate(System.currentTimeMillis());
    }

    /**
     * Handle the button click event when the date is chosen
     *
     * @param view The DatePicker view
     */
    public void onDateChosenClicked(View view) {
        Intent intent = new Intent(this, CompanyListActivity.class);
        DatePicker dateChosen = (DatePicker) findViewById(R.id.datePicker);
        intent.putExtra("day", dateChosen.getDayOfMonth());
        //Adding 1 to month because months start at 0
        intent.putExtra("month", dateChosen.getMonth() + 1);
        intent.putExtra("year", dateChosen.getYear());

        startActivity(intent);
    }

}
