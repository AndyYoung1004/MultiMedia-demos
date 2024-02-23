package com.example.multimedia.designpattern;

public class ShapeFactory extends AbstractFactory {
    @Override
    public Shape getShape(String shapeType) {
        switch (shapeType) {
            case "Circle":
                return new Circle();
            case "Rectangle":
                return new Rectangle();
            case "Triangle":
                return new Triangle();
            default:
                break;
        }
        return null;
    }

    @Override
    public Color getColor(String colorType) {
        return null;
    }
}
