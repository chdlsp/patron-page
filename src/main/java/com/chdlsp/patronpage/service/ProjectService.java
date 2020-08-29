package com.chdlsp.patronpage.service;

import com.chdlsp.patronpage.model.network.request.SupportRequest;
import com.chdlsp.patronpage.model.network.response.ProjectResponse;
import com.chdlsp.patronpage.model.vo.ProjectVO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    // 프로젝트 등록
    public ProjectResponse createProject(ProjectVO projectVO) {

        return null;
    }

    // 프로젝트 수정
    public ProjectResponse updateProject(ProjectVO projectVO) {

        return null;
    }

    // 프로젝트 삭제 => front 단에서 사용자가 클릭한 프로젝트의 projectId (UUID) 값이 넘어온다고 가정한다.
    public ProjectResponse deleteProject(String projectId) {

        return null;
    }

    // 공개된 프로젝트 조회 (paging 단위 : 10개)
    public ProjectVO getAllProject(String orderCode, Pageable pageable) {

        /* orderCode
         * 0 : none
         * 1 : 시작일 순
         * 2 : 마감일 순
         * 3 : 목표액 순
         * 4 : 후원액 순
         * 기타 : 에러 */

        return null;
    }

    // 특정 프로젝트 정보 조회
    public ProjectVO getProjectInfo(String projectId) {

        return null;
    }

    // 프로젝트 후원
    public ProjectResponse sponsorProject(SupportRequest supportRequest) {

        return null;
    }

}
