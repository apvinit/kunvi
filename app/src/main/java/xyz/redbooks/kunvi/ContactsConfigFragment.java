package xyz.redbooks.kunvi;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import xyz.redbooks.kunvi.database.AppDatabase;

import static android.app.Activity.RESULT_OK;

public class ContactsConfigFragment extends Fragment {

    List<Contact> contacts = new ArrayList<>();
    AppDatabase db;
    ContactAdapter adapter;
    RecyclerView recyclerView;
    ImageView emptyView;

    public ContactsConfigFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_contacts_config, container, false);

        db = AppDatabase.getAppDatabase(getActivity().getApplicationContext());
        contacts = db.contactDao().getAllContacts();

        emptyView = view.findViewById(R.id.emptyView);

        recyclerView = view.findViewById(R.id.trustedContactList);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(llm);

        FloatingActionButton fab = view.findViewById(R.id.addContact);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickOneContact();
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new ContactAdapter(contacts, new ContactAdapter.CustomClickListener() {
            @Override
            public void onDeleteButtonClick(View v, int position) {

                db.contactDao().Delete(db.contactDao().getContactById(contacts.get(position).getId()));
                Log.d("TAG", "contact deleted");
                contacts.remove(position);
                recyclerView.removeViewAt(position);
                adapter.notifyDataSetChanged();
                Snackbar.make(view.findViewById(R.id.root), "Removed Contact", Snackbar.LENGTH_SHORT).show();
                checkEmptyState();
            }
        });
        recyclerView.setAdapter(adapter);

        checkEmptyState();

        return view ;
    }

    private void pickOneContact() {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(i, 99);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 99 && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(contactUri, null, null, null, null);
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
            contacts.add(contact);
            adapter.notifyItemInserted(contacts.size());
            checkEmptyState();
        }
    }

    private void checkEmptyState(){
        if (contacts.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

}
