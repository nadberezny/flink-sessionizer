package com.getindata.sessionizer.datagen;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
@ToString
@Slf4j
public final class UserBucket implements AutoCloseable {

    private final String bucketName;
    private final long maxActiveUsers;
    private final Duration minSession;
    private final Duration maxSession;
    private final int chanceForPlacingOrder;
    private final Duration pageViewInterval;

    @Getter(AccessLevel.NONE)
    private final ConcurrentLinkedQueue<Boolean> queue = new ConcurrentLinkedQueue<>();

    private volatile long noOfActiveUsers = 0;

    private boolean isClosed = false;

    public UserBucket(String bucketName,
                      long maxActiveUsers,
                      Duration minSession,
                      Duration maxSession,
                      int chanceForPlacingOrder,
                      Duration pageViewInterval) {
        this.bucketName = bucketName;
        this.maxActiveUsers = maxActiveUsers;
        this.minSession = minSession;
        this.maxSession = maxSession;
        this.chanceForPlacingOrder = chanceForPlacingOrder;
        this.pageViewInterval = pageViewInterval;
        Thread.ofVirtual().start(new Worker());
    }

    public void increment() {
        queue.add(true);
    }
    public void decrement() {
        queue.add(false);
    }

    @Override
    public void close() {
        isClosed = true;
    }

    private final class Worker implements Runnable {

        @Override
        public void run() {
            while(!isClosed) {
                try {
                    if (queue.peek() != null) {
                        Boolean poll = queue.poll();
                        // non-atomic operation but Bucket/Worker should be accessed by single thread
                        noOfActiveUsers = poll ? noOfActiveUsers + 1 : noOfActiveUsers - 1;
                    } else {
                        Thread.sleep(200);
                    }
                } catch (InterruptedException e) {
                    log.error("UserBucket Worker interrupted", e);
                    isClosed = true;
                }
            }
        }
    }
}
