package ru.coolone.adventure_emulation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import ru.coolone.adventure_emulation.GameCore;
import ru.coolone.adventure_emulation.game.scripts.button.Button;
import ru.coolone.adventure_emulation.game.scripts.joystick.Joystick;
import ru.coolone.adventure_emulation.game.scripts.persons.Player;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.screen.ScreenScene;

/**
 * Created by coolone on 22.12.17.
 */

public class GameScreen extends ScreenScene {

    public static final String TAG = GameScreen.class.getSimpleName();

    private static final String name = "GameScene";
    private static final int BUTTON_INDENT = 30;
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
     * Joystick for move @{@link Player}
     */
    private Joystick joystick;
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

        // Joystick
        joystick = new Joystick(
                core,
                "joystick"
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
                        Gdx.app.log(TAG, "Left button");
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

//        if (player.isSpawned()) {
//
//            // Camera coords
//            final Vector2 playerPos = new Vector2(
//                    player.transform.x,
//                    player.transform.y
//            );
//            playerPos.x /= PhysicsBodyLoader.getScale();
//            playerPos.y /= PhysicsBodyLoader.getScale();
//
//            final Vector2 cameraPos = new Vector2();
//
//            final ArrayList<InputGroups.InputGroupId> activeGroups = InputGroups.getActiveGroups();
//            cameraPos.x = playerPos.x + (player.dimensions.width / 2);
//
//            if (activeGroups.contains(InputGroups.InputGroupId.MOVE_LEFT))
//                cameraPos.x -= GameCore.WIDTH / 3;
//            else if (activeGroups.contains(InputGroups.InputGroupId.MOVE_RIGHT))
//                cameraPos.x += (GameCore.WIDTH / 3);
//
//
//            cameraPos.y = playerPos.y;
//
//            // Limit coords
//            if (cameraPos.x < GameCore.WIDTH / 2)
//                cameraPos.x = GameCore.WIDTH / 2;
//            if (cameraPos.y < GameCore.HEIGHT / 2)
//                cameraPos.y = GameCore.HEIGHT / 2;
//
//            // Set camera coords
//            core.getScreenManager()
//                    .getCamera()
//                    .position.set(
//                    new Vector3(
//                            cameraPos,
//                            0
//                    )
//            );
//
//            // --- Buttons ---
//
//            // Left
//            leftButton.setCoord(
//                    cameraPos.x - (GameCore.WIDTH / 2) + BUTTON_INDENT,
//                    cameraPos.y - (GameCore.HEIGHT / 2) + BUTTON_INDENT
//            );
//
//            // Right
//            rightButton.setCoord(
//                    leftButton.getCoord().x + leftButton.getBoundRect().width + BUTTON_INDENT,
//                    leftButton.getCoord().y
//            );
//
//            // Up
//            upButton.setCoord(
//                    cameraPos.x + (GameCore.WIDTH / 2) - upButton.getBoundRect().width - BUTTON_INDENT,
//                    cameraPos.y - (GameCore.HEIGHT / 2) + BUTTON_INDENT
//            );
//
//            // Down
//            downButton.setCoord(
//                    upButton.getCoord().x - downButton.getBoundRect().width - BUTTON_INDENT,
//                    upButton.getCoord().y
//            );
//        }

        // Debug
        if (GameCore.DEBUG) {
            // Debug Box2d physics
            debugRenderer.render(
                    core.getScreenManager().getWorld(),
                    core.getScreenManager().getCamera().combined
            );

            Batch uiBatch = core.getUiBatch();
            uiBatch.begin();

            // Debug player text
            font.draw(uiBatch,
                    //"Move: " + player.getMove() + '\n'
                             "Grounded: " + player.isGrounded() + '\n'
                            + "Mode: " + player.getCurrentModeId() + '\n'
                            + '\t' + "Movable: " + player.getCurrentMode().movable + '\n'
                            + '\t' + "Move velocity: " + player.getCurrentMode().moveVelocity + '\n'
                            + '\t' + "Move acceleration: " + player.getCurrentMode().moveAcceleration + '\n'
                            + "Player velocity: " +
                            (
                                    player.physic.body != null
                                            ? player.physic.body.getLinearVelocity()
                                            : "null"
                            ) + '\n'
                            + "FPS: " + Gdx.graphics.getFramesPerSecond(),
                    10, Gdx.graphics.getHeight() - 10);

            uiBatch.end();
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
        leftButton.dispose();
        rightButton.dispose();
        upButton.dispose();
        downButton.dispose();
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
