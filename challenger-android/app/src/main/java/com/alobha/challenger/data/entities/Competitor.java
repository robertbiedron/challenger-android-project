package com.alobha.challenger.data.entities;

import android.location.Location;
import android.support.annotation.NonNull;

import com.alobha.challenger.business.db.Database;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by mrNRG on 16.06.2016.
 */

@Parcel
@Table(database = Database.class)
public class Competitor extends BaseModel implements Comparable<Competitor>, Serializable {
    private static final double KM_H = 3.6;
    private static final double MP_H = 2.23694;

    @Expose
    @Column
    @PrimaryKey(autoincrement = true)
    @NotNull
    public long id;

    @Column
    @ForeignKey(saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE, onUpdate = ForeignKeyAction.CASCADE)
    public User user;

    @Column
    @ForeignKey(saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE, onUpdate = ForeignKeyAction.CASCADE)
    public Challenge challenge;

    @Expose
    @Column
    public int position;

    @Expose
    @Column
    public float avg_speed;

    @Expose
    @Column
    public float distance;

    @Expose
    @Column
    public long time;

    @Expose
    @Column
    public double latitude;

    @Expose
    @Column
    public double longitude;

    @Expose
    @Column
    public double altitude;

    @Expose
    @Column
    public double currentSpeed;

    @Expose
    @Column
    public double currentCourse;

    @Expose
    @Column
    public double offset;

    public void setLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
        currentSpeed = location.getSpeed() * KM_H;
        currentCourse = location.getBearing();
    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public int compareTo(@NonNull Competitor another) {
        int compare = Float.compare(this.time, another.time);
        if (compare == 0)
            return Float.compare(another.distance, this.distance);
        else return compare;
    }
}
