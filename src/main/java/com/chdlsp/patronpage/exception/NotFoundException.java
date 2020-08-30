package com.chdlsp.patronpage.exception;

import java.util.UUID;

public class NotFoundException extends RuntimeException {

    public NotFoundException(UUID projectId) {
        super("프로젝트 ID를 찾을 수 없습니다. : " + projectId.toString());
    }

}
