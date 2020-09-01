package com.chdlsp.patronpage.controller;

import com.chdlsp.patronpage.exception.SupportAmountException;
import com.chdlsp.patronpage.model.network.request.SupportRequest;
import com.chdlsp.patronpage.model.network.response.ProjectAllResponse;
import com.chdlsp.patronpage.model.network.response.ProjectInfoResponse;
import com.chdlsp.patronpage.model.network.response.ProjectResultResponse;
import com.chdlsp.patronpage.model.vo.ProjectDefaultVO;
import com.chdlsp.patronpage.model.vo.ProjectUUIDVO;
import com.chdlsp.patronpage.service.ProjectService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
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
    @ApiOperation(value = "프로젝트 등록", notes = "신규 프로젝트를 등록합니다. \n 성공 CODE : 1000 \n 실패 CODE : 3000(데이터 Valid Check)")
    @PostMapping
    public ProjectResultResponse createProject(@RequestBody @Valid ProjectDefaultVO projectDefaultVO, BindingResult bindingResult) {

        ProjectResultResponse result = new ProjectResultResponse();

        if(bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            result.setCode("3000");
            result.setMessage(fieldError.getDefaultMessage());
        } else {
            result = projectService.createProject(projectDefaultVO);
        }
        return result;
    }

    // 프로젝트 수정 => front 단에서 사용자가 클릭한 프로젝트의 projectId (UUID) 값이 넘어온다고 가정한다.
    @ApiOperation(value = "프로젝트 수정", notes = "등록된 프로젝트를 수정합니다. \n" +
            " 성공 CODE : 1000 \n" +
            " 실패 CODE : 2010(DB Update 시 에러), 3000(데이터 Valid Check), 4000(프로젝트 ID 미존재)")
    @PutMapping
    public ProjectResultResponse updateProject(@RequestBody @Valid ProjectDefaultVO projectDefaultVO, BindingResult bindingResult) {

        log.info("updateProject projectVO UUID : " + projectDefaultVO.getProjectId());

        ProjectResultResponse result = new ProjectResultResponse();

        if(bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            result.setCode("3000");
            result.setMessage(fieldError.getDefaultMessage());
        } else {
            result = projectService.updateProject(projectDefaultVO);
        }

        return result;
    }

    // 프로젝트 삭제 => front 단에서 사용자가 클릭한 프로젝트의 projectId (UUID) 값이 넘어온다고 가정한다.
    @ApiOperation(value = "프로젝트 삭제", notes = "등록된 프로젝트를 삭제합니다. \n" +
            " 성공 CODE : 1000 \n" +
            " 실패 CODE : 2020(DB Delete 시 에러), 3000(데이터 Valid Check), 4000(프로젝트 ID 미존재)")
    @DeleteMapping
    public ProjectResultResponse deleteProject(@RequestBody ProjectUUIDVO projectUUIDVO) {

        log.info("deleteProject projectId UUID : " + projectUUIDVO.toString());

        // projectId null 여부 체크에 따른 메세지 리턴
        Optional<UUID> isExistsId = Optional.ofNullable(projectUUIDVO.getProjectId());

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
    // swagger pageable 처리 참고 : https://github.com/springfox/springfox/issues/2623
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Sorting criteria in the format: property(,asc|desc). " +
                            "Default sort order is ascending. " +
                            "Multiple sort criteria are supported.")
    })
    @ApiOperation(value = "프로젝트 조회", notes = "공개된 프로젝트를 조회합니다. (paging 단위 : 10개 => PageableDefault size default = 10)")
    @GetMapping
    public List<ProjectAllResponse> getAllProject(@PageableDefault Pageable pageable) {

        log.info("pageable : {}", pageable);

        List<ProjectAllResponse> result = projectService.getAllProject(pageable);

        return result;
    }

    // 특정 프로젝트 정보 조회
    @GetMapping("/info")
    @ApiOperation(value = "특정 프로젝트 조회", notes = "등록된 특정 프로젝트를 조회합니다.")
    public ProjectInfoResponse getProjectInfo(ProjectUUIDVO projectUUIDVO) {

        log.info("projectId : " + projectUUIDVO.getProjectId());
        ProjectInfoResponse result = projectService.getProjectInfo(projectUUIDVO.getProjectId());

        return result;
    }

    // 프로젝트 후원
    @PutMapping("/support")
    @ApiOperation(value = "프로젝트 후원", notes = "등록된 프로젝트를 수정합니다. \n" +
            " 성공 CODE : 1000 \n" +
            " 실패 CODE : 3000(후원금액 Data 검증), 5000(후원금액 초과), 5010(후원자 초과)")
    public ProjectResultResponse sponsorProject(@RequestBody @Valid SupportRequest supportRequest, BindingResult bindingResult) {

        ProjectResultResponse result = new ProjectResultResponse();

        if(bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            result.setCode("3000");
            result.setMessage(fieldError.getDefaultMessage());
        } else {
            // 후원 입력 받은 금액이 0원 이하인 경우
            if(supportRequest.getSponsorAmt().compareTo(BigDecimal.ZERO) <= 0) {
                throw new SupportAmountException(supportRequest.getSponsorAmt());
            } else {
                result = projectService.sponsorProject(supportRequest);
            }
        }

        return result;
    }

    @PostMapping("/dummy")
    @ApiOperation(value = "더미 프로젝트 생성", notes = "더미 프로젝트를 생성합니다. (100건)")
    public ProjectResultResponse createDummyProject() {

        ProjectResultResponse result = projectService.createDummyProject();

        return result;
    }

}
