package scuffedbots.pagehelpertools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import scuffedbots.pagehelpertools.MainActivity.PageData;

public class LoadingPage extends AppCompatActivity {

    private int amount = 0;
    private boolean allOrSome = false;
    private String message;
    private TextView progress;
    private PageData page = new PageData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);

        // Step 1: setup debugger
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        // Step 2: retrieve data
        Intent intent = getIntent();
        if(intent!=null){
            Bundle b = intent.getExtras();
            if (b != null) {
                amount = b.getInt("amount");
                allOrSome = b.getBoolean("allOrSome");
                message = b.getString("message");

                // recover selected page
                String pageString = b.getString("page");
                if(pageString!=null){
                    String[] pageInfo = pageString.split("=");
                    page.name = pageInfo[0];
                    page.id = pageInfo[1];
                    page.access_token = pageInfo[2];
                }

            }
        }

        // Step 3: variables
        progress = findViewById(R.id.progress);

        // Step 4: start progresso
        load_client_page();
    }

    private void load_client_page() {
        /* make the API call */

        // Step 2: give it some fields
        Bundle parameters = new Bundle();
        parameters.putString("fields", "participants,link");
        parameters.putString("access_token", page.access_token);
        parameters.putInt("limit", 499);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final GraphRequest request = new GraphRequest(
                accessToken,
                "/" + page.id + "/conversations",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        Log.i("HH", "Response::" + response);

                    }
                }
        );
        Log.i("HH", "request.getGraphPath() "+ request.getGraphPath());
        Log.i("HH", "request.getHttpMethod() "+ request.getHttpMethod());
        Log.i("HH", "request.getVersion() "+ request.getVersion());
        Log.i("HH", "request.getParameters() "+ request.getParameters());
        Log.i("HH", "request.getParameters() "+ request.getGraphObject());

        // Step 3: execute
        request.executeAsync();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
