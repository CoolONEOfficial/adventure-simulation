package ru.coolone.adventure_emulation.scripts.person;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.utils.Array;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Mainline;
import com.brashmonkey.spriter.Player;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.Getter;
import lombok.val;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.InputGroups;
import ru.coolone.adventure_emulation.script.Script;

/**
 * CompositeItem with spriter animation
 *
 * @author coolone
 */

abstract public class Person<PersonModeId extends Enum, AnimationId extends Enum> extends PersonComposite
        implements Player.PlayerListener,
        InputGroups.InputGroupsListener {

    @SuppressWarnings("unused")
    private static final String TAG = Person.class.getSimpleName();

    /**
     * Link to @{@link Core}
     */
    protected final Core core;
    /**
     * Move handling @{@link InputGroups.InputGroupId}'s
     */
    private final InputGroups.InputGroupId inputMoveLeft;
    private final InputGroups.InputGroupId inputMoveRight;
    /**
     * @see Spriter
     */
    protected Spriter spriter;
    /**
     * Current @{@link PersonMode} id
     */
    @Getter
    protected PersonModeId currentModeId;
    /**
     * Current @{@link MoveDir}
     */
    @Getter
    private MoveDir moveDir = MoveDir.NONE;
    /**
     * That @{@link InputGroups.InputGroupId} will be handled after end of end animation
     */
    private InputGroups.InputGroupId endInputGroupId = null;

    /**
     * @param core Link to @{@link Core}
     * @param name Name of CompositeItem
     */
    public Person(
            Core core,
            String name,
            InputGroups.InputGroupId inputMoveLeft,
            InputGroups.InputGroupId inputMoveRight
    ) {
        this.core = core;

        // Connect scripts

        val selfItem = core
                .getScreenManager()
                .getRootItem()
                .getChild(name);

        // PersonComposite
        selfItem.addScript(this);

        // Spriter
        spriter = new Spriter();

        spriter.scriptListeners.add(
                () -> {
                    // Start listen spriter
                    spriter.getAnimationPlayer().addListener(Person.this);
                }
        );

        selfItem.getChild("spriter")
                .addScript(spriter);

        // Start listen input
        this.core.getInputGroups().getListeners().add(this);

        // Input groups
        this.inputMoveLeft = inputMoveLeft;
        this.inputMoveRight = inputMoveRight;
    }

    /**
     * @return @{@link PersonMode}'s array
     */
    protected abstract PersonMode<PersonModeId, AnimationId>[] getModes();

    /**
     * @param modeId @{@link PersonMode} id
     * @return Find @{@link PersonMode}
     */
    protected PersonMode<PersonModeId, AnimationId> getMode(PersonModeId modeId) {
        return getModes()[modeId.ordinal()];
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        val currentMode = getCurrentMode();

        // Handle act
        currentMode.onAct();

        if (moveDir != MoveDir.NONE) {
            // Move physic body
            val moveAcceleration = new Vector2() {{
                switch (moveDir) {
                    case LEFT:
                        x = -currentMode.moveAcceleration;
                        break;
                    case RIGHT:
                        x = currentMode.moveAcceleration;
                        break;
                }
                x *= Gdx.graphics.getDeltaTime();
            }};

            // Apply acceleration
            getBody().applyLinearImpulse(
                    moveAcceleration,
                    getBody().getPosition(),
                    true
            );

            // Limit velocity
            val currentVelocity = getBody().getLinearVelocity().x;
            if (Math.abs(currentVelocity) > currentMode.moveVelocity)
                getBody().setLinearVelocity(
                        new Vector2(getBody().getLinearVelocity()) {{
                            x = (currentVelocity > 0)
                                    ? currentMode.moveVelocity
                                    : -currentMode.moveVelocity;
                        }}
                );
        }

        // Check end
        if (currentMode.behavior.checkEnd()) {
            val nextModeId = currentMode.behavior.getNextModeId();

            Gdx.app.log(TAG, "End of mode " + getCurrentModeId() + " detected");

            // Activate next mode
            activateMode(nextModeId, null);
        }
    }

    /**
     * Method, that check @{@link PersonMode.ChangeMode} and start or end animation (if she exists)
     * and call {@link #onActivateMode(Enum)} or call {@link #onActivateMode(Enum)} directly
     *
     * @param newModeId         Id of @{@link PersonMode}, that will be activated
     * @param checkInputGroupId Input group, that will be checked before activate newModeId
     */
    protected void activateMode(
            PersonModeId newModeId,
            InputGroups.InputGroupId checkInputGroupId
    ) {
        val currentMode = getCurrentMode();
        val changeMode = currentMode.changeMap[newModeId.ordinal()];

        // Check change mode from changeMap
        if (changeMode != PersonMode.ChangeMode.NOT_ALLOWED) {

            Gdx.app.log(TAG, "Start activating mode " + newModeId);

            if (changeMode == PersonMode.ChangeMode.ALLOWED_SOFT &&
                    currentMode.animationStartLoopEnd) {
                Gdx.app.log(TAG, "Starting end animation");

                // Start end animation
                spriter.setAnimation(currentMode.getAnimationId(
                        PersonMode.AnimationType.END
                ).ordinal());

                // Save input group
                if (endInputGroupId == null)
                    endInputGroupId = checkInputGroupId;
            } else {
                // Activate mode
                onActivateMode(newModeId);
            }
        } else Gdx.app.log(TAG, "Start mode "
                + newModeId + " not allowed in "
                + currentModeId + " changeMap");
    }

    protected void activateMode(
            PersonModeId newModeId
    ) {
        activateMode(newModeId, null);
    }

    /**
     * Function, that activate newModeId
     *
     * @param newModeId Id of @{@link PersonMode}, that will be activated
     */
    private void onActivateMode(PersonModeId newModeId) {
        Gdx.app.log(TAG, "Activating mode " + newModeId);

        val newMode = getMode(newModeId);

        // Change current mode id
        setCurrentModeId(newModeId);

        // Stop moving
        if (!newMode.movable)
            setMoveDir(MoveDir.NONE);

        // Start animation
        spriter.setAnimation(
                newMode.getAnimationId(
                        newMode.animationStartLoopEnd
                                ? PersonMode.AnimationType.START
                                : PersonMode.AnimationType.LOOP
                ).ordinal()
        );
    }

    private void setCurrentModeId(PersonModeId newModeId) {
        val oldModeId = getCurrentModeId();
        val oldMode = getCurrentMode();
        val newMode = getMode(newModeId);

        // Handle deactivate
        if (oldMode != null) {
            currentModeId = newModeId;
            oldMode.onDeactivate();
        }

        // Handle activate
        currentModeId = oldModeId;
        newMode.onActivate();

        // Activate mode
        currentModeId = newModeId;
    }

    /**
     * Activates next @{@link PersonMode}
     */
    protected void toNextMode(InputGroups.InputGroupId checkInputGroupId) {
        // Activate next mode
        PersonModeId nextMode = getCurrentMode().behavior.getNextModeId();
        if (nextMode != null)
            activateMode(nextMode, checkInputGroupId);
    }

    protected void toNextMode() {
        toNextMode(null);
    }

    /**
     * @return Current @{@link PersonMode}
     */
    public PersonMode<PersonModeId, AnimationId> getCurrentMode() {
        return getModes()[getCurrentModeId().ordinal()];
    }

    /**
     * Set's move dir and handle's move end
     *
     * @param moveDir New @{@link MoveDir}
     */
    protected void setMoveDir(MoveDir moveDir) {
        // Handle move end
        if (this.moveDir != moveDir &&
                moveDir == MoveDir.NONE)
            getCurrentMode().onMoveEnded();

        // Set move
        this.moveDir = moveDir;
    }

    /**
     * Checks physic body collision of other @{@link com.badlogic.gdx.physics.box2d.Body}'ies
     *
     * @return Grounded bool
     */
    public boolean isGrounded() {
        // Check all contacts
        Array<Contact> contactList = core.getScreenManager().getWorld().getContactList();
        for (int i = 0; i < contactList.size; i++) {
            Contact contact = contactList.get(i);
            // Check touch to sensor fixture
            if (contact.isTouching() &&
                    (getBody().getFixtureList().contains(contact.getFixtureA(), false) ||
                            getBody().getFixtureList().contains(contact.getFixtureB(), false))) {

                // Check below
                boolean below = true;
                WorldManifold manifold = contact.getWorldManifold();
                for (Vector2 mPoint : manifold.getPoints()) {
                    below &= (mPoint.y < getY());
                }
                return below;
            }
        }
        return false;
    }

    /**
     * @param groupId @{@link InputGroups.InputGroupId}, that will be set
     */
    private void refreshMoveDir(InputGroups.InputGroupId groupId) {
        if (getCurrentMode().movable) {
            // Handle move @InputGroupId's
            if (groupId == inputMoveLeft) {
                // Start moving at left
                setMoveDir(MoveDir.LEFT);

                // Flip animation
                spriter.setFlipped(true);
            } else if (groupId == inputMoveRight) {
                // Start moving at right
                setMoveDir(MoveDir.RIGHT);

                // Not flip animation
                spriter.setFlipped(false);
            }
        }
    }

    @Override
    public boolean onInputGroupActivate(InputGroups.InputGroupId groupId) {
        // Refresh move direction
        refreshMoveDir(groupId);

        return false;
    }

    @Override
    public boolean onInputGroupDeactivate(InputGroups.InputGroupId groupId) {
        // Deactivate endInputGroupId
        if (groupId == endInputGroupId)
            endInputGroupId = null;

        // Stop moving
        if (groupId == inputMoveLeft ||
                groupId == inputMoveRight) {
            val currentMode = getCurrentMode();

            if (currentMode.movable) {
                // Stop moving
                setMoveDir(MoveDir.NONE);

                return true;
            }
        }
        return false;
    }

    @Override
    public void animationFinished(Animation animation) {
        Gdx.app.log(TAG, "Animation end detected");

        int animationId = animation.id;
        val currentMode = getCurrentMode();

        if (currentMode.animationStartLoopEnd) {
            // Check end of end
            if (animationId == currentMode.getAnimationId(
                    PersonMode.AnimationType.END
            ).ordinal()) {
                boolean checkEndGroupIdResult = (
                        endInputGroupId == null ||
                                core.getInputGroups().getActiveGroups().contains(endInputGroupId)
                );

                Gdx.app.log(
                        TAG,
                        "End of end animation of mode " + currentModeId + " detected" + '\n'
                                + "endInputGroupId: " + endInputGroupId + '\n'
                                + "checkEndGroupIdResult: " + checkEndGroupIdResult + '\n'
                                + "active input groups: " + core.getInputGroups().getActiveGroups()
                );

                // To...
                if (checkEndGroupIdResult && endInputGroupId != null)
                    // ...endModeId
                    onInputGroupActivate(endInputGroupId);
                    // onActivateMode(endModeId);
                else
                    // ...default mode
                    onActivateMode(currentMode.behavior.getDefaultNextModeId());

                // Refresh move direction
                for (InputGroups.InputGroupId mInputGroupId : core.getInputGroups().getActiveGroups()) {
                    refreshMoveDir(mInputGroupId);
                }

                // Clear saved modeId and input group id
                endInputGroupId = null;
            }

            // Check end of start
            else if (animationId == currentMode.getAnimationId(
                    PersonMode.AnimationType.START
            ).ordinal()) {
                // To loop animation
                spriter.setAnimation(currentMode.getAnimationId(
                        PersonMode.AnimationType.LOOP
                ).ordinal());
            }
        } else Gdx.app.log(TAG, "Ignored because animation only loop");
    }

    @Override
    public void animationChanged(Animation oldAnim, Animation newAnim) {
    }

    @Override
    public void preProcess(Player player) {
    }

    @Override
    public void postProcess(Player player) {
    }

    @Override
    public void mainlineKeyChanged(Mainline.Key prevKey, Mainline.Key newKey) {
    }

    @Override
    public void dispose() {
        // Stop listen input
        core.getInputGroups().getListeners().remove(this);

        super.dispose();

        // Stop listen spriter
        spriter.getAnimationPlayer().removeListener(this);
    }

    /**
     * Move directions
     */
    public enum MoveDir {
        NONE,
        LEFT,
        RIGHT
    }

    /**
     * Spriter item in CompositeItem
     * Animation of person
     */
    static public class Spriter extends Script {

        Spriter() {
            super();
            componentClassesForInit.addAll(
                    new ArrayList<>(
                            Arrays.asList(
                                    TransformComponent.class,
                                    DimensionsComponent.class,
                                    SpriterComponent.class
                            )
                    )
            );
        }
    }
}

abstract class PersonComposite extends Script {

    PersonComposite() {
        super();
        componentClassesForInit.addAll(
                new ArrayList<>(
                        Arrays.asList(
                                LayerMapComponent.class,
                                TransformComponent.class,
                                DimensionsComponent.class,
                                PhysicsBodyComponent.class
                        )
                )
        );
    }

    @Override
    public void act(float delta) {
        if (!getBody().isFixedRotation())
            getBody().setFixedRotation(true);
    }
}