import edu.usu.graphics.*;

import java.util.HashMap;
import java.util.Stack;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private final Graphics2D graphics;
    private HashMap<GameStateEnum, IGameState> states;
    private IGameState currentState;
    GameStateEnum nextStateEnum = GameStateEnum.MainMenu;
    GameStateEnum prevStateEnum = GameStateEnum.MainMenu;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
    }

    public void initialize() {
        states = new HashMap<>() {{
//            put(GameStateEnum.MainMenu, new MainMenuView());
//            put(GameStateEnum.GamePlay, new GamePlayView());
//            put(GameStateEnum.HighScores, new HighScoresView());
//            put(GameStateEnum.Help, new HelpView());
//            put(GameStateEnum.About, new AboutView());
        }};
        for (var state : states.values()) {
            state.initialize(graphics);
        }
        currentState = states.get(GameStateEnum.MainMenu);
        currentState.initializeSession();
    }

    public void shutdown() {
    }

    public void run() {
        // Grab the first time
        double previousTime = glfwGetTime();

        while (!graphics.shouldClose()) {
            double currentTime = glfwGetTime();
            double elapsedTime = currentTime - previousTime;    // elapsed time is in seconds
            previousTime = currentTime;

            processInput(elapsedTime);
            update(elapsedTime);
            render(elapsedTime);
        }
    }

    private void processInput(double elapsedTime) {
        // Poll for window events: required in order for window, keyboard, etc events are captured.
        glfwPollEvents();

        // If user presses ESC, then exit the program
        if (glfwGetKey(graphics.getWindow(), GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(graphics.getWindow(), true);
        }
    }

    private void update(double elapsedTime) {
    }

    private void render(double elapsedTime) {
        graphics.begin();

        graphics.end();
    }
}
