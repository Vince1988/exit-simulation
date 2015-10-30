package ch.bfh.exit_simulation;

/**
 * Created by Vincent Genecand on 23.09.2015.
 */
public class SimulationEngine implements Runnable {

    private SimulationCanvas canvas;

    private GamePanel gamePanel;

    public SimulationEngine(SimulationCanvas c) {
        this.gamePanel = GamePanel.getInstance();
        this.canvas = c;
    }

    private void update() {
        gamePanel.update();
    }

    private void render(float interpolation) {
        this.canvas.clear();
        gamePanel.render(this.canvas.getGraphics(), interpolation);
        this.canvas.display();
    }

    private void loop() {
        boolean running = true;

        //move later
        int frameCount = 0;
        int fps;

        final double UPS = 30.0;
        final double TIME_BETWEEN_UPDATES = 1000000000 / UPS;
        final int MAX_UPDATES_BEFORE_RENDER = 5;

        double lastUpdateTime = System.nanoTime();
        double lastRenderTime;

        final double TARGET_FPS = 60;
        final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

        int lastSecondTime = (int)(lastUpdateTime / 1000000000);

        while(running) {
            double now = System.nanoTime();
            int updateCount = 0;

            while(now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER) {
                this.update();
                lastUpdateTime += TIME_BETWEEN_UPDATES;
                updateCount++;
            }

            if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
                lastUpdateTime = now - TIME_BETWEEN_UPDATES;
            }

            float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES));
            this.render(interpolation);
            frameCount++;
            lastRenderTime = now;

            int thisSecond = (int) (lastUpdateTime / 1000000000);

            if (thisSecond > lastSecondTime) {
                System.out.println("Second: " + thisSecond + " | Frames: " + frameCount);
                fps = frameCount;
//                this.frame.setTitle("FPS: " + fps);
                frameCount = 0;
                lastSecondTime = thisSecond;
            }

            while(now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
                Thread.yield();
                try {
                    Thread.sleep(1);
                } catch(Exception e) {

                }

                now = System.nanoTime();
            }




        }
    }

    @Override
    public void run() {
        this.loop();
    }

}
