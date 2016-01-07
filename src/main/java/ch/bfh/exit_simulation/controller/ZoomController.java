package ch.bfh.exit_simulation.controller;

/**
 * Created by Dominik on 13.11.2015.
 */
public class ZoomController {

    private static ZoomController instance = null;
    private double scale = 1;
    private double translateX = 1;
    private double translateY = 1;

    protected ZoomController(){
        //defeat instantiation
    }

    public static ZoomController getInstance(){
        if (instance == null){
            instance = new ZoomController();
        }
        return instance;
    }



    public void setScale(double scale) {
        this.scale = scale;
    }



    public double getScale() {
        return scale;
    }

    public void setTranslateX(double translateX) {
        this.translateX = translateX;
    }
    public double getTranslateX() {
        return translateX;
    }
    public void setTranslateY(double translateY) {
        this.translateY = translateY;
    }
    public double getTranslateY() {
        return translateY;
    }

}
