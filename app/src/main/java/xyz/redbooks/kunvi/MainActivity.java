package xyz.redbooks.kunvi;

import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Contact> contacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView emptyView = findViewById(R.id.emptyView);

        RecyclerView recyclerView = findViewById(R.id.trustedContactList);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        contacts = createList(5);
        ContactAdapter adapter = new ContactAdapter(contacts);
        recyclerView.setAdapter(adapter);

        if (contacts.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        FloatingActionButton fab = findViewById(R.id.addContact);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickOneContact();
                Snackbar.make(findViewById(R.id.root),"Testing",Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private List<Contact> createList(int size) {
        for(int i = 1; i <= size; i++){
            Contact contact = new Contact();
            contact.setId(i);
            contact.setName("Name_" + i);
            contacts.add(contact);
        }
        return contacts;
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            // do what you want with the power button
            Snackbar.make(findViewById(R.id.root),"Volume Button is clicked",Snackbar.LENGTH_SHORT).show();
            Log.d("Button Press", "Power button is pressed");
            return true;// I have eaten up the event, if false it will not pass the event to the system
        }
        return super.onKeyDown(keyCode, event);
    }

    private void pickOneContact() {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(i, 99);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 99 && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int test = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            Log.d("phone number", cursor.getString(column));
            Log.d("phone name", cursor.getString(test));
        }
    }
}
