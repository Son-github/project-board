package com.project.boardproject.service;

import com.project.boardproject.domain.Hashtag;
import com.project.boardproject.repository.HashtagRepository;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Disabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.project.boardproject.domain.Article;
import com.project.boardproject.domain.UserAccount;
import com.project.boardproject.domain.constant.SearchType;
import com.project.boardproject.dto.*;
import com.project.boardproject.repository.ArticleRepository;
import com.project.boardproject.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;


@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class) // mocking이 무엇?
class ArticleServiceTest {

    Logger log = LoggerFactory.getLogger(ArticleServiceTest.class);
    
    @InjectMocks private ArticleService sut; // parameter?

    @Mock private HashtagService hashtagService;
    @Mock private ArticleRepository articleRepository; // final은 뭐야?
    @Mock private UserAccountRepository userAccountRepository;
    @Mock private HashtagRepository hashtagRepository;

    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {
        // Given
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable); // 제목, 본문, ID, 닉네임, 해시태그
        // 정렬기능이 들어간다,,

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }

    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenSearchingWords_whenSearchingArticle_thenReturnsArticlePage() {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    @DisplayName("검색어 없이 게시글을 해시태그로 검색하면, 빈 페이지를 반환한다.")
    @Test
    void givenNoSearchParameters_whenSearchingArticlesViaHashtag_thenReturnsEmptyPage() {
        // Given
        Pageable pageable = Pageable.ofSize(20);

        // When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(null, pageable);

        // Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).shouldHaveNoInteractions();
        then(articleRepository).shouldHaveNoInteractions();
    }

    @DisplayName("없는 해시태글르 검색하면, 빈 페이지를 전환한다.")
    @Test
    void givenNonexistentHashtag_whenSearchingArticlesViaHashtag_thenReturnEmptyPage() {
        // Given
        String hashtagName = "난 없지로오옹";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByHashtagNames(List.of(hashtagName), pageable)).willReturn(new PageImpl<>(List.of(), pageable, 0));

        // When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtagName, pageable);

        // Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).should().findByHashtagNames(List.of(hashtagName), pageable);
    }

    @DisplayName("게시글을 해시태그로 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenHashtag_whenSearchingArticlesViaHashtag_thenReturnsArticlesPage() {
        // Given
        String hashtagName = "java";
        Pageable pageable = Pageable.ofSize(20);
        Article expecctedArticle = createArticle();
        log.info("hashtagName: {}", List.of(hashtagName));
        given(articleRepository.findByHashtagNames(List.of(hashtagName), pageable)).willReturn(new PageImpl<>(List.of(expecctedArticle), pageable, 1));

        // When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtagName, pageable);

        // Then
        assertThat(articles).isEqualTo(new PageImpl<>(List.of(ArticleDto.from(expecctedArticle)), pageable, 1));
        then(articleRepository).should().findByHashtagNames(List.of(hashtagName), pageable);
    }

    @DisplayName("게시글 ID로 조회하면 댓글 달린 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticleWithComments_thenReturnsArticleWithComments() {
        // Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
        ArticleWithCommentsDto dto = sut.getArticleWithComments(articleId);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtagDtos", article.getHashtags().stream()
                        .map(HashtagDto::from)
                        .collect(Collectors.toUnmodifiableSet()));
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("댓글 달린 게시글이 없으면, 예외를 던진다.")
    @Test
    void givenNonexistentArticleId_whenSearchingArticleWithComments_thenThrowException() {
        // Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.getArticleWithComments(articleId));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다. - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    @Test
    void givenArticleInfo_whenSearchingArticle_thenReturnsArticle() {
        // Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
        ArticleDto dto = sut.getArticle(articleId);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtagDtos", article.getHashtags().stream()
                        .map(HashtagDto::from)
                        .collect(Collectors.toUnmodifiableSet())
                );
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글이 없으면, 예외를 던진다.")
    @Test
    void givenNonexistentArticleId_whenSearchingArticle_thenThrowsExceptions() {
        // Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.getArticle(articleId));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 정보를 입력하면, 본문에서 해시태그 정보를 추출하여 해시태그 정보가 초함된 게시글을 생성한다.")
    @Test
    void givenArticleInfo_whenSavingArticle_thenExtractsHashtagsFromContentAndSavesArticleWithExtractedHashtags() {
        // Given
        ArticleDto dto = createArticleDto();
        Set<String> expectedHashtagNames = Set.of("java", "spring");
        Set<Hashtag> expectedHashtags = new HashSet<>();
        expectedHashtags.add(createHashtag("java"));

        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());
        given(hashtagService.parseHashtagNames(dto.content())).willReturn(expectedHashtagNames);
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());

        // When
        sut.saveArticle(dto);

        // Then
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(hashtagService).should().parseHashtagNames(dto.content());
        then(hashtagService).should().findHashtagsByNames(expectedHashtagNames);
        then(articleRepository).should().save(any(Article.class));
    }

    @Disabled
    @DisplayName("게시글의 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void givenNonexistentArticleId_whenSearchingArticle_thenThrowsException() {
        // Given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용");
        Set<String> expectedHashtagNames = Set.of("springboot");
        Set<Hashtag> expectedHashtags = new HashSet<>();

        given(articleRepository.getReferenceById(dto.id())).willReturn(article); // Reference?
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(dto.userAccountDto().toEntity());
        willDoNothing().given(articleRepository).flush();
        willDoNothing().given(hashtagService).deleteHashtagWithoutArticles(any());
        given(hashtagService.parseHashtagNames(dto.content())).willReturn(expectedHashtagNames);
        given(hashtagService.findHashtagsByNames(expectedHashtagNames)).willReturn(expectedHashtags);

        // When
        sut.updateArticle(dto.id(), dto);

        // Then
        log.info("과연 dto는 변경 되었나요? hashtagDtos: {}", dto.hashtagDtos());
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .extracting("hashtags", as(InstanceOfAssertFactories.COLLECTION))
                        .hasSize(2)
                                .extracting("hashtagName")
                                        .containsExactly("springboot");
        then(articleRepository).should().getReferenceById(dto.id());
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(articleRepository).should().flush();
        then(hashtagService).should(times(2)).deleteHashtagWithoutArticles(any());
        then(hashtagService).should().parseHashtagNames(dto.content());
        then(hashtagService).should().findHashtagsByNames(expectedHashtagNames);
    }

    @DisplayName("없는 게시글의 수정 정보를 입력하면, 경고 로그를 찍고 아무 것도 하지 않는다.")
    @Test
    void givenNonexistentArticleInfo_whenUpdatingArticle_thenLogsWarningAndDoesNothing() {
        // Given
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용");
        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updateArticle(dto.id(), dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.id());
        then(userAccountRepository).shouldHaveNoInteractions();
        then(hashtagService).shouldHaveNoInteractions();
    }

    @DisplayName("게시글 작성자가 아닌 사람이 수정 정볼르 입력하면 아무 것도 하지 않는다.")
    @Test
    void givenModifiedArticleInfoWithDifferentUser_whenUpdatingArticle_thenDoesNothing() {
        // Given
        Long differentArticleId = 22L;
        Article differentArticle = createArticle(differentArticleId);
        differentArticle.setUserAccount(createUserAccount("John"));
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용");
        given(articleRepository.getReferenceById(differentArticleId)).willReturn(differentArticle);
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(dto.userAccountDto().toEntity());

        // When
        sut.updateArticle(differentArticleId, dto);

        // Then
        then(articleRepository).should().getReferenceById(differentArticleId);
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(hashtagService).shouldHaveNoInteractions();
    }

    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다.")
    @Test
    void givenArticleInfo_whenInsertingArticle_thenDeleteArticle() {
        // Given
        Long articleId = 1L;
        String userId = "uno";
        given(articleRepository.getReferenceById(articleId)).willReturn(createArticle());
        willDoNothing().given(articleRepository).deleteByIdAndUserAccount_UserId(articleId, userId);
        willDoNothing().given(articleRepository).flush();
        willDoNothing().given(hashtagService).deleteHashtagWithoutArticles(any());

        // willDoNothing, willReturn??

        // When
        sut.deleteArticle(1L, userId);

        // Then
        then(articleRepository).should().getReferenceById(articleId);
        then(articleRepository).should().deleteByIdAndUserAccount_UserId(articleId, userId);
        then(articleRepository).should().flush();
        then(hashtagService).should(times(2)).deleteHashtagWithoutArticles(any());
    }

    @DisplayName("게시글 수를 조회하면, 게시글 수를 반환한다")
    @Test
    void givenNothing_whenCountingArtilces_thenReturnsArticleCount() {
        // Given
        long expected = 0L;
        given(articleRepository.count()).willReturn(expected);

        // When
        long actual = sut.getArticleCount();

        // Then
        assertThat(actual).isEqualTo(expected);
        then(articleRepository).should().count();
    }

    @DisplayName("해시태그를 조회하면, 유니크 해시태그 리스트를 반환한다.")
    @Test
    void givenNothing_whenCalling_thenReturnsHashtags() {
        // Given
        Article article = createArticle();
        List<String> expectedHashtags = List.of("java", "spring", "boot");
        //log.info("hashtagRepository.findAllHashtagNames: {}", hashtagRepository.findAllHashtagNames());
        given(hashtagRepository.findAllHashtagNames()).willReturn(expectedHashtags);

        // When
        List<String> actualHashtags = sut.getHashtags();

        // Then
        assertThat(actualHashtags).isEqualTo(expectedHashtags);
        then(hashtagRepository).should().findAllHashtagNames();
    }

    private UserAccount createUserAccount() {
        return createUserAccount("uno");
    }

    private UserAccount createUserAccount(String userId) {
        return UserAccount.of(
                userId,
                "password",
                "uno@eamil.com",
                "Uno",
                null
        );
    }

    private Article createArticle() {
        log.info("createArticle:");
        return createArticle(1L);
    }

    private Article createArticle(Long id) {
        Article article = Article.of(
                createUserAccount(),
                "title",
                "content"
        );

        log.info("article: {}", article.getTitle());
        log.info("article: {}", article.getId());
        log.info("article: {}", article.getUserAccount().getUserId());

        article.addHashtags(Set.of(
                createHashtag(1L, "java"),
                createHashtag(2L, "spring")
        ));
        ReflectionTestUtils.setField(article, "id", id);

        return article;
    }

    private Hashtag createHashtag(String hashtagName) {
        return createHashtag(1L, hashtagName);
    }

    private Hashtag createHashtag(Long id, String hashtagName) {
        Hashtag hashtag = Hashtag.of(hashtagName);
        ReflectionTestUtils.setField(hashtag, "id", id);

        return hashtag;
    }


    private ArticleDto createArticleDto() {
        return createArticleDto("title", "content");
    }

    private ArticleDto createArticleDto(String title, String content) {
        return ArticleDto.of(
                1L,
                createUserAccountDto(),
                title,
                content,
                null,
                LocalDateTime.now(),
                "Uno",
                LocalDateTime.now(),
                "Uno");
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "uno",
                "password",
                "uno@mail.com",
                "Uno",
                "This is memo",
                LocalDateTime.now(),
                "uno",
                LocalDateTime.now(),
                "uno"
        );
    }

}