package ru.coolone.adventure_emulation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.EnumMap;

import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.screen.ScreenScene;
import ru.coolone.adventure_emulation.scripts.Button;
import ru.coolone.adventure_emulation.scripts.joystick.Joystick;
import ru.coolone.adventure_emulation.scripts.persons.Player;

/**
 * Game @{@link ScreenScene}
 *
 * @author coolone
 */

public class GameScreen extends ScreenScene
        implements InputGroups.InputGroupsListener {
    public static final String TAG = GameScreen.class.getSimpleName();

    private static final String name = "GameScene";
    /**
     * Intent between ui components, e.g. @{@link Button} or @{@link Joystick}
     */
    private static final int UI_INDENT = 30;
    /**
     * Using @{@link MoveMode}
     */
    private static final MoveMode moveMode = MoveMode.JOYSTICK;
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
            Core core
    ) {
        super(core, name);

        // Listen input
        core.getInputGroups().listeners.add(this);
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
                "joystick",
                new EnumMap<Joystick.TriggerId, InputGroups.InputGroupId>(Joystick.TriggerId.class) {{
                    put(
                            Joystick.TriggerId.LEFT,
                            InputGroups.InputGroupId.MOVE_LEFT
                    );
                    put(
                            Joystick.TriggerId.RIGHT,
                            InputGroups.InputGroupId.MOVE_RIGHT
                    );
                    put(
                            Joystick.TriggerId.UP,
                            InputGroups.InputGroupId.JUMP
                    );
                    put(
                            Joystick.TriggerId.DOWN,
                            InputGroups.InputGroupId.CROUCH
                    );
                }}
        );

        // Move buttons
        downButton = new Button(
                core,
                "buttonDown"
        );
        downButton.listeners.add(
                new Button.ButtonListener() {
                    @Override
                    public void onButtonClick() {
                    }

                    @Override
                    public void onButtonDown() {
                        core.getInputGroups().groupActivate(InputGroups.InputGroupId.CROUCH);
                    }

                    @Override
                    public void onButtonUp() {
                        core.getInputGroups().groupDeactivate(InputGroups.InputGroupId.CROUCH);
                    }
                }
        );
        upButton = new Button(
                core,
                "buttonUp"
        );
        upButton.listeners.add(
                new Button.ButtonListener() {
                    @Override
                    public void onButtonClick() {
                    }

                    @Override
                    public void onButtonDown() {
                        core.getInputGroups().groupActivate(InputGroups.InputGroupId.JUMP);
                    }

                    @Override
                    public void onButtonUp() {
                        core.getInputGroups().groupDeactivate(InputGroups.InputGroupId.JUMP);
                    }
                }
        );
        leftButton = new Button(
                core,
                "buttonLeft"
        );
        leftButton.listeners.add(
                new Button.ButtonListener() {
                    @Override
                    public void onButtonClick() {
                        Gdx.app.log(TAG, "Left button");
                    }

                    @Override
                    public void onButtonDown() {
                        core.getInputGroups().groupActivate(InputGroups.InputGroupId.MOVE_LEFT);
                    }

                    @Override
                    public void onButtonUp() {
                        core.getInputGroups().groupDeactivate(InputGroups.InputGroupId.MOVE_LEFT);
                    }
                }
        );
        rightButton = new Button(
                core,
                "buttonRight"
        );
        rightButton.listeners.add(
                new Button.ButtonListener() {
                    @Override
                    public void onButtonClick() {
                    }

                    @Override
                    public void onButtonDown() {
                        core.getInputGroups().groupActivate(InputGroups.InputGroupId.MOVE_RIGHT);
                    }

                    @Override
                    public void onButtonUp() {
                        core.getInputGroups().groupDeactivate(InputGroups.InputGroupId.MOVE_RIGHT);
                    }
                }
        );

        // Debug info
        debugRenderer = new Box2DDebugRenderer();
        font = new BitmapFont();
    }

    @Override
    public void render(float delta) {

        if (player.isSpawned()) {

            // Camera coords
            final Vector2 playerPos = player.getCoord();

            final Vector2 cameraPos = new Vector2();

            //final ArrayList<InputGroups.InputGroupId> activeGroups = core.getInputGroups().getActiveGroups();
            cameraPos.x = playerPos.x + (player.getWidth() / 2);

//            if (activeGroups.contains(InputGroups.InputGroupId.MOVE_LEFT))
//                cameraPos.x -= Core.WIDTH / 3;
//            else if (activeGroups.contains(InputGroups.InputGroupId.MOVE_RIGHT))
//                cameraPos.x += (Core.WIDTH / 3);
//            cameraPos.y = playerPos.y;

            // Limit coords
            if (cameraPos.x < Core.WIDTH / 2)
                cameraPos.x = Core.WIDTH / 2;
            if (cameraPos.y < Core.HEIGHT / 2)
                cameraPos.y = Core.HEIGHT / 2;

            // Set camera coords
            core.getScreenManager()
                    .camera
                    .position.set(
                    new Vector3(
                            cameraPos.x,
                            cameraPos.y,
                            0f
                    )
            );

            switch (moveMode) {
                case BUTTONS:
                    // Left
                    leftButton.setCoord(
                            cameraPos.x - (Core.WIDTH / 2) + UI_INDENT,
                            cameraPos.y - (Core.HEIGHT / 2) + UI_INDENT
                    );

                    // Right
                    rightButton.setCoord(
                            leftButton.getCoord().x + leftButton.getBoundRect().width + UI_INDENT,
                            leftButton.getCoord().y
                    );

                    // Up
                    upButton.setCoord(
                            cameraPos.x + (Core.WIDTH / 2) - upButton.getBoundRect().width - UI_INDENT,
                            cameraPos.y - (Core.HEIGHT / 2) + UI_INDENT
                    );

                    // Down
                    downButton.setCoord(
                            upButton.getCoord().x - downButton.getBoundRect().width - UI_INDENT,
                            upButton.getCoord().y
                    );
                    break;
                case JOYSTICK:
                    // Joystick
                    joystick.setCoord(
                            new Vector2(cameraPos) {{
                                x -= (Core.WIDTH / 2) - UI_INDENT;
                                y -= (Core.HEIGHT / 2) - UI_INDENT;
                            }}
                    );
                    break;
            }
        }

        // Debug
        if (Core.DEBUG) {
            // Debug Box2d physics
            debugRenderer.render(
                    core.getScreenManager().getWorld(),
                    core.getScreenManager().camera.combined
            );

            Batch uiBatch = core.getUiBatch();
            uiBatch.begin();

            // Debug player text
            font.draw(uiBatch,
                    "Move: " + player.getMoveDir() + '\n'
                            + "Grounded: " + player.isGrounded() + '\n'
                            + "Mode: " + player.getCurrentModeId() + '\n'
                            + '\t' + "Movable: " + player.getCurrentMode().movable + '\n'
                            + '\t' + "Move velocity: " + player.getCurrentMode().moveVelocity + '\n'
                            + '\t' + "Move acceleration: " + player.getCurrentMode().moveAcceleration + '\n'
                            + "Player velocity: " +
                            (
                                    player.getBody() != null
                                            ? player.getBody().getLinearVelocity()
                                            : "null"
                            ) + '\n' +
                            (
                                    (moveMode == MoveMode.JOYSTICK)
                                            ? ("Current joystick trigger: " + joystick.getCurrentTriggerId() + '\n')
                                            : ""
                            ) + "FPS: " + Gdx.graphics.getFramesPerSecond(),
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
        joystick.dispose();
    }

    @Override
    public void dispose() {
        // Stop listen input
        core.getInputGroups().listeners.remove(this);

        super.dispose();
        font.dispose();
    }

    @Override
    public boolean onInputGroupActivate(InputGroups.InputGroupId groupId) {
        Vector2 indentPos = new Vector2();
        switch (groupId) {
            case MOVE_LEFT:
                indentPos.x -= Core.WIDTH / 3f;
                break;
            case MOVE_RIGHT:
                indentPos.x += Core.WIDTH / 3f;
                break;
        }
        core.getScreenManager().camera.moveIndentTo(
                indentPos,
                1000
        );
        return false;
    }

    @Override
    public boolean onInputGroupDeactivate(InputGroups.InputGroupId groupId) {
        return false;
    }

    /**
     * Move @{@link Player} modes
     */
    enum MoveMode {
        /**
         * With move buttons
         */
        BUTTONS,
        /**
         * With joystick
         */
        JOYSTICK
    }
}
