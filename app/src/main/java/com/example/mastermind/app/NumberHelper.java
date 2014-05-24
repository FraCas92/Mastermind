package com.example.mastermind.app;

import java.util.ArrayList;

/**
 * Created by Francesco on 15/05/2014.
 */
public class NumberHelper
{
    static byte[] CheckNumber(ArrayList<Integer> Num1, ArrayList<Integer> Num2)
    {
        byte nSquares=0, nDots=0;
        for (int i=0; i<4; i++)
        {
            if (Num1.get(i).equals(Num2.get(i)))
                nSquares++;
            else if (Num1.contains(Num2.get(i)))
                nDots++;
        }
        byte[] result = new byte[2];
        result[0]=nSquares;
        result[1]=nDots;
        return result;
    }

    static String GetString(ArrayList<Integer> num)
    {
        String strNum="";
        for (int i=0; i<4; i++)
            strNum += num.get(i).toString();
        return strNum;
    }

    static boolean IsValid(ArrayList<Integer> number)
    {
        if ((number.get(0).equals(number.get(1))) ||
            (number.get(0).equals(number.get(2))) ||
            (number.get(0).equals(number.get(3))) ||
            (number.get(1).equals(number.get(2))) ||
            (number.get(1).equals(number.get(3))) ||
            (number.get(2).equals(number.get(3))))
            return false;
        return true;
    }

    static ArrayList<ArrayList<Integer>> GetNumbersListFromString(String data)
    {
        ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
        for (int j = 0; j < data.length(); j++)
        {
            ArrayList<Integer> number = new ArrayList<Integer>(4);
            for (int i = j; i < j+4; i++) {
                String s = data.substring(i, i+1);
                int a = Integer.valueOf(s);
                number.add(a);
            }
            list.add(number);
            // Incremento j per saltare il trattino di separazione dei numeri (1234-1234-1234....)
            j++;
        }

        return list;
    }


















}
