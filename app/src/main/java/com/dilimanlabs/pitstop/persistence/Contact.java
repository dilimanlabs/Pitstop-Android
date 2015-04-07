package com.dilimanlabs.pitstop.persistence;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Contacts")
public class Contact extends Model {
    @Column(name = "Phone")
    public String phone;

    @Column(name = "FormattedPhone")
    public String formattedPhone;

    public Contact() {
        super();
    }
}
