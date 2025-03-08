package com.example.views;
import com.example.GameStateEnum;
import com.example.game.Line;
import com.example.game.Terrain;
import edu.usu.graphics.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class GamePlayView extends GameStateView {

    private KeyboardInput inputKeyboard;
    private GameStateEnum nextGameState = GameStateEnum.GamePlay;
    private Font font;
    Terrain terrain;

    @Override
    public void initialize(Graphics2D graphics) {
        Random random = new Random();
        super.initialize(graphics);

        font = new Font("resources/fonts/Roboto-Regular.ttf", 48, false);

        inputKeyboard = new KeyboardInput(graphics.getWindow());
        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> {
            nextGameState = GameStateEnum.MainMenu;
        });

        terrain = new Terrain(true, 0.7f);
    }

    @Override
    public void initializeSession() {
        nextGameState = GameStateEnum.GamePlay;
    }

    @Override
    public GameStateEnum processInput(double elapsedTime) {
        // Updating the keyboard can change the nextGameState
        inputKeyboard.update(elapsedTime);
        return nextGameState;
    }

    @Override
    public void update(double elapsedTime) {
    }

    @Override
    public void render(double elapsedTime) {
        final String message = "Isn't this game fun!";
        final float height = 0.075f;
        final float width = font.measureTextWidth(message, height);

        ArrayList<Line> lines = terrain.getLines();
        ArrayList<Line> safeZones = terrain.getSafeZones();

        graphics.drawTextByHeight(font, message, 0.0f - width / 2, 0 - height / 2, height, Color.YELLOW);
        for(Line line : lines){
            Vector3f startPoint = line.getStart();
            Vector3f endPoint = line.getEnd();
            graphics.draw(startPoint,endPoint,Color.WHITE);
            Vector3f thirdPoint = new Vector3f(startPoint.x,1,0);
            Vector3f fourthPoint = new Vector3f(endPoint.x,1,0);
            graphics.draw(new Triangle(startPoint,endPoint,thirdPoint), new Color(0.3f, 0.3f, 0.3f));
            graphics.draw(new Triangle(thirdPoint,endPoint,fourthPoint), new Color(0.3f, 0.3f, 0.3f));
        }

        for(Line zone : safeZones){
            Vector3f startPoint = zone.getStart();
            Vector3f endPoint = zone.getEnd();
            graphics.draw(startPoint,endPoint,Color.WHITE);
            graphics.draw(new Rectangle(startPoint.x,startPoint.y,endPoint.x- startPoint.x,1f), new Color(0.3f, 0.3f, 0.3f));
        }


    }
}