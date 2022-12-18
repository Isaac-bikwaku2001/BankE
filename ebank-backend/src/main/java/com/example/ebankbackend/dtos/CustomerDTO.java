package com.example.ebankbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CustomerDTO {
    private Long id;
    private String nom;
    private String email;
}
