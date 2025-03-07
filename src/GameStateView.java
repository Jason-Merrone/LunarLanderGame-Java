import edu.usu.graphics.Graphics2D;

public abstract class GameStateView implements IGameState {
    protected Graphics2D graphics;
    public void initialize(Graphics2D graphics) {
        this.graphics = graphics;
    }
    public void initializeSession() {};
    public abstract GameStateEnum processInput(double elapsedTime);
    public abstract void update(double elapsedTime);
    public abstract void render(double elapsedTime);
}