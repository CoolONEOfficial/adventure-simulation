package ru.coolone.adventure_emulation.other.vectors;

import lombok.NoArgsConstructor;

/**
 * Clone of @{@link com.badlogic.gdx.math.Vector2}, but with {@link #Vector2(com.badlogic.gdx.math.Vector3)}
 *
 * @author coolone
 * @since 20.01.18
 */

@NoArgsConstructor
public class Vector2 extends com.badlogic.gdx.math.Vector2 {

    /**
     * Constructs a vector with the given components
     *
     * @param x The x-component
     * @param y The y-component
     */
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a vector from the given @{@link com.badlogic.gdx.math.Vector2}
     *
     * @param v The vector
     */
    public Vector2(com.badlogic.gdx.math.Vector2 v) {
        set(v);
    }

    /**
     * Constructs a vector from the given @{@link com.badlogic.gdx.math.Vector3}
     *
     * @param v The vector
     */
    public Vector2(com.badlogic.gdx.math.Vector3 v) {
        set(v.x, v.y);
    }
}
