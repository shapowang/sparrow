package net.xser.ratelimiter;

public interface IService {
    @RateLimit(countLimit = 3, durationSeconds = 10)
    void run();
}