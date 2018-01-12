package ru.coolone.adventure_emulation;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.BeforeClass;

import static org.mockito.Mockito.mock;

/**
 * Abstract test class with initialize gdx static vars and mockito annotations
 *
 * @author coolone
 */

abstract public class AbsTest extends PowerMockTestCase {

    protected static void initGdx() {
        // Initialize libgdx headless for use static vars (e.g. Gdx.input.setInputProcessor)
        final HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        config.renderInterval = 1f / 60; // Likely want 1f/60 for 60 fps
        new HeadlessApplication(mock(Core.class), config);
    }

    @BeforeClass
    protected void setUpClass() throws Exception {
        super.beforePowerMockTestClass();
        initGdx();
    }
}
