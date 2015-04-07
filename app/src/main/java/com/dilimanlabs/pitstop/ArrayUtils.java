package com.dilimanlabs.pitstop;

public class ArrayUtils {

    public static boolean containsItem(int[] arr, int targetValue) {
        for(int i: arr){
            if(i == targetValue)
                return true;
        }
        return false;
    }
}
