package a2;

/**
 * Created by willk on 9/30/2015.
 */
public interface IObservable {
    void addObserver(a2.IObserver o);

    void notifyObservers();
}
