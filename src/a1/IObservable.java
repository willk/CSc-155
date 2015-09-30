package a1;

/**
 * Created by willk on 9/30/2015.
 */
public interface IObservable {
    void addObserver(IObserver o);

    void notifyObservers();
}
