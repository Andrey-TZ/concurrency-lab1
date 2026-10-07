package org.labs;

import java.util.concurrent.locks.ReentrantLock;

public class Spoon {
    ReentrantLock lock = new ReentrantLock(true);

    public void pickUp() {
        lock.lock();
    }

    public void putDown() {
        lock.unlock();
    }
}
