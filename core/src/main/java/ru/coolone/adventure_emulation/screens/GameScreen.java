package ru.coolone.adventure_emulation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import ru.coolone.adventure_emulation.GameCore;
import ru.coolone.adventure_emulation.SceneScreen;
import ru.coolone.adventure_emulation.game.scripts.persons.Player;

/**
 * Created by coolone on 22.12.17.
 */

public class GameScreen extends SceneScreen {

    /**
     * Player behavior for @{@link ItemWrapper}
     */
    static private Player player;
    /**
     * Just for debug
     */
    private BitmapFont font;
    private Box2DDebugRenderer debugRenderer;

    public GameScreen(String sceneName, SceneLoader loader) {
        super(sceneName, loader);
    }

    @Override
    public void show() {
        super.show();

        player = new Player(
                GameCore.getInstance()
                        .getRootItem(),
                "player",
                GameCore.getInstance()
                        .loader.world
        );

        // Debug info
        debugRenderer = new Box2DDebugRenderer();
        font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        // Debug text
        GameCore.getInstance()
                .loader.getBatch().begin();
        font.draw(GameCore.getInstance()
                        .loader.getBatch(),
                "Move: " + player.getMove() + '\n'
                        + "Grounded: " + player.isPlayerGrounded() + '\n'
                        + "Mode: " + player.getModeId() + '\n'
                        + '\t' + "Movable: " + player.getCurrentMode().movable + '\n'
                        + '\t' + "Move speed: " + player.getCurrentMode().moveVelocity + '\n'
                        + '\t' + "Move max speed: " + player.getCurrentMode().moveMaxVelocity + '\n'
                        + "Player velocity: " + player.getPhysic().body.getLinearVelocity() + '\n'
                        + "FPS: " + Gdx.graphics.getFramesPerSecond(),
                10, GameCore.HEIGHT - 10);
        GameCore.getInstance()
                .loader.getBatch().end();
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
