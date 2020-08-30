package com.chdlsp.patronpage.controller;


import com.chdlsp.patronpage.model.entity.ProjectEntity;
import com.chdlsp.patronpage.model.network.request.SupportRequest;
import com.chdlsp.patronpage.model.network.response.ProjectResultResponse;
import com.chdlsp.patronpage.model.vo.ProjectVO;
import com.chdlsp.patronpage.model.vo.DeleteProjectVO;
import com.chdlsp.patronpage.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* 사용자 Activity 를 처리하는 컨트롤러 */

@Slf4j
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // 프로젝트 등록
    @PostMapping
    public ProjectResultResponse createProject(@RequestBody @Valid ProjectVO projectVO, BindingResult bindingResult) {

        ProjectResultResponse result = new ProjectResultResponse();

        if(bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            result.setCode("3000");
            result.setMessage(fieldError.getDefaultMessage());
        } else {
            result = projectService.createProject(projectVO);
        }
        return result;
    }

    // 프로젝트 수정
    @PutMapping
    public ProjectResultResponse updateProject(@RequestBody @Valid ProjectVO projectVO, BindingResult bindingResult) {

        log.info("updateProject projectVO UUID : " + projectVO.getProjectId());

        ProjectResultResponse result = new ProjectResultResponse();

        if(bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            result.setCode("3000");
            result.setMessage(fieldError.getDefaultMessage());
        } else {
            result = projectService.updateProject(projectVO);
        }

        return result;
    }

    // 프로젝트 삭제 => front 단에서 사용자가 클릭한 프로젝트의 projectId (UUID) 값이 넘어온다고 가정한다.
    @DeleteMapping
    public ProjectResultResponse deleteProject(@RequestBody DeleteProjectVO deleteProjectVO) {

        log.info("deleteProject projectId UUID : " + deleteProjectVO.toString());

        // projectId null 여부 체크에 따른 메세지 리턴
        Optional<UUID> isExistsId = Optional.ofNullable(deleteProjectVO.getProjectId());

        return isExistsId.map(selectData -> {
            ProjectResultResponse projectResultResponse = projectService.deleteProject(selectData);
            return projectResultResponse;
        }).orElseGet(() -> {
            ProjectResultResponse result = new ProjectResultResponse();
            result.setCode("4000");
            result.setMessage("선택한 프로젝트 ID 값이 없습니다.");
            return result;
        });

    }

    // 공개된 프로젝트 조회 (paging 단위 : 10개 => PageableDefault size default = 10)
    @GetMapping
    public List<ProjectVO> getAllProject(@RequestParam String orderCode, @PageableDefault Pageable pageable) {

        /* orderCode
        * 0 : none
        * 1 : 시작일 순
        * 2 : 마감일 순
        * 3 : 목표액 순
        * 4 : 후원액 순
        * 기타 : 에러 */

        List<ProjectVO> result = projectService.getAllProject(orderCode, pageable);

        return result;
    }

    // 특정 프로젝트 정보 조회
    @GetMapping("/info")
    public ProjectVO getProjectInfo(@RequestParam UUID projectId) {

        ProjectVO result = projectService.getProjectInfo(projectId);

        return result;
    }

    // 프로젝트 후원
    @PutMapping("/support")
    public ProjectResultResponse sponsorProject(@RequestBody SupportRequest supportRequest) {

        ProjectResultResponse result = projectService.sponsorProject(supportRequest);

        return result;
    }

    @GetMapping("/dummy")
    public ProjectResultResponse createDummyProject() {

        ProjectResultResponse result = projectService.createDummyProject();

        return result;
    }

}
