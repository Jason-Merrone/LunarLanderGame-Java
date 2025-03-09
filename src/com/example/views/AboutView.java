package com.example.views;

import com.example.GameStateEnum;
import com.example.KeyboardInput;
import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;

import static org.lwjgl.glfw.GLFW.*;

public class AboutView extends GameStateView {

    private KeyboardInput inputKeyboard;
    private GameStateEnum nextGameState = GameStateEnum.About;
    private Font font;

    @Override
    public void initialize(Graphics2D graphics) {
        super.initialize(graphics);

        font = new Font("resources/fonts/Roboto-Regular.ttf", 48, false); // Keep the font initialization

        inputKeyboard = new KeyboardInput(graphics.getWindow());
        // When ESC is pressed, set the appropriate new game state
        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> {
            nextGameState = GameStateEnum.MainMenu;
        });
    }

    @Override
    public void initializeSession() {
        nextGameState = GameStateEnum.About;
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

        final String gameByLine = "Game by";
        final String authorLine = "Jason Merrone";
        final String creditLine = "Credit";
        final String qwenLine = "Qwen2.5 14b Coder";
        final String elevenlabsLine = "elevenlabs for sound effects";
        final String nasaLine = "NASA for image assets"; // Added NASA credit
        final String stableDiffusionLine = "Stable Diffusion for images"; // Added Stable Diffusion credit

        float height = 0.05f; // Slightly reduced height to accommodate more lines
        float currentY = -0.2f; // Start position further up to fit more credits

        // "Game by" line
        float gameByWidth = font.measureTextWidth(gameByLine, height * 0.8f);
        graphics.drawTextByHeight(font, gameByLine, 0.0f - gameByWidth / 2, currentY, height * 0.8f, Color.WHITE);
        currentY += height * 0.8f;

        // "Jason Merrone" line
        float authorWidth = font.measureTextWidth(authorLine, height * 1.2f);
        graphics.drawTextByHeight(font, authorLine, 0.0f - authorWidth / 2, currentY, height * 1.2f, Color.YELLOW);
        currentY += height * 1.4f; // Slightly reduced spacing after author

        // "Credit" line
        float creditWidth = font.measureTextWidth(creditLine, height);
        graphics.drawTextByHeight(font, creditLine, 0.0f - creditWidth / 2, currentY, height, Color.WHITE);
        currentY += height * 1.0f; // Slightly reduced spacing before first credit

        // "Qwen2.5 14b Coder" line
        float qwenWidth = font.measureTextWidth(qwenLine, height);
        graphics.drawTextByHeight(font, qwenLine, 0.0f - qwenWidth / 2, currentY, height, Color.WHITE);
        currentY += height * 1.0f; // Reduced spacing between credit items

        // "elevenlabs for sound effects" line
        float elevenlabsWidth = font.measureTextWidth(elevenlabsLine, height);
        graphics.drawTextByHeight(font, elevenlabsLine, 0.0f - elevenlabsWidth / 2, currentY, height, Color.WHITE);
        currentY += height * 1.0f; // Reduced spacing between credit items

        // "NASA for image assets" line
        float nasaWidth = font.measureTextWidth(nasaLine, height);
        graphics.drawTextByHeight(font, nasaLine, 0.0f - nasaWidth / 2, currentY, height, Color.WHITE);
        currentY += height * 1.0f; // Reduced spacing between credit items

        // "Stable Diffusion for images" line
        float stableDiffusionWidth = font.measureTextWidth(stableDiffusionLine, height);
        graphics.drawTextByHeight(font, stableDiffusionLine, 0.0f - stableDiffusionWidth / 2, currentY, height, Color.WHITE);
    }
}