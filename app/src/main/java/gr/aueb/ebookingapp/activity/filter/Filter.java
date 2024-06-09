package gr.aueb.ebookingapp.activity.filter;

import static src.shared.ClientActions.FILTER;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import gr.aueb.ebookingapp.R;
import gr.aueb.ebookingapp.activity.Thread.RequestHandler;
import gr.aueb.ebookingapp.activity.book.Book;
import gr.aueb.ebookingapp.activity.filteredrooms.FilteredRooms;
import src.backend.lodging.Lodging;

public class Filter extends AppCompatActivity {

    private HashMap<String, Object> filters;
    private EditText editTextStars;
    private EditText editTextPrice;
    private EditText editTextPeople;
    private EditText editTextLocation;
    private EditText editTextDateFrom;
    private EditText editTextDateTo;

    private Button filterButton;
    private ArrayList<Lodging> lodges;

    private Calendar calendar;

    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            /* Update the UI with the lodge data */
            lodges = (ArrayList<Lodging>) msg.obj;
            if (lodges == null)
            {
                runOnUiThread(() -> Toast.makeText(Filter.this, "No rooms found...", Toast.LENGTH_LONG).show());
            }
            else
            {
                Intent intent = new Intent(Filter.this, FilteredRooms.class);
                intent.putExtra("username", getUsername());
                intent.putExtra("lodges", lodges);
                startActivity(intent);
            }

            /* Clear filters */
            filters.clear();

        }
    };

    private String username;

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);
        filters = new HashMap<>();
        setUsername(this.getIntent().getStringExtra("username"));

        editTextStars = findViewById(R.id.editTextStars);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextPeople = findViewById(R.id.editTextPeople);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextDateFrom = findViewById(R.id.editTextDateFrom);
        editTextDateTo = findViewById(R.id.editTextDateTo);
        filterButton = findViewById(R.id.filter);

        calendar = Calendar.getInstance();

        editTextDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(editTextDateFrom);
            }
        });

        editTextDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(editTextDateTo);
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilters();
            }
        });
    }

    private String formatDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void showDatePickerDialog(EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Filter.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String selectedDate = dateFormat.format(calendar.getTime());
                        editText.setText(selectedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void applyFilters() {

        String stars = editTextStars.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();
        String people = editTextPeople.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String dateFrom = editTextDateFrom.getText().toString().trim();
        String dateTo = editTextDateTo.getText().toString().trim();

        if ((dateFrom.isEmpty() && !dateTo.isEmpty()) || (!dateFrom.isEmpty() && dateTo.isEmpty())) {
            Toast.makeText(Filter.this, "Please fill both dates in if you wish to search for availability!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!TextUtils.isEmpty(stars)) {
            try {
                int starsValue = Integer.parseInt(stars);
                if (starsValue >= 1 && starsValue <= 5) {
                    filters.put("stars", starsValue);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(price)) {
            try {
                int roomPrice = Integer.parseInt(price);
                if (roomPrice > 0) {
                    filters.put("roomPrice", roomPrice);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(people)) {
            try {
                int peopleValue = Integer.parseInt(people);
                if (peopleValue > 0) {
                    filters.put("noOfPersons", peopleValue);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(location)) {
            filters.put("area", location);
        }

        if (!TextUtils.isEmpty(dateFrom) || !TextUtils.isEmpty(dateTo)) {

            /* Create a HashMap to store the 'From' and 'To' dates in */
            HashMap<String, String> dateMap = new HashMap<String, String>();
            dateMap.put("dateFrom", dateFrom);
            dateMap.put("dateTo", dateTo);

            filters.put("date", dateMap);
        }

        RequestHandler runnable = new RequestHandler(this,FILTER,this.getUsername(), handler);
        runnable.setFilters(filters);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    protected void OnDestroy()
    {
        super.onDestroy();
        filterButton = findViewById(R.id.filter);
        filterButton.setOnClickListener(null);
    }
}
