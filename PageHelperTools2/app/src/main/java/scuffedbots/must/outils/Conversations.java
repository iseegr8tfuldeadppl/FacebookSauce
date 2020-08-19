package scuffedbots.must.outils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
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
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import scuffedbots.must.outils.SQL.SQL;
import scuffedbots.must.outils.SQL.SQLSharing;
import scuffedbots.must.outils.MainActivity.PageData;

public class Conversations extends AppCompatActivity implements CommunicationInterface2 {


    private int selectedPage = 0;
    private List<PageData> pages = new ArrayList<>();
    private List<Image> images = new ArrayList<>();
    private FrameLayout previewHolder, loadingPage;
    private Spinner pagesSpinner;
    private Context context;
    private TouchImageView preview;
    private RecyclerView i_paid_presses_recycler, images_received_recycler;
    private String uid;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        // Step 1: setup debugger
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            goBackToMainMenu();
            return;
        }
        database = FirebaseDatabase.getInstance();
        uid = currentUser.getUid();

        context = this;

        previewHolder = findViewById(R.id.previewHolder);

        preview = findViewById(R.id.preview);
        loadingPage = findViewById(R.id.loadingPage);
        i_paid_presses_recycler = findViewById(R.id.i_paid_presses_recycler);
        images_received_recycler = findViewById(R.id.images_received_recycler);


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

        // Step 3: setup pages spinner
        pagesSpinner = findViewById(R.id.pagesSpinner);
        ArrayAdapter<String> pagesSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinnerdispaly, android.R.id.text1);
        pagesSpinnerAdapter.setDropDownViewResource(R.layout.spinnerdropdown);
        for(PageData page:pages)
            pagesSpinnerAdapter.add(page.name);
        pagesSpinner.setAdapter(pagesSpinnerAdapter);
        pagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedPage = position;

                // Step 4: load images sent and i_pressed requests
                if(loadingPage.getVisibility()==View.GONE)
                    loadingPage.setVisibility(View.VISIBLE);
                load_images_sent_and_ipressed_requests(uid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}

        });
        pagesSpinner.setSelection(selectedPage);

    }

    @Override
    public void onBackPressed() {
        if(previewHolder.getVisibility()==View.VISIBLE){
            previewHolder.setVisibility(View.GONE);
            return;
        }

        goBackToMainMenu();
    }

    private void goBackToMainMenu(){
        Intent intent = getIntent();
        if(intent!=null){
            Bundle previous_b = intent.getExtras();
            if(previous_b!=null){

                Intent mainPage = new Intent(this, MainMenu.class);
                Bundle b = new Bundle();

                b.putString("pages",previous_b.getString("pages"));
                mainPage.putExtras(b);
                startActivity(mainPage);
                finish();
            }
        }
    }

    private void load_images_sent_and_ipressed_requests(String uid) {
        DatabaseReference dataRef = database.getReference("users/" + uid + "/conversations");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                images = new ArrayList<>();
                for(final DataSnapshot image:dataSnapshot.child(pages.get(selectedPage).id).child("images_received").getChildren()){

                    images.add(new Image(){{
                        url = String.valueOf(image.child("url").getValue());
                        sender = new LoadingPage.Client(){{
                            id = String.valueOf(image.child("sender").child("id").getValue());
                            name = String.valueOf(image.child("sender").child("name").getValue());
                            conversation_id = String.valueOf(image.child("sender").child("conversation_id").getValue());
                        }};
                    }});
                }

                ipaids = new ArrayList<>();
                for(final DataSnapshot image:dataSnapshot.child(pages.get(selectedPage).id).child("i_paid_presses").getChildren()){

                    ipaids.add(new LoadingPage.Client(){{
                        id = String.valueOf(image.child("id").getValue());
                        name = String.valueOf(image.child("name").getValue());
                        conversation_id = String.valueOf(image.child("conversation_id").getValue());
                    }});
                }

                // Step 4: load names of clients

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


    List<LoadingPage.Client> ipaids = new ArrayList<>();
    List<LoadingPage.Client> unused_clients = new ArrayList<>();
    private List<LoadingPage.Client> clients = new ArrayList<>();
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
                                            image.sender = new LoadingPage.Client(){{
                                                name=clientData.getString("name");
                                                id=clientData.getString("id");
                                                conversation_id=conversation.getString("id");
                                            }};
                                        }
                                    }

                                    // Look for names of clients that i have from firebase to get their names
                                    for(LoadingPage.Client client: ipaids){
                                        if(client.id.equals(clientData.getString("id"))){

                                            client.name=clientData.getString("name");
                                            client.id=clientData.getString("id");
                                            client.conversation_id=conversation.getString("id");
                                        }
                                    }

                                    // just fill the list of unused clients to save them in firebase incase needed later
                                    unused_clients.add(new LoadingPage.Client(){{
                                        name=clientData.getString("name");
                                        id=clientData.getString("id");
                                        conversation_id=conversation.getString("id");
                                    }});

                                }

                                i ++;
                            }

                            boolean all_done = true;
                            for(Image image:images){
                                if(image.sender.name==null){
                                    all_done = false;
                                    break;
                                }
                            }
                            for(LoadingPage.Client ipaid:ipaids){
                                if(ipaid.name==null){
                                    all_done = false;
                                    break;
                                }
                            }
                            if(all_done){
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
                                for(LoadingPage.Client client:unused_clients){
                                    boolean found = false;
                                    while(SQLSharing.cursor.moveToNext()){
                                        if(client.id.equals(SQLSharing.cursor.getString(1))){
                                            found = true;
                                            break;
                                        }
                                    }

                                    if(!found){
                                        SQLSharing.db.insertClient(client.id, client.name, client.conversation_id);
                                    }
                                }

                                // clear unused client list after saving new clients in sql for use in other tools and this one too
                                unused_clients = new ArrayList<>();

                                new Handler(new Handler.Callback() {
                                    @Override
                                    public boolean handleMessage(@NonNull Message msg) {

                                        // reverse arrays upside down
                                        List<Image> reversed_images = new ArrayList<>();
                                        List<LoadingPage.Client> reversed_ipaids = new ArrayList<>();
                                        for(int i=images.size()-1; i>=0; i--){
                                            reversed_images.add(images.get(i));
                                        }
                                        for(int i=ipaids.size()-1; i>=0; i--){
                                            reversed_ipaids.add(ipaids.get(i));
                                        }
                                        images = new ArrayList<>(reversed_images);
                                        ipaids = new ArrayList<>(reversed_ipaids);

                                        ImagesSentAdapter imagesSentAdapter = new ImagesSentAdapter(context, images);
                                        images_received_recycler.setAdapter(imagesSentAdapter);
                                        images_received_recycler.setLayoutManager(new BetterLinearLayoutManager(context, 1, false));

                                        IPaidAdapter iPaidAdapter = new IPaidAdapter(context, ipaids);
                                        i_paid_presses_recycler.setAdapter(iPaidAdapter);
                                        i_paid_presses_recycler.setLayoutManager(new BetterLinearLayoutManager(context, 1, false));

                                        loadingPage.setVisibility(View.GONE);
                                        return true;
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

    @Override
    public void checkOutPhoto(Image image) {
        putInClipboard(image.sender.name);
    }

    @Override
    public void previewPhoto(Image image) {
        Picasso.get().load(image.url).into(preview);
        previewHolder.setVisibility(View.VISIBLE);
        preview.reset();
    }

    @Override
    public void checkOutIPaid(LoadingPage.Client ipaid) {
        putInClipboard(ipaid.name);
    }

    public void putInClipboard(String stuff){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if(clipboard!=null){
            ClipData clip = ClipData.newPlainText("", stuff);
            clipboard.setPrimaryClip(clip);
            print("Client name copied!");
            return;
        }

        print("Failed to copy name clipboard is null");
    }

    public void print(Object log){
        Toast.makeText(getApplicationContext(), String.valueOf(log), Toast.LENGTH_SHORT).show();
    }

    public void exitPreviewClicked(View view) {
        previewHolder.setVisibility(View.GONE);
    }

    public void setDrawable(ImageView image, int drawable){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image.setImageDrawable(getDrawable(drawable));
            return;
        }
        image.setImageDrawable(getResources().getDrawable(drawable));
    }

    public void toggleImagesReceivedIPaidClicked(View view) {
        ImageView toggler = (ImageView) view;
        if(i_paid_presses_recycler.getVisibility()==View.VISIBLE){
            i_paid_presses_recycler.setVisibility(View.GONE);
            images_received_recycler.setVisibility(View.VISIBLE);
            setDrawable(toggler, R.drawable.i_paid_icon);
            return;
        }

        i_paid_presses_recycler.setVisibility(View.VISIBLE);
        images_received_recycler.setVisibility(View.GONE);
        setDrawable(toggler, R.drawable.images_received_icon);
    }

    public void trashClicked(View view) {

        final boolean images_or_ipaids = images_received_recycler.getVisibility()==View.VISIBLE;

        // first check if list is already empty
        if(images_or_ipaids){
            if(images.size()==0){
                print("Image list is already empty");
                return;
            }
        } else {
            if(ipaids.size()==0){
                print("Requests list is already empty");
                return;
            }
        }

        // then prompt a check dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Clear Confirmation");
        builder.setMessage("Are you sure you want to clear all " + (images_or_ipaids? "images?":"i paid requests?") );
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(images_or_ipaids){
                            DatabaseReference dataRef = database.getReference("users/" + uid + "/conversations/" + pages.get(selectedPage).id + "/images_received");
                            dataRef.removeValue();
                            return;
                        }

                        DatabaseReference dataRef = database.getReference("users/" + uid + "/conversations/" + pages.get(selectedPage).id + "/i_paid_presses");
                        dataRef.removeValue();
                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).create().show();
    }

    public static class Image {
        public String url;
        public LoadingPage.Client sender;
    };
}
