package com.example.game;

import org.joml.Vector3f;

public class Spaceship {
    float speed = 0f;
    float thrust = 1f;
    float gravity = 0.001f;
    Vector3f position = new Vector3f(0, 0, 0);
    float angle = 0; // in radians
}
