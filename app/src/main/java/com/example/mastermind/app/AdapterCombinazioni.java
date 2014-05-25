package com.example.mastermind.app;

/**
 * Created by Mirko on 11/05/2014.
 */
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import Bean.Combinazione;


public class AdapterCombinazioni extends ArrayAdapter<Combinazione> {

    private int resource;
    private LayoutInflater inflater;
    private Context context;


    public AdapterCombinazioni(Context context, int resourceId, List<Combinazione> objects) {
        super(context, resourceId, objects);
        resource = resourceId;
        inflater = LayoutInflater.from(context);
        this.context = context;

    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {

        // Recuperiamo l'oggetti che dobbiamo inserire a questa posizione
        Combinazione c = getItem(position);

        ViewHolder holder;

        if (v == null) {

            int layout = R.layout.row_combinazione;

            v = LayoutInflater.from(getContext()).inflate(layout, null);

            holder = new ViewHolder();
            //holder.testo = (TextView) v.findViewById(R.id.textView);
            holder.prima = (ImageView) v.findViewById(R.id.imageView1);
            holder.seconda = (ImageView) v.findViewById(R.id.imageView2);
            holder.terza = (ImageView) v.findViewById(R.id.imageView3);
            holder.quarta = (ImageView) v.findViewById(R.id.imageView4);

            holder.image_esito1 = (ImageView) v.findViewById(R.id.imageView10);
            holder.image_esito2 = (ImageView) v.findViewById(R.id.imageView11);
            holder.image_esito3 = (ImageView) v.findViewById(R.id.imageView12);
            holder.image_esito4 = (ImageView) v.findViewById(R.id.imageView13);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.prima.setImageResource(c.getColore(0));
        holder.seconda.setImageResource(c.getColore(1));
        holder.terza.setImageResource(c.getColore(2));
        holder.quarta.setImageResource(c.getColore(3));

        // Setto le immagini con errore inizialmente

        holder.image_esito1.setImageResource(R.drawable.ic_errore);
        holder.image_esito2.setImageResource(R.drawable.ic_errore);
        holder.image_esito3.setImageResource(R.drawable.ic_errore);
        holder.image_esito4.setImageResource(R.drawable.ic_errore);

        // Numero di verdi da impostare uguale al numero di posizioni indovinate
        // Ogni volta che un giallo verra' impostato verra' decrementato di uno.
        int num_verdi = c.getNumPosizioniIndovinate();

        // Numero di gialli da impostare uguale al numero di colori indovinati
        // Ogni volta che un giallo verra' impostato verra' decrementato di uno.
        int num_gialli = c.getNumColoriIndovinati();

        if (num_verdi > 0) {
            holder.image_esito1.setImageResource(R.drawable.ic_ok);
            num_verdi--;
        }else if (num_gialli > 0) {
            holder.image_esito1.setImageResource(R.drawable.ic_errorepos);
            num_gialli--;
        }

        if (num_verdi > 0) {
            holder.image_esito2.setImageResource(R.drawable.ic_ok);
            num_verdi--;
        }else if (num_gialli > 0) {
            holder.image_esito2.setImageResource(R.drawable.ic_errorepos);
            num_gialli--;
        }

        if (num_verdi > 0){
            holder.image_esito3.setImageResource(R.drawable.ic_ok);
            num_verdi--;
        } else if (num_gialli > 0 ) {
            holder.image_esito3.setImageResource(R.drawable.ic_errorepos);
            num_gialli--;
        }

        if (num_verdi > 0) {
            holder.image_esito4.setImageResource(R.drawable.ic_ok);
            num_verdi--;
        }else if (num_gialli > 0) {
            holder.image_esito4.setImageResource(R.drawable.ic_errorepos);
            num_gialli--;
        }

        return v;
    }

    private static class ViewHolder {

        //TextView testo;
        ImageView prima;
        ImageView seconda;
        ImageView terza;
        ImageView quarta;

        ImageView image_esito1;
        ImageView image_esito2;
        ImageView image_esito3;
        ImageView image_esito4;


    }

}

