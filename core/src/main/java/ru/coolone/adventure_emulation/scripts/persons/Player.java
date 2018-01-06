package ru.coolone.adventure_emulation.scripts.persons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import lombok.Getter;
import lombok.val;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.scripts.person.Person;
import ru.coolone.adventure_emulation.scripts.person.PersonMode;
import ru.coolone.adventure_emulation.scripts.person.PersonMode.ChangeMode;

/**
 * Player behavior to CompositeItem and spriter animation
 */

public class Player extends Person<Player.PlayerModeId, Player.AnimationId>
        implements InputGroups.InputGroupsListener {

    /**
     * Move speed constants for @{@link PersonMode}'s
     */
    private static final float WALK_MOVE_ACCELERATION = 3000f;
    private static final float WALK_MOVE_VELOCITY = 15f;
    private static final float CROUCH_WALK_MOVE_ACCELERATION = 3000f;
    private static final float CROUCH_WALK_MOVE_VELOCITY = 4f;
    private static final float JUMP_MOVE_ACCELERATION = 1000f;
    private static final float JUMP_MOVE_VELOCITY = 10f;
    /**
     * Array of @{@link PersonMode}'s
     */
    @SuppressWarnings("unchecked")
    @Getter private PersonMode[] modes = new PersonMode[]{
            // IDLE
            new PersonMode(
                    new AnimationId[]{
                            AnimationId.IDLE
                    },
                    new ChangeMode[]{
                            ChangeMode.NOT_ALLOWED, // IDLE
                            ChangeMode.ALLOWED_SOFT, // WALK
                            ChangeMode.ALLOWED_SOFT, // BREAK
                            ChangeMode.ALLOWED_SOFT, // SLIDE
                            ChangeMode.ALLOWED_SOFT, // CROUCH
                            ChangeMode.ALLOWED_SOFT, // CROUCH_WALK
                            ChangeMode.ALLOWED_SOFT, // JUMP
                    },
                    new PersonMode.Behavior<PlayerModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return false;
                        }

                        @Override
                        public PlayerModeId getNextModeId() {
                            return null;
                        }

                        @Override
                        public PlayerModeId getDefaultNextModeId() {
                            return PlayerModeId.IDLE;
                        }
                    }
            ),

            // WALK
            new PersonMode(
                    WALK_MOVE_ACCELERATION, WALK_MOVE_VELOCITY,
                    new AnimationId[]{
                            AnimationId.WALK
                    },
                    new ChangeMode[]{
                            ChangeMode.ALLOWED_SOFT, // IDLE
                            ChangeMode.NOT_ALLOWED, // WALK
                            ChangeMode.ALLOWED_SOFT, // BREAK
                            ChangeMode.ALLOWED_SOFT, // SLIDE
                            ChangeMode.ALLOWED_SOFT, // CROUCH
                            ChangeMode.NOT_ALLOWED, // CROUCH_WALK
                            ChangeMode.ALLOWED_SOFT, // JUMP
                    },
                    new PersonMode.Behavior<PlayerModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return false;
                        }

                        @Override
                        public PlayerModeId getNextModeId() {
                            return PlayerModeId.BREAK;
                        }

                        @Override
                        public PlayerModeId getDefaultNextModeId() {
                            return PlayerModeId.IDLE;
                        }
                    }
            ) {{
                listeners.add(
                        new PersonMode.Listener() {
                            @Override
                            protected void onMoveEnded() {
                                super.onMoveEnded();
                                toNextMode(null);
                            }
                        });
            }},


            // BREAK
            new PersonMode(
                    new AnimationId[]{
                            AnimationId.BREAK_LOOP,
                            AnimationId.BREAK_START,
                            AnimationId.BREAK_END
                    },
                    new ChangeMode[]{
                            ChangeMode.ALLOWED_SOFT, // IDLE
                            ChangeMode.ALLOWED_SOFT, // WALK
                            ChangeMode.NOT_ALLOWED, // BREAK
                            ChangeMode.ALLOWED_SOFT, // SLIDE
                            ChangeMode.ALLOWED_SOFT, // CROUCH
                            ChangeMode.ALLOWED_SOFT, // CROUCH_WALK
                            ChangeMode.ALLOWED_HARD, // JUMP
                    },
                    new PersonMode.Behavior<PlayerModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return getBody().getLinearVelocity().x == 0;
                        }

                        @Override
                        public PlayerModeId getNextModeId() {
                            return PlayerModeId.IDLE;
                        }

                        @Override
                        public PlayerModeId getDefaultNextModeId() {
                            return PlayerModeId.IDLE;
                        }
                    }
            ),

            // SLIDE
            new PersonMode(
                    new AnimationId[]{
                            AnimationId.SLIDE_LOOP,
                            AnimationId.SLIDE_START,
                            AnimationId.SLIDE_END
                    },
                    new ChangeMode[]{
                            ChangeMode.ALLOWED_SOFT, // IDLE
                            ChangeMode.ALLOWED_SOFT, // WALK
                            ChangeMode.ALLOWED_SOFT, // BREAK
                            ChangeMode.NOT_ALLOWED, // SLIDE
                            ChangeMode.NOT_ALLOWED, // CROUCH
                            ChangeMode.ALLOWED_SOFT, // CROUCH_WALK
                            ChangeMode.ALLOWED_SOFT, // JUMP
                    },
                    new PersonMode.Behavior<PlayerModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return false;
                        }

                        @Override
                        public PlayerModeId getNextModeId() {
                            return PlayerModeId.IDLE;
                        }

                        @Override
                        public PlayerModeId getDefaultNextModeId() {
                            return PlayerModeId.IDLE;
                        }
                    }
            ),

            // CROUCH
            new PersonMode(
                    new AnimationId[]{
                            AnimationId.CROUCH_LOOP,
                            AnimationId.CROUCH_START,
                            AnimationId.CROUCH_END
                    },
                    new ChangeMode[]{
                            ChangeMode.ALLOWED_SOFT, // IDLE
                            ChangeMode.ALLOWED_SOFT, // WALK
                            ChangeMode.ALLOWED_SOFT, // BREAK
                            ChangeMode.ALLOWED_SOFT, // SLIDE
                            ChangeMode.NOT_ALLOWED, // CROUCH
                            ChangeMode.ALLOWED_HARD, // CROUCH_WALK
                            ChangeMode.ALLOWED_SOFT, // JUMP
                    },
                    new PersonMode.Behavior<PlayerModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return false;
                        }

                        @Override
                        public PlayerModeId getNextModeId() {
                            PlayerModeId next = PlayerModeId.IDLE;

                            Gdx.app.log(TAG, "Crouch next mode: " + next);

                            return next;
                        }

                        @Override
                        public PlayerModeId getDefaultNextModeId() {
                            return getNextModeId();
                        }
                    }
            ) {{
                listeners.add(
                        new PersonMode.Listener() {
                            @Override
                            protected void onDeactivate() {
                                super.onDeactivate();
                            }
                        }
                );
            }},

            // CROUCH_WALK
            new PersonMode(
                    CROUCH_WALK_MOVE_ACCELERATION, CROUCH_WALK_MOVE_VELOCITY,
                    new AnimationId[]{
                            AnimationId.CROUCH_WALK
                    },
                    new ChangeMode[]{
                            ChangeMode.ALLOWED_SOFT, // IDLE
                            ChangeMode.NOT_ALLOWED, // WALK
                            ChangeMode.ALLOWED_SOFT, // BREAK
                            ChangeMode.ALLOWED_SOFT, // SLIDE
                            ChangeMode.ALLOWED_HARD, // CROUCH
                            ChangeMode.NOT_ALLOWED, // CROUCH_WALK
                            ChangeMode.ALLOWED_SOFT, // JUMP
                    },
                    new PersonMode.Behavior<PlayerModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return false;
                        }

                        @Override
                        public PlayerModeId getNextModeId() {
                            return PlayerModeId.IDLE;
                        }

                        @Override
                        public PlayerModeId getDefaultNextModeId() {
                            return PlayerModeId.IDLE;
                        }
                    }
            ),

            // JUMP
            new PersonMode(
                    JUMP_MOVE_ACCELERATION, JUMP_MOVE_VELOCITY,
                    new AnimationId[]{
                            AnimationId.JUMP_LOOP,
                            AnimationId.JUMP_START,
                            AnimationId.JUMP_END
                    },
                    new ChangeMode[]{
                            ChangeMode.ALLOWED_SOFT, // IDLE
                            ChangeMode.NOT_ALLOWED, // WALK
                            ChangeMode.ALLOWED_SOFT, // BREAK
                            ChangeMode.ALLOWED_SOFT, // SLIDE
                            ChangeMode.ALLOWED_SOFT, // CROUCH
                            ChangeMode.ALLOWED_SOFT, // CROUCH_WALK
                            ChangeMode.NOT_ALLOWED, // JUMP
                    },
                    new PersonMode.Behavior<PlayerModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return spriter.spriter.player.getAnimation().id ==
                                    AnimationId.JUMP_LOOP.ordinal() &&
                                    isGrounded();
                        }

                        @Override
                        public PlayerModeId getNextModeId() {
                            return PlayerModeId.IDLE;
                        }

                        @Override
                        public PlayerModeId getDefaultNextModeId() {
                            return PlayerModeId.IDLE;
                        }
                    }
            ) {{
                listeners.add(
                        new PersonMode.Listener() {
                            @Override
                            protected void onActivate() {
                                super.onActivate();
                                getBody().applyLinearImpulse(
                                        new Vector2(
                                                0f,
                                                1000f
                                        ),
                                        getBody().getPosition(),
                                        true
                                );
                            }
                        }
                );
            }}
    };

    /**
     * @param core Link to @{@link Core}
     * @param name Name of CompositeItem
     */
    public Player(Core core, String name) {
        super(core, name,
                InputGroups.InputGroupId.MOVE_LEFT,
                InputGroups.InputGroupId.MOVE_RIGHT);

        // Default mode id
        currentModeId = PlayerModeId.IDLE;

        // Listen input
        core.getInputGroups().getListeners().add(this);
    }

    @Override
    public boolean onInputGroupActivate(InputGroups.InputGroupId groupId) {
        boolean ret = false;

        PlayerModeId activateModeId = null;

        // Check input group
        switch (groupId) {
            case JUMP:
                activateModeId = PlayerModeId.JUMP;
                break;
            case MOVE_LEFT:
            case MOVE_RIGHT:
                if (getCurrentModeId() == PlayerModeId.CROUCH)
                    activateModeId = PlayerModeId.CROUCH_WALK;
                else activateModeId = PlayerModeId.WALK;
                break;
            case CROUCH:
                activateModeId = (getMoveDir() != MoveDir.NONE &&
                        getCurrentModeId() == PlayerModeId.WALK)
                        ? PlayerModeId.SLIDE
                        : PlayerModeId.CROUCH;
                break;
        }

        // Activate @PersonMode
        if (activateModeId != null) {
            activateMode(activateModeId, groupId);

            ret = true;
        }

        return super.onInputGroupActivate(groupId) || ret;
    }

    @Override
    public boolean onInputGroupDeactivate(InputGroups.InputGroupId groupId) {
        boolean ret = false;

        val currentModeId = getCurrentModeId();

        switch (groupId) {
            case CROUCH:
                switch (currentModeId) {
                    case CROUCH:
                    case CROUCH_WALK:
                        Gdx.app.log(TAG, "Crouch input group deactivate detected");

                        // Stand up
                        activateMode(PlayerModeId.IDLE, groupId);

                        ret = true;
                        break;
                }
                break;
            case MOVE_RIGHT:
            case MOVE_LEFT:
                switch (currentModeId) {
                    case CROUCH_WALK:
                        activateMode(PlayerModeId.CROUCH, groupId);
                }
                break;
        }

        return super.onInputGroupDeactivate(groupId) || ret;
    }

    @Override
    public void dispose() {
        // Stop listen input
        core.getInputGroups().getListeners().remove(this);

        super.dispose();
    }

    /**
     * Animation ids
     */
    public enum AnimationId {
        IDLE,

        WALK,

        BREAK_START,
        BREAK_LOOP,
        BREAK_END,

        SLIDE_START,
        SLIDE_LOOP,
        SLIDE_END,

        CROUCH_START,
        CROUCH_LOOP,
        CROUCH_END,

        CROUCH_WALK,

        JUMP_START,
        JUMP_LOOP,
        JUMP_END
    }

    /**
     * Id's of @modes
     */
    public enum PlayerModeId {
        IDLE,
        WALK,
        BREAK,
        SLIDE,
        CROUCH,
        CROUCH_WALK,
        JUMP
    }
}
