package com.project.boardproject.controller;


import com.project.boardproject.config.TestSecurityConfig;
import com.project.boardproject.service.ArticleService;
import com.project.boardproject.service.PaginationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("View 컨트롤러 - 인증")
@Import(TestSecurityConfig.class)
@WebMvcTest(AuthControllerTest.EmptyController.class)
public class AuthControllerTest {

    private final MockMvc mvc;

    @MockBean private ArticleService articleService;
    @MockBean private PaginationService paginationService;


    AuthControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[view][GET] 로그인 페이지 - 정상 호출")
    @Test
    void givenNothing_whenTryLoggingIn_thenReturnLogInView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/login"))
                .andExpect(status().isOk()) // 200 Ok
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)); // TEXT_HTML 컨텐츠 타입
        then(articleService).shouldHaveNoInteractions();
        then(paginationService).shouldHaveNoInteractions();
    }

    /*
    * 어떤 컨트롤러도 필요하지 않은 테스트임을 나타내기 위해 테스트용 빈 컴포넌트를 사용함
    * */
    @TestComponent
    public class EmptyController {
    }
}
