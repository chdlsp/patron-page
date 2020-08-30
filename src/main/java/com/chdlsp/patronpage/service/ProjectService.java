package com.chdlsp.patronpage.service;

import com.chdlsp.patronpage.exception.NotFoundException;
import com.chdlsp.patronpage.model.entity.ProjectEntity;
import com.chdlsp.patronpage.model.enumClass.OpenYnStatus;
import com.chdlsp.patronpage.model.enumClass.ProjectStatus;
import com.chdlsp.patronpage.model.vo.ProjectPatronVO;
import com.chdlsp.patronpage.model.vo.ProjectStatusVO;
import com.chdlsp.patronpage.model.network.request.SupportRequest;
import com.chdlsp.patronpage.model.network.response.ProjectAllResponse;
import com.chdlsp.patronpage.model.network.response.ProjectInfoResponse;
import com.chdlsp.patronpage.model.network.response.ProjectResultResponse;
import com.chdlsp.patronpage.model.vo.ProjectVO;
import com.chdlsp.patronpage.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

@Service
@Transactional
@Slf4j
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    // 프로젝트 등록
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

    // 시스템은 프로젝트 상태를 업데이트 합니다.
    public ProjectStatus updateCurrentProjectStatus(ProjectStatusVO projectStatusVO,
                                                    ProjectPatronVO projectPatronVO) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(projectStatusVO.getProjectStartTime(), dateTimeFormatter);
        String nowTimeString = LocalDateTime.now().format(dateTimeFormatter);
        LocalDateTime nowDateTime = LocalDateTime.parse(nowTimeString, dateTimeFormatter);
        LocalDateTime endTime = LocalDateTime.parse(projectStatusVO.getProjectEndTime(), dateTimeFormatter);

        ProjectStatus projectStatus = projectStatusVO.getCurrentStatus();

        if(nowDateTime.isBefore(startTime)) {
            // 준비중(READY) : 현재시간 < 프로젝트 시작일
            projectStatus = ProjectStatus.READY;
        } else if (startTime.isBefore(nowDateTime) || startTime.isEqual(nowDateTime)) {
            if (nowDateTime.isBefore(endTime)) {
                // 진행중(RUNNING) : 프로젝트 시작일 ≤ 현재시간 < 프로젝트 마감일
                projectStatus = ProjectStatus.RUNNING;
            }
        }

        // 후원받은 금액이 있는 경우 @TODO SUCCESS, FAILURE 로직 체크 필요
        if(projectPatronVO.getCurrentAmount().compareTo(BigDecimal.ZERO) > 0) {

            log.info("projectPatronVO : " + projectPatronVO.toString());
            log.info("time Info : " + nowDateTime.isAfter(endTime));

            if(nowDateTime.isAfter(endTime)) {
                if(projectPatronVO.getCurrentAmount().compareTo(projectPatronVO.getGoalAmount()) < 0) {
                    projectStatus = ProjectStatus.FAILURE;
                } else {
                    projectStatus = ProjectStatus.SUCCESS;
                }
            }
        }

        // 프로젝트 상태 조회 값이 변경된 경우 update 처리
        if(!projectStatusVO.getCurrentStatus().equals(projectStatus)) {

            // @TODO : PK인 UUID 시 UPDATE 처리가 제대로 되지 않아 확인 필요
            Optional<ProjectEntity> selectEntity = projectRepository.findByProjectId(projectStatusVO.getProjectId());

            ProjectEntity projectEntity = ProjectEntity.builder()
                    .projectId(projectStatusVO.getProjectId())
                    .projectName(selectEntity.get().getProjectName())
                    .projectDesc(selectEntity.get().getProjectDesc())
                    .artistName(selectEntity.get().getArtistName())
                    .artistEmail(selectEntity.get().getArtistEmail())
                    .artistPhoneNumber(selectEntity.get().getArtistPhoneNumber())
                    .projectStartTime(projectStatusVO.getProjectStartTime())
                    .projectEndTime(projectStatusVO.getProjectEndTime())
                    .goalAmt(selectEntity.get().getGoalAmt())
                    .patronUsers(selectEntity.get().getPatronUsers())
                    .patronAmt(selectEntity.get().getPatronAmt())
                    .openYn(selectEntity.get().getOpenYn())
                    .projectStatus(projectStatus)
                    .createdAt(selectEntity.get().getCreatedAt())
                    .updatedAt(nowDateTime)
                    .build();

            projectRepository.save(projectEntity);
        }

        return projectStatus;
    }

    // 공개된 프로젝트 조회 (paging 단위 : 10개)
    public List<ProjectAllResponse> getAllProject(Pageable pageable) {

        Page<ProjectEntity> project = projectRepository.findAll(pageable);

        List<ProjectAllResponse> result = new ArrayList<>();

        for(ProjectEntity selectEntity : project) {

            ProjectStatus currentStatus = selectEntity.getProjectStatus();
            UUID projectId = selectEntity.getProjectId();
            String projectStartTime = selectEntity.getProjectStartTime();
            String projectEndTime = selectEntity.getProjectEndTime();

            // 프로젝트 상태정보, 시간정보 세팅
            ProjectStatusVO projectStatusVO = ProjectStatusVO.builder()
                    .currentStatus(currentStatus)
                    .projectId(projectId)
                    .projectStartTime(projectStartTime)
                    .projectEndTime(projectEndTime)
                    .build();

            // 시스템은 프로젝트 상태를 업데이트 합니다.
            ProjectStatus projectStatus = this.updateCurrentProjectStatus(projectStatusVO,
                    new ProjectPatronVO(BigDecimal.ZERO, BigDecimal.ZERO));

            ProjectAllResponse projectAllResponse = ProjectAllResponse.builder()
                    .projectName(selectEntity.getProjectName())
                    .artistName(selectEntity.getArtistName())
                    .projectStartTime(projectStartTime)
                    .projectEndTime(projectEndTime)
                    .goalAmt(selectEntity.getGoalAmt())
                    .patronUsers(selectEntity.getPatronUsers())
                    .patronAmt(selectEntity.getPatronAmt())
                    .projectStatus(projectStatus)
                    .build();
            
            result.add(projectAllResponse);
        }

        return result;
    }

    // 특정 프로젝트 정보 조회
    public ProjectInfoResponse getProjectInfo(UUID projectId) {

        Optional<ProjectEntity> projectEntityByProjectId = projectRepository.findByProjectId(projectId);

        if(!projectEntityByProjectId.isPresent()) {
            throw new NotFoundException(projectId);
        }
        ProjectStatus currentStatus = projectEntityByProjectId.get().getProjectStatus();
        String projectStartTime = projectEntityByProjectId.get().getProjectStartTime();
        String projectEndTime = projectEntityByProjectId.get().getProjectEndTime();

        // 프로젝트 상태정보, 시간정보 세팅
        ProjectStatusVO projectStatusVO = ProjectStatusVO.builder()
                .currentStatus(currentStatus)
                .projectId(projectId)
                .projectStartTime(projectStartTime)
                .projectEndTime(projectEndTime)
                .build();

        // 시스템은 프로젝트 상태를 업데이트 합니다.
        ProjectStatus projectStatus = this.updateCurrentProjectStatus(projectStatusVO,
                new ProjectPatronVO(BigDecimal.ZERO, BigDecimal.ZERO));

        ProjectInfoResponse result = ProjectInfoResponse.builder()
                .projectName(projectEntityByProjectId.get().getProjectName())
                .projectDesc(projectEntityByProjectId.get().getProjectDesc())
                .artistName(projectEntityByProjectId.get().getArtistName())
                .artistEmail(projectEntityByProjectId.get().getArtistEmail())
                .artistPhoneNumber(projectEntityByProjectId.get().getArtistPhoneNumber())
                .projectStartTime(projectStartTime)
                .projectEndTime(projectEndTime)
                .goalAmt(projectEntityByProjectId.get().getGoalAmt())
                .patronUsers(projectEntityByProjectId.get().getPatronUsers())
                .patronAmt(projectEntityByProjectId.get().getPatronAmt())
                .openYn(projectEntityByProjectId.get().getOpenYn())
                .projectStatus(projectStatus)
                .build();

        return result;
    }

    // 프로젝트 후원
    public ProjectResultResponse sponsorProject(SupportRequest supportRequest) {

        UUID projectId = supportRequest.getProjectId();
        Optional<ProjectEntity> projectEntityByProjectId = projectRepository.findByProjectId(projectId);

        ProjectStatus currentStatus = projectEntityByProjectId.get().getProjectStatus();
        String projectStartTime = projectEntityByProjectId.get().getProjectStartTime();
        String projectEndTime = projectEntityByProjectId.get().getProjectEndTime();

        BigDecimal currentAmount = projectEntityByProjectId.get().getPatronAmt(); // 현재 누적 후원금
        BigDecimal supportAmount = supportRequest.getSponsorAmt(); // 합산 예정 후원금

        projectEntityByProjectId.get().setPatronAmt(currentAmount.add(supportAmount)); // 후원금 합산
        projectEntityByProjectId.get().setPatronUsers(projectEntityByProjectId.get().getPatronUsers() + 1); // 후원자 증가

        // 후원금 UPDATE
        projectRepository.save(projectEntityByProjectId.get());

        // 프로젝트 후원정보 세팅
        ProjectPatronVO projectPatronVO = ProjectPatronVO.builder()
                .currentAmount(projectEntityByProjectId.get().getPatronAmt())
                .goalAmount(projectEntityByProjectId.get().getGoalAmt())
                .build();

        // 프로젝트 상태정보, 시간정보 세팅
        ProjectStatusVO projectStatusVO = ProjectStatusVO.builder()
                .currentStatus(currentStatus)
                .projectId(projectId)
                .projectStartTime(projectStartTime)
                .projectEndTime(projectEndTime)
                .build();

        // 시스템은 프로젝트 상태를 업데이트 합니다.
        this.updateCurrentProjectStatus(projectStatusVO, projectPatronVO);

        ProjectResultResponse result = ProjectResultResponse.builder()
                .code("1000")
                .message("후원 처리에 성공했습니다.")
                .build();

        return result;
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
                    .openYn(OpenYnStatus.PUBLIC)
                    .createdAt(LocalDateTime.now())
                    .build();

            projectRepository.save(projectEntity);

        });

        result.setCode("1000");
        result.setMessage("DUMMY 생성에 성공했습니다.");

        return result;
    }
}
