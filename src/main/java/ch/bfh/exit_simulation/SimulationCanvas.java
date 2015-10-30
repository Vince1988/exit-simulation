package ch.bfh.exit_simulation;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Created by Vincent Genecand on 05.10.2015.
 */
public class SimulationCanvas extends Canvas {

    public static final int W = 1280;
    public static final int H = 720;

    public SimulationCanvas() {
        this.setBounds(0, 50, W, H);
        this.setIgnoreRepaint(true);
        this.setBackground(Color.WHITE);
        this.addMouseListener(GamePanel.getInstance());
        this.addMouseMotionListener(GamePanel.getInstance());
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
