package com.example.game;

import org.joml.Vector3f;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Random;

public class Terrain {
    private ArrayList<Line> lines = new ArrayList<>();
    private ArrayList<Line> safeZones = new ArrayList<>();

    public Terrain(boolean twoSafeZones, float smoothness){
       generate(twoSafeZones,smoothness);
    }

    public void generate(boolean twoSafeZones, float smoothness){
        lines = new ArrayList<>();
        safeZones = new ArrayList<>();
        Random random = new Random();
        Vector3f point1 = new Vector3f(-1f, random.nextFloat() * 0.5f, 0);
        Vector3f point2 = new Vector3f(1f, random.nextFloat() * 0.5f, 0);

        float safeMargin = 0.5f;
        float safeZoneLength = 0.14f;
        if(!twoSafeZones)
            safeZoneLength = 0.07f;
        float gapBetweenZones = 0.1f;
        float leftBoundary = -1f + safeMargin;
        float safeZone1MaxStart = 1f - safeMargin - 2 * safeZoneLength - gapBetweenZones;
        float safeZone2MaxStart = 1f - safeMargin - safeZoneLength;

        float safeZone1X = random.nextFloat() * (safeZone1MaxStart - leftBoundary) + leftBoundary;
        Vector3f safePoint1 = new Vector3f(safeZone1X, random.nextFloat() * 0.5f, 0);
        Vector3f safePoint2 = new Vector3f(safeZone1X + safeZoneLength, safePoint1.y, 0);

        Line line1 = new Line(point1, safePoint1);
        lines.add(line1);

        Line line2 = new Line(safePoint1, safePoint2);
        safeZones.add(new Line(line2));

        if(twoSafeZones) {
            float safeZone2MinStart = safeZone1X + safeZoneLength + gapBetweenZones;
            float safeZone2X = random.nextFloat() * (safeZone2MaxStart - safeZone2MinStart) + safeZone2MinStart;
            Vector3f safePoint3 = new Vector3f(safeZone2X, random.nextFloat() * 0.5f, 0);
            Vector3f safePoint4 = new Vector3f(safeZone2X + safeZoneLength, safePoint3.y, 0);

            Line line3 = new Line(safePoint2, safePoint3);
            lines.add(line3);

            Line line4 = new Line(safePoint3, safePoint4);
            safeZones.add(new Line(line4));

            Line line5 = new Line(safePoint4, point2);
            lines.add(line5);
        }
        else{
            Line line5 = new Line(safePoint2, point2);
            lines.add(line5);
        }

        for(int i = 0; i < 14; i++){
            ArrayList<Line> newLines = new ArrayList<>();
            for(Line line : lines){
                Line secondHalfLine = line.halveLine();
                float r = (float) ( smoothness *random.nextGaussian()*(line.getStart().x-secondHalfLine.getEnd().x));
                secondHalfLine.getStart().y = (float).5*(line.getStart().y + secondHalfLine.getEnd().y) + r;

                if(secondHalfLine.getStart().y > 0.56f) {
                    r = 0;
                    secondHalfLine.getStart().y = (float).5*(line.getStart().y + secondHalfLine.getEnd().y) + r;
                }

                if(secondHalfLine.getStart().y < 0f ) {
                    r = 0;
                    secondHalfLine.getStart().y = (float).5*(line.getStart().y + secondHalfLine.getEnd().y) + r;
                }
                newLines.add(secondHalfLine);
            }
            lines.addAll(newLines);
        }
    }

    public ArrayList<Line>  getLines(){
        return new ArrayList<>(lines);
    }

    public ArrayList<Line> getSafeZones() {
        return new ArrayList<>(safeZones);
    }
}
