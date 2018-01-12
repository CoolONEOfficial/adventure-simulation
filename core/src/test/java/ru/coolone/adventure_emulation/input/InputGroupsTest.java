package ru.coolone.adventure_emulation.input;

import com.badlogic.gdx.Gdx;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import lombok.val;
import ru.coolone.adventure_emulation.AbsTest;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static ru.coolone.adventure_emulation.input.InputGroups.keyGroups;

/**
 * @author coolone
 * @since 07.01.18
 */
@PrepareForTest({InputGroups.class})
public class InputGroupsTest extends AbsTest {

    @SuppressWarnings("unused")
    private static final String TAG = InputGroupsTest.class.getSimpleName();

    private InputGroups inputGroups;

    @BeforeClass
    @Override
    protected void setUpClass() throws Exception {
        super.setUpClass();
        inputGroups = new InputGroups();
        inputGroups.getListeners().clear();
        inputGroups.getListeners().add(listener);
        Gdx.app.log(TAG, "Check group index: " + checkIndex);
    }

    private final int checkIndex = (int) (Math.random() * (InputGroups.InputGroupId.values().length - 1));

    private int activateCount;
    private int deactivateCount;
    private InputGroups.InputGroupsListener listener = new InputGroups.InputGroupsListener() {
        @Override
        public boolean onInputGroupActivate(InputGroups.InputGroupId groupId) {
            Gdx.app.log(TAG, "Activate group index: " + groupId);
            if (groupId.ordinal() == checkIndex)
                Gdx.app.log(TAG, "Activate success");
                activateCount++;
            return false;
        }

        @Override
        public boolean onInputGroupDeactivate(InputGroups.InputGroupId groupId) {
            Gdx.app.log(TAG, "DEActivate group index: " + groupId);
            if (groupId.ordinal() == checkIndex)
                Gdx.app.log(TAG, "DEActivate success");
                deactivateCount++;
            return false;
        }
    };

    @Test
    public void testGroupActivate() throws Exception {
        int oldActivateCount = activateCount;
        Gdx.app.log(TAG, "Starting DEActivate group index: " + checkIndex);
        inputGroups.groupActivate(InputGroups.InputGroupId.values()[checkIndex]);
        assertEquals(activateCount, oldActivateCount + 1);
    }

    @Test
    public void testGroupDeactivate() throws Exception {
        Gdx.app.log(TAG, "Starting DEActivate group index: " + checkIndex);
        val checkGroupId = InputGroups.InputGroupId.values()[checkIndex];

        inputGroups.getActiveGroups().clear();
        assertFalse(inputGroups.groupDeactivate(checkGroupId));

        int oldDeactivateCount = deactivateCount;
        inputGroups.groupActivate(checkGroupId);
        assertTrue(inputGroups.groupDeactivate(checkGroupId));
        assertEquals(deactivateCount, oldDeactivateCount + 1);
    }

    @Test
    public void testKeyToInputGroup() throws Exception {
        for (InputGroups.InputGroupId mGroup : keyGroups.keySet()) {
            for (Integer mKeycode : keyGroups.get(mGroup)) {
                assertEquals(InputGroups.keyToInputGroup(mKeycode), mGroup);
            }
        }
        assertNull(InputGroups.keyToInputGroup(-1));
    }

    @Test
    public void testKeyDown() throws Exception {
        mockStatic(InputGroups.class);

        assertFalse(keyGroups.isEmpty());
        //noinspection SuspiciousMethodCalls
        inputGroups.keyDown(keyGroups.entrySet().iterator().next().getValue()[0]);

        verifyStatic(InputGroups.class);
        InputGroups.keyToInputGroup(anyInt());
    }

    @Test
    public void testKeyUp() throws Exception {
        mockStatic(InputGroups.class);

        assertFalse(keyGroups.isEmpty());
        //noinspection SuspiciousMethodCalls
        inputGroups.keyDown(keyGroups.entrySet().iterator().next().getValue()[0]);
        //noinspection SuspiciousMethodCalls
        inputGroups.keyUp(keyGroups.entrySet().iterator().next().getValue()[0]);

        verifyStatic(InputGroups.class, times(2));
        InputGroups.keyToInputGroup(anyInt());
    }
}
