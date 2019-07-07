package vola.systers.com.android.activities;

import vola.systers.com.android.utils.NetworkUtil;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import android.widget.TextView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import vola.systers.com.android.R;
import vola.systers.com.android.manager.PrefManager;

 /*
  * User can SignIn manually or through social in this activity.
  */

public class SignInActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_GOOGLE_SIGN_IN = 007;
    private static final int RC_FACEBOOK_SIGN_IN=64206;
    ProgressDialog verifyingAuthProgressDialog ;
    private GoogleApiClient mGoogleApiClient;
    CallbackManager callbackManager;
    private CoordinatorLayout coordinatorLayout;
    private static final String TAG = SignInActivity.class.getSimpleName();
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText emailText,passwordText;
    private Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        prefManager = new PrefManager(this);
        verifyingAuthProgressDialog = new ProgressDialog(SignInActivity.this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

         if(! new NetworkUtil().checkConnectivity(this)) {
             Snackbar snackbar = Snackbar
                     .make(coordinatorLayout, "Please Make Sure You are Connected to Internet!", Snackbar.LENGTH_LONG);
             View sbView = snackbar.getView();
             sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
             snackbar.show();
        }

        LoginButton btnFacebookLogin=(LoginButton) findViewById(R.id.btn_fb_sign_in) ;
        Button btnGoogleLogin = (Button) findViewById(R.id.btn_sign_in_google);
        TextView skipLink = (TextView)findViewById(R.id.link_skip);
        TextView signUpLink = (TextView)findViewById(R.id.link_signup);
        btnGoogleLogin.setOnClickListener(this);
        skipLink.setOnClickListener(this);
        signUpLink.setOnClickListener(this);

        emailText= (EditText)findViewById(R.id.input_email) ;
        passwordText= (EditText)findViewById(R.id.input_password);

        login =(Button)findViewById(R.id.btn_login) ;
        login.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(! new NetworkUtil().checkConnectivity(getApplicationContext())) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Please Make Sure You are Connected to Internet!", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                    snackbar.show();
                }
                else
                {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webClientToken))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        callbackManager = CallbackManager.Factory.create();
        btnFacebookLogin.setReadPermissions(Arrays.asList("public_profile", "email"));
        btnFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("Response", response.toString());
                          }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
                launchHomeScreen();
            }

            @Override
            public void onCancel() {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, R.string.cancelled_request, Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                snackbar.show();

            }

            @Override
            public void onError(FacebookException error) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, R.string.error, Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                snackbar.show();
            }
        });
    }

    private void SignIn() {
        final String email = emailText.getText().toString();
        final String pass = passwordText.getText().toString();

        if (!isValidEmail(email)) {
            emailText.setError(getText(R.string.invalid_username));
        }

        if (!isValidPassword(pass)) {
            passwordText.setError(getText(R.string.empty_password));
        }

        if (isValidEmail(email) && isValidPassword(pass))
            signin(emailText, passwordText);
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        if (verifyingAuthProgressDialog != null && verifyingAuthProgressDialog.isShowing()) {
            verifyingAuthProgressDialog.dismiss();
        }
        startActivity(new Intent(SignInActivity.this, MenuActivity.class));
        finish();
    }

    public void signin(EditText email,EditText password) {

            Log.d(TAG, "SignIn");
            verifyingAuthProgressDialog.setIndeterminate(true);
            verifyingAuthProgressDialog.setMessage("Verifying User..");
            verifyingAuthProgressDialog.show();

            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.

                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, R.string.auth_failed, Snackbar.LENGTH_LONG);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                                snackbar.show();
                            }
                            else
                            {
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, R.string.auth_success, Snackbar.LENGTH_LONG);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                                snackbar.show();
                                launchHomeScreen();
                            }
                        }
                    });

    }


    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }


    public void onSkipClicked() {
        Intent intent = new Intent(SignInActivity.this,MenuActivity.class);
        startActivity(intent);
    }

    public void signUp() {
        Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
        startActivity(intent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userToken = user.getUid();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference usersRef = database.getReference("users");
                            String[] name= user.getDisplayName().split(" ");
                            usersRef.child(userToken).child("first_name").setValue(name[0]);
                            usersRef.child(userToken).child("last_name").setValue(name[1]);
                            launchHomeScreen();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("Sign In Fail",  task.getException().getMessage());
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Authentication failed.", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                            snackbar.show();

                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(! new NetworkUtil().checkConnectivity(this)) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Please Make Sure You are Connected to Internet!", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            snackbar.show();
        }
        else {
            int id = v.getId();
            switch (id) {
                case R.id.btn_sign_in_google:
                    googleSignIn();
                    break;
                case R.id.link_skip:
                    onSkipClicked();
                    break;
                case R.id.link_signup:
                    signUp();
                    break;
                case R.id.btn_login:
                    SignIn();
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.i(TAG,"failure");
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Authentication failed.", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                snackbar.show();

            }
        }
        else if( requestCode == RC_FACEBOOK_SIGN_IN){
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 0) {
            return true;
        }
        return false;
    }
}