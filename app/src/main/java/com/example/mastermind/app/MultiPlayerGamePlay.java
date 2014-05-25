package com.example.mastermind.app;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import Bean.ActionItem;
import Bean.Combinazione;
import QuickAction.QuickAction;


public class MultiPlayerGamePlay extends ActionBarActivity {

    Button button_checkCombinazione;
    ImageView mImg_primo_colore;
    ImageView mImg_secondo_colore;
    ImageView mImg_terzo_colore;
    ImageView mImg_quarto_colore;
    ImageView mChangeImage;  // variabile che conterra' l'ID dell'immagine da modificare ogni qual volta si apre QuickAction
    ArrayList<Integer> mCombinazioneScelta = new ArrayList<Integer>(4);
    ArrayList<Integer> mCombinazioneVincente;
    Integer mPos_colore;
    byte mCurPlayer;
    boolean mChoose_your_code;
    QuickAction mQuickAction;
    AdapterCombinazioni mAdapter;              // Adapter della listView
    ListView mList_combinazioni;               // Lista combinazioni provate
    List<Combinazione> mCombinazioniProvate;   // Elenco combinazioni provate
    String mStrMyNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        mCurPlayer = getIntent().getByteExtra("curPlayer",(byte)0);

        for (int i=0; i<4; i++)
            mCombinazioneScelta.add(-1);

        mList_combinazioni = (ListView) findViewById(R.id.listView1);

        mCombinazioniProvate = new ArrayList<Combinazione>();
        mAdapter = new AdapterCombinazioni(this,R.layout.row_combinazione,mCombinazioniProvate);
        mList_combinazioni.setAdapter(mAdapter);

        if (MainActivity.mTurnData.player1Num.isEmpty() || MainActivity.mTurnData.player2Num.isEmpty())
        {
            mChoose_your_code = true;
            ((TextView) findViewById(R.id.textView)).setText(R.string.choose_your_code);
        }
        else
        {
            // Reperisco la mia combinazione e quella dell'avversario
            String sOpponentNumber;
            String sMyData;
            if (mCurPlayer==1)
            {
                mStrMyNumber = MainActivity.mTurnData.player1Num;
                sOpponentNumber = MainActivity.mTurnData.player2Num;
                sMyData = MainActivity.mTurnData.data1;
            }
            else
            {
                mStrMyNumber = MainActivity.mTurnData.player2Num;
                sOpponentNumber = MainActivity.mTurnData.player1Num;
                sMyData = MainActivity.mTurnData.data2;
            }

            mCombinazioneVincente = new ArrayList<Integer>(4);
            for (int i = 0; i < 4; i++)
            {
                String s = sOpponentNumber.substring(i, i+1);
                int a = Integer.valueOf(s);
                mCombinazioneVincente.add(a);
            }

            // Valorizzo la tavola iniziale con i miei tentativi precedenti
            if (!sMyData.isEmpty())
            {
                ArrayList<ArrayList<Integer>> combinazioni = NumberHelper.GetNumbersListFromString(sMyData);
                for (ArrayList<Integer> element:combinazioni)
                {
                    Combinazione combinazione = new Combinazione(element, mCombinazioneVincente, 4);
                    mCombinazioniProvate.add(combinazione);
                }
            }
        }

        // Recupero id delle immagini
        mImg_primo_colore = (ImageView) findViewById(R.id.imageView);
        mImg_secondo_colore = (ImageView) findViewById(R.id.imageView2);
        mImg_terzo_colore = (ImageView) findViewById(R.id.imageView3);
        mImg_quarto_colore = (ImageView) findViewById(R.id.imageView4);
        mList_combinazioni = (ListView) findViewById(R.id.listView1);

        // Colori da visualizzare nella Quick Action (per la scelta del colore)
        ActionItem addRed = new ActionItem();
        ActionItem addBlu = new ActionItem();
        ActionItem addOrange = new ActionItem();
        ActionItem addGreen = new ActionItem();
        ActionItem addYellow = new ActionItem();
        ActionItem addBlack = new ActionItem();


        addBlu.setIcon(getResources().getDrawable(R.drawable.ic_blu));
        addGreen.setIcon(getResources().getDrawable(R.drawable.ic_verde));
        addRed.setIcon(getResources().getDrawable(R.drawable.ic_rosso));
        addYellow.setIcon(getResources().getDrawable(R.drawable.ic_giallo));
        addBlack.setIcon(getResources().getDrawable(R.drawable.ic_nero));
        addOrange.setIcon(getResources().getDrawable(R.drawable.ic_arancione));

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
        mImg_primo_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                avviaSceltaColore(mImg_primo_colore,0);

            }
        });
        mImg_secondo_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                avviaSceltaColore(mImg_secondo_colore,1);

            }
        });
        mImg_terzo_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                avviaSceltaColore(mImg_terzo_colore,2);

            }
        });
        mImg_quarto_colore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                avviaSceltaColore(mImg_quarto_colore,3);

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
                    mChangeImage.setImageResource(R.drawable.ic_arancione);
                    coloreScelto=3;
                } else if (pos == 3) {
                    mChangeImage.setImageResource(R.drawable.ic_verde);
                    coloreScelto=4;
                } else if (pos == 4) {
                    mChangeImage.setImageResource(R.drawable.ic_giallo);
                    coloreScelto=5;
                } else if (pos == 5) {
                    mChangeImage.setImageResource(R.drawable.ic_nero);
                    coloreScelto=6;
                }

                // Salvo sul posizione della combinazione il colore scelto
                if (coloreScelto>0)
                    mCombinazioneScelta.set(mPos_colore, coloreScelto);

            }
        });

        button_checkCombinazione = (Button) findViewById(R.id.button1);
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

        // Se sto scegliendo il mio codice e l'avversario ha già scelto il suo, allora non esco dall'activity e inizio il gioco
        if (mChoose_your_code)
        {
            if ((mCurPlayer==1 && !MainActivity.mTurnData.player2Num.isEmpty()) || (mCurPlayer==2 && !MainActivity.mTurnData.player1Num.isEmpty()))
            {
                if (mCurPlayer==1)
                    MainActivity.mTurnData.player1Num = NumberHelper.GetString(mCombinazioneScelta);
                else
                    MainActivity.mTurnData.player2Num = NumberHelper.GetString(mCombinazioneScelta);

                ((TextView) findViewById(R.id.textView)).setText(R.string.find_opponent_code);
                SvuotaSceltaColori();
                mChoose_your_code = false;
                return;
            }
        }

        int nResultCode = RESULT_OK;
        // L'activity va conclusa solo se sto immettendo la mia combinazione..
        // In caso contrario l'utente deve poter vedere il proprio tentativo
        boolean bFinish = true;

        // Se la mia combinazione non è vuota.. Devo confrontare quella inserita con quella dell'avversario
        if (mStrMyNumber!=null && !mStrMyNumber.equals(""))
        {
            Combinazione combinazioneProvata = new Combinazione(mCombinazioneScelta, mCombinazioneVincente, 4);

            if (combinazioneProvata.isCombinazioneEsistente(mCombinazioniProvate)){
                Toast.makeText(this,"La combinazione è gia' stata provata" ,Toast.LENGTH_SHORT).show();
                return;
            }

            // aggiungo la combinazione alla list View
            mCombinazioniProvate.add(combinazioneProvata);
            mAdapter.notifyDataSetChanged();

            // Mi posiziono sull'ultimo elemento aggiunto
            mList_combinazioni.smoothScrollToPosition(mCombinazioniProvate.size());

            bFinish=false;
            button_checkCombinazione.setEnabled(false);

            // Se ho azzeccato la combinazione devo evidenziare in qualche modo la vittoria e restituire un codice speciale
            if (combinazioneProvata.getStato_combinazione()==4)
            {

                nResultCode = MainActivity.RESULT_FINISH;
            }
        }

        // e poi restituire la combinazione alla MainActivity che salva il turno..
        getIntent().putIntegerArrayListExtra("combination", mCombinazioneScelta);
        setResult(nResultCode, getIntent());
        if (bFinish)
            finish();
    }

    public void SvuotaSceltaColori()
    {
        mImg_primo_colore.setImageResource(R.drawable.ic_help);
        mImg_secondo_colore.setImageResource(R.drawable.ic_help);
        mImg_terzo_colore.setImageResource(R.drawable.ic_help);
        mImg_quarto_colore.setImageResource(R.drawable.ic_help);
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

    private void avviaSceltaColore(ImageView immagine, int posizione){
        mChangeImage = immagine;
		mPos_colore = posizione;
        //imageScelta = tmp;
        mQuickAction.setAnimStyle(QuickAction.ANIM_AUTO);
        mQuickAction.show(immagine);
    }

    /* Dialog riferito alla vincita della partita *
    /
     */
    private void avviaMessaggioRisultato() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_gamewin);
        dialog.setTitle("COMPLIMENTI");
        dialog.setCancelable(true);

        ImageView dialog_img_prima = (ImageView) dialog.findViewById(R.id.imageView);
        ImageView dialog_img_seconda = (ImageView) dialog.findViewById(R.id.imageView2);
        ImageView dialog_img_terza = (ImageView) dialog.findViewById(R.id.imageView3);
        ImageView dialog_img_quarta = (ImageView) dialog.findViewById(R.id.imageView4);

        int nCombinazioni = mCombinazioniProvate.size();

        dialog_img_prima.setImageResource(mCombinazioniProvate.get(nCombinazioni - 1).getColore(0));
        dialog_img_seconda.setImageResource(mCombinazioniProvate.get(nCombinazioni - 1).getColore(1));
        dialog_img_terza.setImageResource(mCombinazioniProvate.get(nCombinazioni - 1).getColore(2));
        dialog_img_quarta.setImageResource(mCombinazioniProvate.get(nCombinazioni - 1).getColore(3));

        dialog.show();
    }


}
