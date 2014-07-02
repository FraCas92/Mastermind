package com.example.mastermind.app;


import android.app.Dialog;

import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import Bean.ActionItem;
import Bean.Combinazione;
import QuickAction.QuickAction;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class SinglePlayerGamePlay extends ActionBarActivity {

    ImageView img_primo_colore;
    ImageView img_secondo_colore;
    ImageView img_terzo_colore;
    ImageView img_quarto_colore;
    Button button_checkCombinazione;
    ImageView changeImage;                    // variabile che conterra' l'ID dell'immagine da modificare ogni qual volta si apre QuickAction
    ArrayList<Integer> combinazioneVincente;  // Combinazione da indodvinare
    ArrayList<Integer> combinazioneScelta;    // Combinazione scelta dall'utente
    int conta_combinazioni;                   // Numero di combinazioni provate dall'utente
    int max_combinazioni = 4;                 // Numero max combinazioni di colori
    int max_colori = 6;                       // Numero max combinazioni di colori disponibili
    int pos_colore = 0;                       // Posizione del colore scelto
    AdapterCombinazioni adapter;              // Adapter della listView
    ListView list_combinazioni;               // Lista combinazioni provate
    List<Combinazione> combinazioniProvate;   // Elenco combinazioni provate


    QuickAction mQuickAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        // Genero una nuova combinazione da scoprire
        getCombinazioneMagica();

        // Recupero id delle immagini
        img_primo_colore = (ImageView) findViewById(R.id.imageView);
        img_secondo_colore = (ImageView) findViewById(R.id.imageView2);
        img_terzo_colore = (ImageView) findViewById(R.id.imageView3);
        img_quarto_colore = (ImageView) findViewById(R.id.imageView4);

        list_combinazioni = (ListView) findViewById(R.id.listView1);

        combinazioniProvate = new ArrayList<Combinazione>();
        adapter = new AdapterCombinazioni(this, R.layout.row_combinazione, combinazioniProvate);
        list_combinazioni.setAdapter(adapter);

        // Colori da visualizzare nella Quick Action (per la scelta del colore)
        ActionItem addRed = new ActionItem();
        ActionItem addBlu = new ActionItem();
        ActionItem addOrange = new ActionItem();
        ActionItem addGreen = new ActionItem();
        ActionItem addYellow = new ActionItem();
        ActionItem addBlack = new ActionItem();


        addRed.setIcon(getResources().getDrawable(R.drawable.ic_rosso));
        addBlu.setIcon(getResources().getDrawable(R.drawable.ic_blu));
        addOrange.setIcon(getResources().getDrawable(R.drawable.ic_arancione));
        addGreen.setIcon(getResources().getDrawable(R.drawable.ic_verde));
        addYellow.setIcon(getResources().getDrawable(R.drawable.ic_giallo));
        addBlack.setIcon(getResources().getDrawable(R.drawable.ic_nero));


        // Creo la Quick action e ci aggiungo i colori
        mQuickAction = new QuickAction(this);
        mQuickAction.addActionItem(addRed);
        mQuickAction.addActionItem(addBlu);
        mQuickAction.addActionItem(addOrange);
        mQuickAction.addActionItem(addGreen);
        mQuickAction.addActionItem(addYellow);
        mQuickAction.addActionItem(addBlack);


        // Gestione eventi: al click di ogni singola immagine (colore) presente
        // nell'activity devo richiamare Quick action per la scelta del colore
        img_primo_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                avviaSceltaColore(img_primo_colore, 0);

            }
        });
        img_secondo_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                avviaSceltaColore(img_secondo_colore, 1);

            }
        });
        img_terzo_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                avviaSceltaColore(img_terzo_colore, 2);

            }
        });
        img_quarto_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                avviaSceltaColore(img_quarto_colore, 3);

            }
        });

        // Dopo aver premuto l'immagine dalla quick action è necessario impostare
        // il colore scelto nell'activity, quindi è presente l'ascoltatore di eventi
        // che una volta intercettata una click sull'immagine nella Quick action restituisce
        // il controllo in questo punto
        mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(int pos) {

                // In base alla posizione imposto il colore
                int coloreScelto = impostaColore(pos);

                // Salvo sul posizione della combinazione il colore scelto
                if (coloreScelto > 0)
                    combinazioneScelta.set(pos_colore, coloreScelto);

            }
        });

        button_checkCombinazione = (Button) findViewById(R.id.button1);
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

        // Combinazione random
        if (id == R.id.action_random) {

            /// Recupero una combinazione random
            combinazioneScelta.clear();
            Combinazione combinazioneRandom = new Combinazione(max_combinazioni);
            combinazioneScelta = combinazioneRandom.getCombinazionRandom(max_colori);

            // Imposto i colori della combinazione random
            for (int i = 0; i < max_combinazioni; i++) {

                switch (i) {
                    case 0:
                        changeImage = img_primo_colore;
                        break;
                    case 1:
                        changeImage = img_secondo_colore;
                        break;
                    case 2:
                        changeImage = img_terzo_colore;
                        break;
                    case 3:
                        changeImage = img_quarto_colore;
                        break;
                }
                impostaColore(combinazioneScelta.get(i)-1);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public int impostaColore(int pos) {

        int coloreScelto = 0;

        if (pos == 0) { //Add item selected
            changeImage.setImageResource(R.drawable.ic_rosso);
            coloreScelto = 1;
        } else if (pos == 1) {
            changeImage.setImageResource(R.drawable.ic_blu);
            coloreScelto = 2;
        } else if (pos == 2) {
            changeImage.setImageResource(R.drawable.ic_arancione);
            coloreScelto = 3;
        } else if (pos == 3) {
            changeImage.setImageResource(R.drawable.ic_verde);
            coloreScelto = 4;
        } else if (pos == 4) {
            changeImage.setImageResource(R.drawable.ic_giallo);
            coloreScelto = 5;
        } else if (pos == 5) {
            changeImage.setImageResource(R.drawable.ic_nero);
            coloreScelto = 6;
        }

        return coloreScelto;

    }

    private void avviaSceltaColore(ImageView immagine, int posizione) {
        changeImage = immagine;
        pos_colore = posizione;
        //imageScelta = tmp;
        mQuickAction.setAnimStyle(QuickAction.ANIM_AUTO);
        mQuickAction.show(immagine);
    }

    /*
    * Genero 4 numeri casuali compresi tra 1 e 4
    * 1-Rosso
    * 2-Blu
    * 3-Verde
    * 4-Giallo
    * */
    private void getCombinazioneMagica() {

        combinazioneVincente = new ArrayList<Integer>();
        combinazioneScelta = new ArrayList<Integer>();
        conta_combinazioni = 0;

        // Inizializzo combinazioni scelte dall'utente

        for (int i = 0; i < max_combinazioni; i++) {

            // Inizializzo la combinazione scelta
            combinazioneScelta.add(i, 0);

        }

        // Recupero combinazione vincente random
        Combinazione combinazioneRandom = new Combinazione(max_combinazioni);
        combinazioneVincente = combinazioneRandom.getCombinazionRandom(max_colori);
    }

    public void onCheckClicked(View view) {

        Combinazione combinazioneProvata = new Combinazione(combinazioneScelta, combinazioneVincente, max_combinazioni);

        switch (combinazioneProvata.getStato_combinazione()) {
            case 1:
                Toast.makeText(this, "La combinazione non contiene tutti i colori!", Toast.LENGTH_SHORT).show();
                return;
            case 2:
                Toast.makeText(this, "Un colore non puo' essere impostato in piu' combinazioni!", Toast.LENGTH_SHORT).show();
                return;
        }


        if (combinazioneProvata.isCombinazioneEsistente(combinazioniProvate)) {
            Toast.makeText(this, "La combinazione è gia' stata provata", Toast.LENGTH_SHORT).show();
            return;
        }

        conta_combinazioni++;


        // aggiungo la combinazione alla list View
        combinazioniProvate.add(combinazioneProvata);
        adapter.notifyDataSetChanged();

        PlayInsertCombinationSound();

        // Mi posiziono sull'ultimo elemento aggiunto
        list_combinazioni.smoothScrollToPosition(conta_combinazioni);

        // Se ho trovato tutti i colori emetto dialog di avviso
        if (combinazioneProvata.getStato_combinazione() == 4) {

            button_checkCombinazione.setEnabled(false);
            avviaMessaggioPartitaVinta();

            return;

        }

    }

    /* Dialog riferito alla vincita della partita *
    /
     */
    private void avviaMessaggioPartitaVinta() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_gamewin);
        dialog.setTitle("COMPLIMENTI");
        dialog.setCancelable(true);

        ImageView dialog_img_prima = (ImageView) dialog.findViewById(R.id.imageView);
        ImageView dialog_img_seconda = (ImageView) dialog.findViewById(R.id.imageView2);
        ImageView dialog_img_terza = (ImageView) dialog.findViewById(R.id.imageView3);
        ImageView dialog_img_quarta = (ImageView) dialog.findViewById(R.id.imageView4);

        dialog_img_prima.setImageResource(combinazioniProvate.get(conta_combinazioni - 1).getColore(0));
        dialog_img_seconda.setImageResource(combinazioniProvate.get(conta_combinazioni - 1).getColore(1));
        dialog_img_terza.setImageResource(combinazioniProvate.get(conta_combinazioni - 1).getColore(2));
        dialog_img_quarta.setImageResource(combinazioniProvate.get(conta_combinazioni - 1).getColore(3));

        Button dialog_button_rigioca = (Button) dialog.findViewById(R.id.button1);

        dialog_button_rigioca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img_primo_colore.setImageResource(R.drawable.ic_help);
                img_secondo_colore.setImageResource(R.drawable.ic_help);
                img_terzo_colore.setImageResource(R.drawable.ic_help);
                img_quarto_colore.setImageResource(R.drawable.ic_help);

                getCombinazioneMagica();
                button_checkCombinazione.setEnabled(true);
                adapter.clear();

                dialog.dismiss();
            }

        });

        dialog.show();


    }

    public void PlayInsertCombinationSound() {
        MediaPlayer mp = MediaPlayer.create(SinglePlayerGamePlay.this, R.raw.addrow);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }

        });
        mp.start();
    }
}