package com.example.mastermind.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

/**
 * Created by Francesco on 18/06/2014.
 */
public class MultiPlayerChooseGameActivity extends ActionBarActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        Typeface face = Typeface.createFromAsset(getAssets(), "CarterOne.ttf");
        TextView tv = (TextView) findViewById(R.id.checkGamesButton);
        tv.setTypeface(face);

        tv = (TextView) findViewById(R.id.startMatchButton);
        tv.setTypeface(face);

        tv = (TextView) findViewById(R.id.quickMatchButton);
        tv.setTypeface(face);
    }



    public void onStartMatchClicked(View view) {
        Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(MainActivity.s_ApiClient,
                1, 1, true);
        startActivityForResult(intent, MainActivity.RC_SELECT_PLAYERS);
    }

    public void onCheckGamesClicked(View view) {
        Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(MainActivity.s_ApiClient);
        startActivityForResult(intent, MainActivity.RC_LOOK_AT_MATCHES);
    }

    // Create a one-on-one automatch game.
    public void onQuickMatchClicked(View view) {

//        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
//                1, 1, 0);
//
//        TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
//                .setAutoMatchCriteria(autoMatchCriteria).build();
//
//
//        // Start the match
//        ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> cb = new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
//            @Override
//            public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
//                processResult(result);
//            }
//        };
//        Games.TurnBasedMultiplayer.createMatch(getApiClient(), tbmc).setResultCallback(cb);
    }
}
