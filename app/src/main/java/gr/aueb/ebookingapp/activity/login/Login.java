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

import gr.aueb.ebookingapp.activity.register.Register;
import gr.aueb.ebookingapp.dao.MemoryGuestDAO;
import gr.aueb.ebookingapp.R;
import gr.aueb.ebookingapp.activity.homepage.Homepage;

public class Login extends AppCompatActivity {

    private ImageView logoImage;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button signUpButton;
    private Button registerButton;
    private MemoryGuestDAO guestDAO;
    private static boolean isDAOInitialized;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        // Initialize the views
        logoImage = findViewById(R.id.imageView);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        signUpButton = findViewById(R.id.button2);
        registerButton = findViewById(R.id.register);

        // Set up the sign-up button click listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignIn();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){ goToRegister(); };
        });

    }

    public void handleSignIn()
    {
        /* Create and initialize guestDAO if not already initialized*/
        if (!isDAOInitialized)
        {
            guestDAO = new MemoryGuestDAO();
            guestDAO.initialize();
            isDAOInitialized = true;
        }
        else
        {
            guestDAO = new MemoryGuestDAO();
        }
        /* Get the text from the EditText fields and trim it so that no leading or trailing whitespace remains */
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
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

    public void goToRegister()
    {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }


    protected void onDestroy()
    {
        super.onDestroy();
        signUpButton = findViewById(R.id.button2);
        signUpButton.setOnClickListener(null);
        registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(null);
    }
}