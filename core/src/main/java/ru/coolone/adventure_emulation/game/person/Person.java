package ru.coolone.adventure_emulation.game.person;

import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;

/**
 * Person abstract class
 * Person is entity, that have mode, modeAdapter and spriter component for change animations
 */
public abstract class Person<PlayerModeId extends Enum> {
    // Mode id (adapters or modes)
    protected PlayerModeId modeId;

    // Modes
    public abstract PersonModeData[] getModes();

    public PersonModeData getCurrentMode() {
        return getModes()[modeId.ordinal()];
    }

    // Mode adapters
    public abstract PersonModeAdapter[] getModeAdapters();

    public PersonModeAdapter getCurrentModeAdapter() {
        return getModeAdapters()[modeId.ordinal()];
    }

    // Spriter component
    public abstract SpriterComponent getSpriter();

    // Physic component
    public abstract PhysicsBodyComponent getPhysic();
}
