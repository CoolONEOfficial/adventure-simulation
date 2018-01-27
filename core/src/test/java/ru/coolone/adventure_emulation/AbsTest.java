package ru.coolone.adventure_emulation;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.BeforeClass;

import lombok.val;

import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Abstract test class with initialize gdx static vars and mockito annotations
 *
 * @author coolone
 */

abstract public class AbsTest extends PowerMockTestCase {

    private boolean gdxInitialized = false;

    protected static void initGdx(AbsTest absTest) {
        if (!absTest.gdxInitialized) {
            // Initialize libgdx headless for use static vars (e.g. Gdx.input.setInputProcessor)
            val config = new HeadlessApplicationConfiguration() {{
                renderInterval = 1f / 60; // Likely want 1f/60 for 60 fps
            }};
            new HeadlessApplication(mock(Core.class), config);

            absTest.gdxInitialized = true;
        }
    }

    @BeforeClass
    protected void setUpClass() throws Exception {
        super.beforePowerMockTestClass();
        initGdx(this);
    }
}
