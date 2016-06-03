package com.example.vitaliy.taskmanager.utils;

import java.util.Random;

/**
 * Created by Vitaliy on 03.06.2016.
 */
public class RandomString {

    private final static String CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    //Створює рандомну назву завдання
    public static String randomName() {
        Random random = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = random.nextInt(10);
        for (int i = 0; i < randomLength; i++) {
            randomStringBuilder.append(CHARACTERS.charAt((random.nextInt(CHARACTERS.length()))));
        }
        return randomStringBuilder.toString();
    }

    //Створює рандомний опис завдання
    public static String randomDescription() {
        Random random = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = random.nextInt(30);
        for (int i = 0; i < randomLength; i++) {
            randomStringBuilder.append(CHARACTERS.charAt((random.nextInt(CHARACTERS.length()))));
        }
        return randomStringBuilder.toString();
    }
}
