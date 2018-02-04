package ru.coolone.adventure_emulation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.EnumMap;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.other.vectors.Vector2;
import ru.coolone.adventure_emulation.other.vectors.Vector3;
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
    @Getter
    private MoveMode moveMode;
    private static final MoveMode moveModeDefault = MoveMode.JOYSTICK;
    /**
     * Player behavior for @{@link ItemWrapper}
     */
    Player player;
    /**
     * Move buttons
     */
    Button downButton;
    Button upButton;
    Button leftButton;
    Button rightButton;
    /**
     * Joystick for move @{@link Player}
     */
    Joystick joystick;
    /**
     * Just for debug
     */
    private BitmapFont font;
    private Box2DDebugRenderer debugRenderer;

    @SuppressWarnings("WeakerAccess")
    public GameScreen(
            @NonNull Core core
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

        val joystickMap = new EnumMap<Joystick.TriggerId, InputGroups.InputGroupId>(Joystick.TriggerId.class);
        joystickMap.put(
                Joystick.TriggerId.LEFT,
                InputGroups.InputGroupId.MOVE_LEFT
        );
        joystickMap.put(
                Joystick.TriggerId.RIGHT,
                InputGroups.InputGroupId.MOVE_RIGHT
        );
        joystickMap.put(
                Joystick.TriggerId.UP,
                InputGroups.InputGroupId.JUMP
        );
        joystickMap.put(
                Joystick.TriggerId.DOWN,
                InputGroups.InputGroupId.CROUCH
        );
        joystick = new Joystick(
                core,
                "joystick",
                joystickMap
        );

        downButton = new Button(
                core,
                "buttonDown"
        );
        downButton.buttonListeners.add(
                new Button.ButtonListener() {
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
                    public void onButtonDown() {
                        core.getInputGroups().groupActivate(InputGroups.InputGroupId.MOVE_RIGHT);
                    }

                    @Override
                    public void onButtonUp() {
                        core.getInputGroups().groupDeactivate(InputGroups.InputGroupId.MOVE_RIGHT);
                    }
                }
        );

        // Set move mode
        setMoveMode(moveModeDefault);

        if (Core.DEBUG) {
            // Debug info
            debugRenderer = new Box2DDebugRenderer();
            font = new BitmapFont();
        }

        // Listen input
        core.getInputGroups().getListeners()
                .add(this);
    }

    @Override
    public void render(float delta) {

        if (player.isSpawned()) {

            // Camera coords
            val playerPos = player.getCoord();

            val cameraPos = new Vector2();

            //val activeGroups = core.getInputGroups().getActiveGroups();
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
                    .getCamera()
                    .position.set(
                    new Vector3(cameraPos)
            );

            switch (getMoveMode()) {
                case BUTTONS:
                    // Left
                    leftButton.setCoord(
                            new Vector2(
                                    cameraPos
                            ).add(
                                    -(Core.WIDTH / 2) + UI_INDENT,
                                    -(Core.HEIGHT / 2) + UI_INDENT
                            )
                    );

                    // Right
                    rightButton.setCoord(
                            new Vector2(
                                    leftButton.getCoord()
                            ).add(
                                    leftButton.getWidth() + UI_INDENT,
                                    0f
                            )
                    );

                    // Up
                    upButton.setCoord(
                            new Vector2(
                                    cameraPos
                            ).add(
                                    -upButton.getWidth() - UI_INDENT + (Core.WIDTH / 2),
                                    -(Core.HEIGHT / 2) + UI_INDENT
                            )
                    );

                    // Down
                    downButton.setCoord(
                            new Vector2(
                                    upButton.getCoord()
                            ).add(
                                    -downButton.getWidth() - UI_INDENT,
                                    0f
                            )
                    );
                    break;
                case JOYSTICK:
                    // Joystick
                    joystick.setCoord(
                            new Vector2(
                                    cameraPos
                            ).add(
                                    -(Core.WIDTH / 2) + UI_INDENT,
                                    -(Core.HEIGHT / 2) + UI_INDENT
                            )
                    );
                    break;
                default:
                    Gdx.app.error(TAG, "Move mode is unknown (" + getMoveMode() + ")");
            }
        }

        // Debug
        if (Core.DEBUG) {
            // Debug Box2d physics
            debugRenderer.render(
                    core.getScreenManager().getWorld(),
                    core.getScreenManager().getCamera().combined
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
                                    (getMoveMode() == MoveMode.JOYSTICK)
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

        player.dispose();
        joystick.dispose();
        leftButton.dispose();
        rightButton.dispose();
        upButton.dispose();
        downButton.dispose();

        if (Core.DEBUG) {
            font.dispose();
        }

        // Stop listen input
        core.getInputGroups().getListeners()
                .remove(this);
    }

    @Override
    public void dispose() {
        super.dispose();
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
        core.getScreenManager().getCamera().moveIndentTo(
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
     * Updates {@link #moveMode} and create
     * or @{@link Disposable#dispose()} {@link #joystick} or @{@link Button}'s
     *
     * @param moveMode @{@link MoveMode}, that will be set
     */
    public void setMoveMode(MoveMode moveMode) {
        boolean isJoystick = false;
        boolean isButtons = false;
        switch (moveMode) {
            case JOYSTICK:
                isJoystick = true;
                break;
            case BUTTONS:
                isButtons = true;
                break;
            default:
                Gdx.app.error(TAG, "Move mode unknown (" + getMoveMode() + ")");
        }

        leftButton.setVisible(isButtons);
        rightButton.setVisible(isButtons);
        upButton.setVisible(isButtons);
        downButton.setVisible(isButtons);
        joystick.setVisible(isJoystick);

        this.moveMode = moveMode;
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
