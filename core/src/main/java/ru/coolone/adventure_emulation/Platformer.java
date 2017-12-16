package ru.coolone.adventure_emulation;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.ArrayList;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Platformer extends ApplicationAdapter
        implements InputProcessor {
    private SceneLoader loader;

    private SpriteBatch batch;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    @Override
    public void create() {
        Viewport viewport = new FitViewport(800, 480);
        loader = new SceneLoader();
        loader.loadScene("MainScene", viewport);

        // Create scripts
        Player player = new Player(loader.world);

        InputMultiplexer inputMultiplexer = new InputMultiplexer(
                player
        );
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Add scripts
        ItemWrapper root = new ItemWrapper(loader.getRoot());
        root.getChild("playerComposite")
                .addScript(player.new CompositeScript());
        root.getChild("playerComposite")
                .getChild("playerSpriter")
                .addScript(player.new SpriterScript());

        debugRenderer = new Box2DDebugRenderer();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false,
                Gdx.graphics.getWidth() * PhysicsBodyLoader.getScale(),
                Gdx.graphics.getHeight() * PhysicsBodyLoader.getScale()
        );
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(36 / 225f, 20 / 225f, 116 / 225f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        loader.getEngine().update(Gdx.graphics.getDeltaTime());

        batch.begin();
        debugRenderer.render(loader.world, camera.combined);
        batch.end();
    }

    static final ArrayList<Integer> pressedKeys = new ArrayList<Integer>();

    @Override
    public boolean keyDown(int keycode) {
        pressedKeys.add(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        pressedKeys.remove((Integer)keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}