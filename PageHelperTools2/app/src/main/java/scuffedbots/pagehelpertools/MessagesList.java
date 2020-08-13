package scuffedbots.pagehelpertools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import scuffedbots.pagehelpertools.SQL.SQL;
import scuffedbots.pagehelpertools.SQL.SQLSharing;

public class MessagesList extends AppCompatActivity implements CommunicationInterface {

    private List<Message> messages = new ArrayList<>();
    private RecyclerView messagesRecycler;
    private int total_clients;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);

        // Step 1: setup debugger
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        messagesRecycler = findViewById(R.id.messagesRecycler);

        SQLSharing.db = SQL.getInstance(this);
        SQLSharing.cursor = SQLSharing.db.getMessages();

        while(SQLSharing.cursor.moveToNext()){
            messages.add(new Message(){{
                ID = SQLSharing.cursor.getString(0);
                MESSAGE = SQLSharing.cursor.getString(1);
                CLIENTS_MESSAGED = Integer.parseInt(SQLSharing.cursor.getString(2));
                AMOUNT = Integer.parseInt(SQLSharing.cursor.getString(3));
                PAGE_ID = SQLSharing.cursor.getString(4);
                ALLORSOME = SQLSharing.cursor.getString(3).equals("-1");

            }});
        }

        MessagesAdapter messagesAdapter = new MessagesAdapter(this, messages, total_clients);
        messagesRecycler.setAdapter(messagesAdapter);
        messagesRecycler.setLayoutManager(new BetterLinearLayoutManager(this, 1, false));
    }


    private void exit(){
        Intent intent = getIntent();
        if(intent!=null){
            Bundle previous_b = intent.getExtras();
            if(previous_b!=null){

                Intent mainPage = new Intent(this, MessageAllClients.class);
                Bundle b = new Bundle();

                b.putString("pages",previous_b.getString("pages"));
                mainPage.putExtras(b);
                startActivity(mainPage);
                finish();
            }
        }
    }

    @Override
    public void continueMessaging(Message message) {

    }

    public void newmessageClicked(View view) {
        exit();
    }

    public static class Message {
        public String PAGE_ID;

        public String MESSAGE;
        public String ID;

        public int CLIENTS_MESSAGED;
        public int AMOUNT;
        public boolean ALLORSOME;
    };
}
