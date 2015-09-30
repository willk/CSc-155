package a1;

import java.util.ArrayList;

/**
 * Created by willk on 9/29/2015.
 */

/*
 * TODO:
 * move most of this class into the map view class.
 * hope that shit works.
 */
public class GameWorld implements IObservable {
    private float xAxis, yAxis, scaleAmount;
    private int colorNumber;
    private ArrayList<IObserver> observers;

    public void initLayout() {
        colorNumber = 0;
        xAxis = 0;
        yAxis = 0;
        scaleAmount = 1;

        observers = new ArrayList<>();
    }

    public void up() {
        if (yAxis < 1.0) yAxis += 0.03;
        notifyObservers();
    }

    public void down() {
        if (yAxis > -1.0) yAxis -= 0.03;
        notifyObservers();
    }

    public void changeColor() {
        colorNumber += 1;
        colorNumber %= 4;
        notifyObservers();
    }

    public int getColorNumber() {
        return this.colorNumber;
    }

    public float getYAxis() {
        return this.yAxis;
    }

    public float getXAxis() {
        return this.getXAxis();
    }

    @Override
    public void addObserver(IObserver o) {
        observers.add(o);
    }

    @Override
    public void notifyObservers() {
        System.out.println(yAxis);
        for (IObserver observer : observers) {
            observer.update(this);
        }
    }
}
