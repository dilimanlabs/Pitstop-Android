package com.dilimanlabs.pitstop.persistence;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

@Table(name = "Categories")
public class Category extends Model {

    @Column(name = "Description")
    public String description;

    @Column(name = "Name")
    public String name;

    @Column(name = "PluralName")
    public String pluralName;

    @Column(name = "PrimaryImage")
    public String primaryImage;

    @Column(name = "URL", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String url;

    public Category() {
        super();
    }

    public Category(String description, String name, String pluralName, String primaryImage, String url) {
        super();

        this.description = description;
        this.name = name;
        this.pluralName = pluralName;
        this.primaryImage = primaryImage;
        this.url = url;
    }

    public static List<Category> getAll() {
        List<Category> categories = new Select().from(Category.class).execute();
        if (categories == null) {
            return new ArrayList<>();
        } else {
            return categories;
        }
    }

    public static Category getByUrl(String url){
        return new Select().from(Category.class).where("URL = ?", url).executeSingle();
    }
}
