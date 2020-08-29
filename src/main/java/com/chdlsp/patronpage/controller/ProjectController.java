package com.chdlsp.patronpage.controller;


import com.chdlsp.patronpage.model.network.request.SupportRequest;
import com.chdlsp.patronpage.model.network.response.ProjectResponse;
import com.chdlsp.patronpage.model.vo.ProjectVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/* 사용자 Activity 를 처리하는 컨트롤러 */

@Slf4j
@RestController
@RequestMapping("/project")
public class ProjectController {

    // 프로젝트 등록
    @PostMapping
    public ProjectResponse createProject(@RequestBody ProjectVO projectVO) {

        return null;
    }

    // 프로젝트 수정
    @PutMapping
    public ProjectResponse updateProject(@RequestBody ProjectVO projectVO) {

        return null;
    }

    // 프로젝트 삭제 => front 단에서 사용자가 클릭한 프로젝트의 projectId (UUID) 값이 넘어온다고 가정한다.
    @DeleteMapping
    public ProjectResponse deleteProject(@RequestParam String projectId) {

        return null;
    }

    // 공개된 프로젝트 조회 (paging 단위 : 10개)
    @GetMapping
    public ProjectVO getAllProject(@RequestParam String orderCode, @PageableDefault Pageable pageable) {

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
    @GetMapping("/info")
    public ProjectVO getProjectInfo(@RequestParam String projectId) {

        return null;
    }

    // 프로젝트 후원
    @PutMapping("/support")
    public ProjectResponse sponsorProject(@RequestBody SupportRequest supportRequest) {

        return null;
    }

}
