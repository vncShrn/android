package com.utd.hci.contactmanager;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

/**
 * @author Lopamudra Muduli lxm160730
 * @author Vincy Shrine vxc152130
 * @version 1.0
 * @desc Helper class for miscellaneous methods
 * @created 10/29/2017
 */

public class Helper {

    public static final int addFlag = 10;
    public static final int editFlag = 100;


    private final static String TAG = "Helper";

    /**
     * Populate app's memory with contacts from file on startup
     *
     * @param traceFile
     * @param cHashMap
     * @author Lopamudra Muduli
     */
    public static void createContactsListOnStartup(File traceFile, TreeMap<String, Contact> cHashMap) {
        String line;
        try {
            Log.d(TAG, "path :" + traceFile.getAbsolutePath());
            if (!traceFile.exists())
                traceFile.createNewFile();
            FileReader fr = new FileReader(traceFile);
            BufferedReader br = new BufferedReader(fr);
            Log.d(TAG, "Inside createContactsListOnStartup ");

            // Read line by line from file
            while ((line = br.readLine()) != null) {
                String input[] = line.split("\t");
                Log.d(TAG, "Inside createContactsListOnStartup " + line);
                Contact contact = new Contact(input[0], input[1], input[2], input[3]);
                String key = contact.getFirstName() + contact.getKey();
                cHashMap.put(key, contact);
            }
            fr.close();
            br.close();
        } catch (IOException e) {
            Log.e(TAG, "Unable to open the txt file.");
            e.printStackTrace();
        }
    }


    /**
     * Write to file only on closing app
     *
     * @param traceFile
     * @param cHashMap
     * @author Vincy Shrine
     */
    public static void writeToFile(File traceFile, TreeMap<String, Contact> cHashMap) {
        String line;
        FileWriter fileWriter = null;
        StringBuilder sb;

        try {
            fileWriter = new FileWriter(traceFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter br = new BufferedWriter(fileWriter);
        try {
            for (Contact value : cHashMap.values()) {
                sb = new StringBuilder();
                sb.append(value.getFirstName() + "\t");
                sb.append(value.getLastName() + "\t");
                sb.append(value.getPhoneNumber() + "\t");
                sb.append(value.getEmailID());

                br.write(sb.toString());
                br.newLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
