package com.dilimanlabs.pitstop.persistence;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Account")
public class Account extends Model {
    @Column(name = "AccountId")
    public String id;

    @Column(name = "Password")
    public String password;

    @Column(name = "AuthToken")
    public String authToken;

    public Account() {
        super();
    }

    public Account(String id, String password, String authToken) {
        super();

        this.id = id;
        this.password = password;
        this.authToken = authToken;
    }

    public static Account getAccount() {
        return new Select().from(Account.class).executeSingle();
    }
}
