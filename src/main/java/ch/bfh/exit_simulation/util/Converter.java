package ch.bfh.exit_simulation.util;

/**
 * Created by Dominik on 06.11.2015.
 */
public class Converter {
    private static Converter instance = null;
    private int scaleFactor = 300;

    protected Converter(){
        //defeat instantiation
    }

    public static Converter getInstance(){
        if (instance == null){
            instance = new Converter();
        }
        return instance;
    }


    public int getScaleFactor(){
        return scaleFactor;
    }
    public int setScaleFactor(int sFactor){
        scaleFactor = sFactor;
        return scaleFactor;
    }
    public  double getMeter(double pixel){
        return pixel/scaleFactor;
    }
    public double getCentimeter(double pixel){
        return pixel/scaleFactor;
    }
    public double getMillimeter(double pixel){
        return pixel/scaleFactor;
    }
    public int getPixelFromMeter(int m){
        return m*scaleFactor;
    }
    public int getPixelFromCentimeter(int cm){
        return cm*scaleFactor/100;
    }
    public int getPixelFromMillimeter(int mm){
        return mm*scaleFactor/1000;                         //Reihenfolge ist wichtig! (Integer!)
    }
}