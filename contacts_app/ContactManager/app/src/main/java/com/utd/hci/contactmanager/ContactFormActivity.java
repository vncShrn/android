package com.utd.hci.contactmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Lopamudra Muduli lxm160730
 * @author Vincy Shrine vxc152130
 * @version 1.0
 * @desc Class that handles Add/Edit/Delete contacts intent
 * @created 10/22/17
 */
public class ContactFormActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    EditText etFname, etLname, etPhone, etEmail;

    /**
     * @param savedInstanceState
     * @author Lopamudra Muduli
     * Called when Edit/Add contact clicked
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_form);

        Intent intent = getIntent();
        if (intent != null) {
            etFname = (EditText) findViewById(R.id.etFName);
            etLname = (EditText) findViewById(R.id.etLName);
            etPhone = (EditText) findViewById(R.id.etPhone);
            etEmail = (EditText) findViewById(R.id.etEmail);

            int requestCode = intent.getIntExtra(getString(R.string.requestCode), Helper.addFlag);
            if (requestCode == Helper.addFlag) {
                // Add new contact
                setTitle(R.string.addContactTitle);

            } else {
                // Edit/Delete existing contact
                setTitle(R.string.editContactTitle);
                String[] existingContact = intent.getStringArrayExtra(getString(R.string.contactFromFirstActivity));
                etFname.setText(existingContact[0]);
                etLname.setText(existingContact[1]);
                etPhone.setText(existingContact[2]);
                etEmail.setText(existingContact[3]);
            }
        } else {
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    /**
     * @param menu
     * @return
     * @author Lopamudra Muduli
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_save_del, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Returns the result(contact details) to the activity based on the Save/Delete option selected.
     *
     * @param item
     * @return
     * @author Vincy Shrine
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String fname = String.valueOf(etFname.getText());
        String lname = etLname.getText().toString();
        if (lname == null || lname.isEmpty())
            lname = "";
        String phone = etPhone.getText().toString();
        if (phone == null || phone.isEmpty())
            phone = "";
        String email = etEmail.getText().toString();
        if (email == null || email.isEmpty())
            email = "";

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.item_delete:
                String[] contactArray = {fname, lname, phone, email};

                if (!fname.isEmpty()) {
                    Intent info = new Intent();
                    info.putExtra(getString(R.string.contactFromFirstActivity), contactArray);
                    info.putExtra(getString(R.string.isDelete), true);
                    setResult(RESULT_OK, info);
                    finish();
                } else {
                    Toast.makeText(this, "Please enter the details", Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.item_save:

                String[] newContact = {fname, lname, phone, email};

                if (!fname.isEmpty()) {
                    Intent info = new Intent();
                    info.putExtra(getString(R.string.newContact), newContact);
                    setResult(RESULT_OK, info);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "First name cannot be empty", Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
