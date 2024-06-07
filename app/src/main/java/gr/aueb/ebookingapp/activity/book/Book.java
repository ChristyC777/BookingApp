package gr.aueb.ebookingapp.activity.book;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import gr.aueb.ebookingapp.R;
import gr.aueb.ebookingapp.activity.Thread.RequestHandler;
import gr.aueb.ebookingapp.dao.MemoryGuestDAO;
import src.shared.ClientActions;

public class Book extends AppCompatActivity {

    private EditText checkInEditText;
    private EditText checkOutEditText;
    private Button selectButton;
    private Button backButton;
    private ImageView imageView2;
    private ImageView imageView5;
    private TextView addDatesTextView;
    private String selectedCheckInDate;
    private String selectedCheckOutDate;
    private MemoryGuestDAO guestDAO;
    private static boolean isInitialized;
    private String username;

    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            // Update the UI with the lodges data
            String message = (String) msg.obj;
            Toast.makeText(Book.this, message, Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    public void setSelectedCheckInDate(String selectedCheckInDate) {
        this.selectedCheckInDate = selectedCheckInDate;
    }

    public void setSelectedCheckOutDate(String selectedCheckOutDate) {
        this.selectedCheckOutDate = selectedCheckOutDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.booking);

        imageView2 = findViewById(R.id.imageView2);
        imageView5 = findViewById(R.id.imageView5);
        addDatesTextView = findViewById(R.id.addDates);
        checkInEditText = findViewById(R.id.checkIn);
        checkOutEditText = findViewById(R.id.checkOut);
        selectButton = findViewById(R.id.selectButton);
        backButton = findViewById(R.id.backButton);

        setUsername(getIntent().getStringExtra("username"));
        String lodgeName = this.getIntent().getStringExtra("lodgeName");

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCheckInDate = checkInEditText.getText().toString();
                selectedCheckOutDate = checkOutEditText.getText().toString();

                if (selectedCheckInDate.isEmpty() || selectedCheckOutDate.isEmpty()) {
                    Toast.makeText(Book.this, "Please enter both check-in and check-out dates", Toast.LENGTH_SHORT).show();
                    return;
                }

                bookLodge(lodgeName);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String formatDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    // Method to handle booking
    private void bookLodge(String lodgeName) {
        RequestHandler requestHandler = new RequestHandler(this, ClientActions.BOOK, getUsername(), handler);
        requestHandler.setDates(getSelectedCheckInDate(), getSelectedCheckOutDate());
        requestHandler.setLodgeName(lodgeName);
        Thread thread = new Thread(requestHandler);
        thread.start();
    }

    private String getUsername() {
        return username;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    // Getter method for selected check-in date
    private String getSelectedCheckInDate() {
        return selectedCheckInDate;
    }

    // Getter method for selected check-out date
    private String getSelectedCheckOutDate() {
        return selectedCheckOutDate;
    }
}
