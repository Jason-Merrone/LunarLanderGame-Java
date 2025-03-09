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

    public boolean lineCircleIntersection(Vector2f circleCenter, float circleRadius) {
        Vector2f pt1 = new Vector2f(this.start.x,this.start.y);
        Vector2f pt2 = new Vector2f(this.end.x,this.end.y);

        // Translate points to circle's coordinate system
        Vector2f d = pt2.sub(pt1); // Direction vector of the line
        Vector2f f = pt1.sub(circleCenter); // Vector from circle center to the start of the line

        float a = d.dot(d);
        float b = 2 * f.dot(d);
        float c = f.dot(f) - circleRadius * circleRadius;

        float discriminant = b * b - 4 * a * c;

        // If the discriminant is negative, no real roots and thus no intersection
        if (discriminant < 0) {
            return false;
        }

        // Check if the intersection points are within the segment
        discriminant = (float) Math.sqrt(discriminant);
        float t1 = (-b - discriminant) / (2 * a);
        float t2 = (-b + discriminant) / (2 * a);

        if (t1 >= 0 && t1 <= 1) {
            return true;
        }
        return t2 >= 0 && t2 <= 1;
    }
}
