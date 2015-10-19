package a2;


import java.util.ArrayList;

/**
 * Created by willk on 9/29/2015.
 */

public class GameWorld implements IObservable {
    private float yAxis;
    private int colorNumber;
    private ArrayList<IObserver> observers;

    public void initLayout() {
        colorNumber = 0;
        yAxis = 0;
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

    @Override
    public void addObserver(IObserver o) {
        observers.add(o);
    }

    @Override
    public void notifyObservers() {
        for (IObserver observer : observers) {
            observer.update(this);
        }
    }
}
