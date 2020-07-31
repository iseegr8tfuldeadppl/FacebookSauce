package scuffedbots.pagehelpertools;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // facebook key hash: 47fPpCbYdR2a4KFWUSx1VhlKEQc=

    private LoginButton txtFbLogin;
    private AccessToken mAccessToken;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private static final String PAGES_MESSAGING = "pages_messaging";
    private static final String PAGES_SHOW_LIST = "pages_show_list";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

        txtFbLogin = findViewById(R.id.login_button);

        txtFbLogin.setPermissions(PAGES_MESSAGING, PAGES_SHOW_LIST);
        txtFbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mAccessToken = loginResult.getAccessToken();

                handleFacebookAccessToken(mAccessToken);
            }@Override
            public void onCancel() {

            }@Override
            public void onError(FacebookException error) {
                Log.i("HH", String.valueOf(error));
            }
        });
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.i("HH", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("HH", "signInWithCredential:success");

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            Log.i("HH", "new currentUser " + currentUser);
                            getUserProfile(token);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("HH", "signInWithCredential:failure" + task.getException());
                        }

                        // ...
                    }
                });
    }

    private void getUserProfile(AccessToken currentAccessToken) {
        Log.i("HH", "token "  +mAccessToken.getToken());
        Log.i("HH", "expiry "  +mAccessToken.getExpires());
        Log.i("HH", "expiry 2 "  +mAccessToken.getDataAccessExpirationTime());
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            //You can fetch user info like this…
                            //object.getJSONObject(“picture”).
                            Log.i("HH", "response " + response);
                            Log.i("HH", "object " + object);
                            //Log.i("HH", "yes " + object.getString("email"));
                            //Log.i("HH", "yes " + object.getString("name"));
                            //Log.i("HH", "yes " + object.getString("id"));
                        } catch (Exception e) {
                            Log.i("HH", e.toString());
                        }}
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "accounts,id,name,email,picture.width(200)");
        request.setParameters(parameters); //
        request.executeAsync();
    }

    private FirebaseAuth mAuth;
    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.i("HH", "currentUser " + currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode,  data);
    }
}
