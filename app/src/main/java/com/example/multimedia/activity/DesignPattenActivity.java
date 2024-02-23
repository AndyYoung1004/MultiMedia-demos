package com.example.multimedia.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.multimedia.R;
import com.example.multimedia.designpattern.AbstractFactory;
import com.example.multimedia.designpattern.Color;
import com.example.multimedia.designpattern.FactoryProducer;
import com.example.multimedia.designpattern.Shape;
import com.example.multimedia.designpattern.ShapeFactory;

public class DesignPattenActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer);
    }

    void testFactoryPattern() {
        ShapeFactory factory = new ShapeFactory();
        Shape circelShape = factory.getShape("Circle");
        circelShape.draw();
    }

    void testAbstractFactoryPattern() {
        FactoryProducer producer = new FactoryProducer();
        AbstractFactory colorFactory = producer.getFactory("color");
        Color color = colorFactory.getColor("red");
        color.fill();

        AbstractFactory shapeFactory = producer.getFactory("shape");
        Shape shape = shapeFactory.getShape("circle");
        shape.draw();
    }
}
