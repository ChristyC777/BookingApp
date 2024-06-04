package gr.aueb.ebookingapp.activity.filteredrooms;

import static src.shared.ClientActions.HOMEPAGE_LODGES;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import gr.aueb.ebookingapp.R;
import gr.aueb.ebookingapp.activity.filter.Filter;
import gr.aueb.ebookingapp.activity.homepage.Homepage;
import gr.aueb.ebookingapp.activity.selectedlodge.SelectedLodge;
import gr.aueb.ebookingapp.adapter.CollectionHomepageAdapter;
import gr.aueb.ebookingapp.adapter.FilteredRoomsAdapter;
import gr.aueb.ebookingapp.activity.Thread.RequestHandler;
import gr.aueb.ebookingapp.dao.MemoryGuestDAO;
import src.backend.lodging.Lodging;
import src.shared.ClientActions;

public class FilteredRooms extends AppCompatActivity {

    private ImageView logoImage;
    private ListView listView;
    private Button filterButton;
    private Button goBack;
    private MemoryGuestDAO guestDAO;
    private String username;

    private ArrayList<Lodging> lodgingList;

    private CollectionHomepageAdapter adapter;

    public void setLodgingList(ArrayList<Lodging> lodgingList)
    {
        this.lodgingList = lodgingList;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.filtered_rooms);

        // Initialize Views
        logoImage = findViewById(R.id.imageView);

        filterButton = findViewById(R.id.filterButton);

        listView = findViewById(R.id.homePageList);

        setUsername(this.getIntent().getStringExtra("username"));
        ArrayList<Lodging> lodges = (ArrayList<Lodging>) this.getIntent().getSerializableExtra("lodges");

        adapter = new CollectionHomepageAdapter(lodges, FilteredRooms.this);

        runOnUiThread(() -> listView.setAdapter(adapter));

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilter();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Lodging lodging = (Lodging) parent.getItemAtPosition(position);
                goToNewActivity(lodging);
            }
        });

    }

    private void goToNewActivity(Lodging lodging)
    {
        Intent intent = new Intent(FilteredRooms.this, SelectedLodge.class);
        intent.putExtra("username", getUsername());
        intent.putExtra("lodging", lodging);
        startActivity(intent);
    }

    public void goToFilter()
    {
        Intent intent = new Intent(this, Filter.class);
        intent.putExtra("username", this.getIntent().getStringExtra("username"));
        startActivity(intent);
    }

    protected void OnDestroy()
    {
        super.onDestroy();
        filterButton = findViewById(R.id.filterButton);
        filterButton.setOnClickListener(null);
    }
}
