package com.chdlsp.patronpage.model.enumClass;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OpenYnStatus {
    PUBLIC(0, "Y", "공개"),
    PRIVATE(1, "N", "비공개");

    private Integer id;
    private String value;
    private String description;
}
