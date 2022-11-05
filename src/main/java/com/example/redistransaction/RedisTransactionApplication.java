package com.example.redistransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

@SpringBootApplication
public class RedisTransactionApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RedisTransactionApplication.class, args);
    }

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        var hashOperations = redisTemplate.opsForValue();
        var account1 = Account.of("1", 1, 100);
        var account2 = Account.of("2", 2, 100);

        hashOperations.set("account:1", account1);
        hashOperations.set("account:2", account2);

        var balanceTransfer = BalanceTransfer.of(1, 2, 10);
        try {
            redisTemplate.execute(new RedisRollback());
        }catch (Exception e) {
            // TRANSACTION ROLLBACK
            System.out.println("account1 = " + hashOperations.get("account:1"));
            System.out.println("account2 = " + hashOperations.get("account:2"));
        }

        System.out.println("account1 = " + hashOperations.get("account:1"));
        System.out.println("account2 = " + hashOperations.get("account:2"));
        // TRANSACTION
    }
}
class RedisRollback implements SessionCallback<List<Object>> {

    @Override
    public <K, V> List<Object> execute(RedisOperations<K, V> operations) throws DataAccessException {
        operations.multi();
        operations.discard();
        System.out.println("RedisRollback.execute");
        return operations.exec();
    }
}
