package com.chdlsp.patronpage.service;

import com.chdlsp.patronpage.PatronPageApplicationTests;
import com.chdlsp.patronpage.model.entity.ProjectEntity;
import com.chdlsp.patronpage.model.enumClass.ProjectStatus;
import com.chdlsp.patronpage.repository.ProjectRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@SpringJUnitConfig
public class ProjectServiceTest extends PatronPageApplicationTests {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void createProject() {

        List<String> category = Arrays.asList("COMPUTER","CLOTHING","MULTI_SHOP","INTERIOR","FOOD","SPORTS","SHOPPING_MALL","DUTY_FREE","BEAUTY");
        List<String> title = Arrays.asList("컴퓨터-전자제품","의류","멀티샵","인테리어","음식","스포츠","쇼핑몰","면세점","화장");

        IntStream.range(0, 100).forEach(i -> {
            String categoryString = category.get(i % category.size());
            String titleString = title.get(i % title.size());
            String forEachSeq = String.format("%03d", i);

            ProjectEntity projectEntity = ProjectEntity.builder()
                    .projectName(categoryString.concat(forEachSeq))
                    .projectDesc(titleString.concat(forEachSeq))
                    .artistName("USER".concat(forEachSeq))
                    .artistEmail("USER".concat(forEachSeq).concat("@gmail.com"))
                    .artistPhoneNumber("010-1234-".concat(String.format("%04d", i)))
                    .projectStartTime(LocalDateTime.now())
                    .projectEndTime(LocalDateTime.now().plusMinutes(i * 10))
                    .goalAmt(BigDecimal.valueOf(i * 100000L))
                    .patronUsers(0)
                    .patronAmt(BigDecimal.ZERO)
                    .projectStatus(ProjectStatus.READY)
                    .build();

            ProjectEntity projectEntityNew = projectRepository.save(projectEntity);
            Assert.assertNotNull(projectEntityNew);

        });
    }
}