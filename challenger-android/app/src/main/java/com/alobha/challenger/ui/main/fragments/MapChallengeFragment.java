package com.alobha.challenger.ui.main.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alobha.challenger.GlobalConstants;
import com.alobha.challenger.R;
import com.alobha.challenger.business.gps.EstimatedDistanceTracker;
import com.alobha.challenger.business.gps.TrackingController;
import com.alobha.challenger.business.gps.TrackingService;
import com.alobha.challenger.business.receivers.ChallengeReceiver;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.entities.Challenge;
import com.alobha.challenger.data.entities.Competitor;
import com.alobha.challenger.data.entities.User;
import com.alobha.challenger.navigation.MFragmentManager;
import com.alobha.challenger.ui.base.BaseFragment;
import com.alobha.challenger.ui.main.adapters.ChallengeCompetitorAdapter;
import com.alobha.challenger.utils.DefaultFormatter;
import com.alobha.challenger.utils.DialogFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mrNRG on 05.07.2016.
 */
public class MapChallengeFragment extends BaseFragment implements OnMapReadyCallback {
    private LocationManager locationManager;

    public static final String TAG = MapChallengeFragment.class.getSimpleName();
    public static final String CHALLENGE_ID = "challenge_id";
    public static final String OLD_POSITION = "old_position";
    public static final String ESTIMATED_DISTANCE_TRACKER = "estimated_distance_tracker";
    private static final int SHORT_DELAY_MILLIS = 500;
    private static final float METERS_IN_KM = 1000;

    private PersistentPreferences preferences;

    private AlertDialog alertDialog;
    private AlertDialog noGpsDialog;
    private TextToSpeech ttobj;
    private CountDownTimer timer;
    private long countdown = 4000;
    private EstimatedDistanceTracker distancesCalled;

    private boolean started = false;
    private double distance;
    private int oldPosition;
    private List<User> selected;
    private Challenge challenge;

    private List<Competitor> competitors;
    private PolylineOptions polylineOptions;
    private Polyline polyline;
    private ArrayList<Marker> markers;

    private BroadcastReceiver receiver;
    private BroadcastReceiver challengeChangeReceiver;

    private ChallengeCompetitorAdapter mAdapter;
    private SimpleDateFormat timeFormat = DefaultFormatter.timeFormat;
    private DecimalFormat distanceFormat = DefaultFormatter.distanceFormat;
    private DecimalFormat speedFormat = DefaultFormatter.speedFormat;

    @Bind(R.id.tvNumber)
    TextView tvNumber;

    @Bind(R.id.tvDistance)
    TextView tvDistance;

    @Bind(R.id.tvTime)
    TextView tvTime;

    @Bind(R.id.tvSpeed)
    TextView tvSpeed;

    @Bind(R.id.rvUsers)
    RecyclerView recyclerView;

    @Bind(R.id.btnPause)
    ToggleButton btnPause;

    @Bind(R.id.btnEndRun)
    Button btnEndRun;

    public static MapChallengeFragment newInstance() {
        MapChallengeFragment fragment = new MapChallengeFragment();

        Bundle argumentBundle = new Bundle();
        fragment.setArguments(argumentBundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PersistentPreferences.getInstance();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        noGpsDialog = DialogFactory.createAlertMessageNoGps(getActivity());
        if (savedInstanceState == null) {
            distancesCalled = new EstimatedDistanceTracker();
        } else {
            oldPosition = savedInstanceState.getInt(OLD_POSITION);
            distancesCalled = savedInstanceState.getParcelable(ESTIMATED_DISTANCE_TRACKER);
            challenge = (Challenge) savedInstanceState.getSerializable(GlobalConstants.CHALLENGE_NEW_CHALLENGE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(OLD_POSITION, oldPosition);
        outState.putParcelable(ESTIMATED_DISTANCE_TRACKER, distancesCalled);
        outState.putSerializable(GlobalConstants.CHALLENGE_NEW_CHALLENGE, challenge);
//        if (!preferences.isActiveChallengeFinished()) {
//            TrackingService.startService(getContext(), ChallengeReceiver.RECEIVE_CHALLENGE_PAUSED, challenge);
//        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (preferences.getActiveChallenge() == -1) {
            preferences.setActiveChallengePaused(false);
            MFragmentManager.nextFragment(MFragmentManager.MAIN_MY_RESULTS_FRAGMENT, null);
        } else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            noGpsDialog.show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_challenge, container, false);
        ButterKnife.bind(this, view);

        challenge = (Challenge) getArguments().getSerializable(GlobalConstants.CHALLENGE_NEW_CHALLENGE);
        ttobj = new TextToSpeech(getContext().getApplicationContext(), status -> {
        });


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.title_new_challenge));

        if (challenge != null) {
            if (!challenge.completed) {
                setUpAdapter();

//                SupportMapFragment mapFragment =
//                        (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//                mapFragment.getMapAsync(this);
                btnPause.setChecked(preferences.isActiveChallengePaused());
            } else {
                MFragmentManager.nextFragment(MFragmentManager.MAIN_MY_RESULTS_FRAGMENT, null);
            }
        }
    }

    private void setUpAdapter() {
        mAdapter = new ChallengeCompetitorAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initializeLoadingDialog() {
        setLoadingDialog(DialogFactory.createLoadingDialog(getActivity(), getString(R.string.message_gps_search)));
    }

    @Override
    protected void initializePresenter() {
    }

    @OnClick(R.id.btnPause)
    public void onButtonPauseClick() {
        boolean activeChallengePaused = preferences.isActiveChallengePaused();
        if (activeChallengePaused) {
            TrackingService.startService(getContext(), ChallengeReceiver.RECEIVE_CHALLENGE_STARTED, challenge);
        } else if (challenge != null)
            TrackingService.startService(getContext(), ChallengeReceiver.RECEIVE_CHALLENGE_PAUSED, challenge);
        preferences.setActiveChallengePaused(!activeChallengePaused);
        btnPause.setChecked(!activeChallengePaused);
    }

    @OnClick(R.id.btnEndRun)
    public void onButtonEndRunClick() {
        TrackingService.startService(getContext(), ChallengeReceiver.RECEIVE_CHALLENGE_COMPLETED, challenge);
        btnEndRun.setActivated(false);
    }

    @Override
    public void onDestroy() {
        hideLoadingDialog();
        setLoadingDialog(null);
        alertDialog = null;
        TrackingService.startService(getContext(), ChallengeReceiver.RECEIVE_CHALLENGE_COMPLETED, challenge);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(challengeChangeReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
//        recyclerView.setAdapter(null);
        super.onDestroy();
    }

    private void showStartDialog() {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        ttobj.speak("Get ready!", TextToSpeech.QUEUE_ADD, null);
        alertDialog = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
        alertDialog.setTitle("Challenge starting in");
        TextView messageText = new TextView(getActivity());
        messageText.setText("3");
        messageText.setGravity(Gravity.CENTER);
        messageText.setTextSize(20);
        messageText.setTextColor(getResources().getColor(R.color.colorAccent));
        alertDialog.setView(messageText);
        alertDialog.show();
        timer = new CountDownTimer(countdown, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdown = millisUntilFinished;
                messageText.setText(String.valueOf(millisUntilFinished / 1000));
                toneG.startTone(ToneGenerator.TONE_CDMA_PRESSHOLDKEY_LITE, 150);
//                ttobj.speak(messageText.getText().toString(), TextToSpeech.QUEUE_ADD, null);
            }

            @Override
            public void onFinish() {
                if (isResumed()) {
                    DialogFactory.showSnackBarLong(getActivity(), getString(R.string.message_challenge_started));
                }
                ttobj.speak("Run!", TextToSpeech.QUEUE_ADD, null);
                started = true;
                if (alertDialog != null)
                    alertDialog.dismiss();
            }
        }.start();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        hideLoadingDialog();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TrackingController.RECEIVE_POSITION_UPDATE);
        filter.addAction(ChallengeReceiver.RECEIVE_CHALLENGE_COMPLETED);

        challenge.start_date = Calendar.getInstance().getTime();
        mAdapter.setChallenge(challenge);

        competitors = challenge.competitors;
        polylineOptions = new PolylineOptions().width(10)
                .color(getResources().getColor(R.color.colorAccent))
                .geodesic(true);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (started) {
                    if (intent.getAction().equals(TrackingController.RECEIVE_POSITION_UPDATE)) {
                        challenge = (Challenge) intent.getSerializableExtra(GlobalConstants.CHALLENGE_NEW_CHALLENGE);
                        competitors = challenge.competitors;
                        Competitor me = challenge.getCompetitorById(preferences.getUserId());
                        LatLng point = me.getLocation();
                        polylineOptions.add(point);
                        if (polyline != null)
                            polyline.remove();
                        polyline = map.addPolyline(polylineOptions);
                        float zoom = map.getCameraPosition().zoom;
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom > 15 ? zoom : 15));
                        updateMarkers(map);
                        mAdapter.setChallenge(challenge);
                    } else {
                        Competitor competitor = challenge.getCompetitorById(preferences.getUserId());
                        int position = competitor.position;

                        String text;
                        String difference;
                        if (position == 1) {
                            text = "You won! You are number 1! Great work! ";
                            Competitor second = challenge.competitors.get(1);
                            difference = String.format(" You were %d meters in front your competitors ", (long) (competitor.distance - second.distance));
                        } else {
                            Competitor leader = challenge.competitors.get(0);
                            text = " You lost! You are number " + position + "! You have to run faster next time! ";
                            difference = String.format("%d meters behind the leader ", (long) (leader.distance - competitor.distance));
                        }
                        double currentAvgSpeed = competitor.distance / competitor.time * 3600;
                        String speed = String.format("Your average speed was %.2f kilometers per hour ", currentAvgSpeed);
                        appendVoiceFeed(text, SHORT_DELAY_MILLIS);
                        appendVoiceFeed(difference, SHORT_DELAY_MILLIS);
                        appendVoiceFeed(speed, SHORT_DELAY_MILLIS);
                        Log.d(TAG, text + difference + speed);
                        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this);
//                        if (getActivity() != null && isResumed()) {
//                            MFragmentManager.nextFragment(MFragmentManager.MAIN_MY_RESULTS_FRAGMENT, null);
//                        } else {
//                            preferences.setActiveChallengeFinished(true);
//                        }
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);
        map.addPolyline(polylineOptions);
        TrackingService.startService(getContext(), ChallengeReceiver.RECEIVE_CHALLENGE_STARTED, challenge);
        showStartDialog();
    }

    private void updateMarkers(GoogleMap map) {
        if (markers == null) {
            markers = new ArrayList<>(challenge.competitors.size());
            for (int i = 0; i < challenge.competitors.size(); i++) {
                User user = competitors.get(i).user;
                int colorId = user.id == preferences.getUserId() ? R.color.colorAccent : R.color.colorPrimary;
                float hue[] = new float[3];
                Color.colorToHSV(getResources().getColor(colorId), hue);
                markers.add(map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(hue[0]))
                        .alpha(hue[1])
                        .title(user.first_name)
                        .position(competitors.get(i).getLocation())));
            }
        } else {
            for (int i = 0; i < competitors.size(); i++) {
                Competitor competitor = competitors.get(i);
                if (competitor.user.id == preferences.getUserId()) {
                    if (competitor.position != oldPosition && competitor.distance > 500) {
                        if (oldPosition != 0) {
                            String text = "You are now number " + competitor.position;
                            appendVoiceFeed(text, SHORT_DELAY_MILLIS);
                        }
                        oldPosition = competitor.position;
                    }
                    if (competitor.distance > 500) {
                        float estimated = challenge.distance - competitor.distance;
                        String estimatedDistance = distancesCalled.getText(estimated);
                        if (estimatedDistance != null) {
                            appendVoiceFeed(estimatedDistance, SHORT_DELAY_MILLIS);
                            String text = "You are now number " + competitor.position;
                            appendVoiceFeed(text, SHORT_DELAY_MILLIS);
                            String averageSpeed = String.format("Average speed: %.2f kilometers per hour ", competitor.avg_speed);
                            appendVoiceFeed(averageSpeed, SHORT_DELAY_MILLIS);
                            Log.d(TAG, averageSpeed);
                            if (competitor.position == 1)
                                for (int j = 0; j < competitors.size(); j++) {
                                    if (competitors.get(j).position == 2) {
                                        double difference = competitor.distance - competitors.get(j).distance;
                                        if (!Double.isInfinite(difference)) {
                                            String interval = String.format("%d meters infront your competitors ", (long) difference);
                                            appendVoiceFeed(interval, SHORT_DELAY_MILLIS);
                                            Log.d(TAG, interval);
                                        }
                                    }
                                }
                            else {
                                for (int j = 0; j < competitors.size(); j++) {
                                    if (competitors.get(j).position == 1) {
                                        double difference = competitors.get(j).distance - competitor.distance;
                                        if (!Double.isInfinite(difference)) {
                                            String interval = String.format("%d meters behind the leader ", (long) difference);
                                            appendVoiceFeed(interval, SHORT_DELAY_MILLIS);
                                            Log.d(TAG, interval);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    updateUI(competitor);
                } else {
                    //Log.i("Competitor distance", String.valueOf(competitor.distance));
                }
                markers.get(i).setPosition(competitor.getLocation());
            }
        }
    }

    private void updateUI(Competitor competitor) {
        if (tvNumber != null) {
            tvNumber.setText(String.valueOf(competitor.position));

            tvSpeed.setText(String.format(this.getString(R.string.speed_wrapper), speedFormat.format(competitor.avg_speed)));
            float distanceInKilometers = competitor.distance / 1000;
            tvDistance.setText(String.format(this.getString(R.string.distance_wrapper), distanceFormat.format(Float.isInfinite(distanceInKilometers) ? 0 : distanceInKilometers)));
            final Calendar cal = Calendar.getInstance(DefaultFormatter.UTC);
            cal.setTimeInMillis(competitor.time);
            final String timeString = timeFormat.format(cal.getTime());
            tvTime.setText(timeString);
        }
    }

    public void appendVoiceFeed(String feed, long delayMillis) {
        if (Build.VERSION.SDK_INT >= 21) {
            ttobj.speak(feed, TextToSpeech.QUEUE_ADD, null, "voice_feed");
            if (delayMillis != 0)
                ttobj.playSilentUtterance(delayMillis, TextToSpeech.QUEUE_ADD, "voice_feed");
        } else {
            ttobj.speak(feed, TextToSpeech.QUEUE_ADD, null);
            if (delayMillis != 0)
                ttobj.playSilence(delayMillis, TextToSpeech.QUEUE_ADD, null);
        }
    }
}
