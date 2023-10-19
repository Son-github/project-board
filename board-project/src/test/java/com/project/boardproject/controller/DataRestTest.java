package com.project.boardproject.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("Spring Data REST 통합테스트는 불필요하기 때문에 제외")
@DisplayName("Data REST-api 테스트")
@Transactional // Test에서 동작하는 기능들 RollBack
@AutoConfigureMockMvc
@SpringBootTest
class DataRestTest {

    private final MockMvc mvc;

    @Autowired
    DataRestTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    /*
      여기서 @Autowired 어노테이션이 없다면 밑의 에러가 뜬다
      No ParameterResolver registered for parameter [org.springframework.test.web.servlet.MockMvc mvc] in constructor [public com.project.boardproject.controller.DataRestTest(org.springframework.test.web.servlet.MockMvc)]. at org.junit.jupiter.engine.execution.ExecutableInvoker.resolveParameter(ExecutableInvoker.java:200) at org.junit.jupiter.engine.execution.ExecutableInvoker.resolveParameters(ExecutableInvoker.java:183) at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:74) at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.invokeTestClassConstructor(ClassBasedTestDescriptor.java:355) at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.instantiateTestClass(ClassBasedTestDescriptor.java:302) at org.junit.jupiter.engine.descriptor.ClassTestDescriptor.instantiateTestClass(ClassTestDescriptor.java:79) at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.instantiateAndPostProcessTestInstance(ClassBasedTestDescriptor.java:280) at org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor.lambda$testInstancesProvider$4(ClassBasedTestDescriptor.java:272)
      그 이유는 @SpringBootTest는 SpringBoot와 비슷한 환경을 만들어 주는 것일 뿐이기 때문에
      @Autowired가 없다면 junit은 객체를 찾을 수가 없다.

      @Autowired란?
      스프링 컨테이너에 등록한 빈에게 의존관계주입이 필요할 때, DI(의존성 주입)을 도와주는 어노테이션이다.
      스프링 컨테이너에 빈들을 모두 등록한 후에, 의존성 주입 단계가 이루어진다.
      이 때 @Autowired 어노테이션이 부여된 메서드가 실행되며 필요한 인스턴스를 주입해준다.
    * */

    @DisplayName("[api] 게시글 리스트 조회")
    @Test
    void GivenNothing_WhenRequestingArticles_ThenArticlesJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
                .andDo(print());
    }

    @DisplayName("[api] 게시글 단건 조회")
    @Test
    void GivenNothing_WhenRequestingArticle_ThenArticleJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
                .andDo(print());
    }

    @DisplayName("[api] 게시글 -> 댓글 리스트 조회")
    @Test
    void GivenNothing_WhenRequestingArticleCommentsFromArticle_ThenReturnsArticleCommentsJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articles/1/articleComments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
                .andDo(print());
    }

    @DisplayName("[api] 댓글 리스트 조회")
    @Test
    void GivenNothing_WhenRequestingArticleComments_ThenReturnsArticleCommentsJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articleComments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
                .andDo(print());
    }

    @DisplayName("[api] 댓글 단건 조회")
    @Test
    void GivenNothing_WhenRequestingArticleComment_ThenReturnsArticleCommentJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articleComments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
                .andDo(print());
    } // articlecomments에 아무것도 없기때문에 Fail
}
