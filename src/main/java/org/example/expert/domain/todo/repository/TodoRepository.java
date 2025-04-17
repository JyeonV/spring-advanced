package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    @EntityGraph(attributePaths = {"comments", "managers"})
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN FETCH t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    int countById(Long todoId);
}

/**
 * findAll()을 수행 시 Todo를 전체 조회하는 쿼리 1번 + comments를 조회하는 쿼리 N번 + Manager를 조회하는 쿼리 N번 이 발생
 * FetchType이 Lazy기 때문에 컬렉션에 접근할 때 마다 쿼리가 추가로 발생
 * 따라서 fetch join을 사용해서 OnetoMany 연관 관계에 있는 테이블의 정보도 같이 불러와서 1번의 쿼리만 발생
 *
 * @EntityGraph -> 조회에서만 유효, JPQL 없이 fetch-join을 대체 가능, 지연로딩으로 설정된 연관관계를 fetch 로딩하도록 지정
 */