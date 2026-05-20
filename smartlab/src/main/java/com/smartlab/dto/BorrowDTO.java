package com.smartlab.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BorrowDTO {

    private Long equipmentId;
    private Integer quantity;
    private LocalDate expectedReturnDate;
}
