package ch.bfh.exit_simulation;

/**
 * Created by Vincent Genecand on 23.09.2015.
 */
public class ExitSimulation {

    private SimulationEngine engine;

    public ExitSimulation(SimulationEngine engine) {
        this.engine = engine;
        new Thread(engine).start();
    }

    public static void main(String[] args) {
        SimulationCanvas c = new SimulationCanvas();

        new SimulationWindow(c);
        new ExitSimulation(new SimulationEngine(c));
    }
}
