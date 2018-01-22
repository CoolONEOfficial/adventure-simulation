package ru.coolone.adventure_emulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.Test;

import ru.coolone.adventure_emulation.screen.ScreenManager;
import ru.coolone.adventure_emulation.screens.GameScreen;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static ru.coolone.adventure_emulation.Core.DEBUG;

// TODO: autoclean with lombok

/**
 * @author coolone
 */
public class CoreTest extends AbsTest {
    @InjectMocks
    Core core = new Core();

    @Mock
    ScreenManager screenManager = mock(ScreenManager.class);

    @Test
    public void testRender() throws Exception {
        Gdx.gl = mock(GL20.class);
        doNothing().when(Gdx.gl).glClearColor(anyInt(), anyInt(), anyInt(), anyInt());
        doNothing().when(Gdx.gl).glClear(anyInt());

        Gdx.graphics = mock(Graphics.class);
        when(Gdx.graphics.getDeltaTime()).thenReturn(0.10f);

        DEBUG = false;
        core.render();

        verify(screenManager).updateEngine(Gdx.graphics.getDeltaTime());
    }

    @Test
    public void testResize() throws Exception {
        when(screenManager.getCurrentScreen()).thenReturn(mock(GameScreen.class));
        core.resize(10, 10);
        verify(screenManager).openScreen(any());
    }
}