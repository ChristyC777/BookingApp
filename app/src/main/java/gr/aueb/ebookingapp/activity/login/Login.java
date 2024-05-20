package gr.aueb.ebookingapp.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import gr.aueb.ebookingapp.dao.MemoryGuestDAO;
import gr.aueb.ebookingapp.R;
import gr.aueb.ebookingapp.activity.homepage.Homepage;

public class Login extends AppCompatActivity {

    private ImageView backgroundImage;
    private ImageView logoImage;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button signUpButton;

    private MemoryGuestDAO guestDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        guestDAO = new MemoryGuestDAO();
        guestDAO.initialize();

        // Initialize the views
        backgroundImage = findViewById(R.id.background);
        logoImage = findViewById(R.id.imageView);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        signUpButton = findViewById(R.id.button2);

        // Set up the sign-up button click listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });

    }

    public void handleSignUp()
    {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(guestDAO.find(username, password)!=null)
        {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            Intent intent= new Intent(this, Homepage.class);
            intent.putExtra("username", username);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "We couldn't find any user with these credentials", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onDestroy()
    {
        super.onDestroy();
        signUpButton = findViewById(R.id.button2);
        signUpButton.setOnClickListener(null);
    }
}