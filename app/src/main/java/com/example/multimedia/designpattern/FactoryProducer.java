package com.example.multimedia.designpattern;

public class FactoryProducer {
    public AbstractFactory getFactory(String factoryType) {
        if (factoryType.equals("shape")) {
            return new ShapeFactory();
        } else if (factoryType.equals("color")) {
            return new ColorFactory();
        }
        return null;
    }
}
