package scuffedbots.must.outils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void goToMessageAllClientsClicked(View view) {
        goToMessageAllClients();
    }

    public void goToConversationsClicked(View view) {
        goToConvesationsClients();
    }

    private void goToMessageAllClients(){
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
    private void goToConvesationsClients(){
        Intent intent = getIntent();
        if(intent!=null){
            Bundle previous_b = intent.getExtras();
            if(previous_b!=null){

                Intent mainPage = new Intent(this, Conversations.class);
                Bundle b = new Bundle();

                b.putString("pages",previous_b.getString("pages"));
                mainPage.putExtras(b);
                startActivity(mainPage);
                finish();
            }
        }
    }
}
