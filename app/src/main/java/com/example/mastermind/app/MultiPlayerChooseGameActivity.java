
package com.example.mastermind.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.ParticipantResult;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.BaseGameActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Our main activity for the game.
 *
 * IMPORTANT: Before attempting to run this sample, please change
 * the package name to your own package name (not com.android.*) and
 * replace the IDs on res/values/ids.xml by your own IDs (you must
 * create a game in the developer console to get those IDs).
 *
 * @author Bruno Oliveira
 */
public class MultiPlayerChooseGameActivity extends BaseGameActivity
        implements OnInvitationReceivedListener,
        OnTurnBasedMatchUpdateReceivedListener {

    // Local convenience pointers
    public TextView mDataView;
    public TextView mTurnTextView;

    private AlertDialog mAlertDialog;

    final static int RC_COMBINATION_REQUEST = 1;

    final static int RESULT_FINISH = 2;

    // For our intents
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;

    public static GoogleApiClient s_ApiClient;

    // How long to show toasts.
    final static int TOAST_DELAY = 2000;

    // Should I be showing the turn API?
    public boolean isDoingTurn = false;

    // This is the current match we're in; null if not loaded
    public TurnBasedMatch mMatch;

    // This is the current match data after being unpersisted.
    // Do not retain references to match data once you have
    // taken an action on the match, such as takeTurn()
    // QUESTO AL MOMENTO è STATIC PERCHè NON HO ANCORA CAPITO COME PASSARLO ALLA NUOVA ACTIVITY!
    public static Turn mTurnData;

    // Fragments
//    MainMenuFragment mMainMenuFragment;
//    GameplayFragment mGameplayFragment;
    MultiPlayerGamePlay mMultiPlayerActivity;

    // request codes we use when invoking an external activity
    final int RC_RESOLVE = 5000, RC_UNUSED = 5001;

    // tag for debug logging
    final boolean ENABLE_DEBUG = true;
    final String TAG = "TanC";

    // playing on single mode?
    boolean mSingleMode = false;

    // achievements and scores we're pending to push to the cloud
    // (waiting for the user to sign in, for instance)
    AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        enableDebugLog(ENABLE_DEBUG, TAG);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // create fragments
//        mMainMenuFragment = new MainMenuFragment();
//        mMainMenuFragment.setListener(this);
        // add initial fragment (welcome fragment)
//        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
//                mMainMenuFragment).commit();
/*        mGameplayFragment = new GameplayFragment();

        // listen to fragment events
        mGameplayFragment.setListener(this);

        // IMPORTANT: if this Activity supported rotation, we'd have to be
        // more careful about adding the fragment, since the fragment would
        // already be there after rotation and trying to add it again would
        // result in overlapping fragments. But since we don't support rotation,
        // we don't deal with that for code simplicity.

        // load outbox from file
        mOutbox.loadLocal(this);*/


        // Setup signin button
//        findViewById(R.id.sign_out_button).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        signOut();
//                        setViewVisibility();
//                    }
//                });

//        findViewById(R.id.sign_in_button).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // start the asynchronous sign in flow
//                        beginUserInitiatedSignIn();
//
//                        findViewById(R.id.sign_in_button).setVisibility(
//                                View.GONE);
//
//                    }
//                }
        //);

        mDataView = ((TextView) findViewById(R.id.data_view));
        mTurnTextView = ((TextView) findViewById(R.id.turn_counter_view));

        Typeface face = Typeface.createFromAsset(getAssets(), "CarterOne.ttf");
        TextView tv = (TextView) findViewById(R.id.checkGamesButton);
        tv.setTypeface(face);

        tv = (TextView) findViewById(R.id.startMatchButton);
        tv.setTypeface(face);

        tv = (TextView) findViewById(R.id.quickMatchButton);
        tv.setTypeface(face);

        setViewVisibility();
    }

    // Displays your inbox. You will get back onActivityResult where
    // you will need to figure out what you clicked on.
    public void onCheckGamesClicked(View view) {
        Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(getApiClient());
        startActivityForResult(intent, RC_LOOK_AT_MATCHES);
    }

    // Open the create-game UI. You will get back an onActivityResult
    // and figure out what to do.
    public void onStartMatchClicked(View view) {
        Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(getApiClient(),
                1, 1, true);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    public void onSingleMatchClicked(View view) {

        Intent intent = new Intent(getApplicationContext(),SinglePlayerGamePlay.class);
        startActivity(intent);
    }

    // Create a one-on-one automatch game.
    public void onQuickMatchClicked(View view) {

        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                1, 1, 0);

        TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                .setAutoMatchCriteria(autoMatchCriteria).build();


        // Start the match
        ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> cb = new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
            @Override
            public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                processResult(result);
            }
        };
        Games.TurnBasedMultiplayer.createMatch(getApiClient(), tbmc).setResultCallback(cb);
    }

    // In-game controls

    // This function is what gets called when you return from either the Play
    // Games built-in inbox, or else the create game built-in interface.
    @Override
    public void onActivityResult(int request, int response, Intent data) {
        // It's VERY IMPORTANT for you to remember to call your superclass.
        // BaseGameActivity will not work otherwise.
        super.onActivityResult(request, response, data);

        if (request == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            TurnBasedMatch match = data
                    .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                updateMatch(match);
            }

            Log.d(TAG, "Match = " + match);
        } else if (request == RC_SELECT_PLAYERS) {
            // Returned from 'Select players to Invite' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            // get the invitee list
            final ArrayList<String> invitees = data
                    .getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get automatch criteria
            Bundle autoMatchCriteria = null;

            int minAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees)
                    .setAutoMatchCriteria(autoMatchCriteria).build();

            // Start the match
            Games.TurnBasedMultiplayer.createMatch(getApiClient(), tbmc).setResultCallback(
                    new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                            processResult(result);
                        }
                    }
            );
        }
        else if (request == RC_COMBINATION_REQUEST)
        {
            if (response == Activity.RESULT_OK)
                takeTurn(data);
            else if (response==RESULT_FINISH)
                finishMatch();
            else
            {
                isDoingTurn=false;
                setViewVisibility();
            }
        }
    }

    public void takeTurn(Intent data)
    {
        boolean incrementTurnCount = false;

        ArrayList<Integer> res = data.getIntegerArrayListExtra("combination");
        if (Games.Players.getCurrentPlayerId(getApiClient()).equals(mTurnData.player1Id))
        {
            if (mTurnData.player1Num.equals(""))
                mTurnData.player1Num = NumberHelper.GetString(res);
            else {
                if (!mTurnData.data1.isEmpty())
                    mTurnData.data1 += "-";
                mTurnData.data1 += NumberHelper.GetString(res);
                incrementTurnCount = true;
            }
        }
        else
        {
            if (mTurnData.player2Num.equals(""))
                mTurnData.player2Num = NumberHelper.GetString(res);
            else {
                if (!mTurnData.data1.isEmpty())
                    mTurnData.data1 += "-";
                mTurnData.data1 += NumberHelper.GetString(res);
            }
        }


        String nextParticipantId = getNextParticipantId();

        // Solo il player 1 incrementa il contatore dei turni.. Questo contatore ci serve per gli achievements
        if (incrementTurnCount)
            mTurnData.turnCounter += 1;

        Games.TurnBasedMultiplayer.takeTurn(getApiClient(), mMatch.getMatchId(),
                mTurnData.persist(), nextParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                }
        );

    }

    public void finishMatch()
    {
        String playerId = Games.Players.getCurrentPlayerId(getApiClient());
        String myParticipantId = mMatch.getParticipantId(playerId);
        ParticipantResult playerResult = new ParticipantResult(myParticipantId, ParticipantResult.MATCH_RESULT_WIN, 1);

        String nextParticipantId = getNextParticipantId();
        ParticipantResult opponentResult = new ParticipantResult(nextParticipantId, ParticipantResult.MATCH_RESULT_LOSS, 2);

        List<ParticipantResult> resultList = new ArrayList<ParticipantResult>();
        resultList.add(playerResult);
        resultList.add(opponentResult);

        Games.TurnBasedMultiplayer.finishMatch(getApiClient(), mMatch.getMatchId(),
                mTurnData.persist(), resultList).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                }
        );

    }


    // Sign-in, Sign out behavior

    // Update the visibility based on what state we're in.
    public void setViewVisibility()
    {
        if (!isSignedIn())
        {
            //findViewById(R.id.login_layout).setVisibility(View.GONE);
            //findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.matchup_layout).setVisibility(View.VISIBLE);
//            findViewById(R.id.gameplay_layout).setVisibility(View.GONE);
            //findViewById(R.id.sign_out_button).setVisibility(View.GONE);
//            mMainMenuFragment.setShowSignInButton(true);

            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
            return;
        }

//        mMainMenuFragment.setShowSignInButton(false);

        //findViewById(R.id.login_layout).setVisibility(View.GONE);

        //findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        //findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);

        if (isDoingTurn) {
//            findViewById(R.id.matchup_layout).setVisibility(View.GONE);
            byte curPlayer = 1;
            if (!Games.Players.getCurrentPlayerId(getApiClient()).equals(mTurnData.player1Id))
                curPlayer = 2;
            //mMultiPlayerActivity = new MultiPlayerGamePlay(mTurnData, curPlayer);
            Intent intent = new Intent(getApplicationContext(),MultiPlayerGamePlay.class);
            intent.putExtra("curPlayer", curPlayer);
            startActivityForResult(intent, RC_COMBINATION_REQUEST);
            //findViewById(R.id.gameplay_layout).setVisibility(View.VISIBLE);
        } else {
//            findViewById(R.id.matchup_layout).setVisibility(View.VISIBLE);
//            findViewById(R.id.gameplay_layout).setVisibility(View.GONE);
        }

    }

    // Switch UI to the given fragment
    void switchToFragment(Fragment newFrag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFrag)
                .commit();
    }

    public void onShowAchievementsRequested(View view) {
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()),
                    RC_UNUSED);
        } else {
            showAlert(getString(R.string.achievements_not_available));
        }
    }

    public void ShowInstructions()
    {
        startActivity(new Intent(getApplicationContext(), HelpActivity.class));
    }

    public void onMultiPlayerClicked(View view)
    {
        startActivity(new Intent(getApplicationContext(), MultiPlayerChooseGameActivity.class));
    }

    public void onShowLeaderboardsRequested(View view)
    {
        if (isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()),
                    RC_UNUSED);
            //startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(), getString(R.string.leaderboard_multi)), RC_UNUSED);
        } else {
            showAlert(getString(R.string.leaderboards_not_available));
        }
    }

    public void onEnteredScore(int finalScore) {

        // check for achievements
        checkForAchievements(finalScore, finalScore);

        // update leaderboards
        updateLeaderboards(finalScore);
    }

    /**
     * Check for achievements and unlock the appropriate ones.
     *
     * @param requestedScore the score the user requested.
     * @param finalScore the score the user got.
     */
    void checkForAchievements(int requestedScore, int finalScore) {
        // Check if each condition is met; if so, unlock the corresponding
        // achievement.
//        if (isPrime(finalScore)) {
//            mOutbox.mPrimeAchievement = true;
//            achievementToast(getString(R.string.achievement_prime_toast_text));
//        }
        if (mSingleMode)
            mOutbox.mSingleAchievement = true;
        else
            mOutbox.mMultiAchievement = true;

        if (mTurnData.data1.isEmpty() && mTurnData.data2.isEmpty())
            mOutbox.mTelepathicAchievement = true;

        if (mTurnData.turnCounter<=5)
        {
            mOutbox.mCleverAchievement = true;
            if (mTurnData.turnCounter<=3)
                mOutbox.mMasterAchievement = true;
        }

        // Manca solo la gestione del "LuckyDay" Achievement.. è un po diverso rispetto agli altri perchè dovrebbe essere controllato al primo turno e non a fine match


        mOutbox.mBoredSteps++;

        pushAccomplishments();
    }

    void unlockAchievement(int achievementId, String fallbackString) {
        if (isSignedIn()) {
            Games.Achievements.unlock(getApiClient(), getString(achievementId));
        } else {
            Toast.makeText(this, getString(R.string.achievement) + ": " + fallbackString,
                    Toast.LENGTH_LONG).show();
        }
    }

    void achievementToast(String achievement) {
        // Only show toast if not signed in. If signed in, the standard Google Play
        // toasts will appear, so we don't need to show our own.
        if (!isSignedIn()) {
            Toast.makeText(this, getString(R.string.achievement) + ": " + achievement,
                    Toast.LENGTH_LONG).show();
        }
    }

    void pushAccomplishments() {
        if (!isSignedIn()) {
            // can't push to the cloud, so save locally
            mOutbox.saveLocal(this);
            return;
        }
        if (mOutbox.mSingleAchievement) {
            Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_solo));
            mOutbox.mSingleAchievement = false;
        }
        if (mOutbox.mMultiAchievement) {
            Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_beginner));
            mOutbox.mMultiAchievement = false;
        }
        if (mOutbox.mLuckyAchievement) {
            Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_lucky_day));
            mOutbox.mLuckyAchievement = false;
        }
        if (mOutbox.mTelepathicAchievement) {
            Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_telepathic));
            mOutbox.mTelepathicAchievement = false;
        }

        if (mOutbox.mCleverAchievement) {
            Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_clever_mind));
            mOutbox.mCleverAchievement = false;
        }

        if (mOutbox.mMasterAchievement) {
            Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_master_mind));
            mOutbox.mMasterAchievement = false;
        }

        if (mOutbox.mBoredSteps > 0) {
            Games.Achievements.increment(getApiClient(), getString(R.string.achievement_really_bored),
                    mOutbox.mBoredSteps);
            Games.Achievements.increment(getApiClient(), getString(R.string.achievement_bored),
                    mOutbox.mBoredSteps);
        }
        if (mOutbox.mMultiPlayerModeScore >= 0) {
            Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_multi),
                    mOutbox.mMultiPlayerModeScore);
            mOutbox.mMultiPlayerModeScore = -1;
        }
        if (mOutbox.mSinglePlayerModeScore >= 0) {
            Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_single),
                    mOutbox.mSinglePlayerModeScore);
            mOutbox.mSinglePlayerModeScore = -1;
        }
        mOutbox.saveLocal(this);
    }

    /**
     * Update leaderboards with the user's score.
     *
     * @param finalScore The score the user got.
     */
    void updateLeaderboards(final int finalScore) {
        if (mSingleMode && mOutbox.mSinglePlayerModeScore < finalScore) {
            mOutbox.mSinglePlayerModeScore = finalScore;
        } else if (!mSingleMode)
        {
            if (mOutbox.mMultiPlayerModeScore < 0)
            {
                Games.Leaderboards.loadCurrentPlayerLeaderboardScore(getApiClient(),
                        getString(R.string.leaderboard_multi), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC)
                        .setResultCallback(
                                new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
                                    @Override
                                    public void onResult(Leaderboards.LoadPlayerScoreResult result)
                                    {
                                        mOutbox.mMultiPlayerModeScore = finalScore;
                                        LeaderboardScore score = result.getScore();
                                        if (score != null)
                                            mOutbox.mMultiPlayerModeScore += (int) score.getRawScore();

                                        // push those accomplishments to the cloud, if signed in
                                        pushAccomplishments();
                                    }
                                }
                        );
            }
            else
            {
                mOutbox.mMultiPlayerModeScore += finalScore;
                pushAccomplishments();
            }
        }
    }

    @Override
    public void onSignInFailed() {
        // Sign-in failed, so show sign-in button on main menu
//        mMainMenuFragment.setGreeting(getString(R.string.signed_out_greeting));
//        mMainMenuFragment.setShowSignInButton(true);

        setViewVisibility();
    }

    @Override
    public void onSignInSucceeded() {
        // Show sign-out button on main menu
        //mMainMenuFragment.setShowSignInButton(false);

        s_ApiClient = getApiClient();

        Button signbtn = ((Button) findViewById(R.id.sign_in_button));
        try {
            Connection a = new Connection();
            Drawable img = (Drawable)a.execute(Games.Players.getCurrentPlayer(getApiClient()).getHiResImageUrl()).get();
            signbtn.setBackgroundDrawable(img);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }


        if (mHelper.getTurnBasedMatch() != null) {
            // GameHelper will cache any connection hint it gets. In this case,
            // it can cache a TurnBasedMatch that it got from choosing a turn-based
            // game notification. If that's the case, you should go straight into
            // the game.
            updateMatch(mHelper.getTurnBasedMatch());
            return;
        }

        setViewVisibility();

        // As a demonstration, we are registering this activity as a handler for
        // invitation and match events.

        // This is *NOT* required; if you do not register a handler for
        // invitation events, you will get standard notifications instead.
        // Standard notifications may be preferable behavior in many cases.
        Games.Invitations.registerInvitationListener(getApiClient(), this);

        // Likewise, we are registering the optional MatchUpdateListener, which
        // will replace notifications you would get otherwise. You do *NOT* have
        // to register a MatchUpdateListener.
        //Games.TurnBasedMultiplayer.registerMatchUpdateListener(getApiClient(), this);
    }

    public void onSignInButtonClicked(View view) {
        // start the sign-in flow
        if (!isSignedIn())
            beginUserInitiatedSignIn();
        else
        {
            signOut();
            Button signBtn = (Button)findViewById(R.id.sign_in_button);
            Drawable glight = getResources().getDrawable(R.drawable.glight);
            signBtn.setBackgroundDrawable(glight);
        }
    }

    public void onSignOutButtonClicked() {
        signOut();
//        mMainMenuFragment.setGreeting(getString(R.string.signed_out_greeting));
//        mMainMenuFragment.setShowSignInButton(true);
    }

    // Switch to gameplay view.
    public void setGameplayUI() {
        isDoingTurn = true;
        setViewVisibility();
        //mDataView.setText(mTurnData.data);


        //mTurnTextView.setText("Turn " + mTurnData.turnCounter);
    }

    // Generic warning/info dialog
    public void showWarning(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                    }
                });

        // create alert dialog
        mAlertDialog = alertDialogBuilder.create();

        // show it
        mAlertDialog.show();
    }

    // Rematch dialog
    public void askForRematch() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Do you want a rematch?");

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Sure, rematch!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                rematch();
                            }
                        })
                .setNegativeButton("No.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

        alertDialogBuilder.show();
    }

    class AccomplishmentsOutbox {
        boolean mCleverAchievement = false;
        boolean mMasterAchievement = false;
        boolean mSingleAchievement = false;
        boolean mMultiAchievement = false;
        boolean mLuckyAchievement = false;
        boolean mTelepathicAchievement = false;
        int mBoredSteps = 0;
        int mSinglePlayerModeScore = -1;
        int mMultiPlayerModeScore = -1;

//        boolean isEmpty() {
//            return !mPrimeAchievement && !mHumbleAchievement && !mLeetAchievement &&
//                    !mArrogantAchievement && mBoredSteps == 0 && mSinglePlayerModeScore < 0 &&
//                    mMultiPlayerModeScore < 0;
//        }

        public void saveLocal(Context ctx) {
            /* TODO: This is left as an exercise. To make it more difficult to cheat,
             * this data should be stored in an encrypted file! And remember not to
             * expose your encryption key (obfuscate it by building it from bits and
             * pieces and/or XORing with another string, for instance). */
        }

        public void loadLocal(Context ctx) {
            /* TODO: This is left as an exercise. Write code here that loads data
             * from the file you wrote in saveLocal(). */
        }
    }

    // startMatch() happens in response to the createTurnBasedMatch()
    // above. This is only called on success, so we should have a
    // valid match object. We're taking this opportunity to setup the
    // game, saving our initial state. Calling takeTurn() will
    // callback to OnTurnBasedMatchUpdated(), which will show the game
    // UI.
    public void startMatch(TurnBasedMatch match) {
        mTurnData = new Turn();
        // Some basic turn data
        //mTurnData.data = "First turn";

        mMatch = match;

        String playerId = Games.Players.getCurrentPlayerId(getApiClient());
        String myParticipantId = mMatch.getParticipantId(playerId);

        mTurnData.player1Id = playerId;
        mTurnData.player2Id = myParticipantId;

        Games.TurnBasedMultiplayer.takeTurn(getApiClient(), match.getMatchId(),
                mTurnData.persist(), myParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });
    }

    // If you choose to rematch, then call it and wait for a response.
    public void rematch() {
        Games.TurnBasedMultiplayer.rematch(getApiClient(), mMatch.getMatchId()).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                        processResult(result);
                    }
                });
        mMatch = null;
        isDoingTurn = false;
    }

    /**
     * Get the next participant. In this function, we assume that we are
     * round-robin, with all known players going before all automatch players.
     * This is not a requirement; players can go in any order. However, you can
     * take turns in any order.
     *
     * @return participantId of next player, or null if automatching
     */
    public String getNextParticipantId() {
        String playerId = Games.Players.getCurrentPlayerId(getApiClient());
        String myParticipantId = mMatch.getParticipantId(playerId);

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }

    // This is the main function that gets called when players choose a match
    // from the inbox, or else create a match and want to start it.
    public void updateMatch(TurnBasedMatch match) {
        mMatch = match;

        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                showWarning("Canceled!", "This game was canceled!");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                showWarning("Expired!", "This game is expired.  So sad!");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                showWarning("Waiting for auto-match...",
                        "We're still waiting for an automatch partner.");
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    showWarning(
                            "Complete!",
                            "This game is over; someone finished it, and so did you!  There is nothing to be done.");
                    break;
                }

                // Note that in this state, you must still call "Finish" yourself,
                // so we allow this to continue.
                showWarning("Complete!",
                        "This game is over; someone finished it!  You can only finish it now.");
        }

        // OK, it's active. Check on turn status.
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                mTurnData = Turn.unpersist(mMatch.getData());
                setGameplayUI();
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                // Should return results.
                showWarning("Alas...", "It's not your turn.");
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                showWarning("Good inititative!",
                        "Still waiting for invitations.\n\nBe patient!");
        }

        setViewVisibility();
    }

    private void processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        TurnBasedMatch match = result.getMatch();

        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }

        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            updateMatch(match);
            return;
        }

        startMatch(match);
    }


    public void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
        TurnBasedMatch match = result.getMatch();

        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            mTurnData = null;
            return;
        }

        // Se ho terminato il match, aggiungo 3 punti al giocatore corrente che è il vincitore
        if (match.getStatus() == TurnBasedMatch.MATCH_STATUS_COMPLETE) {
            onEnteredScore(3);
            //mTurnData = null;
        }

        mTurnData = null;

        // Proposta rivincita
        if (match.canRematch()) {
            askForRematch();
        }

        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);

        if (isDoingTurn) {
            updateMatch(match);
            return;
        }

        setViewVisibility();
    }

    // Handle notification events.
    @Override
    public void onInvitationReceived(Invitation invitation) {
        Toast.makeText(
                this,
                "An invitation has arrived from "
                        + invitation.getInviter().getDisplayName(), TOAST_DELAY)
                .show();
    }

    @Override
    public void onInvitationRemoved(String invitationId) {
        Toast.makeText(this, "An invitation was removed.", TOAST_DELAY).show();
    }

    @Override
    public void onTurnBasedMatchReceived(TurnBasedMatch match) {
        Toast.makeText(this, "A match was updated.", TOAST_DELAY).show();
    }

    @Override
    public void onTurnBasedMatchRemoved(String matchId) {
        Toast.makeText(this, "A match was removed.", TOAST_DELAY).show();

    }

    public void showErrorMessage(TurnBasedMatch match, int statusCode,
                                 int stringId) {

        showWarning("Warning", getResources().getString(stringId));
    }

    // Returns false if something went wrong, probably. This should handle
    // more cases, and probably report more accurate results.
    private boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                // This is OK; the action is stored by Google Play Services and will
                // be dealt with later.
                Toast.makeText(
                        this,
                        "Stored action for later.  (Please remove this toast before release.)",
                        TOAST_DELAY).show();
                // NOTE: This toast is for informative reasons only; please remove
                // it from your final application.
                return true;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(match, statusCode,
                        R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_already_rematched);
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(match, statusCode,
                        R.string.network_error_operation_failed);
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                showErrorMessage(match, statusCode,
                        R.string.client_reconnect_required);
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                showErrorMessage(match, statusCode, R.string.internal_error);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(match, statusCode,
                        R.string.match_error_inactive_match);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_locally_modified);
                break;
            default:
                showErrorMessage(match, statusCode, R.string.unexpected_status);
                Log.d(TAG, "Did not have warning or string to deal with: "
                        + statusCode);
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.help:
                ShowInstructions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class Connection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            return getDrawableFromURL((String)arg0[0]);
        }

    }

    private Drawable getDrawableFromURL(String url1) {
        try
        {
            URL url = new URL(url1);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            myBitmap = createRoundImage(myBitmap);
            Drawable draw = new BitmapDrawable(getResources(),myBitmap);
            return draw;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap createRoundImage(Bitmap loadedImage) {
        Bitmap circleBitmap = Bitmap.createBitmap(loadedImage.getWidth(), loadedImage.getHeight(), Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(loadedImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(loadedImage.getWidth() / 2, loadedImage.getHeight() / 2, loadedImage.getWidth() / 2, paint);

        return circleBitmap;
    }
}
