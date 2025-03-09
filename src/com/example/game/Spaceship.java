package com.example.game;

import org.joml.Vector3f;

public class Spaceship {
    private final Vector3f speed = new Vector3f(0, 0, 0);;
    private final Vector3f position = new Vector3f(0, 0, 0);
    private float angle = 0; // in radians
    private boolean thrusting = false;
    private boolean left = false;
    private boolean right = false;
    private boolean collided = false;
    private float remainingFuel = 100f;
    private float fuelConsumptionRate = 0.2f; // Adjust as needed for your game balance.

    private float rotationVelocity = 0.0f;


    public Spaceship(float x, float y, float startAngle){
        this.position.x = x;
        this.position.y = y;
        this.angle = startAngle;
    }

    public void updatePosition() {
        if(!collided) {
            if (left) {
                rotationVelocity -= 0.005f;
            }

            if (right) {
                rotationVelocity += 0.005f;
            }

            angle += rotationVelocity;
            // Update position based on speed, thrust, and gravity
            if (this.thrusting && remainingFuel > 0.0f) {
                float thrust = .00015f;
                speed.y -= (float) (thrust * Math.cos(angle));
                speed.x += (float) (thrust * Math.sin(angle));
                this.remainingFuel -= fuelConsumptionRate;
            }

            float gravity = 0.00005f;
            speed.y += gravity; // Apply gravity

            // Update position based on speed
            position.x += speed.x;
            position.y += speed.y;
        }
    }

    public void setThrust(boolean isThrusting){
        this.thrusting = isThrusting;
    }

    public Vector3f getSpeed() {
        return new Vector3f(speed);
    }

    public float getAngle() {
        return angle;
    }

    public void left(boolean left){
        this.left = left;
    }

    public void right(boolean right){
        this.right = right;
    }

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public void isCollided(boolean collided){
        this.collided=collided;
    }

    public float getRemainingFuel(){
        return this.remainingFuel;
    }
}
