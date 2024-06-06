package gr.aueb.ebookingapp.activity.booking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import gr.aueb.ebookingapp.R;
import gr.aueb.ebookingapp.activity.Thread.RequestHandler;
import gr.aueb.ebookingapp.dao.MemoryGuestDAO;
import src.backend.lodging.Lodging;
import src.shared.ClientActions;

public class Booking extends AppCompatActivity {

    private CalendarView calendarView;
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
            runOnUiThread(() -> Toast.makeText(Booking.this, message, Toast.LENGTH_SHORT).show());
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
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
        setContentView(R.layout.booking);

        calendarView = findViewById(R.id.calendarView);

        setUsername(getIntent().getStringExtra("username"));
        String lodgeName = this.getIntent().getStringExtra("lodgeName");
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                if (selectedCheckInDate == null) {
                    selectedCheckInDate = formatDate(selectedDate);
                    Toast.makeText(Booking.this, "Check-in Date Selected: " + selectedCheckInDate, Toast.LENGTH_SHORT).show();
                } else {
                    selectedCheckOutDate = formatDate(selectedDate);
                    Toast.makeText(Booking.this, "Check-out Date Selected: " + selectedCheckOutDate, Toast.LENGTH_SHORT).show();
                    bookLodge(lodgeName);
                }
                setSelectedCheckInDate(selectedCheckInDate);
                setSelectedCheckOutDate(selectedCheckOutDate);
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

    private void setUsername(String username)
    {
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
