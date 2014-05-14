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

    // TODO x MIRKO S: aggiungere colori, per ora altri 2.. con relative immagini ed eventi

    ImageView img_primo_colore;
    ImageView img_secondo_colore;
    ImageView img_terzo_colore;
    ImageView img_quarto_colore;
    ImageView changeImage;  // variabile che conterra' l'ID dell'immagine da modificare ogni qual volta si apre QuickAction

    ArrayList<Integer> combinazioneScelta = new ArrayList<Integer>(4);

    Integer pos_colore;

    QuickAction mQuickAction;

    Turn mTurnData;


    public MultiPlayerGamePlay(Turn turn)
    {
        mTurnData = turn;
    }

    public MultiPlayerGamePlay()
    {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        // TODO: qui nell'OnCreate dovrò valorizzare la tavola iniziale con i miei tentativi precedenti..
        // Per farlo devo leggere da mTurnData...

        for (int i=0; i<4; i++)
            combinazioneScelta.add(-1);

        // Recupero id delle immagini
        img_primo_colore = (ImageView) findViewById(R.id.imageView);
        img_secondo_colore = (ImageView) findViewById(R.id.imageView2);
        img_terzo_colore = (ImageView) findViewById(R.id.imageView3);
        img_quarto_colore = (ImageView) findViewById(R.id.imageView4);

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
        img_primo_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos_colore=0;
                avviaSceltaColore(img_primo_colore);

            }
        });
        img_secondo_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos_colore=1;
                avviaSceltaColore(img_secondo_colore);

            }
        });
        img_terzo_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos_colore=2;
                avviaSceltaColore(img_terzo_colore);

            }
        });
        img_quarto_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos_colore=3;
                avviaSceltaColore(img_quarto_colore);

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
                    changeImage.setImageResource(R.drawable.ic_rosso);
                    coloreScelto=1;
                } else if (pos == 1) {
                    changeImage.setImageResource(R.drawable.ic_blu);
                    coloreScelto=2;
                } else if (pos == 2) {
                    changeImage.setImageResource(R.drawable.ic_verde);
                    coloreScelto=3;
                } else if (pos == 3) {
                    changeImage.setImageResource(R.drawable.ic_giallo);
                    coloreScelto=4;
                } else if (pos == 4) {
                    changeImage.setImageResource(R.drawable.ic_rosa);
                    coloreScelto=5;
                } else if (pos == 5) {
                    changeImage.setImageResource(R.drawable.ic_arancione);
                    coloreScelto=6;
                }

                // Salvo sul posizione della combinazione il colore scelto
                if (coloreScelto>0)
                    combinazioneScelta.set(pos_colore, coloreScelto);

            }
        });


    }

    public void onCheckClicked(View view)
    {
        if (combinazioneScelta.contains(-1))
            return;

        // Controllo che non ci siano valori ripetuti nella combinazione..
        // PORCHERIA.. ma per ora va bene così!
        if ((combinazioneScelta.get(0).equals(combinazioneScelta.get(1))) ||
            (combinazioneScelta.get(0).equals(combinazioneScelta.get(2))) ||
            (combinazioneScelta.get(0).equals(combinazioneScelta.get(3))) ||
            (combinazioneScelta.get(1).equals(combinazioneScelta.get(2))) ||
            (combinazioneScelta.get(1).equals(combinazioneScelta.get(3))) ||
            (combinazioneScelta.get(2).equals(combinazioneScelta.get(3))))
        {
            Toast.makeText(this, "I colori della combinazione non possono essere ripetuti!",Toast.LENGTH_LONG).show();
            return;
        }

        // TODO: Devo anche controllare se la mia combinazione è già presente o se sto facendo un tentativo.. In quel caso devo mostrare il risultato..

        // e poi restituire la combinazione alla MainActivity che salva il turno..
        getIntent().putIntegerArrayListExtra("combination", combinazioneScelta);
        setResult(RESULT_OK, getIntent());
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
        changeImage = immagine;
        //imageScelta = tmp;
        mQuickAction.setAnimStyle(QuickAction.ANIM_AUTO);
        mQuickAction.show(immagine);
    }


}
