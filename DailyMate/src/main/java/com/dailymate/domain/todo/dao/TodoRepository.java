package com.dailymate.domain.todo.dao;

import com.dailymate.domain.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

	List<Todo> findByUserIdAndDate(Long userId, String date);
}
