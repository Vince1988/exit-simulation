package ch.bfh.exit_simulation.controller;

import ch.bfh.exit_simulation.view.Renderer;

import java.awt.Graphics2D;

/**
 * Created by Vincent Genecand on 23.09.2015.
 */
public abstract class Controller<T> {

    protected final T model;
    protected final Renderer<T> view;

    public Controller(T model, Renderer<T> view) {
        this.model = model;
        this.view = view;
    }

    public abstract void updateModel();

    public void collisionCorrection(T model) {
        // Nothing if not needed!
    }

    public final void updateView(Graphics2D g, float interpolation) {
        this.view.render(this.model, g, interpolation);
    }


}
