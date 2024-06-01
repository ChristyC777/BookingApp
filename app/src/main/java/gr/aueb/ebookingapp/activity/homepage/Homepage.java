package gr.aueb.ebookingapp.activity.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import gr.aueb.ebookingapp.activity.homepage.Homepage;
import gr.aueb.ebookingapp.activity.login.Login;
import gr.aueb.ebookingapp.dao.MemoryGuestDAO;
import gr.aueb.ebookingapp.R;
import gr.aueb.ebookingapp.domain.backend.users.Guest;

public class Homepage extends AppCompatActivity {

    private ImageView logoImage;
    private ListView listView;

    private Button filterButton;
    private MemoryGuestDAO guestDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.homepage);

        // Initialize Views
        logoImage = findViewById(R.id.imageView);

        listView = findViewById(R.id.listView);

        filterButton = findViewById(R.id.button1);


    }

    protected void OnDestroy()
    {

    }


}
