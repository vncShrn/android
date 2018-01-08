package com.utd.hci.contactmanager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.TreeMap;

/**
 * @author Lopamudra Muduli lxm160730
 * @author Vincy Shrine vxc152130
 * @version 1.0
 * @desc Adapter class to display contacts list
 * @created 10/21/17
 */

public class ContactAdapter extends BaseAdapter {
    private Activity activity;
    private TreeMap<String, Contact> contacts = null;
    private String[] data = null;

    /**
     * Constructor to populate list view
     *
     * @param activity
     * @param contacts
     * @author Lopamudra Muduli
     */
    public ContactAdapter(Activity activity, TreeMap<String, Contact> contacts) {
        this.activity = activity;
        this.contacts = contacts;
        data = contacts.keySet().toArray(new String[contacts.size()]);
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int i) {
        return contacts.get(data[i]);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class ViewHolder {
        TextView tvName;
        TextView tvPhone;
    }

    /**
     * Renders the view for an item
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     * @author Lopamudra Muduli
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            // setLayout
            convertView = activity.getLayoutInflater().inflate(R.layout.listview, parent, false);
            viewHolder = new ViewHolder();

            // get view for name and phone number
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvPhone = (TextView) convertView.findViewById(R.id.tvPhone);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Contact contactPerson = contacts.get(data[position]);
        String lastName = contactPerson.getLastName();
        if (lastName == null)
            lastName = " ";
        viewHolder.tvName.setText(contactPerson.getFirstName() + " " + lastName);
        viewHolder.tvPhone.setText(contactPerson.getPhoneNumber());

        return convertView;
    }


}
