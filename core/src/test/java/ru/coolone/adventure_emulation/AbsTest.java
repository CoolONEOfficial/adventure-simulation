package ru.coolone.adventure_emulation;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;

import static org.mockito.Mockito.mock;

/**
 * @author coolone
 */

abstract public class AbsTest {

    protected static HeadlessApplication headlessApplication;

    static {
        // Initialize libgdx headless for use static vars (e.g. Gdx.input.setInputProcessor)
        final HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        config.renderInterval = 1f / 60; // Likely want 1f/60 for 60 fps
        headlessApplication = new HeadlessApplication(mock(Core.class), config);
    }

    @BeforeMethod(alwaysRun = true)
    public void initMethod() throws Exception {
        // Initialize mocks
        MockitoAnnotations.initMocks(this);
    }
}
