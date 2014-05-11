package com.example.mastermind.app;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Basic turn data. It's just a blank data string and a turn number counter.
 * 
 * @author wolff
 * 
 */
public class Turn {

    public static final String TAG = "EBTurn";

//    public ArrayList<int[]> data1 = new ArrayList<int[]>();
//    public ArrayList<int[]> data2 = new ArrayList<int[]>();
    public String data1 ="";
    public String data2 ="";
    public int turnCounter;
    public String player1Id = "";
    public String player2Id = "";
    public String player1Num = "";
    public String player2Num = "";

    public Turn() {
    }

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();

        try {
            retVal.put("data1", data1);
            retVal.put("data2", data2);
            retVal.put("turnCounter", turnCounter);
            retVal.put("player1Id", player1Id);
            retVal.put("player2Id", player2Id);
            retVal.put("player1Num", player1Num);
            retVal.put("player2Num", player2Num);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String st = retVal.toString();

        Log.d(TAG, "==== PERSISTING\n" + st);

        return st.getBytes(Charset.forName("UTF-16"));
    }

    // Creates a new instance of Turn.
    static public Turn unpersist(byte[] byteArray) {

        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new Turn();
        }

        String st = null;
        try {
            st = new String(byteArray, "UTF-16");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }

        Log.d(TAG, "====UNPERSIST \n" + st);

        Turn retVal = new Turn();

        try {
            JSONObject obj = new JSONObject(st);

            if (obj.has("data1")) {
                retVal.data1 = obj.getString("data1");
            }

            if (obj.has("data2")) {
                retVal.data2 = obj.getString("data2");
            }
            if (obj.has("turnCounter")) {
                retVal.turnCounter = obj.getInt("turnCounter");
            }

            if (obj.has("player1Id")) {
                retVal.player1Id = obj.getString("player1Id");
            }

            if (obj.has("player2Id")) {
                retVal.player2Id = obj.getString("player2Id");
            }

            if (obj.has("player1Num")) {
                retVal.player1Num = obj.getString("player1Num");
            }

            if (obj.has("player2Num")) {
                retVal.player2Num = obj.getString("player2Num");
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return retVal;
    }
}
