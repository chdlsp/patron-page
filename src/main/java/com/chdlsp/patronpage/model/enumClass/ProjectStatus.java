package com.chdlsp.patronpage.model.enumClass;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProjectStatus {
    READY(0, "준비중", "준비중"),
    RUNNING(1, "진행중", "진행중"),
    SUCCESS(2, "성공", "성공"),
    FAILURE(3, "실패", "실패");

    private Integer id;
    private String value;
    private String description;
}
