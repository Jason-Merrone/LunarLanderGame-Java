package com.example.views;

import com.example.GameStateEnum;
import com.example.KeyboardInput;
import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;
import com.example.serialization.HighScoresManager;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class HighScoresView extends GameStateView {

    private KeyboardInput inputKeyboard;
    private GameStateEnum nextGameState = GameStateEnum.HighScores;
    private Font font;
    private List<Integer> highScores;

    @Override
    public void initialize(Graphics2D graphics) {
        super.initialize(graphics);

        font = new Font("resources/fonts/Roboto-Regular.ttf", 48, false);

        inputKeyboard = new KeyboardInput(graphics.getWindow());
        // When ESC is pressed, set the appropriate new game state
        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> {
            nextGameState = GameStateEnum.MainMenu;
        });
    }

    @Override
    public void initializeSession() {
        nextGameState = GameStateEnum.HighScores;
        highScores = HighScoresManager.instance().getTopHighScores(); // Load high scores when view is initialized
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
        final String message = "High Scores";
        float height = 0.095f;
        float width = font.measureTextWidth(message, height);

        graphics.drawTextByHeight(font, message, 0.0f - width / 2, 0.4f, height, Color.YELLOW);

        float scoreHeight = 0.075f;
        float top = 0.2f;
        int rank = 1;
        for (Integer score : highScores) {
            String scoreMessage = rank + ". " + score;
            width = font.measureTextWidth(scoreMessage, scoreHeight);
            graphics.drawTextByHeight(font, scoreMessage, 0.0f - width / 2, top - (rank * scoreHeight), scoreHeight, Color.WHITE);
            rank++;
            if (rank > 5) break; // Display only top 5
        }
        if (highScores.isEmpty()) {
            String noScoresMessage = "No scores yet!";
            float noScoresWidth = font.measureTextWidth(noScoresMessage, scoreHeight);
            graphics.drawTextByHeight(font, noScoresMessage, 0.0f - noScoresWidth / 2, -0.1f, scoreHeight, Color.WHITE);
        }
    }
}