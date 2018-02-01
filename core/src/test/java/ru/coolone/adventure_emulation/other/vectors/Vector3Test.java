package ru.coolone.adventure_emulation.other.vectors;

import org.testng.annotations.Test;

import lombok.val;

import static org.testng.Assert.assertEquals;

/**
 * @author coolone
 * @since 22.01.18
 */
public class Vector3Test {
    @Test
    public void createFrom2d() {
        val vec2 = new Vector2(
                (float) (Math.random() * 200.),
                (float) (Math.random() * 100.)
        );

        val vec3 = new Vector3(
                vec2
        );
        assertEquals(vec2.x, vec3.x);
        assertEquals(vec2.y, vec3.y);
    }

    @Test
    public void createFrom3d() {
        val vecSrc = new Vector3(
                (float) (Math.random() * 200.),
                (float) (Math.random() * 100.),
                (float) (Math.random() * 500.)
        );

        val vec3 = new Vector3(
                vecSrc
        );
        assertEquals(vecSrc.x, vec3.x);
        assertEquals(vecSrc.y, vec3.y);
    }

}