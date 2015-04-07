package com.dilimanlabs.pitstop.persistence;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Businesses")
public class Business extends Model {
    private final String URLpattern = "^.*/businesses/([a-zA-Z0-9]{11})/?$";

    @Column(name = "Description")
    public String description;

    @Column(name = "Name")
    public String name;

    @Column(name = "PrimaryImage")
    public String primaryImage;

    @Column(name = "RegisteredName")
    public String registeredName;

    @Column(name = "URL", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String url;

    public Business() {
        super();
    }

    public Business(String description, String name, String primaryImage, String registeredName, String url) {
        super();

        this.description = description;
        this.name = name;
        this.primaryImage = primaryImage;
        this.registeredName = registeredName;
        this.url = url;
    }

    public static Business getBusinessByUrl(String url){
        return new Select().from(Business.class).where("URL = ?", url).executeSingle();
    }
}
