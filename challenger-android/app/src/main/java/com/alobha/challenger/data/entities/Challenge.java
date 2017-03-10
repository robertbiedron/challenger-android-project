package com.alobha.challenger.data.entities;

import android.support.annotation.NonNull;

import com.alobha.challenger.business.db.Database;
import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by mrNRG on 16.06.2016.
 */

@Parcel
@Table(database = Database.class)
public class Challenge extends BaseModel implements Serializable, Comparable<Challenge> {

    @Expose
    @Column
    @PrimaryKey
    @NotNull
    public long id;

    @Expose
    @Column
    public long host;

    @Column
    @ForeignKey(saveForeignKeyModel = false)
    public User owner;

    @Expose
    @Column
    public float distance;

    @Expose
    @Column
    public long time;

    @Expose
    @Column
    public Date start_date;

    @Expose
    @Column
    public Date end_date;

    @Expose
    public List<Competitor> competitors;

    @Column
    public int synced;

    @Column
    public boolean completed;

    @Column
    public boolean seen;

    public Competitor getCompetitorById(long userId) {
        for (Competitor competitor : competitors) {
            if (competitor.user.id == userId)
                return competitor;
        }
        return null;
    }

    @Override
    public int compareTo(@NonNull Challenge another) {
        int compare = Float.compare(this.start_date.getTime(), another.start_date.getTime());
        if (compare == 0)
            return Float.compare(another.time, this.time);
        else return compare;
    }

//    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "competitors")
//    public List<Competitor> getCompetitors() {
//        if (competitors == null || competitors.isEmpty()) {
//            competitors = SQLite.select()
//                    .from(Competitor.class)
//                    .where(Competitor_Table.challenge_id.eq(id))
//                    .queryList();
//        }
//        return competitors;
//    }
}
