package model;

import java.util.Observable;



public abstract class Model extends Observable {


    public Model() {
    }

    public abstract void reset();

}
