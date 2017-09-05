package com.example.abhinav.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final int SIGN_IN_REQUEST_CODE =1 ;
    private FirebaseListAdapter<ChatMessage> adapter;
    EditText input;

    FloatingActionButton fab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .build(),SIGN_IN_REQUEST_CODE);
        }
        else
        {
            Toast.makeText(this,"WELCOME "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),Toast.LENGTH_SHORT).show();
            displayChatMessage();
        }
         fab=(FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input=(EditText)findViewById(R.id.textinput);

             FirebaseDatabase.getInstance().getReference()
                     .push().setValue(new ChatMessage(input.getText().toString(),FirebaseAuth.getInstance()
             .getCurrentUser().getDisplayName()));
                input.setText("");
            }

        });
    }

    private void displayChatMessage() {
        final ListView listView=(ListView)findViewById(R.id.list);
        adapter=new FirebaseListAdapter<ChatMessage>(this,ChatMessage.class,R.layout.chat_message,FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messagetext=(TextView)v.findViewById(R.id.message_text);
                TextView messageuser=(TextView)v.findViewById(R.id.message_user);
                TextView messagetime=(TextView)v.findViewById(R.id.message_time);
                messagetext.setText(model.getMessageText().toString());
                messageuser.setText(model.getMessageUser().toString());
                messagetime.setText(DateFormat.format("dd-mm-yy(HH:mm:ss))",model.getMessageTime()));



            }
        };

       listView.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SIGN_IN_REQUEST_CODE)
        {

            if(resultCode==RESULT_OK)
            {
                Toast.makeText(MainActivity.this,"Successfully Signed In",Toast.LENGTH_SHORT).show();

                displayChatMessage();
            }
            else
            {
                Toast.makeText(MainActivity.this,"Failed to Sign You In..Try Again",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId=item.getItemId();
        if(itemId==R.id.signout)
        {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(MainActivity.this,"You Are Signed Out",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        if(itemId==R.id.profile)
        {
            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
        }
        return true;
    }
}
