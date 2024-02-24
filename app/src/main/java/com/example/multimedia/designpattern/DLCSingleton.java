package com.example.multimedia.designpattern;

public class DLCSingleton {
    private static DLCSingleton instance;

    private DLCSingleton() {}

    public static DLCSingleton getInstance() {
        if (instance == null) {
            synchronized (DLCSingleton.class) {
                instance = new DLCSingleton();
            }
        }
        return instance;
    }
}
