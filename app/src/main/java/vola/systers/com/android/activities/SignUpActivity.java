package vola.systers.com.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vola.systers.com.android.R;
import vola.systers.com.android.utils.NetworkUtil;

public class SignUpActivity extends AppCompatActivity implements
        View.OnClickListener{

    private EditText editTextEmail,editTextPassword,editTextCnfPassword,editTextFname,editTextLname;
    private Button buttonSignup;
    private TextView skipLink,loginLink;
    private CoordinatorLayout coordinatorLayout;
    private ProgressDialog progressDialog;
    final static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static String userToken="";

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        if(! new NetworkUtil().checkConnectivity(this)) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Please Make Sure You are Connected to Internet!", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            snackbar.show();
        }

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.input_email);
        editTextPassword = (EditText) findViewById(R.id.input_password);
        editTextCnfPassword = (EditText) findViewById(R.id.input_cnf_password);
        editTextFname=(EditText)findViewById(R.id.input_fname);
        editTextLname=(EditText)findViewById(R.id.input_lname);
        buttonSignup = (Button) findViewById(R.id.btn_signup);
        skipLink = (TextView) findViewById(R.id.link_skip);
        loginLink = (TextView) findViewById(R.id.link_login);

        progressDialog = new ProgressDialog(this);
        buttonSignup.setOnClickListener(this);
        skipLink.setOnClickListener(this);
        loginLink.setOnClickListener(this);
    }

    private void createNewUSer(String email, String password, final String fname, final String lname) {

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                userToken = user.getUid();
                            }

                            //update user module.
                            DatabaseReference eventsRef = database.getReference("users");
                            eventsRef.child(userToken).child("first_name").setValue(fname);
                            eventsRef.child(userToken).child("last_name").setValue(lname);

                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Successfully registered!! Please Login to continue.", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                            snackbar.show();

                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                            startActivity(intent);

                        } else {

                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Registration Error", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                            snackbar.show();

                        }
                        progressDialog.dismiss();

                    }
                });
    }

    private void SignUp() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String cnfPassword = editTextCnfPassword.getText().toString().trim();
        String fname = editTextFname.getText().toString().trim();
        String lname = editTextLname.getText().toString().trim();

        if(! new NetworkUtil().checkConnectivity(this)) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Please Make Sure You are Connected to Internet!", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            snackbar.show();
        }

        else {
            if(!isValidEmail(email)) {
                editTextEmail.setError(getText(R.string.invalid_username));
            }
            else if(!isValidPassword(password)) {
                editTextPassword.setError(getText(R.string.invalid_password));
            }
            else if(!cnfPassword.equals(password)){
                editTextCnfPassword.setError(getText(R.string.password_mismatch));
            }
            else{
                progressDialog.setMessage("Registering Please Wait...");
                progressDialog.show();
                createNewUSer(email,password,fname,lname);
            }
        }
    }
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 6) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.link_login:
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                break;
            case R.id.link_skip:
                startActivity(new Intent(SignUpActivity.this, MenuActivity.class));
                break;
            case R.id.btn_signup:
                SignUp();
        }
    }
}
