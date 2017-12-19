package ru.coolone.adventure_emulation;

import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;

/**
 * Created by coolone on 19.12.17.
 */
abstract class Person<PlayerModeId extends Enum> {
    // Mode id (adapters or modes)
    PlayerModeId modeId;

    // Modes
    abstract PersonMode[] getModes();

    PersonMode getCurrentMode() {
        return getModes()[modeId.ordinal()];
    }

    // Mode adapters
    abstract PersonModeAdapter[] getModeAdapters();

    PersonModeAdapter getCurrentModeAdapter() {
        return getModeAdapters()[modeId.ordinal()];
    }

    // Spiter component
    SpriterComponent spriter;

    abstract SpriterComponent getSpriter();
}
