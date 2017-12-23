package ru.coolone.adventure_emulation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import ru.coolone.adventure_emulation.GameCore;
import ru.coolone.adventure_emulation.SceneScreen;
import ru.coolone.adventure_emulation.game.scripts.persons.Player;

/**
 * Created by coolone on 22.12.17.
 */

public class GameScreen extends SceneScreen {

    public static final String TAG = GameScreen.class.getSimpleName();

    private static final String name = "GameScene";
    /**
     * Player behavior for @{@link ItemWrapper}
     */
    static private Player player;
    /**
     * Link for @{@link GameCore}
     */
    private GameCore core;
    /**
     * Just for debug
     */
    private BitmapFont font;
    private Box2DDebugRenderer debugRenderer;

    public GameScreen(
            GameCore core
    ) {
        super(core, name);

        this.core = core;
    }

    @Override
    public void show() {
        super.show();

        player = new Player(
                core,
                "player"
        );

        // Debug info
        debugRenderer = new Box2DDebugRenderer();
        font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        // Debug Box2d physics
        debugRenderer.render(core.getWorld(), core.camera.combined);

        Batch coreBatch = core.getBatch();
        coreBatch.begin();

        if (player.getPhysic().body != null)
            // Debug player text
            font.draw(coreBatch,
                    "Move: " + player.getMove() + '\n'
                            + "Grounded: " + player.isPlayerGrounded() + '\n'
                            + "Mode: " + player.getModeId() + '\n'
                            + '\t' + "Movable: " + player.getCurrentMode().movable + '\n'
                            + '\t' + "Move speed: " + player.getCurrentMode().moveVelocity + '\n'
                            + '\t' + "Move max speed: " + player.getCurrentMode().moveMaxVelocity + '\n'
                            + "Player velocity: " + player.getPhysic().body.getLinearVelocity() + '\n'
                            + "FPS: " + Gdx.graphics.getFramesPerSecond(),
                    10, GameCore.HEIGHT - 10);

        coreBatch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
