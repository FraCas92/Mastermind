package com.example.mastermind.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import Bean.ActionItem;
import QuickAction.QuickAction;


public class MultiPlayerGamePlay extends ActionBarActivity {

    ImageView mImg_primo_colore;
    ImageView mImg_secondo_colore;
    ImageView mImg_terzo_colore;
    ImageView mImg_quarto_colore;
    ImageView mChangeImage;  // variabile che conterra' l'ID dell'immagine da modificare ogni qual volta si apre QuickAction

    ArrayList<Integer> mCombinazioneScelta = new ArrayList<Integer>(4);

    Integer mPos_colore;

    byte mCurPlayer;

    QuickAction mQuickAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        // TODO: qui nell'OnCreate dovrò valorizzare la tavola iniziale con i miei tentativi precedenti..
        // Per farlo devo leggere da mTurnData...

        mCurPlayer = getIntent().getByteExtra("curPlayer",(byte)0);

        for (int i=0; i<4; i++)
            mCombinazioneScelta.add(-1);

        // Recupero id delle immagini
        mImg_primo_colore = (ImageView) findViewById(R.id.imageView);
        mImg_secondo_colore = (ImageView) findViewById(R.id.imageView2);
        mImg_terzo_colore = (ImageView) findViewById(R.id.imageView3);
        mImg_quarto_colore = (ImageView) findViewById(R.id.imageView4);

        // Colori da visualizzare nella Quick Action (per la scelta del colore)
        ActionItem addBlu = new ActionItem();
        ActionItem addGreen = new ActionItem();
        ActionItem addRed = new ActionItem();
        ActionItem addYellow = new ActionItem();
        ActionItem addPink = new ActionItem();
        ActionItem addOrange = new ActionItem();

        addBlu.setIcon(getResources().getDrawable(R.drawable.ic_rosso));
        addGreen.setIcon(getResources().getDrawable(R.drawable.ic_blu));
        addRed.setIcon(getResources().getDrawable(R.drawable.ic_verde));
        addYellow.setIcon(getResources().getDrawable(R.drawable.ic_giallo));
        addPink.setIcon(getResources().getDrawable(R.drawable.ic_rosa));
        addOrange.setIcon(getResources().getDrawable(R.drawable.ic_arancione));

        // Creo la Quick action e ci aggiungo i colori
        mQuickAction = new QuickAction(this);
        mQuickAction.addActionItem(addBlu);
        mQuickAction.addActionItem(addGreen);
        mQuickAction.addActionItem(addRed);
        mQuickAction.addActionItem(addYellow);
        mQuickAction.addActionItem(addPink);
        mQuickAction.addActionItem(addOrange);

        // Gestione eventi: al click di ogni singola immagine (colore) presente
        // nell'activity devo richiamare Quick action per la scelta del colore
        mImg_primo_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPos_colore = 0;
                avviaSceltaColore(mImg_primo_colore);

            }
        });
        mImg_secondo_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPos_colore = 1;
                avviaSceltaColore(mImg_secondo_colore);

            }
        });
        mImg_terzo_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPos_colore = 2;
                avviaSceltaColore(mImg_terzo_colore);

            }
        });
        mImg_quarto_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPos_colore = 3;
                avviaSceltaColore(mImg_quarto_colore);

            }
        });

        // Dopo aver premuto l'immagine dalla quick action è necessario impostare
        // il colore scelto nell'activity, quindi è presente l'ascoltatore di eventi
        // che una volta intercettata una click sull'immagine nella Quick action restituisce
        // il controllo in questo punto
        mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                int coloreScelto = 0;

                if (pos == 0) { //Add item selected
                    mChangeImage.setImageResource(R.drawable.ic_rosso);
                    coloreScelto=1;
                } else if (pos == 1) {
                    mChangeImage.setImageResource(R.drawable.ic_blu);
                    coloreScelto=2;
                } else if (pos == 2) {
                    mChangeImage.setImageResource(R.drawable.ic_verde);
                    coloreScelto=3;
                } else if (pos == 3) {
                    mChangeImage.setImageResource(R.drawable.ic_giallo);
                    coloreScelto=4;
                } else if (pos == 4) {
                    mChangeImage.setImageResource(R.drawable.ic_rosa);
                    coloreScelto=5;
                } else if (pos == 5) {
                    mChangeImage.setImageResource(R.drawable.ic_arancione);
                    coloreScelto=6;
                }

                // Salvo sul posizione della combinazione il colore scelto
                if (coloreScelto>0)
                    mCombinazioneScelta.set(mPos_colore, coloreScelto);

            }
        });


    }

    public void onCheckClicked(View view)
    {
        if (mCombinazioneScelta.contains(-1))
            return;

        // Controllo che non ci siano valori ripetuti nella combinazione..
        if (!NumberHelper.IsValid(mCombinazioneScelta))
        {
            Toast.makeText(this, "I colori della combinazione non possono essere ripetuti!",Toast.LENGTH_LONG).show();
            return;
        }

        // Reperisco la mia combinazione e quella dell'avversario
        String sOpponentNumber;
        String sMyNumber;
        if (mCurPlayer==1) {
            sMyNumber = MainActivity.mTurnData.player1Num;
            sOpponentNumber = MainActivity.mTurnData.player2Num;

        }
        else {
            sMyNumber = MainActivity.mTurnData.player2Num;
            sOpponentNumber = MainActivity.mTurnData.player1Num;
        }

        int nResultCode = RESULT_OK;

        // Se la mia combinazione non è vuota.. Devo confrontare quella inserita con quella dell'avversario
        if (!sMyNumber.equals(""))
        {
            ArrayList<Integer> opponentNumber = new ArrayList<Integer>(4);
            for (int i = 0; i < 4; i++) {
                String s = sOpponentNumber.substring(i, i+1);
                int a = Integer.valueOf(s);

                opponentNumber.add(a);
            }



            byte[] result = NumberHelper.CheckNumber(mCombinazioneScelta, opponentNumber);
            byte nSquares = result[0];
            byte nDots = result[1];

            // Se ho azzeccato la combinazione devo evidenziare in qualche modo la vittoria e restituire un codice speciale
            if (nSquares==4)
            {

                nResultCode = MainActivity.RESULT_FINISH;
            }

        }

        // e poi restituire la combinazione alla MainActivity che salva il turno..
        getIntent().putIntegerArrayListExtra("combination", mCombinazioneScelta);
        setResult(nResultCode, getIntent());
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.single_player_game_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void avviaSceltaColore(ImageView immagine){
        mChangeImage = immagine;
        //imageScelta = tmp;
        mQuickAction.setAnimStyle(QuickAction.ANIM_AUTO);
        mQuickAction.show(immagine);
    }


}
