package com.example.views;
import com.example.GameStateEnum;
import com.example.KeyboardInput;
import com.example.game.Line;
import com.example.game.Spaceship;
import com.example.game.Terrain;
import com.example.serialization.HighScoresManager;
import edu.usu.graphics.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class GamePlayView extends GameStateView {

    private KeyboardInput inputKeyboard;
    private GameStateEnum nextGameState = GameStateEnum.GamePlay;
    private Font font;
    Terrain terrain;

    Spaceship spaceship;
    private Rectangle shipRect;
    private final float shipWidth = .1f;
    private final float shipHeight = .1f;
    private Texture shipTexture;
    private boolean wonGame = false;
    private boolean lostGame = false;
    private double countDownToStart = 0;
    private double getCountDownToNextLevel = 0;
    private float score = 1000000;
    private boolean hardMode = false;
    private boolean newGameStart = false;
    private boolean paused = false;
    // 0: Resume, 1: Main Menu
    private int pauseMenuSelection = 0;

    @Override
    public void initialize(Graphics2D graphics) {
        super.initialize(graphics);

        inputKeyboard = new KeyboardInput(graphics.getWindow());
        // Toggle pause mode with ESC
        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> {
            paused = !paused;
            if (paused) {
                pauseMenuSelection = 0; // default to "Resume"
            }
        });

        // UP key: either control spaceship thrust or navigate pause menu
        inputKeyboard.registerCommand(GLFW_KEY_UP, false,
                (double elapsedTime) -> {
                    if (paused) {
                        pauseMenuSelection = (pauseMenuSelection - 1 + 2) % 2;
                    } else {
                        spaceship.setThrust(true);
                    }
                },
                (double elapsedTime) -> {
                    if (!paused) {
                        spaceship.setThrust(false);
                    }
                }
        );

        // DOWN key: navigate pause menu when paused
        inputKeyboard.registerCommand(GLFW_KEY_DOWN, false,
                (double elapsedTime) -> {
                    if (paused) {
                        pauseMenuSelection = (pauseMenuSelection + 1) % 2;
                    }
                },
                null
        );

        // LEFT key: spaceship control only when not paused
        inputKeyboard.registerCommand(GLFW_KEY_LEFT, false,
                (double elapsedTime) -> {
                    if (!paused) {
                        spaceship.left(true);
                    }
                },
                (double elapsedTime) -> {
                    if (!paused) {
                        spaceship.left(false);
                    }
                }
        );

        // RIGHT key: spaceship control only when not paused
        inputKeyboard.registerCommand(GLFW_KEY_RIGHT, false,
                (double elapsedTime) -> {
                    if (!paused) {
                        spaceship.right(true);
                    }
                },
                (double elapsedTime) -> {
                    if (!paused) {
                        spaceship.right(false);
                    }
                }
        );

        // ENTER key: select the highlighted pause menu option when paused
        inputKeyboard.registerCommand(GLFW_KEY_ENTER, true, (double elapsedTime) -> {
            if (paused) {
                if (pauseMenuSelection == 0) { // Resume
                    paused = false;
                } else if (pauseMenuSelection == 1) { // Main Menu
                    nextGameState = GameStateEnum.MainMenu;
                }
            }
        });

        // Keypad ENTER support
        inputKeyboard.registerCommand(GLFW_KEY_KP_ENTER, true, (double elapsedTime) -> {
            if (paused) {
                if (pauseMenuSelection == 0) { // Resume
                    paused = false;
                } else if (pauseMenuSelection == 1) { // Main Menu
                    nextGameState = GameStateEnum.MainMenu;
                }
            }
        });
    }

    @Override
    public void initializeSession() {
        reset(false);
    }

    private void reset(boolean hardMode){
        nextGameState = GameStateEnum.GamePlay;
        Random random = new Random();

        spaceship = new Spaceship(-.1f,-.4f,4.712f);
        shipRect = new Rectangle(spaceship.getPosition().x, spaceship.getPosition().y, shipWidth, shipHeight);
        shipTexture = new Texture("resources/images/lander.png");

        font = new Font("resources/fonts/Roboto-Regular.ttf", 48, false);
        if (!hardMode) {
            terrain = new Terrain(true, 0.7f);
            score = 10000;
        } else {
            terrain = new Terrain(false, 0.7f);
        }
        lostGame = false;
        wonGame = false;
        countDownToStart = 5;
        getCountDownToNextLevel = 0;
        this.hardMode = hardMode;
        this.newGameStart = false;
        paused = false;
    }

    @Override
    public GameStateEnum processInput(double elapsedTime) {
        // Update the keyboard; note that the registered commands handle both game and pause inputs.
        inputKeyboard.update(elapsedTime);
        return nextGameState;
    }

    @Override
    public void update(double elapsedTime) {
        if (!paused) {
            countDownToStart -= elapsedTime;
            getCountDownToNextLevel -= elapsedTime;
            if (countDownToStart < 0)
                spaceship.updatePosition();

            if (getCountDownToNextLevel < 0 && newGameStart && !hardMode)
                reset(true);

            if (getCountDownToNextLevel < 0 && countDownToStart < 0)
                score -= (float) elapsedTime;

            shipRect.left = spaceship.getPosition().x;
            shipRect.top = spaceship.getPosition().y;

            new Thread(() -> {
                for (Line line : terrain.getLines()) {
                    if (line.lineCircleIntersection(
                            new Vector2f(shipRect.left + shipRect.width / 2, shipRect.top + shipRect.height / 2),
                            0.03f)) {
                        spaceship.isCollided(true);
                        lostGame = true;
                    }
                }
                for (Line line : terrain.getSafeZones()) {
                    if (!newGameStart && line.lineCircleIntersection(
                            new Vector2f(shipRect.left + shipRect.width / 2, shipRect.top + shipRect.height / 2),
                            0.03f)) {
                        spaceship.isCollided(true);
                        double shipSpeed = Math.sqrt(Math.pow(spaceship.getSpeed().x, 2) +
                                Math.pow(spaceship.getSpeed().y, 2)) * 1000;
                        double shipAngle = clampAngle(Math.toDegrees(spaceship.getAngle()));
                        if (shipSpeed < 2 && (shipAngle < 5 || (shipAngle > 355 && shipAngle <= 360))) {
                            wonGame = true;
                            getCountDownToNextLevel = 5;
                            newGameStart = true;
                            HighScoresManager.instance().addHighScore((int) score); // Save score when game is won
                        } else {
                            lostGame = true;
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    public void render(double elapsedTime) {
        if (!paused) {
            DecimalFormat df = new DecimalFormat("#.##");
            final String fuelRemaining = "Remaining fuel: " + (int) spaceship.getRemainingFuel();
            final double shipSpeed = Math.sqrt(Math.pow(spaceship.getSpeed().x, 2) +
                    Math.pow(spaceship.getSpeed().y, 2)) * 1000;
            final String currentSpeed = "Speed: " + df.format(shipSpeed);
            final double shipAngle = clampAngle(Math.toDegrees(spaceship.getAngle()));
            final String currentAngle = "Angle: " + df.format(shipAngle);

            ArrayList<Line> lines = terrain.getLines();
            ArrayList<Line> safeZones = terrain.getSafeZones();

            float textSize = 0.044f;

            Color fuelColor = Color.GREEN;
            Color speedColor = Color.GREEN;
            Color angleColor = Color.GREEN;
            if (spaceship.getRemainingFuel() == 0) {
                fuelColor = Color.WHITE;
            }
            if (shipSpeed >= 2) {
                speedColor = Color.WHITE;
            }
            if (shipAngle >= 5 && shipAngle <= 355) {
                angleColor = Color.WHITE;
            }

            if (wonGame) {
                String message = "YOU WON!";
                score = Math.round(100 - spaceship.getRemainingFuel());
                float height = 0.075f;
                float width = font.measureTextWidth(message, height);
                graphics.drawTextByHeight(font, message, 0.0f - width / 2, 0 - height / 2, height, Color.GREEN);
                if (!hardMode) {
                    message = "T-" + Math.round(getCountDownToNextLevel);
                    height = 0.044f;
                    width = font.measureTextWidth(message, height);
                    graphics.drawTextByHeight(font, message, 0.9f - width, 0 - height / 2 + 0.5f, height, Color.GREEN);
                }
            } else if (lostGame) {
                final String message = "YOU LOST!";
                final float height = 0.075f;
                final float width = font.measureTextWidth(message, height);
                graphics.drawTextByHeight(font, message, 0.0f - width / 2, 0 - height / 2, height, Color.RED);
            } else if (countDownToStart > 0) {
                final String message = "T-" + Math.round(countDownToStart);
                final float height = 0.095f;
                final float width = font.measureTextWidth(message, height);
                graphics.drawTextByHeight(font, message, 0.9f - width, 0 - height / 2 + 0.5f, height, Color.GREEN);
            }

            graphics.drawTextByHeight(font, fuelRemaining, .6f, -.5f, textSize, fuelColor);
            graphics.drawTextByHeight(font, currentSpeed, .6f, -.4f, textSize, speedColor);
            graphics.drawTextByHeight(font, currentAngle, .6f, -.3f, textSize, angleColor);

            for (Line line : lines) {
                Vector3f startPoint = line.getStart();
                Vector3f endPoint = line.getEnd();
                graphics.draw(startPoint, endPoint, Color.WHITE);
                Vector3f thirdPoint = new Vector3f(startPoint.x, 1, 0);
                Vector3f fourthPoint = new Vector3f(endPoint.x, 1, 0);
                graphics.draw(new Triangle(startPoint, endPoint, thirdPoint), new Color(0.3f, 0.3f, 0.3f));
                graphics.draw(new Triangle(thirdPoint, endPoint, fourthPoint), new Color(0.3f, 0.3f, 0.3f));
            }

            for (Line zone : safeZones) {
                Vector3f startPoint = zone.getStart();
                Vector3f endPoint = zone.getEnd();
                graphics.draw(startPoint, endPoint, Color.WHITE);
                graphics.draw(new Rectangle(startPoint.x, startPoint.y, endPoint.x - startPoint.x, 1f), new Color(0.3f, 0.3f, 0.3f));
            }

            graphics.draw(shipTexture, shipRect, spaceship.getAngle(),
                    new Vector2f(shipRect.left + shipRect.width / 2, shipRect.top + shipRect.height / 2), Color.WHITE);
        }
        // If the game is paused, draw the pause menu overlay
        else {
            // Draw a semi-transparent overlay covering the whole screen
            graphics.draw(new Rectangle(-1, -1, 2, 2), new Color(0, 0, 0, 0.5f));

            float menuTextHeight = 0.075f;
            String resumeText = "Resume";
            String mainMenuText = "Main Menu";
            float resumeWidth = font.measureTextWidth(resumeText, menuTextHeight);
            float mainMenuWidth = font.measureTextWidth(mainMenuText, menuTextHeight);

            // Draw the options centered on the screen; highlight the selected option in yellow
            graphics.drawTextByHeight(font, resumeText, 0 - resumeWidth / 2, 0.05f, menuTextHeight,
                    pauseMenuSelection == 0 ? Color.YELLOW : Color.WHITE);
            graphics.drawTextByHeight(font, mainMenuText, 0 - mainMenuWidth / 2, -0.05f, menuTextHeight,
                    pauseMenuSelection == 1 ? Color.YELLOW : Color.WHITE);
        }
    }

    private static double clampAngle(double angle) {
        double clampedAngle = angle % 360.0;
        if (clampedAngle < 0) {
            clampedAngle += 360.0;
        }
        return clampedAngle;
    }
}
