package com.example.redistransaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor(staticName = "of")
public class Account implements Serializable {
    private String id;
    private Integer userId;
    private Integer money;
}
