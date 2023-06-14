package me.harry.apartment_comparison.domain.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash
public class BlackList {
    @Id
    private final String token;
    @TimeToLive
    private final Long timeToLive;

    public BlackList(String token, Long timeToLive) {
        this.token = token;
        this.timeToLive = timeToLive;
    }

}
