package gr.aueb.ebookingapp.activity.selectedlodge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import gr.aueb.ebookingapp.R;
import gr.aueb.ebookingapp.activity.book.Book;
import gr.aueb.ebookingapp.activity.homepage.Homepage;
import gr.aueb.ebookingapp.activity.rate.Rate;
import src.backend.lodging.Lodging;

public class SelectedLodge extends AppCompatActivity {

    private ImageView lodgeImageView;
    private TextView lodgeNameTextView;
    private TextView lodgeStarsTextView;
    private TextView lodgePriceTextView;
    private TextView noOfPersonsTextView;
    private TextView locationTextView;
    private TextView reviewsTextView;

    private Button goBack, rate, book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.selectedlodge);

        lodgeImageView = findViewById(R.id.selectedlodge);
        lodgeNameTextView = findViewById(R.id.textLodgeName);
        lodgeStarsTextView = findViewById(R.id.textStars);
        lodgePriceTextView = findViewById(R.id.textPrice);
        noOfPersonsTextView = findViewById(R.id.textNoOfPersons);
        locationTextView = findViewById(R.id.textLocation);
        reviewsTextView = findViewById(R.id.textReviews);
        goBack = findViewById(R.id.goBack);
        rate = findViewById(R.id.rate);
        book = findViewById(R.id.book);

        Lodging lodging = (Lodging) getIntent().getSerializableExtra("lodging");

        if (lodging != null) {
            lodgeNameTextView.setText(lodging.getRoomName());
            lodgeStarsTextView.setText(String.valueOf(lodging.getStars()));
            lodgePriceTextView.setText(String.valueOf(lodging.getPrice()));
            noOfPersonsTextView.setText(String.valueOf(lodging.getNumberOfPersons()));
            locationTextView.setText(lodging.getArea());
            reviewsTextView.setText(String.valueOf(lodging.getNumberOfReviews()));

            int imageResId = getResources().getIdentifier(lodging.getRoomImage(), "drawable", getPackageName());
            lodgeImageView.setImageResource(imageResId);
        }

        goBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToRate();
            }
        });

        book.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToBook();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Retrieve the rating submission state for the current user
        SharedPreferences sharedPreferences = getSharedPreferences("RatingState", MODE_PRIVATE);
        boolean rated = sharedPreferences.getBoolean(getIntent().getStringExtra("username") + "_rated", false);

        // Disable the "Rate" button if the user has already rated
        if (rated) {
            disableRateButton();
        } else {
            // Enable the "Rate" button if the user hasn't rated
            rate.setEnabled(true);
            rate.setBackgroundColor(getResources().getColor(R.color.color1)); // Set background to the original color
        }
    }

    private void disableRateButton() {
        rate.setEnabled(false);
        rate.setBackgroundColor(getResources().getColor(R.color.grey));
    }

    private void goToBook()
    {
        Intent intent = new Intent(this, Book.class);
        intent.putExtra("username", this.getIntent().getStringExtra("username"));
        startActivity(intent);
    }

    private void goToRate()
    {
        Intent intent = new Intent(this, Rate.class);
        intent.putExtra("username", this.getIntent().getStringExtra("username"));
        intent.putExtra("lodgeName",lodgeNameTextView.getText().toString());
        startActivity(intent);
    }

    protected void OnDestroy()
    {
        super.onDestroy();
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(null);
        rate = findViewById(R.id.rate);
        rate.setOnClickListener(null);
        book = findViewById(R.id.book);
        book.setOnClickListener(null);
    }
}