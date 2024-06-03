package gr.aueb.ebookingapp.activity.filteredrooms;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import gr.aueb.ebookingapp.R;
import gr.aueb.ebookingapp.adapter.FilteredRoomsAdapter;
import gr.aueb.ebookingapp.activity.Thread.RequestHandler;
import src.backend.lodging.Lodging;
import src.shared.ClientActions;

public class FilteredRooms extends AppCompatActivity {

    private ListView listView;
    private FilteredRoomsAdapter adapter;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            ArrayList<Lodging> lodges = (ArrayList<Lodging>) msg.obj;
            adapter = new FilteredRoomsAdapter(FilteredRooms.this, lodges);
            listView.setAdapter(adapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filtered_rooms);

        listView = findViewById(R.id.listView);

        HashMap<String, Object> filters = (HashMap<String, Object>) getIntent().getSerializableExtra("filters");

        RequestHandler requestHandler = new RequestHandler(this, ClientActions.FILTER, null, handler);
        requestHandler.setFilters(filters);

        Thread filterThread = new Thread(requestHandler);
        filterThread.start();
    }
}
