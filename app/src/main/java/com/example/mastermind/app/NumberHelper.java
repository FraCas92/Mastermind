package com.example.mastermind.app;

import java.util.ArrayList;

/**
 * Created by Francesco on 15/05/2014.
 */
public class NumberHelper
{
    static void CheckNumber(ArrayList<Integer> Num1, ArrayList<Integer> Num2, Integer nSquares, Integer nDots)
    {


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


















}
