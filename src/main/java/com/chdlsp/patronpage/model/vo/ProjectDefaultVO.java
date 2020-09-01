package com.chdlsp.patronpage.model.vo;

import com.chdlsp.patronpage.model.enumClass.OpenYnStatus;
import com.chdlsp.patronpage.model.enumClass.ProjectStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ProjectDefaultVO {

    private UUID projectId; // project ID

    @NotBlank(message = "프로젝트 제목을 입력해주세요.")
    @Size(max = 50, message = "프로젝트 제목의 최대 길이는 50자 입니다.")
    @Pattern(regexp="[a-zA-Z0-9가-힣]*$", message = "프로젝트 제목에는 특수문자를 사용할 수 없습니다.")
    private String projectName; // project 제목

    @NotBlank(message = "프로젝트 설명을 입력해주세요.")
    @Size(max = 255, message = "프로젝트 설명의 최대 길이는 255자 입니다.")
    private String projectDesc; // project 설명

    @NotBlank(message = "창작자 이름을 입력해주세요.")
    @Size(max = 20, message = "창작자 이름의 최대 길이는 20자 입니다.")
    @Pattern(regexp="[a-zA-Z0-9가-힣_]*$", message = "특수문자는 _ 만 허용됩니다.")
    private String artistName; // 창작자 이름

    @Email(message = "이메일 형식에 맞춰주세요.")
    private String artistEmail; // 창작자 이메일

    @Pattern(regexp="^01(?:0|1|[6-9])[-](\\d{3}|\\d{4})[-](\\d{4})$", message = "휴대폰번호 양식에 맞춰주세요. (010-1234-5678)")
    private String artistPhoneNumber; // 창작자 휴대폰번호

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String projectStartTime; // 프로젝트 시작일

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String projectEndTime; // 프로젝트 마감일

    @Max(value = 100000000)
    public BigDecimal goalAmt; // 목표액

    @Max(value = 100000)
    @Builder.Default
    private int patronUsers = 0; // 후원수 (default : 0)

    @Max(value = 100000000)
    @Builder.Default
    private BigDecimal patronAmt = BigDecimal.ZERO; // 후원액 (default : 0)

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OpenYnStatus openYn = OpenYnStatus.PUBLIC; // 공개여부 (default : PUBLIC)

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProjectStatus projectStatus = ProjectStatus.READY; // 프로젝트 상태

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
