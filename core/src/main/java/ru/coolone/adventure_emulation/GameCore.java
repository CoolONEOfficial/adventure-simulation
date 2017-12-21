package ru.coolone.adventure_emulation;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import ru.coolone.adventure_emulation.persons.Player;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class GameCore extends ApplicationAdapter {
    private static final String TAG = GameCore.class.getSimpleName();

    private static final int WIDTH = 800;
    private static final int HEIGHT = 480;

    /**
     * Overlap2d scene loader
     */
    private SceneLoader loader;

    /**
     * Player behavior for @{@link ItemWrapper}
     */
    private Player player;

    private SpriteBatch batch;
    private BitmapFont font;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    @Override
    public void create() {
        Viewport viewport = new FitViewport(WIDTH, HEIGHT);
        loader = new SceneLoader();
        loader.loadScene("MainScene", viewport);

        // Create scripts
        player = new Player(loader.world);
        InputGroups.addListener(player);

        // Add scripts
        ItemWrapper root = new ItemWrapper(loader.getRoot());
        root.getChild("playerComposite")
                .addScript(player.new CompositeScript());
        root.getChild("playerComposite")
                .getChild("playerSpriter")
                .addScript(player.new SpriterScript());

        batch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();
        font = new BitmapFont();
        camera = new OrthographicCamera();
        camera.setToOrtho(false,
                WIDTH * PhysicsBodyLoader.getScale(),
                HEIGHT * PhysicsBodyLoader.getScale()
        );
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(36 / 225f, 20 / 225f, 116 / 225f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Overlap2d scene
        loader.getEngine().update(Gdx.graphics.getDeltaTime());

        // Debug box2d physic masks
        debugRenderer.render(loader.world, camera.combined);

        // Debug text
        loader.getBatch().begin();
        font.draw(loader.getBatch(),
                "Move: " + player.getMove() + '\n'
                        + "Grounded: " + player.isPlayerGrounded() + '\n'
                        + "Mode: " + player.getModeId() + '\n'
                        + '\t' + "Movable: " + player.getCurrentMode().movable + '\n'
                        + '\t' + "Move speed: " + player.getCurrentMode().moveVelocity + '\n'
                        + '\t' + "Move max speed: " + player.getCurrentMode().moveMaxVelocity + '\n'
                        + "Player velocity: " + player.getPhysic().body.getLinearVelocity() + '\n'
                        + "FPS: " + Gdx.graphics.getFramesPerSecond(),
                10, HEIGHT - 10);
        loader.getBatch().end();
    }

    @Override
    public void dispose() {
        font.dispose();
        batch.dispose();
    }
}