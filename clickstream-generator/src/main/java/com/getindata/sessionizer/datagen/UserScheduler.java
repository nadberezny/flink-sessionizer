package com.getindata.sessionizer.datagen;

import com.getindata.sessionizer.datagen.generators.OrderGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.LongStream;

@Slf4j
record UserScheduler(UserBucket userBucket, boolean isDryRun, OrderGenerator orderGenerator) implements Runnable {

    @Override
    public void run() {
        long noOfUsers = userBucket.getNoOfActiveUsers();
        if (noOfUsers >= 0 && noOfUsers < userBucket.getMaxActiveUsers()) {
            long noOfUsersToAdd = userBucket.getMaxActiveUsers() - noOfUsers;
            LongStream.range(1, noOfUsersToAdd + 1).forEach(uId -> {
                var id = userBucket.getBucketName() + noOfUsers + "-" + uId;
                log.debug("Starting new thread {}", id);
                userBucket.increment();
                Thread.ofVirtual().name(id).start(new User(id, userBucket, isDryRun, orderGenerator));
            });
        }
    }
}
