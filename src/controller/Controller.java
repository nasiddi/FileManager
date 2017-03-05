package controller;

import java.util.Observer;

import model.Model;


public abstract class Controller<ModelType extends Model> implements Observer {

    protected MasterController masterController;
    protected ModelType model;

    public Controller(MasterController masterController) {
        this.masterController = masterController;
    }

    public void setModel(ModelType model) {
        this.model = model;
    }

}
