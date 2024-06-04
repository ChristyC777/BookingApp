package gr.aueb.ebookingapp.activity.rate;

import static src.shared.ClientActions.FILTER;
import static src.shared.ClientActions.RATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import gr.aueb.ebookingapp.R;
import gr.aueb.ebookingapp.activity.Thread.RequestHandler;
import gr.aueb.ebookingapp.activity.filter.Filter;
import gr.aueb.ebookingapp.activity.filteredrooms.FilteredRooms;
import gr.aueb.ebookingapp.dao.MemoryGuestDAO;
import src.backend.lodging.Lodging;

public class Rate extends AppCompatActivity {
    private ImageView[] stars = new ImageView[5];
    private int currentRating = 0;

    private String username;

    private String lodgeName;

    private MemoryGuestDAO guestDAO;
    private static boolean isInitialized;

    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            // Update the UI with the lodges data
            String message = (String) msg.obj;
            if (message.equals("Successfully added rating!"))
            {
                runOnUiThread(() ->Toast.makeText(Rate.this, message, Toast.LENGTH_SHORT).show());
                guestDAO.findGuest(getUsername()).addRatings(lodgeName, currentRating);
                SharedPreferences sharedPreferences = getSharedPreferences("RatingState", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getUsername() + "_rated", true); // Store true if the user has rated
                editor.apply();
                finish();
            }
            else
            {
                runOnUiThread(() -> Toast.makeText(Rate.this, message, Toast.LENGTH_SHORT).show());
                finish();
            }
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate);
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

        stars[0] = findViewById(R.id.star1);
        stars[1] = findViewById(R.id.star2);
        stars[2] = findViewById(R.id.star3);
        stars[3] = findViewById(R.id.star4);
        stars[4] = findViewById(R.id.star5);

        this.setUsername(this.getIntent().getStringExtra("username"));
        lodgeName = this.getIntent().getStringExtra("lodgeName");

        for (ImageView star : stars) {
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int rating = Integer.parseInt(view.getTag().toString());
                    setRating(rating);
                }
            });
        }
    }

    private void setRating(int rating) {
        currentRating = rating;
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.ic_star_filled);
            } else {
                stars[i].setImageResource(R.drawable.ic_star_border);
            }
        }

        RequestHandler requestHandler = new RequestHandler(this,RATE,this.getIntent().getStringExtra("username"), handler);
        requestHandler.setGuestDAO(guestDAO);
        requestHandler.setRating(rating);
        requestHandler.setLodgeName(lodgeName);
        Thread thread = new Thread(requestHandler);
        thread.start();
    }
}
