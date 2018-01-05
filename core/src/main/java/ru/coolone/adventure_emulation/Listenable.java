package ru.coolone.adventure_emulation;

/**
 * Interface for classes, that using Listener pattern
 */

public interface Listenable<T> {
    /**
     * @param listener @{@link T}, that will be added in array of listeners
     */
    void addListener(T listener);

    /**
     * @param listener @{@link T}, that will be removed from array of listeners
     * @return Remove result
     */
    boolean removeListener(T listener);
}
