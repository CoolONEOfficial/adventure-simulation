package ru.coolone.adventure_emulation;

import com.badlogic.gdx.graphics.g2d.Batch;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.Test;

import ru.coolone.adventure_emulation.screen.ScreenManager;

import static org.testng.Assert.assertEquals;

// TODO: autoclean with lombok

/**
 * @author coolone
 */
public class CoreTest extends AbsTest {
    @InjectMocks
    Core core;

    @Mock
    ScreenManager screenManager;

    @Mock
    private ru.coolone.adventure_emulation.input.InputGroups inputGroups;

    @Mock
    private Batch uiBatch;

    @Test
    public void testGetScreenManager() throws Exception {
        assertEquals(core.getScreenManager(), screenManager);
    }

    @Test
    public void testGetInputGroups() throws Exception {
        assertEquals(core.getInputGroups(), inputGroups);
    }

    @Test
    public void testGetUiBatch() throws Exception {
        assertEquals(core.getUiBatch(), uiBatch);
    }

}