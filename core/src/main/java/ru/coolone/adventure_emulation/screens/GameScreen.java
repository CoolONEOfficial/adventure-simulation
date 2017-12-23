package ru.coolone.adventure_emulation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import ru.coolone.adventure_emulation.GameCore;
import ru.coolone.adventure_emulation.SceneScreen;
import ru.coolone.adventure_emulation.game.InputGroups;
import ru.coolone.adventure_emulation.game.scripts.Button;
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
    private Player player;
    /**
     * Move buttons
     */
    private Button downButton;
    private Button upButton;
    private Button leftButton;
    private Button rightButton;
    /**
     * Just for debug
     */
    private BitmapFont font;
    private Box2DDebugRenderer debugRenderer;

    public GameScreen(
            GameCore core
    ) {
        super(core, name);
    }

    @Override
    public void show() {
        super.show();

        // Player
        player = new Player(
                core,
                "player"
        );

        // Move buttons
        downButton = new Button(
                core,
                "downButton"
        );
        downButton.addListener(
                new Button.ButtonListener() {
                    @Override
                    public void onButtonClick() {
                    }

                    @Override
                    public void onButtonDown() {
                        InputGroups.groupActivate(InputGroups.InputGroupId.CROUCH);
                    }

                    @Override
                    public void onButtonUp() {
                        InputGroups.groupDeactivate(InputGroups.InputGroupId.CROUCH);
                    }
                }
        );
        upButton = new Button(
                core,
                "upButton"
        );
        upButton.addListener(
                new Button.ButtonListener() {
                    @Override
                    public void onButtonClick() {
                    }

                    @Override
                    public void onButtonDown() {
                        InputGroups.groupActivate(InputGroups.InputGroupId.JUMP);
                    }

                    @Override
                    public void onButtonUp() {
                        InputGroups.groupDeactivate(InputGroups.InputGroupId.JUMP);
                    }
                }
        );
        leftButton = new Button(
                core,
                "leftButton"
        );
        leftButton.addListener(
                new Button.ButtonListener() {
                    @Override
                    public void onButtonClick() {
                    }

                    @Override
                    public void onButtonDown() {
                        InputGroups.groupActivate(InputGroups.InputGroupId.MOVE_LEFT);
                    }

                    @Override
                    public void onButtonUp() {
                        InputGroups.groupDeactivate(InputGroups.InputGroupId.MOVE_LEFT);
                    }
                }
        );
        rightButton = new Button(
                core,
                "rightButton"
        );
        rightButton.addListener(
                new Button.ButtonListener() {
                    @Override
                    public void onButtonClick() {
                    }

                    @Override
                    public void onButtonDown() {
                        InputGroups.groupActivate(InputGroups.InputGroupId.MOVE_RIGHT);
                    }

                    @Override
                    public void onButtonUp() {
                        InputGroups.groupDeactivate(InputGroups.InputGroupId.MOVE_RIGHT);
                    }
                }
        );

        // Debug info
        debugRenderer = new Box2DDebugRenderer();
        font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        // Debug
        if (GameCore.DEBUG) {
            // Debug Box2d physics
            debugRenderer.render(core.getWorld(), core.camera.combined);

            Batch coreBatch = core.getBatch();
            coreBatch.begin();

            // Debug player text
            font.draw(coreBatch,
                    "Move: " + player.getMove() + '\n'
                            + "Grounded: " + player.isPlayerGrounded() + '\n'
                            + "Mode: " + player.getModeId() + '\n'
                            + '\t' + "Movable: " + player.getCurrentMode().movable + '\n'
                            + '\t' + "Move speed: " + player.getCurrentMode().moveVelocity + '\n'
                            + '\t' + "Move max speed: " + player.getCurrentMode().moveMaxVelocity + '\n'
                            + "Player velocity: " +
                            (
                                    player.getPhysic().body != null
                                            ? player.getPhysic().body.getLinearVelocity()
                                            : "null"
                            ) + '\n'
                            + "FPS: " + Gdx.graphics.getFramesPerSecond(),
                    10, GameCore.HEIGHT - 10);

            coreBatch.end();
        }
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
