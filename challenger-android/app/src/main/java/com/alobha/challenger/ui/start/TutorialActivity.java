package com.alobha.challenger.ui.start;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alobha.challenger.R;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.github.paolorotolo.appintro.AppIntro;

/**
 * Created by mrNRG on 13.06.2016.
 */
public class TutorialActivity extends AppIntro {

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, TutorialActivity.class);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(TutorialFragment.newInstance(R.drawable.tutorial1));
        addSlide(TutorialFragment.newInstance(R.drawable.tutorial2));
        addSlide(TutorialFragment.newInstance(R.drawable.tutorial3));
        addSlide(TutorialFragment.newInstance(R.drawable.tutorial4));
        addSlide(TutorialFragment.newInstance(R.drawable.tutorial5));
        setBarColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onSkipPressed() {
        endTutorial();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        endTutorial();
    }

    @Override
    public void onSlideChanged() {
    }

    @Override
    public void onBackPressed() {
        endTutorial();
    }

    private void endTutorial() {
        PersistentPreferences.getInstance().setTutorialShown(true);
        setResult(RESULT_OK);
        finish();
    }
}
