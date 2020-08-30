package com.chdlsp.patronpage.model.network.response;

import com.chdlsp.patronpage.model.enumClass.OpenYnStatus;
import com.chdlsp.patronpage.model.enumClass.ProjectStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectInfoResponse {

    private String projectName; // project 제목
    private String projectDesc; // project 설명
    private String artistName; // 창작자 이름
    private String artistEmail; // 창작자 이메일
    private String artistPhoneNumber; // 창작자 휴대폰번호
    private String projectStartTime; // 프로젝트 시작일
    private String projectEndTime; // 프로젝트 마감일
    public BigDecimal goalAmt; // 목표액
    private int patronUsers; // 후원수 (default : 0)
    private BigDecimal patronAmt; // 후원액 (default : 0)
    private OpenYnStatus openYn; // 공개여부 (default : PUBLIC)
    private ProjectStatus projectStatus; // 프로젝트 상태

}
