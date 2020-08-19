package scuffedbots.pagehelpertools;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import scuffedbots.pagehelpertools.MainActivity.PageData;
import scuffedbots.pagehelpertools.SQL.SQL;
import scuffedbots.pagehelpertools.SQL.SQLSharing;

import static scuffedbots.pagehelpertools.LoadingPage.*;

public class Conversations extends AppCompatActivity implements CommunicationInterface {


    private int selectedPage = 0;
    private List<PageData> pages = new ArrayList<>();
    private List<Image> images = new ArrayList<>();
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        // Step 1: setup debugger
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        context = getBaseContext();

        // Step 2: load uid from intent
        Intent intent = getIntent();
        if(intent!=null){
            Bundle b = intent.getExtras();
            if(b!=null) {
                String pagesString = b.getString("pages");
                if(pagesString!=null){
                    String[] each = pagesString.split("&");
                    for(String page:each){
                        final String[] data = page.split("=");
                        pages.add(new PageData(){{
                            name = data[0];
                            id = data[1];
                            access_token = data[2];
                        }});
                    }
                }
            }
        }

        // Step 3: load images sent and i_pressed requests

        // Step 4: load pages from firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            load_images_sent_and_ipressed_requests(currentUser.getUid());
        } else {
            Log.i("HH", "client not logged in");
        }
    }

    private void load_images_sent_and_ipressed_requests(String uid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataRef = database.getReference("users/" + uid + "/conversations");
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i("HH", "firebase responded");

                images = new ArrayList<>();
                for(final DataSnapshot image:dataSnapshot.child(pages.get(selectedPage).id).child("images_received").getChildren()){

                    images.add(new Image(){{
                        url = String.valueOf(image.child("url").getValue());
                        sender = new Client(){{
                            id = String.valueOf(image.child("sender").child("id").getValue());
                            name = String.valueOf(image.child("sender").child("name").getValue());
                            conversation_id = String.valueOf(image.child("sender").child("conversation_id").getValue());
                        }};
                    }});
                }

                ipaid = new ArrayList<>();
                for(final DataSnapshot image:dataSnapshot.child(pages.get(selectedPage).id).child("i_paid_presses").getChildren()){

                    ipaid.add(new Client(){{
                        id = String.valueOf(image.child("id").getValue());
                        name = String.valueOf(image.child("name").getValue());
                        conversation_id = String.valueOf(image.child("conversation_id").getValue());
                    }});
                }

                // Step 4: load names of clients
                Log.i("HH", "started loading names of clients");

                /*boolean all_done = true;
                for(Image image:images){
                    if(image.sender.id==null){
                        all_done = false;
                        break;
                    }
                }
                if(!all_done && images.size()>0){*/
                    load_names_of_clients();
                /*} else {
                    Log.i("HH", "skipping rerun");
                }*/
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }


    List<Client> ipaid = new ArrayList<>();
    List<Client> unused_clients = new ArrayList<>();
    private List<Client> clients = new ArrayList<>();
    private void load_names_of_clients(){
        // Step 2: give it some fields
        final Bundle parameters = new Bundle();
        parameters.putString("fields", "participants,link");
        parameters.putString("access_token", pages.get(selectedPage).access_token);
        parameters.putInt("limit", 499);
        clients = new ArrayList<>();

        Log.i("HH", "setup properties");
        load_client_page(parameters);
    }

    private void load_client_page(final Bundle parameters) {
        /* make the API call */
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final GraphRequest request = new GraphRequest(
                accessToken,
                "/" + pages.get(selectedPage).id + "/conversations",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONObject page = response.getJSONObject();
                            JSONArray conversations = page.getJSONArray("data");
                            Log.i("HH", "got " + conversations.length() + " conversations");

                            int i=0;
                            while(i<conversations.length()) {
                                final JSONObject conversation = conversations.getJSONObject(i);
                                final JSONObject clientData = conversation.getJSONObject("participants").getJSONArray("data").getJSONObject(0);

                                if(!clientData.getString("name").equals("Facebook User")){

                                    // Look for names of clients that i have from firebase to get their names
                                    for(Image image: images){
                                        if(image.sender.id.equals(clientData.getString("id"))){
                                            image.sender = new Client(){{
                                                name=clientData.getString("name");
                                                id=clientData.getString("id");
                                                conversation_id=conversation.getString("id");
                                            }};
                                        }
                                    }

                                    // Look for names of clients that i have from firebase to get their names
                                    for(Client client: ipaid){
                                        if(client.id.equals(clientData.getString("id"))){
                                            client = new Client(){{
                                                name=clientData.getString("name");
                                                id=clientData.getString("id");
                                                conversation_id=conversation.getString("id");
                                            }};
                                        }
                                    }

                                    // just fill the list of unused clients to save them in firebase incase needed later
                                    unused_clients.add(new Client(){{
                                        name=clientData.getString("name");
                                        id=clientData.getString("id");
                                        conversation_id=conversation.getString("id");
                                    }});

                                }

                                i ++;
                            }

                            boolean all_done = true;
                            for(Image image:images){
                                if(image.sender.id==null){
                                    all_done = false;
                                    break;
                                }
                            }
                            if(all_done || images.size()==0){
                                Log.i("HH", "all_done baby");
                                conversations.getString(-1);
                            }

                            String after = page.getJSONObject("paging").getJSONObject("cursors").getString("after");
                            parameters.putString("after", after);
                            load_client_page(parameters);

                            Log.i("HH", "ran outta clients");
                            conversations.getString(-1);

                        } catch(Exception e){
                            Log.i("HH", e + " ");

                            Log.i("HH", "started saving into sql");

                            try{
                                // save all these clients to a table specific for this page
                                SQLSharing.db = SQL.getInstance(getApplicationContext());
                                SQLSharing.cursor = SQLSharing.db.getClients();
                                for(Client client:unused_clients){
                                    boolean found = false;
                                    while(SQLSharing.cursor.moveToNext()){
                                        if(client.id.equals(SQLSharing.cursor.getString(1))){
                                            found = true;
                                            break;
                                        }
                                    }

                                    if(!found)
                                        SQLSharing.db.insertClient(client.id, client.name, client.conversation_id);
                                }

                                // clear unused client list after saving new clients in sql for use in other tools and this one too
                                unused_clients = new ArrayList<>();

                                new Handler(new Handler.Callback() {
                                    @Override
                                    public boolean handleMessage(@NonNull Message msg) {
                                        ok();return true;
                                    }}).sendEmptyMessage(0);
                            } catch(Exception e2) {
                                Log.i("HH", e2 + " ");
                            }


                        }

                    }
                }
        );

        // Step 3: execute
        request.executeAsync();
    }

    private void ok(){
        RecyclerView images_sent_recycler = findViewById(R.id.images_sent_recycler);
        ImagesSentAdapter imagesSentAdapter = new ImagesSentAdapter(this, images);
        images_sent_recycler.setAdapter(imagesSentAdapter);
        images_sent_recycler.setLayoutManager(new BetterLinearLayoutManager(this, 1, false));
    }

    // dont use
    @Override
    public void continueMessaging(MessagesList.Message message) {
        // dont use
    }

    public static class Image {
        public String url;
        public Client sender;
    };
}
