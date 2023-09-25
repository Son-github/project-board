package com.project.boardproject.controller;


import com.project.boardproject.domain.type.SearchType;
import com.project.boardproject.response.ArticleResponse;
import com.project.boardproject.response.ArticleWithCommentsResponse;
import com.project.boardproject.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/articles")
@Controller
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public String articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map) {
        map.addAttribute("articles", articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from));

        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap map) { // @PathVariable?
        ArticleWithCommentsResponse articleWithCommentsResponse = ArticleWithCommentsResponse.from(articleService.getArticle(articleId));
        map.addAttribute("article", articleWithCommentsResponse);
        map.addAttribute("articleComments", articleWithCommentsResponse.articleCommentsResponses());

        return "articles/detail";
    }

}
