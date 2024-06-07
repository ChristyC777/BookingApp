package gr.aueb.ebookingapp.activity.selectedlodge;

import static src.shared.ClientActions.FILTER;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import gr.aueb.ebookingapp.R;
import gr.aueb.ebookingapp.activity.Thread.RequestHandler;
import gr.aueb.ebookingapp.activity.book.Book;
import gr.aueb.ebookingapp.activity.rate.Rate;
import gr.aueb.ebookingapp.dao.MemoryGuestDAO;
import src.backend.lodging.Lodging;

public class SelectedLodge extends AppCompatActivity {

    private String username;
    private ImageView lodgeImageView;
    private TextView lodgeNameTextView;
    private TextView lodgeStarsTextView;
    private TextView lodgePriceTextView;
    private TextView noOfPersonsTextView;
    private TextView locationTextView;
    private TextView reviewsTextView;

    private static boolean isInitialized;
    private static boolean bookingInitialized;

    private String checkIn;
    private String checkOut;
    private Lodging lodging;
    private MemoryGuestDAO guestDAO;
    private ActivityResultLauncher<Intent> rateActivityResultLauncher;
    private Button goBack, rate, book;

    private String getUsername()
    {
        return username;
    }

    private void setUsername(String username)
    {
        this.username = username;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            Toast.makeText(SelectedLodge.this, message, Toast.LENGTH_LONG).show();
        }
    };

    public Lodging getLodging() {
        return lodging;
    }

    public void setLodging(Lodging lodging) {
        this.lodging = lodging;
    }

    public Handler ratehandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg) {
            ArrayList<Lodging>  message = (ArrayList<Lodging> ) msg.obj;
            Lodging lodging = message.get(0);
            getIntent().putExtra("username",getUsername());
            getIntent().putExtra("lodging", lodging);
            recreate();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.selectedlodge);

        if (!isInitialized)
        {
            guestDAO = new MemoryGuestDAO();
            guestDAO.initialize();
            isInitialized = true;
        }
        else
        {
            guestDAO = new MemoryGuestDAO();
        }

        setUsername(this.getIntent().getStringExtra("username"));

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
        setLodging(lodging);

        boolean rated = guestDAO.findGuest(getUsername()).hasRated(lodging.getRoomName());

        // Disable the "Rate" button if the user has already rated
        if (rated) {
            rate.setEnabled(false);
            rate.setBackgroundColor(getResources().getColor(R.color.grey));
        } else {
            // Enable the "Rate" button if the user hasn't rated
            rate.setEnabled(true);
            rate.setBackgroundColor(getResources().getColor(R.color.color1)); // Set background to the original color
        }

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

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBook();
            }
        });

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

    }

    private void goToBook()
    {
        Intent intent = new Intent(this, Book.class);
        intent.putExtra("username", this.getIntent().getStringExtra("username"));
        intent.putExtra("lodge",getLodging());
        startActivity(intent);
    }


    private void goToRate()
    {
        Intent intent = new Intent(this, Rate.class);
        intent.putExtra("username", this.getIntent().getStringExtra("username"));
        intent.putExtra("lodgeName",lodgeNameTextView.getText().toString());
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        boolean rated = guestDAO.findGuest(getUsername()).hasRated(getLodging().getRoomName());
        if (rated) {
            rate.setEnabled(false);
            rate.setBackgroundColor(getResources().getColor(R.color.grey));
            HashMap<String, Object> filter = new HashMap<String, Object>();
            filter.put("roomName", lodgeNameTextView.getText().toString());
            RequestHandler requestHandler = new RequestHandler(this, FILTER, getUsername(),handler);
            requestHandler.setFilters(filter);
            Thread thread =  new Thread();
            thread.start();
        } else {
            rate.setEnabled(true);
            rate.setBackgroundColor(getResources().getColor(R.color.color1)); // Set background to the original color
        }


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