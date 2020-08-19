package scuffedbots.pagehelpertools;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import scuffedbots.pagehelpertools.MainActivity.PageData;

public class LoadingPage extends AppCompatActivity {

    private int amount = 0;
    private boolean allOrSome = false;
    private String message;
    private TextView progress, indicator;
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
        indicator = findViewById(R.id.indicator);

        // Step 4: start progresso
        setStep(2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                load_clients();
            }
        }).start();
    }

    List<Client> clients = new ArrayList<>();

    private void load_clients(){
        // Step 2: give it some fields
        final Bundle parameters = new Bundle();
        parameters.putString("fields", "participants,link");
        parameters.putString("access_token", page.access_token);
        parameters.putInt("limit", 499);
        if(!allOrSome){
            if(amount<499)
                parameters.putInt("limit", amount);
        }
        clients = new ArrayList<>();

        load_client_page(parameters);
    }

    private void load_client_page(final Bundle parameters) {
        /* make the API call */
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final GraphRequest request = new GraphRequest(
                accessToken,
                "/" + page.id + "/conversations",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONObject page = response.getJSONObject();

                            JSONArray conversations = page.getJSONArray("data");

                            int limit = conversations.length();
                            if(!allOrSome){
                                if(limit + clients.size()>amount){
                                    limit = amount - clients.size();
                                }
                            }

                            int i=0;
                            while((allOrSome || clients.size()<limit) && i<conversations.length()) {
                                Log.i("HH", "clients " + clients.size() + " limit " + limit);
                                Log.i("HH", "i " + i);
                                final JSONObject conversation = conversations.getJSONObject(i);
                                final JSONObject clientData = conversation.getJSONObject("participants").getJSONArray("data").getJSONObject(0);

                                if(!clientData.getString("name").equals("Facebook User")){
                                    clients.add(new Client(){{
                                        name=clientData.getString("name");
                                        id=clientData.getString("id");
                                        conversation_id=conversation.getString("id");
                                    }});
                                }

                                i ++;
                            }

                            if(!allOrSome) {
                                if (clients.size() >= amount)
                                    page.getJSONObject("justtomakeitcrash");
                            }


                            // update amount of clients indicator
                            new Handler(new Handler.Callback() {
                                @Override
                                public boolean handleMessage(@NonNull Message msg) {
                                    String amount = clients.size() + " Clients loaded";
                                    indicator.setText(amount);
                                    return true;
                                }}).sendEmptyMessage(0);

                            String after = page.getJSONObject("paging").getJSONObject("cursors").getString("after");
                            parameters.putString("after", after);
                            load_client_page(parameters);

                        } catch(JSONException e){
                            Log.i("HH", e + " ");
                            new Handler(new Handler.Callback() {
                                @Override
                                public boolean handleMessage(@NonNull Message msg) {
                                    if(clients.size()>0){
                                        setStep(3);
                                    } else {
                                        indicator.setText("Failure: no clients loaded");
                                    }
                                    return true;
                                }}).sendEmptyMessage(0);


                            message_clients();
                        }

                    }
                }
        );

        // Step 3: execute
        request.executeAsync();
    }

    private void print(Object log){
        Toast.makeText(this, String.valueOf(log), Toast.LENGTH_SHORT).show();
    }

    private void message_clients() {

        final Bundle parameters = new Bundle();
        parameters.putString("access_token", page.access_token);
        parameters.putString("recipient", "{\"id\":\"" + "3352531754819787" + "\"}");
        parameters.putString("message", "{\"text\":\"" + message + "\"}");
        parameters.putString("messaging_type", "MESSAGE_TAG");
        parameters.putString("tag", "ACCOUNT_UPDATE");

        message_single_client(parameters, 0);
    }


    private void message_single_client(final Bundle parameters, final int current_client_index) {
        /* make the API call */

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final GraphRequest request = new GraphRequest(
                accessToken,
                "/" + page.id + "/messages",
                parameters,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        try {
                            JSONObject responso = response.getJSONObject();

                            if(clients.size()-1==current_client_index)
                                responso.getJSONObject("justtomakeitcrash");

                            //parameters.putString("recipient", "{\"id\":\"" + clients.get(current_client_index+1).id + "\"}");
                            message_single_client(parameters, current_client_index + 1);

                        } catch(JSONException ignored){
                            new Handler(new Handler.Callback() {
                                @Override
                                public boolean handleMessage(@NonNull Message msg) {
                                    progress.setText("Done");
                                    return true;
                                }}).sendEmptyMessage(0);
                        }

                    }
                }
        );

        // Step 3: execute
        request.executeAsync();
    }

    private void setStep(int step){
        switch(step){
            case 1:
                progress.setText("1/3 Initialization...");
                break;
            case 2:
                progress.setText("2/3 Loading clients...");
                break;
            case 3:
                progress.setText("3/3 Messaging clients...");
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static class Client {
        public String name;
        public String id;
        public String conversation_id;
    }
}
