package com.example.chat95.cryptology;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TextSplitter {
    /**
     * s is the string to split
     * n is the lenght of each sub string
     * @param s
     * @param n
     * @return
     */
    public static List<String> split(String s, int n) {
        int textLengh= s.length(),i;
        String fixedText= fixText(s,textLengh,n);
        List<String> result=new ArrayList<>();
        for(i=0;i< fixedText.length();i+=n){
            result.add(fixedText.substring(i,i+n));
            Log.d(TAG, "after split: sub string "+i/n+": "+result.get(i/n));
        }

        return result;
    }

    /**
     * add spaces " " in the end to fix the lengh of the original string
     * @param s
     * @param lengh
     * @param n
     * @return
     */
    public static String fixText(String s,int lengh,int n){ // in hex
        int i;
        String result=s;
        while(lengh%n !=0){
                result=result+"20";
                lengh+=2;
        }
        Log.d(TAG, "after fix Text: "+result+" length: "+result.length());
        return result;
    }

}
