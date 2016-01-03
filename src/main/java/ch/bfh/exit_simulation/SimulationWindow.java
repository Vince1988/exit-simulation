package ch.bfh.exit_simulation;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Vincent Genecand on 23.09.2015.
 */
public class SimulationWindow extends JFrame {

    private final SimulationCanvas canvas;

    public SimulationWindow(SimulationCanvas canvas) throws HeadlessException {
        this.setTitle("Title");
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.canvas = canvas;

        this.addSimulationCanvas(canvas);
        this.displayWindow();
    }

    private void addSimulationCanvas(SimulationCanvas canvas) {
        JPanel panel = (JPanel) this.getContentPane();
        panel.setPreferredSize(new Dimension(canvas.getWidth(), canvas.getHeight()));
        panel.setLayout(null);
        panel.setBackground(Color.RED);

        panel.add(canvas);
    }

    private void displayWindow() {
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        this.canvas.initCanvas();
    }
}
