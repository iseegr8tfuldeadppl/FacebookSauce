package scuffedbots.pagehelpertools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("HH", "helo");
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        callbackManager = CallbackManager.Factory.create();

        txtFbLogin = findViewById(R.id.login_button);

        txtFbLogin.setPermissions(Arrays.asList(PAGES_MESSAGING));
        txtFbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mAccessToken = loginResult.getAccessToken();
                Log.i("HH", "token "  +mAccessToken.getToken());
                Log.i("HH", "expiry "  +mAccessToken.getExpires());
                Log.i("HH", "expiry 2 "  +mAccessToken.getDataAccessExpirationTime());
                Log.i("HH", "ah " + mAccessToken);
                getUserProfile(mAccessToken);
            }@Override
            public void onCancel() {

            }@Override
            public void onError(FacebookException error) {
                Log.i("HH", String.valueOf(error));

            }
        });
    }

    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            //You can fetch user info like this…
                            //object.getJSONObject(“picture”).
                            Log.i("HH", "yes " + response);
                            Log.i("HH", "yes " + object);
                            //Log.i("HH", "yes " + object.getString("email"));
                            //Log.i("HH", "yes " + object.getString("name"));
                            //Log.i("HH", "yes " + object.getString("id"));
                            Log.i("HH", "yes " + object.getJSONObject("data").getString("url"));
                        } catch (JSONException e) {
                            Log.i("HH", e.toString());
                        }}
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        request.executeAsync();
    }@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode,  data);
    }
}
