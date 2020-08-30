package com.chdlsp.patronpage.service;

import com.chdlsp.patronpage.PatronPageApplicationTests;
import com.chdlsp.patronpage.model.entity.ProjectEntity;
import com.chdlsp.patronpage.model.enumClass.ProjectStatus;
import com.chdlsp.patronpage.repository.ProjectRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@SpringJUnitConfig
@Slf4j
public class ProjectServiceTest extends PatronPageApplicationTests {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void createDummyProject() {

        List<String> category = Arrays.asList("COMPUTER", "CLOTHING", "MULTI_SHOP", "INTERIOR", "FOOD", "SPORTS", "SHOPPING_MALL", "DUTY_FREE", "BEAUTY");
        List<String> title = Arrays.asList("컴퓨터", "의류", "멀티샵", "인테리어", "음식", "스포츠", "쇼핑몰", "면세점", "화장");

        IntStream.range(0, 10).forEach(i -> {

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

            ProjectEntity projectEntityNew = projectRepository.save(projectEntity);
            Assert.assertNotNull(projectEntityNew);
            System.out.println("projectEntityNew : " + projectEntityNew.toString());

        });
    }
}