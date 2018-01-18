package ru.coolone.adventure_emulation.input;

import com.badlogic.gdx.Gdx;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import lombok.val;
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
public class InputGroupsTest extends AbsTest {

    @SuppressWarnings("unused")
    private static final String TAG = InputGroupsTest.class.getSimpleName();

    private InputGroups inputGroups;

    private final int checkIndex = (int) (Math.random() * (InputGroups.InputGroupId.values().length - 1));

    private int activateCount;
    private int deactivateCount;
    private InputGroups.InputGroupId activatedGroupId;
    private InputGroups.InputGroupId deactivatedGroupId;
    private InputGroups.InputGroupsListener listener = new InputGroups.InputGroupsListener() {
        @Override
        public boolean onInputGroupActivate(InputGroups.InputGroupId groupId) {
            Gdx.app.log(TAG, "Activate group index: " + groupId);
            if (groupId.ordinal() == checkIndex)
                Gdx.app.log(TAG, "Activate success");
            activateCount++;
            activatedGroupId = groupId;
            return false;
        }

        @Override
        public boolean onInputGroupDeactivate(InputGroups.InputGroupId groupId) {
            Gdx.app.log(TAG, "DEActivate group index: " + groupId);
            if (groupId.ordinal() == checkIndex)
                Gdx.app.log(TAG, "DEActivate success");
            deactivateCount++;
            deactivatedGroupId = groupId;
            return false;
        }
    };

    @BeforeClass
    @Override
    protected void setUpClass() throws Exception {
        super.setUpClass();
        inputGroups = new InputGroups();
        inputGroups.getListeners().clear();
        inputGroups.getListeners().add(listener);
        Gdx.app.log(TAG, "Check group index: " + checkIndex);
    }

    @Test
    public void testGroupActivate() throws Exception {
        int oldActivateCount = activateCount;
        val checkGroupId = InputGroups.InputGroupId.values()[checkIndex];

        Gdx.app.log(TAG, "Starting DEActivate group index: " + checkIndex);
        inputGroups.groupActivate(checkGroupId);
        assertEquals(activateCount, oldActivateCount + 1);
        assertEquals(activatedGroupId, checkGroupId);
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
        assertEquals(deactivatedGroupId, checkGroupId);
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
        val checkGroup = keyGroups.entrySet().iterator().next();
        val checkGroupId = checkGroup.getKey();
        val checkGroupKeyId = checkGroup.getValue()
                [(int) (Math.random() * (checkGroup.getValue().length - 1))];
        activatedGroupId = null;
        deactivatedGroupId = null;

        assertFalse(keyGroups.isEmpty());
        //noinspection SuspiciousMethodCalls
        inputGroups.keyDown(checkGroupKeyId);

        assertEquals(checkGroupId, activatedGroupId);
        assertNull(deactivatedGroupId);
    }

    @Test
    public void testKeyUp() throws Exception {
        val checkGroup = keyGroups.entrySet().iterator().next();
        val checkGroupId = checkGroup.getKey();
        val checkGroupKeyId = checkGroup.getValue()
                [(int) (Math.random() * (checkGroup.getValue().length - 1))];
        activatedGroupId = null;
        deactivatedGroupId = null;

        assertFalse(keyGroups.isEmpty());
        //noinspection SuspiciousMethodCalls
        inputGroups.keyDown(checkGroupKeyId);

        assertEquals(activatedGroupId, checkGroupId);
        assertNull(deactivatedGroupId);

        //noinspection SuspiciousMethodCalls
        inputGroups.keyUp(checkGroupKeyId);

        assertEquals(deactivatedGroupId, checkGroupId);
    }
}
