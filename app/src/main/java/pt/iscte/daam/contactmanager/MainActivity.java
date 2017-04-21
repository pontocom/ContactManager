package pt.iscte.daam.contactmanager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import pt.iscte.daam.contactmanager.model.Contact;

public class MainActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1000;

    protected ArrayList<Contact> arrayOfContacts;
    protected String[] listOfContacts;

    protected ListView lvListOfContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvListOfContacts = (ListView) findViewById(R.id.lvListOfContacts);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // No permissions yet, we need to request them to the user!

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        } else {
            new readAllContacts().execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The permission is granted by the user, so perform the operation
                    new readAllContacts().execute();
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied by the user!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(MainActivity.this, "Permission denied by the user!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private class readAllContacts extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading contacts, plase wait...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor =contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            if(cursor.getCount()>0){
                Log.i("CONTACTSMANAGER APP", "Number of contacts =  " + cursor.getCount());
                listOfContacts = new String[cursor.getCount()];
                arrayOfContacts = new ArrayList<Contact>();
                int i = 0;
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String phoneNo = "";

                    if(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0 ) {
                        Cursor pCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        while (pCursor.moveToNext()) {
                            phoneNo = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }

                        pCursor.close();
                    }

                    if(name!=null) listOfContacts[i] = name;
                    else listOfContacts[i] = "";

                    if (id!=null && name!=null && phoneNo != null)
                        arrayOfContacts.add(i, new Contact(id, name, phoneNo));
                    else arrayOfContacts.add(i, new Contact("", "", ""));

                    i++;
                }
            } else {
                Toast.makeText(MainActivity.this, "No contacts found!", Toast.LENGTH_SHORT).show();
            }

            cursor.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();

            // fill the ListView :-)
            ArrayAdapter<String> contactsArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, listOfContacts);
            lvListOfContacts.setAdapter(contactsArrayAdapter);
        }
    }
}
