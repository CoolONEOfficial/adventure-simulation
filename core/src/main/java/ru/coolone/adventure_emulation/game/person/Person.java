package ru.coolone.adventure_emulation.game.person;

import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;

import ru.coolone.adventure_emulation.game.scripts.Player;
import ru.coolone.adventure_emulation.input.InputGroups;

/**
 * Person abstract class
 * Person is entity, that have mode, modeAdapter and spriter component for change animations
 *
 * @author coolone
 */
public abstract class Person<PlayerModeId extends Enum> {

    /**
     * Move direction
     */
    protected Player.MoveDirection move = Player.MoveDirection.NONE;
    /**
     * Current id of @{@link PersonModeData} and @{@link PersonModeAdapter}
     */
    protected PlayerModeId modeId;

    /**
     * @return Array of @{@link PersonModeData}
     */
    public abstract PersonModeData[] getModes();

    /**
     * @return Current @{@link PersonModeData}
     */
    public PersonModeData getCurrentMode() {
        return getModes()[modeId.ordinal()];
    }

    /**
     * @return Array of @{@link PersonModeAdapter}
     */
    public abstract PersonModeAdapter[] getModeAdapters();

    /**
     * @return Current @{@link PersonModeAdapter}
     */
    public PersonModeAdapter getCurrentModeAdapter() {
        return getModeAdapters()[modeId.ordinal()];
    }

    /**
     * @return @{@link SpriterComponent} of person entity
     */
    public abstract SpriterComponent getSpriter();

    // --- Components ---

    /**
     * @return @{@link PhysicsBodyComponent} of person entity
     */
    public abstract PhysicsBodyComponent getPhysic();

    /**
     * @return @{@link DimensionsComponent} of person entity
     */
    public abstract DimensionsComponent getDimensions();

    public abstract InputGroups.InputGroupsListener getInputListener();

    /**
     * Move direction
     */
    public enum MoveDirection {
        NONE,
        LEFT,
        RIGHT
    }
}
