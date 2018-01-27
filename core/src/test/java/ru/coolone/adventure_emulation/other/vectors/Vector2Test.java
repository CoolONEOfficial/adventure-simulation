package ru.coolone.adventure_emulation.other.vectors;

import org.testng.annotations.Test;

import lombok.val;

import static org.testng.Assert.assertEquals;

/**
 * @author coolone
 * @since 22.01.18
 */
public class Vector2Test {
    @Test
    void createFrom3d() {
        val vec3 = new Vector3(
                (float) (Math.random() * 200.),
                (float) (Math.random() * 100.),
                0f
        );

        val vec2 = new Vector2(
                vec3
        );
        assertEquals(vec2.x, vec3.x);
        assertEquals(vec2.y, vec3.y);
    }

    @Test
    void createFrom2d() {
        val vec2Src = new Vector2(
                (float) (Math.random() * 200.),
                (float) (Math.random() * 100.)
        );

        val vec2 = new Vector2(
                vec2Src
        );
        assertEquals(vec2.x, vec2Src.x);
        assertEquals(vec2.y, vec2Src.y);
    }
}