package com.ashcollege.run;

public class Utils {
        static void sleep(int time) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
