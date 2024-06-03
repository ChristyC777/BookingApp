package gr.aueb.ebookingapp.activity.homepage;

import static src.shared.ClientActions.HOMEPAGE_LODGES;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.os.Handler;
import java.util.ArrayList;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import gr.aueb.ebookingapp.activity.Thread.RequestHandler;
import gr.aueb.ebookingapp.activity.filter.Filter;
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

    private CollectionHomepageAdapter adapter;

    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            // Update the UI with the lodges data
            ArrayList<Lodging> lodges = (ArrayList<Lodging>) msg.obj;
            adapter = new CollectionHomepageAdapter(lodges, Homepage.this);
            listView.setAdapter(adapter);
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

        listView = findViewById(R.id.listView);

        setUsername(this.getIntent().getStringExtra("username"));

        Thread mythread = new Thread(new RequestHandler(this, HOMEPAGE_LODGES, this.getIntent().getStringExtra("username"),handler));
        mythread.start();


        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilter();
            }
        });

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
