package ch.bfh.exit_simulation.util;

/**
 * Created by Dominik on 06.11.2015.
 */
public class Converter {
    private static int scaleFactor = 300;       //pixel pro Meter!

    public static int getScaleFactor(){
        return scaleFactor;
    }
    public static int setScaleFactor(int sFactor){
        scaleFactor = sFactor;
        return scaleFactor;
    }
    public static double getMeter(double pixel){
        return pixel/scaleFactor;
    }
    public static double getCentimeter(double pixel){
        return pixel/scaleFactor;
    }
    public static double getMillimeter(double pixel){
        return pixel/scaleFactor;
    }
    public static int getPixelFromMeter(int m){
        return m*scaleFactor;
    }
    public static int getPixelFromCentimeter(int cm){
        return cm*scaleFactor/100;
    }
    public static int getPixelFromMillimeter(int mm){
        return mm*scaleFactor/1000;                         //Reihenfolge ist wichtig! (Integer!)
    }
}