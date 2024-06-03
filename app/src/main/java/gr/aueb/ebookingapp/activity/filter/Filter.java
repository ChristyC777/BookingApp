package gr.aueb.ebookingapp.activity.filter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import gr.aueb.ebookingapp.R;

public class Filter extends AppCompatActivity {

    private HashMap<String, Object> filters;
    private EditText editTextStars;
    private EditText editTextName;
    private EditText editTextPeople;
    private EditText editTextLocation;
    private Button filterButton;
    private  Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);
        filters = new HashMap<>();

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
                // Handle error if needed
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
                // Handle error if needed
            }
        }

        if (!TextUtils.isEmpty(location)) {
            filters.put("area", location);
        }

        // Use the filters HashMap as needed
        // For example, pass it to another activity or use it in a query
    }

    protected void OnDestroy()
    {
        super.onDestroy();
        filterButton = findViewById(R.id.filter);
        filterButton.setOnClickListener(null);
    }
}
