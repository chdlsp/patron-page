package com.chdlsp.patronpage.service;

import com.chdlsp.patronpage.model.entity.ProjectEntity;
import com.chdlsp.patronpage.model.enumClass.ProjectStatus;
import com.chdlsp.patronpage.model.network.request.SupportRequest;
import com.chdlsp.patronpage.model.network.response.ProjectResultResponse;
import com.chdlsp.patronpage.model.vo.ProjectVO;
import com.chdlsp.patronpage.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    // 프로젝트 등록
    @Transactional
    public ProjectResultResponse createProject(ProjectVO projectVO) {

        ProjectEntity projectEntity = ProjectEntity.builder()
                .projectName(projectVO.getProjectName())
                .projectDesc(projectVO.getProjectDesc())
                .artistName(projectVO.getArtistName())
                .artistEmail(projectVO.getArtistEmail())
                .artistPhoneNumber(projectVO.getArtistPhoneNumber())
                .projectStartTime(projectVO.getProjectStartTime())
                .projectEndTime(projectVO.getProjectEndTime())
                .goalAmt(projectVO.getGoalAmt())
                .openYn(projectVO.getOpenYn())
                .patronAmt(projectVO.getPatronAmt())
                .patronUsers(projectVO.getPatronUsers())
                .projectStatus(ProjectStatus.READY)
                .createdAt(LocalDateTime.now())
                .build();

        ProjectResultResponse result = new ProjectResultResponse();

        try {
            projectRepository.save(projectEntity);
            result.setCode("1000");
            result.setMessage("DB Insert 성공");
        } catch (Exception e) {
            result.setCode("2000");
            result.setMessage("DB Insert 중 에러가 발생했습니다.");
        }

        return result;
    }

    // 프로젝트 수정
    public ProjectResultResponse updateProject(ProjectVO projectVO) {

        ProjectEntity projectEntity = ProjectEntity.builder()
                .projectId(projectVO.getProjectId())
                .projectName(projectVO.getProjectName())
                .projectDesc(projectVO.getProjectDesc())
                .artistName(projectVO.getArtistName())
                .artistEmail(projectVO.getArtistEmail())
                .artistPhoneNumber(projectVO.getArtistPhoneNumber())
                .projectStartTime(projectVO.getProjectStartTime())
                .projectEndTime(projectVO.getProjectEndTime())
                .goalAmt(projectVO.getGoalAmt())
                .patronUsers(projectVO.getPatronUsers())
                .patronAmt(projectVO.getPatronAmt())
                .openYn(projectVO.getOpenYn())
                .projectStatus(projectVO.getProjectStatus())
                .updatedAt(LocalDateTime.now())
                .build();

        ProjectResultResponse result = new ProjectResultResponse();

        try {
            projectRepository.save(projectEntity);
            result.setCode("1000");
            result.setMessage("DB Update 성공");
        } catch (Exception e) {
            result.setCode("2010");
            result.setMessage("DB Update 중 에러가 발생했습니다.");
        }

        return result;
    }

    // 프로젝트 삭제 => front 단에서 사용자가 클릭한 프로젝트의 projectId (UUID) 값이 넘어온다고 가정한다.
    public ProjectResultResponse deleteProject(UUID projectId) {

        ProjectResultResponse result = new ProjectResultResponse();
        Optional<ProjectEntity> isExistsId = projectRepository.findByProjectId(projectId);

        return isExistsId.map(selectData -> {
            ProjectEntity projectEntity = ProjectEntity.builder()
                    .projectId(projectId)
                    .build();

            try {
                projectRepository.delete(projectEntity);
                result.setCode("1000");
                result.setMessage("DB Delete 성공");
            } catch (Exception e) {
                result.setCode("2020");
                result.setMessage("DB Delete 중 에러가 발생했습니다.");
            }

            return result;
        }).orElseGet(() -> {
            result.setCode("2021");
            result.setMessage("존재하지 않는 프로젝트 입니다.");

            return result;
        });
    }

    // 공개된 프로젝트 조회 (paging 단위 : 10개)
    @Transactional
    public List<ProjectVO> getAllProject(String orderCode, Pageable pageable) {

        /* orderCode
         * 1 : 시작일 순
         * 2 : 마감일 순
         * 3 : 목표액 순
         * 4 : 후원액 순
         * 기타 : 기본정렬 */

        // Pageable sortedByName = PageRequest.of(0, 3, Sort.by("name"));
        List<ProjectEntity> project;

        // @TODO findALl 하나로 커버가 가능할 것 같은데 테스트 필요...
        if("1".equals(orderCode)) {
            project = projectRepository.findAll(Sort.by("projectStartTime"));
        } else if("2".equals(orderCode)) {
            project = projectRepository.findAll(Sort.by("projectEndTime"));
        } else if("3".equals(orderCode)) {
            project = projectRepository.findAll(Sort.by("goalAmt"));
        } else if("4".equals(orderCode)) {
            project = projectRepository.findAll(Sort.by("patronAmt"));
        } else {
            project = projectRepository.findAll();
        }

        List<ProjectVO> result = new ArrayList<>();

        for(ProjectEntity selectEntity : project) {
            ProjectVO projectVO = ProjectVO.builder()
                    .projectName(selectEntity.getProjectName())
                    .projectDesc(selectEntity.getProjectDesc())
                    .artistName(selectEntity.getArtistName())
                    .artistEmail(selectEntity.getArtistEmail())
                    .artistPhoneNumber(selectEntity.getArtistPhoneNumber())
                    .projectStartTime(selectEntity.getProjectStartTime())
                    .projectEndTime(selectEntity.getProjectEndTime())
                    .goalAmt(selectEntity.getGoalAmt())
                    .patronUsers(selectEntity.getPatronUsers())
                    .patronAmt(selectEntity.getPatronAmt())
                    .openYn(selectEntity.getOpenYn())
                    .projectStatus(selectEntity.getProjectStatus())
                    .build();
            
            result.add(projectVO);
        }

        return result;
    }

    // 특정 프로젝트 정보 조회
    public ProjectVO getProjectInfo(UUID projectId) {

        Optional<ProjectEntity> projectEntityByProjectId = projectRepository.findByProjectId(projectId);
        ProjectVO result = ProjectVO.builder()
                .projectName(projectEntityByProjectId.get().getProjectName())
                .projectDesc(projectEntityByProjectId.get().getProjectDesc())
                .artistName(projectEntityByProjectId.get().getArtistName())
                .artistEmail(projectEntityByProjectId.get().getArtistEmail())
                .artistPhoneNumber(projectEntityByProjectId.get().getArtistPhoneNumber())
                .projectStartTime(projectEntityByProjectId.get().getProjectStartTime())
                .projectEndTime(projectEntityByProjectId.get().getProjectEndTime())
                .goalAmt(projectEntityByProjectId.get().getGoalAmt())
                .patronUsers(projectEntityByProjectId.get().getPatronUsers())
                .patronAmt(projectEntityByProjectId.get().getPatronAmt())
                .openYn(projectEntityByProjectId.get().getOpenYn())
                .projectStatus(projectEntityByProjectId.get().getProjectStatus())
                .build();

        return result;
    }

    // 프로젝트 후원
    public ProjectResultResponse sponsorProject(SupportRequest supportRequest) {

        return null;
    }

    // dummy project 생성 용 서비스
    public ProjectResultResponse createDummyProject() {

        ProjectResultResponse result = new ProjectResultResponse();

        List<String> category = Arrays.asList("COMPUTER", "CLOTHING", "MULTI_SHOP", "INTERIOR", "FOOD", "SPORTS", "SHOPPING_MALL", "DUTY_FREE", "BEAUTY");
        List<String> title = Arrays.asList("컴퓨터", "의류", "멀티샵", "인테리어", "음식", "스포츠", "쇼핑몰", "면세점", "화장");

        IntStream.range(0, 100).forEach(i -> {

            String categoryString = category.get(i % category.size());
            String titleString = title.get(i % title.size());
            String forEachSeq = String.format("%03d", i);

            LocalDateTime nowTime = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            ProjectEntity projectEntity = ProjectEntity.builder()
                    .projectName(categoryString.concat(forEachSeq))
                    .projectDesc(titleString.concat(forEachSeq))
                    .artistName("USER".concat(forEachSeq))
                    .artistEmail("USER".concat(forEachSeq).concat("@gmail.com"))
                    .artistPhoneNumber("0101234".concat(String.format("%04d", i)))
                    .projectStartTime(nowTime.format(dateTimeFormatter))
                    .projectEndTime(nowTime.plusHours(i * 10).format(dateTimeFormatter))
                    .goalAmt(BigDecimal.valueOf(i * 100000L))
                    .patronUsers(0)
                    .patronAmt(BigDecimal.ZERO)
                    .projectStatus(ProjectStatus.READY)
                    .createdAt(LocalDateTime.now())
                    .build();
        });

        result.setCode("1000");
        result.setMessage("DUMMY 생성에 성공했습니다.");

        return result;
    }
}
