package com.chdlsp.patronpage.model.vo;

import com.chdlsp.patronpage.model.enumClass.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectStatusVO {

    ProjectStatus currentStatus;
    UUID projectId;
    String projectStartTime;
    String projectEndTime;
}
