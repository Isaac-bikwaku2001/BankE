package com.example.ebankbackend.dtos;

import lombok.Data;

@Data
public class TransfertRequestDTO {
    private String accountIdSource;
    private String accountIdDestination;
    private double amount;
}
