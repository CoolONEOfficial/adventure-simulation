package ru.coolone.adventure_emulation;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;

/**
 * @author coolone
 */

abstract public class AbsTest {
    @BeforeMethod(alwaysRun = true)
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
}
