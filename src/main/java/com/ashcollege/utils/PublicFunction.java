package com.ashcollege.utils;

import com.ashcollege.entities.Team;
import org.springframework.lang.NonNull;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class PublicFunction {
    public static String createHash(String username, String password) {
        String myHash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((username + password).getBytes());
            byte[] digest = md.digest();
            myHash = DatatypeConverter
                    .printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return myHash;
    }
    public static int fact(int n){
        if (n==0){
            return 1;
        }
        int res =1;
        for(int i=2; i<=n;i++){
            res=res*i;
        }
        return res;
    }
    public static int nCr(int n, int r) { ///n above r
        return fact(n)/(fact(r)*fact(n-r));
    }
    public static List<Team>  shiftRight(ArrayList<Team> list)
    {
        if (!list.isEmpty()){
            Team temp = list.get(list.size()-1);
            for(int i = list.size()-1; i > 0; i--)
            {
                list.set(i,list.get(i-1));
            }
            list.set(0, temp);
        }
        return list;

    }
}

