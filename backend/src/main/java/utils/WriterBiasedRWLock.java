package utils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A simple mutex structure that can be used to manage access between two types of threads (high and low priority ones).
 * Multiple low priority threads can access gain mutex access at the same time, but high priority threads get exclusive access, i.e. if a high priority thread acquires the lock, then not other thread has it (regardless of the priority).
 * If a low priority thread (reader) requests access it only gets access if there is no high priority thread requesting access
 * If a high priority thread (writer) requests access it only has to compete with other high priority threads
 * Fairness inside a priority class can be specified when constructing the lock. This does not affect the writer bias.
 * A high throughput of high priority threads will still lead to starvation of low priority threads, even if the policy is fair
 * This class differs from {@link java.util.concurrent.locks.ReentrantReadWriteLock} since the java standard class is not biased towards writers
 * Please note that as of now this is a rudimentary implementation. For example it does not support the creation of a {@link java.util.concurrent.locks.Condition}. As such it does also not fully implement the interface {@link ReadWriteLock}.
 * Please be aware of this limitations when using the lock.
 * Is reentrant
 */
public class WriterBiasedRWLock {

    private Lock data;
    private Semaphore readerSema;
    private Semaphore writerSema;
    private int waitingWriters = 0;
    private int waitingReaders = 0;
    private int activeReaders = 0;
    private int activeWriters = 0;
    private Long holderId = null;
    private int holderCount = 0; // how often the holder has aquired the lock

    /**
     * Constructor for the class. Creates an unfair, reentrant RWLock
     */
    public WriterBiasedRWLock() {
        this(false);
    }

    /**
     * Constructor for the class.
     *
     * @param fair - specifies whether or not the lock should be fair (among threads of the same priority)
     */
    public WriterBiasedRWLock(boolean fair) {
        data = new ReentrantLock(fair);
        readerSema = new Semaphore(0, fair);
        writerSema = new Semaphore(0, fair);

    }

    /**
     * Tries to acquire access for a low priority task
     *
     * @throws InterruptedException - if the thread gets interrupted while waiting for access
     */
    public void getReadAccess() throws InterruptedException {
        data.lock();
        if(holderId != null && holderId.equals(Thread.currentThread().getId())) {
            data.unlock();
            return; // if a thread has write access it does not care about reads
        }
        if(activeWriters + waitingWriters == 0) {
            readerSema.release();
            activeReaders++;
        } else {
            waitingReaders++;
        }
        data.unlock();
        readerSema.acquire();
    }

    /**
     * Signals that the low priority task has been completed.
     * Can not be interrupted in order to avoid deadlocks on the mutex
     */
    public void finishRead() {
        data.lock();
        if(holderId != null && holderId.equals(Thread.currentThread().getId())) {
            data.unlock();
            return; // if a thread has write access it does not care about reads
        }
        activeReaders--;
        if(activeReaders == 0 && waitingWriters > 0) {
            writerSema.release();
            activeWriters++;
            waitingWriters--;
        }
        data.unlock();
    }

    /**
     * Tries to acquire access for a high priority task
     *
     * @throws InterruptedException - if the thread gets interrupted while waiting for access
     */
    public void getWriteAccess() throws InterruptedException {
        data.lock();
        if(holderId != null && holderId.equals(Thread.currentThread().getId())) {
            data.unlock();
            holderCount++;
            return;
        }
        if(activeWriters + activeReaders + waitingWriters == 0) {
            writerSema.release();
            activeWriters++;
        } else {
            waitingWriters++;
        }
        data.unlock();
        writerSema.acquire();
        data.lock();
        holderId = Thread.currentThread().getId();
        holderCount = 0;
        data.unlock();

    }

    /**
     * Signals that the low priority task has been completed.
     * Can not be interrupted in order to avoid deadlocks on the mutex
     */
    public void finishWrite() {
        data.lock();
        if(holderId != null && holderId.equals(Thread.currentThread().getId())) {
            if(holderCount != 0) {
                holderCount--;
                data.unlock();
                return;
            }
        }
        activeWriters--;
        if(waitingWriters > 0) {
            writerSema.release();
            activeWriters++;
            waitingWriters--;
        } else {
            while(waitingReaders > 0) {
                readerSema.release();
                activeReaders++;
                waitingReaders--;
            }
        }

        data.unlock();
    }


}
