package ru.coolone.adventure_emulation.input;

import com.badlogic.gdx.Gdx;

import org.mockito.InjectMocks;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ru.coolone.adventure_emulation.AbsTest;

import static org.testng.Assert.assertEquals;

/**
 * Created by coolone on 07.01.18.
 */
public class InputGroupsTest extends AbsTest {

    @SuppressWarnings("unused")
    private static final String TAG = InputGroupsTest.class.getSimpleName();

    private final int checkIndex = (int) (Math.random() * InputGroups.InputGroupId.values().length);

    @InjectMocks
    private InputGroups inputGroups;

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

    private int activateCount;
    private int deactivateCount;

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
        inputGroups.groupDeactivate(InputGroups.InputGroupId.values()[checkIndex]);
        assertEquals(deactivateCount, oldDeactivateCount + 1);
    }

}
