package com.chdlsp.patronpage.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectPatronVO {

    BigDecimal currentAmount; // 누적 금액(이번 회차에 후원받은 금액 포함)
    BigDecimal goalAmount; // 목표 금액

}
