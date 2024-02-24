package com.example.multimedia.designpattern;

public class StaticInnerClassSingleton {

    private static class InstanceHolder {
        private static StaticInnerClassSingleton instance = new StaticInnerClassSingleton();
    }

    private StaticInnerClassSingleton() {}
    public static StaticInnerClassSingleton getInstance() {
        return InstanceHolder.instance;
    }
}
