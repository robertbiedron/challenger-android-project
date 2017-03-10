package com.alobha.challenger.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by mrNRG on 20.06.2016.
 */
public class DefaultFormatter {
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    public static DecimalFormat distanceFormat = new DecimalFormat("##.##");
    public static DecimalFormat speedFormat = new DecimalFormat("##.#");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());

    static {
        timeFormat.setTimeZone(UTC);
        distanceFormat.setRoundingMode(RoundingMode.FLOOR);
        speedFormat.setRoundingMode(RoundingMode.HALF_UP);
    }
}
