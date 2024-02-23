package com.example.multimedia.designpattern;

public class ColorFactory extends AbstractFactory {
    @Override
    public Shape getShape(String shapeType) {
        return null;
    }

    @Override
    public Color getColor(String colorType) {
        switch (colorType) {
            case "Red":
                return new Red();
            case "Blue":
                return new Blue();
            case "Black":
                return new Black();
            default:
                break;
        }
        return null;
    }
}
