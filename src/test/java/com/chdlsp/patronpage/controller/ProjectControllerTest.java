package com.chdlsp.patronpage.controller;

import com.chdlsp.patronpage.model.vo.ProjectDefaultVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ProjectController.class})
public class ProjectControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createProject() throws Exception {

        ProjectDefaultVO request = ProjectDefaultVO.builder()
                .projectName("COMPUTER000")
                .projectDesc("컴퓨터000")
                .artistName("USER000")
                .artistEmail("USER000@gmail.com")
                .artistPhoneNumber("010-1234-0000")
                .projectStartTime("2020-08-30 17:17:05")
                .projectEndTime("2020-08-30 17:17:25")
                .goalAmt(BigDecimal.valueOf(100000))
                .build();

        System.out.println("request : " + request.toString());
        String body = objectMapper.writeValueAsString(request.toString());

        try {
            mvc.perform(post("/project")
                    .contentType("application/json")
                    .content(body))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}