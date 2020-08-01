package scuffedbots.pagehelpertools;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import scuffedbots.pagehelpertools.MainActivity.PageData;

public class MainPage extends AppCompatActivity {

    private EditText bubbleInput, amountInput;
    private TextView all;
    private LinearLayout some;
    private Switch allOrSomeCheck;
    private int amount_of_clients = 5000;
    private Spinner pagesSpinner;
    private List<PageData> pages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // Step 1: setup debugger
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler());

        // Preparation: load uid from intent
        Intent intent = getIntent();
        if(intent!=null){
            Bundle b = intent.getExtras();
            if(b!=null){
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


        // Step 2: setup variables
        bubbleInput = findViewById(R.id.bubbleInput);
        amountInput = findViewById(R.id.amountInput);
        allOrSomeCheck = findViewById(R.id.allOrSomeCheck);
        all = findViewById(R.id.all);
        some = findViewById(R.id.some);

        // Step 3: prepare amount selector
        amountInputListener();
        allOrSomeCheckListener();
        allOrSomeCheck.setChecked(true);

        pagesSpinner = findViewById(R.id.pagesSpinner);
        ArrayAdapter<String> pagesSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinnerdispaly, android.R.id.text1);
        pagesSpinnerAdapter.setDropDownViewResource(R.layout.spinnerdropdown);
        for(PageData page:pages)
            pagesSpinnerAdapter.add(page.name);
        pagesSpinner.setAdapter(pagesSpinnerAdapter);
        //spinnerAdapter.notifyDataSetChanged();

    }

    private void amountInputListener() {
    }

    private void allOrSomeCheckListener() {
        allOrSomeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    all.setVisibility(View.VISIBLE);
                    some.setVisibility(View.GONE);
                    return;
                }
                all.setVisibility(View.GONE);
                some.setVisibility(View.VISIBLE);
            }
        });
    }


    public void sendClicked(View view) {

        if(pages.size()==0){
            print("You have no pages allowed to this app");
            return;
        }

        int amount = 0;
        boolean allOrSome = allOrSomeCheck.isChecked();
        String message = bubbleInput.getText().toString();

        if(message.length()<=0){
            print("Message is empty");
            return;
        }

        if(!allOrSome){
            try {
                amount = Integer.parseInt(amountInput.getText().toString());
            } catch(Exception ignored){
                print("Invalid amount");
                return;
            }
        }

        int selectedPageIndex = pagesSpinner.getSelectedItemPosition();
        PageData selectedPageElement = pages.get(selectedPageIndex);
        String selectedPage = selectedPageElement.name + "=" + selectedPageElement.id + "=" + selectedPageElement.access_token;

        Bundle b = new Bundle();
        b.putString("message", message);
        b.putBoolean("allOrSome", allOrSome);
        b.putInt("amount", amount);
        b.putString("page",  selectedPage);

        Intent loadingPage = new Intent(this, LoadingPage.class);
        loadingPage.putExtras(b);
        startActivity(loadingPage);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void print(Object log){
        Toast.makeText(this, String.valueOf(log), Toast.LENGTH_SHORT).show();
    }

    public void bubbleClicked(View view) {
        bubbleInput.requestFocus();
    }

    public void lessClicked(View view) {
        try{
            int amount = Integer.parseInt(amountInput.getText().toString());
            if(amount>1)
                amountInput.setText(String.valueOf(amount-1));
        } catch(Exception ignored){
            amountInput.setText("1");
        }
    }

    public void moreClicked(View view) {
        try{
            int amount = Integer.parseInt(amountInput.getText().toString());
            if(amount<amount_of_clients)
                amountInput.setText(String.valueOf(amount+1));
        } catch(Exception ignored){
            amountInput.setText("1");
        }
    }
}
