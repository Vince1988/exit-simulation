package ch.bfh.exit_simulation;

import ch.bfh.exit_simulation.util.SceneLoader;
import ch.bfh.exit_simulation.util.Converter;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.*;

/**
 * Created by Vincent Genecand on 05.10.2015.
 */
public class SimulationCanvas extends Canvas {

    public static final int W = SceneLoader.getInstance().getWindowDimension().width;
    public static final int H = SceneLoader.getInstance().getWindowDimension().height;

    public SimulationCanvas() {
        this.setBounds(0, 0, W, H);
        this.setIgnoreRepaint(true);
        this.setBackground(Color.WHITE);
        this.addMouseListener(GamePanel.getInstance());
        this.addMouseMotionListener(GamePanel.getInstance());
        this.addMouseWheelListener(GamePanel.getInstance());
    }

    public void initCanvas() {
        this.createBufferStrategy(2);
        this.requestFocus();
    }

    public void clear() {
        this.getGraphics().clearRect(0, 0, getWidth(), getHeight());
    }

    public void display() {
        this.getGraphics().dispose();
        this.getBufferStrategy().show();
    }

    public Graphics2D getGraphics() {
        return (Graphics2D) this.getBufferStrategy().getDrawGraphics();
    }
}
