package com.utd.hci.contactmanager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.Comparator;
import java.util.TreeMap;

/**
 * This is an android app for maintaining contact detail information.
 * It will display the first name and last name along with phone number information in a list.
 * There is a form which will allow user to enter the user contact information for saving or deleting the information.
 *
 * @author Lopamudra Muduli lxm160730
 * @author Vincy Shrine vxc152130
 * @version 1.0
 * @desc This is the ContactManager Main Activity class that allows a user to ADD EDIT and DELETE contacts
 * @created 10/21/17
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private TreeMap<String, Contact> cHashMap;
    private Contact selectedContact = null;
    private ListView listView;
    private final int REQUESTCODE_ASK_PERMISSIONS = 100;

    /**
     * Method called first on this activity
     *
     * @param savedInstanceState
     * @author Vincy Shrine
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set layout
        setContentView(R.layout.activity_main);

        // get list view to manipulate it
        listView = (ListView) findViewById(R.id.lvContact);

        // initialize app's memory of contacts
        cHashMap = new TreeMap<String, Contact>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });

        // check file permissions and populate memory of contacts
        checkStorageAccessPermissions();

        // create handler for select item on list
        listView.setOnItemClickListener(new ListClickHandler());
    }

    /**
     * This method is used to write the contacts details
     * to the text file whenever the app is closed
     *
     * @author Vincy Shrine
     */
    @Override
    protected void onStop() {

        File traceFile = new File(((Context) this).getExternalFilesDir(null), getString(R.string.contactsFileName));
        Helper.writeToFile(traceFile, cHashMap);

        super.onStop();
    }

    /**
     * @param menu
     * @return
     * @author Vincy Shrine
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On selecting menu option ADD, new intent is started
     *
     * @param item
     * @return
     * @author Lopamudra Muduli
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(this, ContactFormActivity.class);
                startActivityForResult(intent, Helper.addFlag);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * This method is called whenever the second activity returns result
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @author Lopamudra Muduli
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Adding new Contact // If condition checks if comes from Save Button
        if (requestCode == Helper.addFlag) {
            if (resultCode == RESULT_OK) {
                String[] newContact = data.getExtras().getStringArray(getString(R.string.newContact));
                Contact contact = null;
                if (newContact != null) {
                    contact = new Contact(newContact[0], newContact[1], newContact[2], newContact[3]);
                    String key = contact.getFirstName() + contact.getKey();
                    cHashMap.put(key, contact);
                    Log.d(TAG, "contact saved:" + contact.getFirstName());
                }

                ContactAdapter adapter = new ContactAdapter(this, cHashMap);
                listView.setAdapter(adapter);

            }
        } else if (requestCode == Helper.editFlag) {
            if (resultCode == RESULT_OK) {

                boolean isDelete = data.getExtras().getBoolean(getString(R.string.isDelete));

                if (isDelete) {
                    // Delete selected contact
                    String key = selectedContact.getFirstName() + selectedContact.getKey();
                    cHashMap.remove(key);
                    Log.d(TAG, "contact deleted: " + selectedContact.getFirstName());

                    ContactAdapter adapter = new ContactAdapter(this, cHashMap);

                    listView.setAdapter(adapter);

                } else {
                    // Edit selected contact
                    String[] existingContact = data.getExtras().getStringArray(getString(R.string.newContact));

                    //Remove selected contact
                    String oldKey = selectedContact.getFirstName() + selectedContact.getKey();
                    cHashMap.remove(oldKey);

                    // Update details
                    selectedContact.setFirstName(existingContact[0]);
                    selectedContact.setLastName(existingContact[1]);
                    selectedContact.setPhoneNumber(existingContact[2]);
                    selectedContact.setEmailID(existingContact[3]);

                    // Add contact with new details
                    String newKey = selectedContact.getFirstName() + selectedContact.getKey();
                    cHashMap.put(newKey, selectedContact);
                    Log.d(TAG, "contact edited: " + selectedContact.getFirstName());

                    ContactAdapter adapter = new ContactAdapter(this, cHashMap);
                    listView.setAdapter(adapter);
                }
            }
            // Make selected contact null once edit/delete is over
            selectedContact = null;
        }
    }


    /**
     * Method to handle select contact from contact list
     *
     * @author Vincy Shrine
     */
    public class ListClickHandler implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {

            String[] arrayOfKeys = cHashMap.keySet().toArray(new String[cHashMap.size()]);
            Contact contact = cHashMap.get(arrayOfKeys[position]);
            selectedContact = contact;

            String fname = contact.getFirstName();
            String lname = contact.getLastName();
            String phone = contact.getPhoneNumber();
            String email = contact.getEmailID();

            String[] contactArray = {fname, lname, phone, email};

            Intent intent = new Intent(MainActivity.this, ContactFormActivity.class);
            intent.putExtra(getString(R.string.contactFromFirstActivity), contactArray);

            /* Sends the selected contacts information in intent to the second activity.*/
            startActivityForResult(intent, Helper.editFlag);
        }
    }

    /**
     * This method checks if the user has given access to access the storage media of the phone.
     * If not, it asks the user to give permission to access the storage media
     * and hence Contacts text file can be created and accessed.
     *
     * @author Lopamudra Muduli
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkStorageAccessPermissions() {

        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_CONTACTS);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUESTCODE_ASK_PERMISSIONS);
            return;
        } else {

            File traceFile = new File(((Context) this).getExternalFilesDir(null), getString(R.string.contactsFileName));
            Helper.createContactsListOnStartup(traceFile, cHashMap);

            // Assigns the contacts list to custom view to display the contacts on the home screen.
            ContactAdapter adapter = new ContactAdapter(this, cHashMap);
            listView.setAdapter(adapter);

            return;
        }

    }

    /**
     * This method is called once user accepts or denies to give storage permission
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @author Lopamudra Muduli
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUESTCODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    File traceFile = new File(((Context) this).getExternalFilesDir(null), getString(R.string.contactsFileName));
                    Helper.createContactsListOnStartup(traceFile, cHashMap);
                    ContactAdapter adapter = new ContactAdapter(this, cHashMap);
                    listView.setAdapter(adapter);

                } else {
                    // Permission Denied
                    Toast.makeText(this, "STORAGE_ACCESS Denied. Please restart the app and allow access!!", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * @param intent
     * @param requestCode
     * @author Vincy Shrine
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra(getString(R.string.requestCode), requestCode);
        super.startActivityForResult(intent, requestCode);
    }

}
