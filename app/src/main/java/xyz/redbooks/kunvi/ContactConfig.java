package xyz.redbooks.kunvi;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import xyz.redbooks.kunvi.database.AppDatabase;

public class ContactConfig extends AppCompatActivity {

    List<Contact> contacts = new ArrayList<>();
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView emptyView = findViewById(R.id.emptyView);

        final RecyclerView recyclerView = findViewById(R.id.trustedContactList);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        db = AppDatabase.getAppDatabase(this);

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        contacts = db.contactDao().getAllContacts();
                        Log.d("THREAD","Message from the thread");
                    }
                }
        ).start();

        if (contacts.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        ContactAdapter adapter = new ContactAdapter(contacts, new ContactAdapter.CustomClickListener() {
            @Override
            public void onDeleteButtonClick(View v, int position) {
                contacts.remove(position);
                recyclerView.removeViewAt(position);
                if (contacts.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
                Snackbar.make(findViewById(R.id.root), "Removed Contact", Snackbar.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);


        FloatingActionButton fab = findViewById(R.id.addContact);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickOneContact();
                Snackbar.make(findViewById(R.id.root),"Contact Added",Snackbar.LENGTH_SHORT).show();
            }
        });
    }



    private void checkListEmptyStatus(){

    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            Snackbar.make(findViewById(R.id.root),"Volume Button is clicked",Snackbar.LENGTH_SHORT).show();
            Log.d("Button Press", "Power button is pressed");
            return true;// I have eaten up the event, if false it will  pass the event to the system
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
            int number = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            final Contact contact = new Contact();
            contact.setName(cursor.getString(name));
            contact.setMobileNumber(cursor.getString(number));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    db.contactDao().insertContact(contact);
                }
            }).start();
        }
    }
}
