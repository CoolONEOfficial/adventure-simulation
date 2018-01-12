package org.mockito.configuration;

/**
 * @author coolone
 * @since 12.01.18
 */

public class MockitoConfiguration extends DefaultMockitoConfiguration {

    @Override
    public boolean enableClassCache() {
        return false;
    }
}