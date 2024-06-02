package gr.aueb.ebookingapp.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import gr.aueb.ebookingapp.activity.homepage.Homepage;
import gr.aueb.ebookingapp.activity.login.Login;
import gr.aueb.ebookingapp.dao.MemoryGuestDAO;
import gr.aueb.ebookingapp.R;
import src.backend.users.Guest;

public class Register extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private Button guestButton;
    private MemoryGuestDAO guestDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        guestDAO = new MemoryGuestDAO();

        // Initialize the views
        usernameEditText = findViewById(R.id.username_reg);
        passwordEditText = findViewById(R.id.Password_reg);
        registerButton = findViewById(R.id.register);
        guestButton = findViewById(R.id.guest);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the EditText fields
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();


                // Validate the inputs
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Register.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                } else if (guestDAO.find(username,password)!=null) {
                    Toast.makeText(Register.this, "This user already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle the registration logic (e.g., save to database, send to server)
                    Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    Guest newGuest = new Guest(username, password);
                    guestDAO.save(newGuest);
                    registerSuccess();
                }

            }
        });

        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Guest newGuest = new Guest();
                guestDAO.save(newGuest);
                Toast.makeText(Register.this, "Your id is: " + newGuest.getUUID() + "Please remember it!",Toast.LENGTH_SHORT).show();
                guestRegistered(newGuest.getUUID());
            }
        });
    }
    public void registerSuccess()
    {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void guestRegistered(String username)
    {
        Intent intent = new Intent(this, Homepage.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }


}
