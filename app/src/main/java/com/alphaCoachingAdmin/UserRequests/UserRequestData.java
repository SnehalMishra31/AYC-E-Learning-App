package com.alphaCoachingAdmin.UserRequests;

public class UserRequestData {

    String student_name,student_class,student_contact_number,student_address,reference_name,previous_class_percentage;
    public UserRequestData() {
    }

    public UserRequestData(String student_name, String student_class, String student_contact_number, String student_address, String reference_name, String previous_class_percentage) {
        this.student_name = student_name;
        this.student_class = student_class;
        this.student_contact_number = student_contact_number;
        this.student_address = student_address;
        this.reference_name = reference_name;
        this.previous_class_percentage = previous_class_percentage;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getStudent_class() {
        return student_class;
    }

    public void setStudent_class(String student_class) {
        this.student_class = student_class;
    }

    public String getStudent_contact_number() {
        return student_contact_number;
    }

    public void setStudent_contact_number(String student_contact_number) {
        this.student_contact_number = student_contact_number;
    }

    public String getStudent_address() {
        return student_address;
    }

    public void setStudent_address(String student_address) {
        this.student_address = student_address;
    }

    public String getReference_name() {
        return reference_name;
    }

    public void setReference_name(String reference_name) {
        this.reference_name = reference_name;
    }

    public String getPrevious_class_percentage() {
        return previous_class_percentage;
    }

    public void setPrevious_class_percentage(String previous_class_percentage) {
        this.previous_class_percentage = previous_class_percentage;
    }
}
