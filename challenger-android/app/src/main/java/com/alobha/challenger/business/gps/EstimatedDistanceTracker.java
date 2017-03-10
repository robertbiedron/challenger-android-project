package com.alobha.challenger.business.gps;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by mrNRG on 3/14/2016.
 * Class to track the estimated distance voice messages. Used in
 * {@link com.alobha.challenger.ui.main.fragments.MapChallengeFragment}.
 */
public class EstimatedDistanceTracker implements Parcelable {
    private static final int meters[] = {100, 200, 300, 400, 500};
    private static final int kilometersRange = 20;
    private static final int METER_IN_KM = 1000;
    private ArrayList<Pair> distanceCalled;

    public EstimatedDistanceTracker() {
        distanceCalled = new ArrayList<>(meters.length + kilometersRange);
        for (int meter : meters)
            distanceCalled.add(new Pair(meter, false));
        for (int i = 1; i <= kilometersRange; i++) {
            distanceCalled.add(new Pair(i * METER_IN_KM, false));
        }
    }

    protected EstimatedDistanceTracker(Parcel in) {
        distanceCalled = new ArrayList<>();
        distanceCalled = in.readArrayList(Pair.class.getClassLoader());
    }

    public static final Creator<EstimatedDistanceTracker> CREATOR = new Creator<EstimatedDistanceTracker>() {
        @Override
        public EstimatedDistanceTracker createFromParcel(Parcel in) {
            return new EstimatedDistanceTracker(in);
        }

        @Override
        public EstimatedDistanceTracker[] newArray(int size) {
            return new EstimatedDistanceTracker[size];
        }
    };

    @Nullable
    public String getText(float distance) {
        for (int i = 0; i < distanceCalled.size(); i++) {
            Integer bond = distanceCalled.get(i).first;
            if ((distance < bond) && ((bond - distance) <= 50)) {
                if (!distanceCalled.get(i).second) {
                    distanceCalled.set(i, Pair.create(bond, true));
                    if (i < meters.length) {
                        return bond + " meters left of your challenge.";
                    } else if (bond == METER_IN_KM) {
                        return bond / METER_IN_KM + " kilometer left of your challenge.";
                    } else {
                        return bond / METER_IN_KM + " kilometers left of your challenge.";
                    }
                } else {
                    break;
                }
            }
        }
        return null;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(distanceCalled);
    }

    private static class Pair extends android.util.Pair<Integer, Boolean> implements Parcelable {

        /**
         * Constructor for a Pair.
         *
         * @param first  the first object in the Pair
         * @param second the second object in the pair
         */
        public Pair(Integer first, Boolean second) {
            super(first, second);
        }

        protected Pair(Parcel in) {
            super(in.readInt(), in.readByte() == 1);
        }

        public static final Creator<Pair> CREATOR = new Creator<Pair>() {
            @Override
            public Pair createFromParcel(Parcel in) {
                return new Pair(in);
            }

            @Override
            public Pair[] newArray(int size) {
                return new Pair[size];
            }
        };

        @Override
        public int describeContents() {
            return hashCode();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(first);
            dest.writeByte(second ? (byte) (1) : (byte) (0));
        }

        public static Pair create(Integer a, Boolean b) {
            return new Pair(a, b);
        }
    }


}
