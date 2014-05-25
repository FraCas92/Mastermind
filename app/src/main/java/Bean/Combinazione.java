package Bean;


import com.example.mastermind.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mirko on 11/05/2014.
 */
public class Combinazione {

    public ArrayList<Integer> combinazioneScelta;
    public ArrayList<Integer> combinazioneVincente;

    private int numPosizioniIndovinate;
    private int numColoriIndovinati;
    private int max_combinazioni;
    private int stato_combinazione;

    public Combinazione(ArrayList<Integer> combinazioneScelta,ArrayList<Integer> combinazioneVincente,int max_combinazioni) {

        this.combinazioneScelta = new ArrayList<Integer>();      // Creo un nuovo oggetto di tipo arraylist
        this.combinazioneScelta.addAll(combinazioneScelta);      // Aggiungo arrayList (altrimenti gli oggetti precedrenti verranno sovrascritti
        this.combinazioneVincente = new ArrayList<Integer>();    // Creo un nuovo oggetto di tipo arraylist
        this.combinazioneVincente.addAll(combinazioneVincente);  // Aggiungo arrayList (altrimenti gli oggetti precedrenti verranno sovrascritti

        this.max_combinazioni=max_combinazioni;                  // max combinazioni possibili

        stato_combinazione = getCheckCombinazione();

    }

    // Costruttore che non fa niente, ma serve per inizializzare solo l'oggetto
    public Combinazione(int max_combinazioni) {
        this.max_combinazioni = max_combinazioni;
    }


    public int getNumColoriIndovinati() {
        return numColoriIndovinati;
    }

    public int getNumPosizioniIndovinate() {
        return numPosizioniIndovinate;
    }

    public int getColore(int numColore){

        switch (combinazioneScelta.get(numColore)){
            case 1:
                return R.drawable.ic_rosso;
            case 2:
                return R.drawable.ic_blu;
            case 3:
                return R.drawable.ic_arancione;
            case 4:
                return R.drawable.ic_verde;
            case 5:
                return R.drawable.ic_giallo;
            case 6:
                return R.drawable.ic_nero;
        }

        return 0;
    }

    /* Restituisce lo stato della combinazione
     * 1-Non sono stati selezionati tutti i colori nella combinazione
     * 2-Colori non si possono ripetere
     * 3-Nessun errore, ma non ha vinto
     * 4-Vittoria
     */
    public int getCheckCombinazione(){

        for (int i = 0; i < max_combinazioni; i++) {

            // Controllo se sono stati selezionati tutti i colori nella combinazione

            if (combinazioneScelta.get(i)==0){

                return 1;

            }

            // Controllo se utente ha ripetuto i colori
            for (int j = 0; j < max_combinazioni; j++){

                // Non controllo la posizione uguale a quella del ciclo principale
                if (j != i){
                    if (combinazioneScelta.get(i)==combinazioneScelta.get(j)){

                        return  2;

                    }
                }
            }
            // Incremento numero di colori\posizioni indovinate se combaciano
            if (combinazioneScelta.get(i)==combinazioneVincente.get(i))
                numPosizioniIndovinate++;
            else{
                // Se il colore non corrisponde nella posizione corretta
                // Controllo se per caso è presente il colore in un'altra posizione
                for (int j = 0; j < max_combinazioni; j++){

                    if (j != i){

                        if (combinazioneScelta.get(i)==combinazioneVincente.get(j)){
                            numColoriIndovinati++;
                            break;
                        }

                    }

                }
            }

        }

        if (numPosizioniIndovinate == max_combinazioni)
            return 4;
        else
            return 3;
    }

    public int getStato_combinazione(){
        return  this.stato_combinazione;
    }


    public ArrayList<Integer> getCombinazionRandom(int max_colori){

        ArrayList<Integer> combinazioneRandom = new ArrayList<Integer>();

        for (int i = 0; i < max_combinazioni; i++) {

            // Genero combinazione vincente

            int new_color =  0;
            boolean trovato = false;

            while (trovato == false){

                // Nuovo colore
                new_color =  1+(int)(Math.random()*max_colori);
                trovato = true;

                // Ciclo sulla combinazione fin'ora creata
                for (int j = 0; j < i; j++) {

                    // Se il colore è stato gia' utilizzato allora devo cercarne uno nuovo
                    // quindi setto false di modo che iteri nuovamente nella ricerca di un nuovo
                    // colore

                    if (combinazioneRandom.get(j)==new_color){
                        trovato = false;
                    }

                }

            }

            combinazioneRandom.add(i,new_color);
        }

        return combinazioneRandom;
    }

    public boolean isCombinazioneEsistente(List<Combinazione> combinazioniProvate){

        for (int i=0;i<combinazioniProvate.size();i++){

            boolean combinazioneEsistente = true;

            for (int j=0;j<max_combinazioni;j++){

                if (combinazioniProvate.get(i).combinazioneScelta.get(j)!=this.combinazioneScelta.get(j)) {
                    combinazioneEsistente = false;
                    break;
                }

            }

            if (combinazioneEsistente)
                return true;
        }

        return false;



    }


}
