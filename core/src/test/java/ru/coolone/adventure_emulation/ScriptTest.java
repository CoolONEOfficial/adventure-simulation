package ru.coolone.adventure_emulation;

import com.badlogic.ashley.core.Component;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.EnumMap;

/**
 * Created by coolone on 08.01.18.
 */
public class ScriptTest extends AbsTest {
    @InjectMocks
    Script script;

    private final EnumMap<Script.ComponentId, Component> components = new EnumMap<>(Script.ComponentId.class);

    @BeforeMethod
    @Override
    public void initMethod() throws Exception {
        super.initMethod();

        Mockito.when(script.getComponents()).thenReturn(components);
    }

    @Test
    public void testAddComponents() throws Exception {
    }

    @Test
    public void testGetVisible() throws Exception {
    }

    @Test
    public void testSetVisible() throws Exception {
    }

    @Test
    public void testGetX() throws Exception {
    }

    @Test
    public void testSetX() throws Exception {
    }

    @Test
    public void testGetY() throws Exception {
    }

    @Test
    public void testSetY() throws Exception {
    }

    @Test
    public void testGetCoord() throws Exception {
    }

    @Test
    public void testSetCoord() throws Exception {
    }

    @Test
    public void testGetWidth() throws Exception {
    }

    @Test
    public void testGetHeight() throws Exception {
    }

    @Test
    public void testIntercepts() throws Exception {
    }

    @Test
    public void testGetBody() throws Exception {
    }

    @Test
    public void testIsSpawned() throws Exception {
    }

    @Test
    public void testGetLayer() throws Exception {
    }

    @Test
    public void testSetAnimation() throws Exception {
    }

    @Test
    public void testGetAnimation() throws Exception {
    }

    @Test
    public void testGetAnimationPlayer() throws Exception {
    }

    @Test
    public void testSetFlipped() throws Exception {
    }

}