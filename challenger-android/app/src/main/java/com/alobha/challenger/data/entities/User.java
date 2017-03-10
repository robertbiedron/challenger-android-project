package com.alobha.challenger.data.entities;

import android.support.annotation.NonNull;

import com.alobha.challenger.business.db.Database;
import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mrNRG on 10.06.2016.
 */

@Parcel
@Table(database = Database.class)
public class User extends BaseModel implements Comparable<User>, Serializable{

    @Expose
    @Column
    @NotNull
    @PrimaryKey
    public long id;

    @Expose
    @Column
    public String username;

    @Expose
    @Column
    public String first_name;

    @Expose
    @Column
    public String last_name;

    @Expose
    @Column
    public String email;

    @Expose
    @Column
    public String phone;

    @Expose
    @Column
    public String source;

    @Expose
    @Column
    public String sex;

    @Expose
    @Column
    public String avatar;

    @Expose
    @Column
    public String facebook_id;

    @Expose
    @Column
    public float distance;

    @Expose
    @Column
    public long avg_time;

    @Expose
    @Column
    public float avg_speed;

    @Expose
    @Column
    public float avg_distance;

    @Expose
    @Column
    public Date last_challenge;

    @Override
    public int compareTo(@NonNull User another) {
        return this.id < another.id ? -1 : (id == another.id ? 0 : 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;
        User tmp = (User) obj;
        return this.id == tmp.id;
    }
}
