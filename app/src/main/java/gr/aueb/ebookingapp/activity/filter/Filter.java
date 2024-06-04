package gr.aueb.ebookingapp.activity.filter;

import static src.shared.ClientActions.FILTER;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import gr.aueb.ebookingapp.R;
import gr.aueb.ebookingapp.activity.Thread.RequestHandler;
import gr.aueb.ebookingapp.activity.filteredrooms.FilteredRooms;
import gr.aueb.ebookingapp.activity.homepage.Homepage;
import gr.aueb.ebookingapp.adapter.CollectionHomepageAdapter;
import src.backend.lodging.Lodging;

public class Filter extends AppCompatActivity {

    private HashMap<String, Object> filters;
    private EditText editTextStars;
    private EditText editTextName;
    private EditText editTextPeople;
    private EditText editTextLocation;
    private Button filterButton;
    private ArrayList<Lodging> lodges;

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
        editTextName = findViewById(R.id.editTextName);
        editTextPeople = findViewById(R.id.editTextPeople);
        editTextLocation = findViewById(R.id.editTextLocation);
        filterButton = findViewById(R.id.filter);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilters();
            }
        });
    }

    private void applyFilters() {

        String stars = editTextStars.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String people = editTextPeople.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();

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

        if (!TextUtils.isEmpty(name)) {
            filters.put("roomName", name);
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
