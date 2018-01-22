package ru.coolone.adventure_emulation.other.vectors;

import lombok.NoArgsConstructor;

/**
 * Clone of @{@link com.badlogic.gdx.math.Vector3}, but with {@link #Vector3(com.badlogic.gdx.math.Vector2)}
 *
 * @author coolone
 * @since 20.01.18
 */

@NoArgsConstructor
public class Vector3 extends com.badlogic.gdx.math.Vector3 {
    /**
     * Creates a vector with the given components
     *
     * @param x The x-component
     * @param y The y-component
     * @param z The z-component
     */
    public Vector3(float x, float y, float z) {
        this.set(x, y, z);
    }

    /**
     * Creates a vector from the given vector
     *
     * @param vector The vector
     */
    public Vector3(final com.badlogic.gdx.math.Vector3 vector) {
        this.set(vector);
    }

    /**
     * Creates a vector from the given array. The array must have at least 3 elements.
     *
     * @param values The array
     */
    public Vector3(final float[] values) {
        this.set(values[0], values[1], values[2]);
    }

    /**
     * Creates a vector from the given {@link com.badlogic.gdx.math.Vector2} and z-component
     *
     * @param vector The vector
     * @param z      The z-component
     */
    public Vector3(final com.badlogic.gdx.math.Vector2 vector, float z) {
        this.set(vector.x, vector.y, z);
    }

    /**
     * Creates a vector from the given {@link com.badlogic.gdx.math.Vector2}
     *
     * @param vector The vector
     */
    public Vector3(final com.badlogic.gdx.math.Vector2 vector) {
        this.set(vector.x, vector.y, 0.0f);
    }
}
