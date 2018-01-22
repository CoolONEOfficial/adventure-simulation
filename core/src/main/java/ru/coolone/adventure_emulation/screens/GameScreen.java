package ru.coolone.adventure_emulation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.EnumMap;

import lombok.val;
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
    @SuppressWarnings("unused")
    private static final String TAG = GameScreen.class.getSimpleName();

    public static final String name = "GameScene";
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
        core.getInputGroups().getListeners().add(this);
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
        downButton.buttonListeners.add(
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
        upButton.buttonListeners.add(
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
        leftButton.buttonListeners.add(
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
        rightButton.buttonListeners.add(
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
            final val playerPos = player.getCoord();

            final val cameraPos = new Vector2();

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
                            new Vector2(cameraPos) {{
                                x -= (Core.WIDTH / 2) - UI_INDENT;
                                y -= (Core.HEIGHT / 2) + UI_INDENT;
                            }}
                    );

                    // Right
                    rightButton.setCoord(
                            new Vector2(leftButton.getCoord()) {{
                                x += leftButton.getWidth() + UI_INDENT;
                            }}
                    );

                    // Up
                    upButton.setCoord(
                            new Vector2(cameraPos) {{
                                x -= upButton.getWidth() + UI_INDENT - (Core.WIDTH / 2);
                                y -= (Core.HEIGHT / 2) - UI_INDENT;
                            }}
                    );

                    // Down
                    downButton.setCoord(
                            new Vector2(upButton.getCoord()) {{
                                x -= downButton.getWidth() + UI_INDENT;
                            }}
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

            val uiBatch = core.getUiBatch();
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
    public void hide() {
        super.hide();

        leftButton.dispose();
        rightButton.dispose();
        upButton.dispose();
        downButton.dispose();
        joystick.dispose();
    }

    @Override
    public void dispose() {
        // Stop listen input
        core.getInputGroups().getListeners().remove(this);

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
