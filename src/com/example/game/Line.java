package com.example.game;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Line {
    private Vector3f start;
    private Vector3f end;

    public Line(Vector3f start, Vector3f end){
        this.start = start;
        this.end = end;
    }

    public Line(Line line){
        this.start = line.start;
        this.end = line.end;
    }

    public Vector3f getStart() {
        return start;
    }

    public Vector3f getEnd(){
        return end;
    }

    //Splits the line into two lines down the middle, returns the second line
    public Line halveLine() {
        float midX = (float) (start.x() + end.x()) / 2.0f;
        float midY = (float) (start.y() + end.y()) / 2.0f;
        Vector3f midpoint = new Vector3f(midX, midY, 0);
        Vector3f temp = new Vector3f(end);
        end = midpoint;
        return new Line(midpoint, temp);
    }
}
