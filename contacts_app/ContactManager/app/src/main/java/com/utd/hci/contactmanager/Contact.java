package com.utd.hci.contactmanager;

/**
 * @author Lopamudra Muduli lxm160730
 * @author Vincy Shrine vxc152130
 * @version 1.0
 * @desc Contact Bean class to represent contact person information
 * @created 10/21/17
 */

public class Contact {
    static int counter = 0;
    private int key;
    private String firstName = " ";
    private String lastName = " ";
    private String phoneNumber = " ";
    private String emailID = " ";

    /**
     * To create new contact in memory
     *
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @param emailID
     * @author Lopamudra Muduli
     */
    public Contact(String firstName, String lastName, String phoneNumber, String emailID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.emailID = emailID;
        this.key = counter++;

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public int getKey() {
        return key;
    }

}
