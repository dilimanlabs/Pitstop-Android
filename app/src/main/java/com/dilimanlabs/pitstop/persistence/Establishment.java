package com.dilimanlabs.pitstop.persistence;

import android.graphics.Bitmap;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Table(name = "Establishments")
public class Establishment extends Model {
    private final String URLpattern = "^.*/businesses/([a-zA-Z0-9]{11})/establishments/([a-zA-Z0-9]{11})/?$";

    @Column(name = "Categories")
    public String[] categories;

    @Column(name = "Contact", onDelete = Column.ForeignKeyAction.CASCADE)
    public Contact contact;

    @Column(name = "Location", onDelete = Column.ForeignKeyAction.CASCADE)
    public Location location;

    @Column(name = "Lat")
    public double lat;

    @Column(name = "Lon")
    public double lon;

    @Column(name = "Name")
    public String name;

    @Column(name = "PrimaryImage")
    public String primaryImage;

    public Bitmap primaryImageBitmap;

    @Column(name = "URL", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String url;

    public Establishment() {
        super();
    }

    public Establishment(String[] categories, Contact contact, Location location, String name, String primaryImage, String url) {
        super();

        this.categories = categories;
        this.contact = contact;
        this.location = location;
        this.lat = location.position.lat;
        this.lon = location.position.lon;
        this.name = name;
        this.primaryImage = primaryImage;
        this.url = url;
    }

    public static Establishment getEstablishmentByUrl(String url){
        return new Select().from(Establishment.class).where("URL = ?", url).executeSingle();
    }

    public static List<Establishment> getEstablishmentsWithinBounds(LatLngBounds bounds){
        double northLat = bounds.northeast.latitude;
        double southLat = bounds.southwest.latitude;
        double eastLon = bounds.northeast.longitude;
        double westLon = bounds.southwest.longitude;

        return new Select().from(Establishment.class).where("(Lat BETWEEN ? AND ?) AND (Lon BETWEEN ? AND ?)", southLat, northLat, westLon, eastLon).execute();
    }

    public static List<Establishment> getEstablishmentsWithinBoundsWithCategories(LatLngBounds bounds, List<String> selectedCategories){
        List<Establishment> establishments = getEstablishmentsWithinBounds(bounds);

        if (selectedCategories == null || selectedCategories.size() == 0) {
            return establishments;
        }

        ArrayList<Establishment> filteredEstablishments = new ArrayList<>();
        for (Establishment establishment : establishments) {
            ArrayList<String> estcat = new ArrayList<>(Arrays.asList(establishment.categories));
            estcat.retainAll(selectedCategories);

            if (estcat.size() > 0) {
                filteredEstablishments.add(establishment);
            }
        }

        return filteredEstablishments;
    }
}
