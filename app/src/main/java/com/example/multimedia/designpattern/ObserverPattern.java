package com.example.multimedia.designpattern;

import java.util.ArrayList;
import java.util.List;

public class ObserverPattern {

    private List<Object> observers = new ArrayList<>();
    public void attach(Object observer) {
        observers.add(observer);
    }

    public void notifyAllObservers() {
        for (Object observer : observers) {
            observer.notify();
        }
    }

    public void updateState() {

    }
}
