package com.spacesoldat.hackathons.implementation.execution.redis;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RedisUpdateTickersImpl {

    public Function updateTickerValues(RedisTemplate redisTemplate){

        return updateRequest -> {
            List result = new ArrayList();

            return result;
        };

    }

}
