package com.dilimanlabs.pitstop.persistence;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Locations")
public class Location extends Model {
    @Column(name = "CC")
    public String cc;

    @Column(name = "Country")
    public String country;

    @Column(name = "CityOrMunicipality")
    public String cityOrMunicipality;

    @Column(name = "Address")
    public String address;

    @Column(name = "Position")
    public Position position ;

    @Column(name = "SlippyPosition")
    public String slippyPosition;

    public Location(){
        super();
    }
}
