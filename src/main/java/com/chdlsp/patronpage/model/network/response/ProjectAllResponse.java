package com.chdlsp.patronpage.model.network.response;

import com.chdlsp.patronpage.model.enumClass.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectAllResponse {

    private String projectName; // 프로젝트 제목
    private String artistName; // 창작자 이름
    public BigDecimal goalAmt; // 목표액
    private int patronUsers; // 후원수
    private BigDecimal patronAmt; // 후원액
    private ProjectStatus projectStatus; // 프로젝트 상태
    private String projectStartTime; // 프로젝트 시작일
    private String projectEndTime; // 프로젝트 시작일

}
