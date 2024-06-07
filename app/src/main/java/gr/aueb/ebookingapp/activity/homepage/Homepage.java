package gr.aueb.ebookingapp.activity.homepage;

import static src.shared.ClientActions.HOMEPAGE_LODGES;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.os.Handler;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import gr.aueb.ebookingapp.activity.Thread.RequestHandler;
import gr.aueb.ebookingapp.activity.filter.Filter;
import gr.aueb.ebookingapp.activity.selectedlodge.SelectedLodge;
import gr.aueb.ebookingapp.adapter.CollectionHomepageAdapter;
import gr.aueb.ebookingapp.dao.MemoryGuestDAO;
import gr.aueb.ebookingapp.R;
import src.backend.lodging.Lodging;

public class Homepage extends AppCompatActivity {

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

    public ArrayList<Lodging> getLodgingList()
    {
        return lodgingList;
    }
    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            ArrayList<Lodging> lodges= (ArrayList<Lodging>) msg.obj;
            if (lodges == null)
            {
                listView.setVisibility(View.GONE);
                Toast.makeText(Homepage.this, "No rooms found...", Toast.LENGTH_LONG).show();
            }
            else
            {
                setLodgingList(lodges);
                adapter = new CollectionHomepageAdapter(getLodgingList(), Homepage.this);

                runOnUiThread(() -> listView.setAdapter(adapter));

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Lodging lodging = (Lodging) parent.getItemAtPosition(position);
                        goToNewActivity(lodging);
                    }
                });
            }
        }
    };

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
        setContentView(R.layout.homepage);

        // Initialize Views
        logoImage = findViewById(R.id.imageView);

        filterButton = findViewById(R.id.filterButton);

        listView = findViewById(R.id.homePageListView);

        setUsername(this.getIntent().getStringExtra("username"));

        Thread mythread = new Thread(new RequestHandler(this, HOMEPAGE_LODGES, getUsername(), handler));
        mythread.start();

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilter();
            }
        });


    }

    private void goToNewActivity(Lodging lodging)
    {
        Intent intent = new Intent(Homepage.this, SelectedLodge.class);
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
