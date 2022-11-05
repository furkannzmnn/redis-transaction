package com.example.redistransaction;


import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor(staticName = "of")
public class BalanceTransfer implements SessionCallback<List<Object>> {

    public static final String ACCOUNT = "account";
    private final int fromAccountId;
    private final int toAccountId;
    private final int amount;

    // HDD -> MYSQL -> TEST -> USER -> ALİ
    // RAM KEY-VALUE


    @Override
    public <K, V> List<Object> execute(RedisOperations<K, V> operations) throws DataAccessException {
        var op =  (RedisTemplate<Object, Object>)  operations;
        var hashOperations = op.opsForValue();

        var fromAccount = (Account) hashOperations.get(ACCOUNT + ":" + fromAccountId);
        var toAccount = (Account) hashOperations.get(ACCOUNT + ":" + toAccountId);
        if (Objects.isNull(fromAccount) || Objects.isNull(toAccount)) {
            System.out.println("Account not found");
            return null;
        }

        try {
            operations.multi();
            fromAccount.setMoney(fromAccount.getMoney() - amount);
            toAccount.setMoney(toAccount.getMoney() + amount);
            hashOperations.set(ACCOUNT + ":" + fromAccountId, fromAccount);
            hashOperations.set(ACCOUNT + ":" + toAccountId, toAccount);

            return operations.exec();
        }catch (Exception e){
            operations.discard();
            throw e;
        }
    }

    private void denemE() {
      //  throw new RuntimeException("deneme");
    }
}
