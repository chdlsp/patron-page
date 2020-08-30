package com.chdlsp.patronpage.model.entity;

import com.chdlsp.patronpage.model.enumClass.OpenYnStatus;
import com.chdlsp.patronpage.model.enumClass.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Accessors(chain = true)
public class ProjectEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID projectId; // project id

    private String projectName; // project 제목
    private String projectDesc; // project 설명
    private String artistName; // 창작자 이름
    private String artistEmail; // 창작자 이메일
    private String artistPhoneNumber; // 창작자 휴대폰번호

    private String projectStartTime; // 프로젝트 시작일
    private String projectEndTime; // 프로젝트 마감일

    private BigDecimal goalAmt; // 목표액
    private int patronUsers; // 후원수
    private BigDecimal patronAmt; // 후원액

    @Enumerated(EnumType.STRING)
    private OpenYnStatus openYn; // 공개여부

    @Enumerated(EnumType.STRING)
    private ProjectStatus projectStatus; // 프로젝트 상태

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
