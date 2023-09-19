package com.project.boardproject.repository;

import com.project.boardproject.domain.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleCommentRepository extends JpaRepository<ArticleComment,Long> {}
/*
    Repository는 Entity를 만들고 그 Entity에서 CRUD를 하기 위해서 만든다.
    장점으로는

    첫째. 모든사람들이 똑같이 사용할 수 있고 편리한 장점
    둘째. 강력하면서 간편한 검색 기능
    셋째. 쿼리
    넷째. 영속화

    가 있다.

 */
