package gr.aueb.ebookingapp.activity.book;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import gr.aueb.ebookingapp.R;
import gr.aueb.ebookingapp.activity.Thread.RequestHandler;
import gr.aueb.ebookingapp.dao.MemoryGuestDAO;
import src.backend.lodging.Lodging;
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
    private Calendar calendar;
    private TextView daterangeTextView;

    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            // Update the UI with the lodges data
            String message = (String) msg.obj;
            if(message.contains("successfully submitted"))
            {
                runOnUiThread(() -> Toast.makeText(Book.this, "Successful booking!", Toast.LENGTH_SHORT).show());
            }
            else
            {
                runOnUiThread(() -> Toast.makeText(Book.this, "Booking failed...", Toast.LENGTH_SHORT).show());
            }
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
        daterangeTextView = findViewById(R.id.daterange);

        setUsername(getIntent().getStringExtra("username"));
        Lodging lodging = (Lodging) getIntent().getSerializableExtra("lodging");

        Calendar from = lodging.getDateRange().getFrom();
        Calendar to = lodging.getDateRange().getTo();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String fromInput = formatter.format(from.getTime());
        String toInput = formatter.format(to.getTime());

        String daterange = fromInput + "-" + toInput;
        daterangeTextView.setText(daterange);

        // Initialize the calendar instance
        calendar = Calendar.getInstance();

        // Set up the click listeners for the EditTexts to show the DatePickerDialog
        checkInEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(checkInEditText);
            }
        });

        checkOutEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(checkOutEditText);
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCheckInDate = checkInEditText.getText().toString();
                selectedCheckOutDate = checkOutEditText.getText().toString();

                if (selectedCheckInDate.isEmpty() || selectedCheckOutDate.isEmpty()) {
                    Toast.makeText(Book.this, "Please enter both check-in and check-out dates", Toast.LENGTH_SHORT).show();
                    return;
                }

                bookLodge(lodging.getRoomName());
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

    // Method to show the DatePickerDialog and set the selected date to the EditText
    private void showDatePickerDialog(EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Book.this,
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
