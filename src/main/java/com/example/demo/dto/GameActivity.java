package com.example.demo.dto;

import lombok.Data;

@Data
public class GameActivity {
    Integer id;
    Integer playerId;
    Double betAmount;
    Double winAmount;
    String currency;
}
