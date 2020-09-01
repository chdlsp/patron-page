package com.chdlsp.patronpage.model.network.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupportRequest {

    UUID projectId;

    @Max(value = 100000000)
    BigDecimal sponsorAmt;

}
