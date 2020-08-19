package scuffedbots.pagehelpertools;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    // facebook key hash: 47fPpCbYdR2a4KFWUSx1VhlKEQc=
    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;
    private static final String PAGES_MESSAGING = "pages_messaging";
    private static final String MANAGE_PAGES = "manage_pages";
    private static final String PAGES_SHOW_LIST = "pages_show_list";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Step 1: setup debugger
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        // Preparation: check if user is already logged in
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            loadAccessTokensFromFirebase(currentUser.getUid());
            return;
        }

        // Step 2: setup facebook & firebase variables
        LoginButton login_button = findViewById(R.id.login_button);
        LinearLayout loading = findViewById(R.id.loading);

        // remove loading layout and show main content
        login_button.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);

        callbackManager = CallbackManager.Factory.create();
        give_function_to_login_button(login_button);


    }

    private void give_function_to_login_button(LoginButton txtFbLogin) {
        txtFbLogin.setPermissions(PAGES_MESSAGING, PAGES_SHOW_LIST, MANAGE_PAGES);
        txtFbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken mAccessToken = loginResult.getAccessToken();
                checkAccessTokenWithFirebase(mAccessToken);
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException error) {
                Log.i("HH", String.valueOf(error));
            }
        });
    }

    private void checkAccessTokenWithFirebase(final AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if(currentUser!=null)
                                sendAccessTokenToFirebase(token, currentUser.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("HH", "signInWithCredential:failure" + task.getException());
                        }
                    }
                });
    }

    private void sendAccessTokenToFirebase(final AccessToken token, final String uid) {

        // Step 1: setup graphapi request
        final GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {

                    // Step 1: setup firebase scope
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference dataRef = database.getReference("users/" + uid + "/data/");

                    // Step 2: collect user's data
                    UserData userData = new UserData();
                    userData.name = object.getString("name");
                    userData.id = object.getString("id");
                    userData.access_token = token.getToken();
                    userData.access_token_expiry = token.getExpires().toString();
                    userData.access_token_data_expiry = token.getDataAccessExpirationTime().toString();

                    // Step 3: Collect all permitted pages
                    JSONArray pages = object.getJSONObject("accounts").getJSONArray("data");
                    for (int i = 0; i < pages.length(); i++) {
                        final JSONObject page = pages.getJSONObject(i);
                        userData.pages.add(new PageData(){{
                            access_token = page.getString("access_token");
                            name = page.getString("name");
                            id = page.getString("id");
                        }});
                    }

                    final List<PageData> final_pages = new ArrayList<>(userData.pages);

                    // Step 4: apply data to firebase
                    dataRef.setValue(userData, new DatabaseReference.CompletionListener(){
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            // Step 5: send to another activity
                            if(databaseError==null)
                                exit(final_pages);
                        }
                    });

                } catch (JSONException ignored) {}
            }
        });

        // Step 2: give it some fields
        Bundle parameters = new Bundle();
        parameters.putString("fields", "accounts,id,name,email,picture.width(200)");
        request.setParameters(parameters);

        // Step 3: execute
        request.executeAsync();
    }

    private void loadAccessTokensFromFirebase(String uid){
        // Step 4: load pages from firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataRef = database.getReference("users/" + uid + "/data/");
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                List<PageData> pages = new ArrayList<>();

                for(DataSnapshot page:dataSnapshot.child("pages").getChildren()){
                    final String gotten_access_token = page.child("access_token").getValue(String.class);
                    final String gotten_id = page.child("id").getValue(String.class);
                    final String gotten_name = page.child("name").getValue(String.class);
                    pages.add(new PageData(){{
                        access_token=gotten_access_token;
                        id=gotten_id;
                        name=gotten_name;
                    }});
                }

                exit(pages);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void exit(List<PageData> pages){
        Intent mainMenu = new Intent(this, MainMenu.class);
        Bundle b = new Bundle();

        StringBuilder pagesString = new StringBuilder();
        int pagesLen = pages.size();
        for(int i=0; i<pagesLen; i++){
            pagesString.append(pages.get(i).name)
                    .append("=")
                    .append(pages.get(i).id)
                    .append("=")
                    .append(pages.get(i).access_token);

            if( 0 < i && i < pagesLen - 1)
                pagesString.append("&");
        }
        b.putString("pages",pagesString.toString());
        mainMenu.putExtras(b);
        startActivity(mainMenu);
        finish();
    }

    public static class UserData {
        public String name;
        public String id;
        public String access_token;
        public String access_token_expiry;
        public String access_token_data_expiry;
        public List<PageData> pages = new ArrayList<>();
    }

    public static class PageData {
        public String access_token;
        public String name;
        public String id;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode,  data);
    }
}
