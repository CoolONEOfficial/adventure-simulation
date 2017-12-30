package ru.coolone.adventure_emulation.game.scripts.person;

import com.badlogic.ashley.core.Entity;
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
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import ru.coolone.adventure_emulation.GameCore;
import ru.coolone.adventure_emulation.input.InputGroups;

/**
 * CompositeItem with spriter animation
 *
 * @author coolone
 */

abstract public class Person<PersonModeId extends Enum, AnimationId extends Enum> extends Composite
        implements Player.PlayerListener,
        InputGroups.InputGroupsListener {

    protected static final String TAG = Person.class.getSimpleName();

    /**
     * Link to @{@link GameCore}
     */
    protected GameCore core;

    /**
     * @see Spriter
     */
    protected Spriter spriter;

    /**
     * Current @{@link PersonMode} id
     */
    protected PersonModeId currentModeId;

    /**
     * Move directions
     */
    public enum MoveDir {
        NONE,
        LEFT,
        RIGHT
    }

    /**
     * Current @{@link MoveDir}
     */
    private MoveDir moveDir = MoveDir.NONE;

    /**
     * Move handling @{@link ru.coolone.adventure_emulation.input.InputGroups.InputGroupId}'s
     */
    final public InputGroups.InputGroupId inputMoveLeft;
    final public InputGroups.InputGroupId inputMoveRight;

    /**
     * @param core Link to @{@link GameCore}
     * @param name Name of CompositeItem
     */
    public Person(
            GameCore core,
            String name,
            InputGroups.InputGroupId inputMoveLeft,
            InputGroups.InputGroupId inputMoveRight
    ) {
        this.core = core;

        // Connect scripts

        final ItemWrapper selfItem = core
                .getScreenManager()
                .getRootItem()
                .getChild(name);

        // Composite
        selfItem.addScript(this);

        // Spriter
        spriter = new Spriter();
        selfItem.getChild("spriter")
                .addScript(spriter);

        // Start listen spriter
        spriter.spriter.player.addListener(this);

        // Start listen input
        this.core.getInputGroups().addListener(this);

        // Input groups
        this.inputMoveLeft = inputMoveLeft;
        this.inputMoveRight = inputMoveRight;
    }

    /**
     * @return @{@link PersonMode}'s array
     */
    protected abstract PersonMode<PersonModeId, AnimationId>[] getModes();

    @Override
    public void act(float delta) {
        super.act(delta);

        final PersonMode<PersonModeId, AnimationId> currentMode = getCurrentMode();

        // Handle act
        currentMode.onAct();

        if (moveDir != MoveDir.NONE) {
            // Move physic body
            Vector2 moveAcceleration = new Vector2() {{
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
            physic.body.applyLinearImpulse(
                    moveAcceleration,
                    physic.body.getPosition(),
                    true
            );

            // Limit velocity
            final float currentVelocity = physic.body.getLinearVelocity().x;
            if (Math.abs(currentVelocity) > currentMode.moveVelocity)
                physic.body.setLinearVelocity(
                        new Vector2(physic.body.getLinearVelocity()) {{
                            x = (currentVelocity > 0)
                                    ? currentMode.moveVelocity
                                    : -currentMode.moveVelocity;
                        }}
                );
        }

        // Check end
        if (currentMode.behavior.checkEnd()) {
            final PersonModeId nextModeId = currentMode.behavior.getNextModeId();

            Gdx.app.log(TAG, "End of mode " + getCurrentModeId() + " detected");

            // Activate next mode
            activateMode(nextModeId, null);
        }
    }

    /**
     * That @{@link ru.coolone.adventure_emulation.input.InputGroups.InputGroupId} will be handled after end of end animation
     */
    private InputGroups.InputGroupId endInputGroupId = null;

    protected void activateMode(
            PersonModeId newModeId,
            InputGroups.InputGroupId checkInputGroupId
    ) {
        PersonMode<PersonModeId, AnimationId> currentMode = getCurrentMode();
        PersonMode.ChangeMode changeMode = currentMode.changeMap[newModeId.ordinal()];

        // Check in changeMap
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

    private void onActivateMode(PersonModeId newModeId) {
        Gdx.app.log(TAG, "Activating mode " + newModeId);

        PersonMode<PersonModeId, AnimationId> oldMode = getCurrentMode();

        // Handle deactivate
        if (oldMode != null)
            oldMode.onDeactivate();

        // Activate mode
        currentModeId = newModeId;

        PersonMode<PersonModeId, AnimationId> newMode = getCurrentMode();

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

        // Handle activate
        newMode.onActivate();
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

    /**
     * @return Current @{@link PersonMode}
     */
    public PersonMode<PersonModeId, AnimationId> getCurrentMode() {
        return getModes()[getCurrentModeId().ordinal()];
    }

    /**
     * @return Current @{@link PersonMode} id
     */
    public PersonModeId getCurrentModeId() {
        return currentModeId;
    }

    /**
     * @return Current @{@link MoveDir}
     */
    public MoveDir getMoveDir() {
        return moveDir;
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
                    (physic.body.getFixtureList().contains(contact.getFixtureA(), false) ||
                            physic.body.getFixtureList().contains(contact.getFixtureB(), false))) {

                // Check below
                boolean below = true;
                WorldManifold manifold = contact.getWorldManifold();
                for (Vector2 mPoint : manifold.getPoints()) {
                    below &= (mPoint.y < transform.y);
                }
                return below;
            }
        }
        return false;
    }

    private void refreshMoveDir(InputGroups.InputGroupId groupId) {
        if (getCurrentMode().movable) {
            // Handle move @InputGroupId's
            if (groupId == inputMoveLeft) {
                // Start moving at left
                moveDir = MoveDir.LEFT;

                // Flip animation
                spriter.setFlipped(true);
            } else if (groupId == inputMoveRight) {
                // Start moving at right
                moveDir = MoveDir.RIGHT;

                // Not flip animation
                spriter.setFlipped(false);
            }
        }
    }

    @Override
    public boolean onInputGroupActivate(InputGroups.InputGroupId groupId) {
        // Refresh move direction
        refreshMoveDir(groupId);

        // Change endInputGroupId
        endInputGroupId = groupId;

        return false;
    }

    @Override
    public boolean onInputGroupDeactivate(InputGroups.InputGroupId groupId) {
        // Deactivate endInputGroupId
        if(groupId == endInputGroupId)
            endInputGroupId = null;

        // Stop moving
        if (groupId == inputMoveLeft ||
                groupId == inputMoveRight) {
            PersonMode<PersonModeId, AnimationId> currentMode = getCurrentMode();

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

        PersonMode<PersonModeId, AnimationId> currentMode = getCurrentMode();

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
        core.getInputGroups().removeListener(this);

        super.dispose();

        // Stop listen spriter
        spriter.spriter.player.removeListener(this);
    }

    /**
     * Spriter item in CompositeItem
     * Animation of person
     */
    static public class Spriter implements IScript {

        /**
         * Components
         */
        public TransformComponent transform;
        public DimensionsComponent dimensions;
        /**
         * Spriter component
         */
        public SpriterComponent spriter;

        @Override
        public void init(Entity entity) {
            // Components
            transform = ComponentRetriever.get(entity, TransformComponent.class);
            dimensions = ComponentRetriever.get(entity, DimensionsComponent.class);
            spriter = ComponentRetriever.get(entity, SpriterComponent.class);
        }

        @Override
        public void act(float delta) {
        }

        @Override
        public void dispose() {
        }

        public void setAnimation(int animationId) {
            spriter.player.setAnimation(animationId);
        }

        /**
         * Flipped flag
         */
        private boolean flipped = false;

        /**
         * Flip's spriter animation at X
         *
         * @param flipped New flipped boolean
         */
        void setFlipped(boolean flipped) {
            if (this.flipped != flipped) {
                // Flip
                spriter.player.flipX();

                // Update flag
                this.flipped = flipped;
            }
        }
    }
}

abstract class Composite implements IScript {

    /**
     * Components
     */
    public LayerMapComponent layers;
    public TransformComponent transform;
    public DimensionsComponent dimensions;
    public PhysicsBodyComponent physic;

    @Override
    public void init(Entity entity) {
        // Components
        layers = ComponentRetriever.get(entity, LayerMapComponent.class);
        transform = ComponentRetriever.get(entity, TransformComponent.class);
        dimensions = ComponentRetriever.get(entity, DimensionsComponent.class);
        physic = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
    }

    @Override
    public void act(float delta) {
        if (!physic.body.isFixedRotation())
            physic.body.setFixedRotation(true);
    }

    @Override
    public void dispose() {
    }

    /**
     * @return Physic body spawned bool
     */
    public boolean isSpawned() {
        return physic.body != null;
    }
}