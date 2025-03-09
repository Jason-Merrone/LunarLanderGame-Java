package com.example;

import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;

public class StarterProject {
    public static void main(String[] args) {
        //1920 x 1080
        try (Graphics2D graphics = new Graphics2D(1920*2, 1080*2, "Moon Game")) {
            graphics.initialize(Color.CORNFLOWER_BLUE);
            Game game = new Game(graphics);
            game.initialize();
            game.run();
            game.shutdown();
        }
    }
}