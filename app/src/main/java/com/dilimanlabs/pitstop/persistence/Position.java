package com.dilimanlabs.pitstop.persistence;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "Positions")
public class Position extends Model {
    @Column(name = "Lat")
    public double lat;

    @Column(name = "Lon")
    public double lon;

    public Position() {
        super();
    }
}
