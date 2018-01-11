package ru.coolone.adventure_emulation.input;

import com.badlogic.gdx.Gdx;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import lombok.NoArgsConstructor;
import ru.coolone.adventure_emulation.AbsTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static ru.coolone.adventure_emulation.input.InputGroups.keyGroups;

/**
 * @author coolone
 * @since 07.01.18
 */
@NoArgsConstructor
public class InputGroupsTest extends AbsTest {

    @SuppressWarnings("unused")
    private static final String TAG = InputGroupsTest.class.getSimpleName();

    private InputGroups inputGroups = new InputGroups();

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

    @BeforeMethod
    @Override
    public void initMethod() throws Exception {
        super.initMethod();
        inputGroups.getListeners().clear();
        inputGroups.getListeners().add(listener);
        Gdx.app.log(TAG, "Check group index: " + checkIndex);
    }

    @Test
    public void testGroupActivate() throws Exception {
        int oldActivateCount = activateCount;
        Gdx.app.log(TAG, "Starting DEActivate group index: " + checkIndex);
        inputGroups.groupActivate(InputGroups.InputGroupId.values()[checkIndex]);
        assertEquals(activateCount, oldActivateCount + 1);
    }

    @Test
    public void testGroupDeactivate() throws Exception {
        int oldDeactivateCount = deactivateCount;
        Gdx.app.log(TAG, "Starting DEActivate group index: " + checkIndex);
        assertTrue(inputGroups.groupDeactivate(InputGroups.InputGroupId.values()[checkIndex]));
        assertEquals(deactivateCount, oldDeactivateCount + 1);
        assertFalse(inputGroups.groupDeactivate(null));
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
        assertFalse(keyGroups.isEmpty());
        //noinspection SuspiciousMethodCalls
        inputGroups.keyDown(keyGroups.entrySet().iterator().next().getValue()[0]);
    }

    @Test
    public void testKeyUp() throws Exception {
    }
}
