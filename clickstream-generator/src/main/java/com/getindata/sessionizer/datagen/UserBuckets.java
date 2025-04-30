package com.getindata.sessionizer.datagen;

import java.time.Duration;
import java.util.List;

/*
 * 45% - 0
 * 25% - up to 1 min
 * 25% - up to 30 min
 * 5% - up to 60 min
 * 0.01% - up to 120 min
 * */
public class UserBuckets {

    UserBucket singleSessionBucket; // 45%
    UserBucket upTo1minBucket;      // 25%
    UserBucket upTo30minBucket;     // 25%
    UserBucket upTo60minBucket;     // 5%

    public UserBuckets(long maxActiveUsers) {
        singleSessionBucket = new UserBucket(
                "singleSession",
                Double.valueOf(maxActiveUsers * 0.45).longValue(),
                Duration.ZERO,
                Duration.ofSeconds(10),
                0,
                Duration.ofSeconds(10));
        upTo1minBucket = new UserBucket(
                "upTo1min",
                Double.valueOf(maxActiveUsers * 0.25).longValue(),
                Duration.ofMinutes(1),
                Duration.ofMinutes(1),
                2,
                Duration.ofSeconds(10));
        upTo30minBucket = new UserBucket(
                "upTo30min",
                Double.valueOf(maxActiveUsers * 0.25).longValue(),
                Duration.ofMinutes(2),
                Duration.ofMinutes(30),
                2,
                Duration.ofSeconds(20));
        upTo60minBucket = new UserBucket(
                "upTo60min",
                Double.valueOf(maxActiveUsers * 0.05).longValue(),
                Duration.ofMinutes(31),
                Duration.ofMinutes(60),
                1,
                Duration.ofSeconds(40));
    }

    public List<UserBucket> toList() {
        return List.of(
                singleSessionBucket
                ,
                upTo1minBucket
                ,
                upTo30minBucket
                ,
                upTo60minBucket
        );
    }
}
