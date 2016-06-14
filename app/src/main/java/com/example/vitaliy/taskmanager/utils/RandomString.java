package com.example.vitaliy.taskmanager.utils;

import java.util.Random;

/**
 * Created by Vitaliy on 03.06.2016.
 */
public class RandomString {

    private final static String CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    //Створює рандомну назву завдання
    public static String getRandom(int length) {
        Random random = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = random.nextInt(length)+1;
        for (int i = 0; i < randomLength; i++) {
            randomStringBuilder.append(CHARACTERS.charAt((random.nextInt(CHARACTERS.length()))));
        }
        return randomStringBuilder.toString();
    }

}
