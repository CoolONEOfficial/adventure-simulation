package ru.coolone.adventure_emulation;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

import org.mockito.MockitoAnnotations;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.BeforeMethod;

import static org.mockito.Mockito.mock;

/**
 * Abstract test class with initialize gdx static vars and mockito annotations
 *
 * @author coolone
 */

abstract public class AbsTest extends PowerMockTestCase {

    static {
        initGdx();
    }

    protected static void initGdx() {
        // Initialize libgdx headless for use static vars (e.g. Gdx.input.setInputProcessor)
        final HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        config.renderInterval = 1f / 60; // Likely want 1f/60 for 60 fps
        new HeadlessApplication(mock(Core.class), config);
    }

    @BeforeMethod(alwaysRun = true)
    public void initMethod() throws Exception {
        // Initialize mocks
        MockitoAnnotations.initMocks(this);
    }
}
