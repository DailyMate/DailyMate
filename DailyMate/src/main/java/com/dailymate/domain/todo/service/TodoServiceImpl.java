package com.dailymate.domain.todo.service;

import com.dailymate.domain.todo.dao.TodoRepository;
import com.dailymate.domain.todo.domain.Todo;
import com.dailymate.domain.todo.dto.*;
import com.dailymate.domain.todo.exception.TodoExceptionMessage;
import com.dailymate.domain.todo.exception.TodoForbiddenException;
import com.dailymate.domain.todo.exception.TodoNotFoundException;
import com.dailymate.global.common.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

	private final TodoRepository todoRepository;
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void addTodo(AddTodoReqDto addTodoReqDto, String token) {
		Long USERID = jwtTokenProvider.getUserId(token);
		log.info("[할일 등록] 할일 등록 요청");
		log.info(addTodoReqDto.getContent());
		log.info("Repeatition: " + addTodoReqDto.getRepeatition());
		LocalDate today = LocalDate.now();
		for(int i = 0; i < addTodoReqDto.getRepeatition(); i++){
			String todayString = today.plusDays(i).toString();
			Todo todo = Todo.builder()
					.userId(USERID)
					.content(addTodoReqDto.getContent())
//					.date(addTodoReqDto.getDate())
					.date(todayString)
					.todoOrder(0)
					.done(false)
					.repeatition(addTodoReqDto.getRepeatition())
					.build();

			todoRepository.save(todo);
		}


		log.info("[할일 등록] 할일 등록 완료");
	}

	@Override
	public TodoResDto updateTodo(Long todoId, UpdateTodoReqDto updateTodoReqDto, String token) {
		Long USERID = jwtTokenProvider.getUserId(token);
		log.info("[할일 수정] 할일 수정 요청. userId : {}", USERID);
		// 1. 존재하는 할일인지 체크
		Todo todo = todoRepository.findById(todoId)
				.orElseThrow(()->{
					log.error("[할일 수정] 존재하지 않는 할일입니다.");
					return new TodoNotFoundException("[UPDATE_TODO] " + TodoExceptionMessage.TODO_NOT_FOUND.getMsg());
				});

		// 2. 이미 삭제된 할일인지 체크
		if(todo.getDeletedAt() != null){
			log.error("[할일 수정] 이미 삭제된 할일입니다.");
			throw new TodoNotFoundException("[UPDATE_TODO] " + TodoExceptionMessage.TODO_NOT_FOUND.getMsg());
		}

		// 3. 로그인 사용자의 할일인지 체크
		if(todo.getUserId() != USERID){
			log.error("[할일 수정] 권한이 없는 할일입니다.");
			throw new TodoForbiddenException("[UPDATE_TODO] " + TodoExceptionMessage.TODO_FORBIDDEN.getMsg());
		}

		log.info("[할일 수정] 할일 찾기 완료.");

		todo.updateTodo(updateTodoReqDto.getContent(), updateTodoReqDto.getDate());
		todoRepository.save(todo);

		log.info("[할일 수정] 할일 수정 완료.");

		return TodoResDto.entityToDto(todo);

	}

	@Transactional
	@Override
	public void deleteTodo(Long todoId, String token) {
		Long USERID = jwtTokenProvider.getUserId(token);

		log.info("[할일 삭제] 할일 삭제 요청. todoId : {}", todoId);

		// 1. 존재하는 할일인지 체크
		Todo todo = todoRepository.findById(todoId)
				.orElseThrow(()->{
					log.error("[할일 삭제] 존재하지 않는 할일입니다.");
					return new TodoNotFoundException("[DELETE_TODO] " + TodoExceptionMessage.TODO_NOT_FOUND.getMsg());
				});

		// 2. 기삭제된 할일인지 체크
		if(todo.getDeletedAt() != null){
			log.error("[할일 삭제] 삭제된 할일 입니다.");
			throw new TodoNotFoundException("[DELETE_TODO] " + TodoExceptionMessage.TODO_NOT_FOUND.getMsg());
		}

		// 3. 로그인 사용자의 할일인지 체크
		if(todo.getUserId() != USERID){
			log.error("[할일 수정] 권한이 없는 할일입니다.");
			throw new TodoForbiddenException("[UPDATE_TODO] " + TodoExceptionMessage.TODO_FORBIDDEN.getMsg());
		}

		log.info("[할일 삭제] 할일 찾기 완료");
		todo.delete();
		todoRepository.save(todo);
		log.info("[할일 삭제] 할일 삭제 완료");

	}

	@Override
	public String postponeTodo(Long todoId, String token) {
		Long USERID = jwtTokenProvider.getUserId(token);
		log.info("[할일 미루기] 할일 미루기 요청. todoId : {}", todoId);

		// 1. 존재하는 할일인지 체크
		Todo todo = todoRepository.findById(todoId)
				.orElseThrow(()->{
					log.error("[할일 삭제] 존재하지 않는 할일입니다.");
					return new TodoNotFoundException("[DELETE_TODO] " + TodoExceptionMessage.TODO_NOT_FOUND.getMsg());
				});

		// 2. 로그인 사용자의 할일인지 체크
		if(todo.getUserId() != USERID){
			log.error("[할일 수정] 권한이 없는 할일입니다.");
			throw new TodoForbiddenException("[UPDATE_TODO] " + TodoExceptionMessage.TODO_FORBIDDEN.getMsg());
		}

		// 3. 할일의 날짜를 하루 미룸
		LocalDate currentDate = LocalDate.parse(todo.getDate()); // 문자열로 저장된 날짜를 LocalDate로 변환
		LocalDate postponedDate = currentDate.plusDays(1); // 현재 날짜에 1일을 추가하여 미룬 날짜 생성

		// 4. 미룬 할일 정보를 새로운 할일로 저장
		Todo postponedTodo = Todo.builder()
				.content(todo.getContent())
				.date(postponedDate.toString()) // LocalDate를 문자열로 변환하여 저장
				.todoOrder(todo.getTodoOrder())
				.done(todo.getDone())
				.userId(todo.getUserId())
				.repeatition(todo.getRepeatition())
				.build();

		deleteTodo(todoId, token);
		todoRepository.save(postponedTodo);

		log.info("[할일 미루기] 할일 미루기 완료");
		return postponedDate.toString();
	}

	@Override
	public List<Todo> findTodoListByDay(String date, String token) {
		Long USERID = jwtTokenProvider.getUserId(token);
		log.info("[할일 일별 조회] 할일 일별 조회 요청. date : {}", date);

		// 1. 해당 날짜에 해당하는 사용자의 할일 목록을 조회
		List<Todo> todoList = todoRepository.findByUserIdAndDate(USERID, date);

		if (todoList.isEmpty()) {
			log.info("[할일 일별 조회] 해당 날짜에 할일이 없습니다. date : {}, userId : {}", date, USERID);
			return Collections.emptyList(); // 비어 있는 리스트 반환
		}

		log.info("[할일 일별 조회] 할일 내용 조회 완료. date : {}, userId : {}", date, USERID);
		return todoList; // todoList 반환
	}


	@Override
	public TodoResDto findTodo(Long todoId, String token) {
		Long USERID = jwtTokenProvider.getUserId(token);
		log.info("[할일 상세] 할일 상세 요청. todoId : {}", todoId);

		// 1. 존재하는 할일인지 체크
		Todo todo = todoRepository.findById(todoId)
				.orElseThrow(() -> {
					log.error("[할일 상세] 존재하지 않는 할일입니다.");
					return new TodoNotFoundException("[FIND_TODO] " + TodoExceptionMessage.TODO_NOT_FOUND.getMsg());
				});

		// 2. 로그인 사용자의 할일인지 체크
		if (!todo.getUserId().equals(USERID)) {
			log.error("[할일 상세] 권한이 없는 할일입니다.");
			throw new TodoForbiddenException("[FIND_TODO] " + TodoExceptionMessage.TODO_FORBIDDEN.getMsg());
		}

		// 3. TodoResDto로 변환하여 반환
		TodoResDto todoResDto = TodoResDto.entityToDto(todo);
		log.info("[할일 상세] 할일 상세 조회 완료. todoId : {}", todoId);
		return todoResDto;

	}

	@Override
	public Integer getSuccessRate(String date, String token) {
		log.info("[토큰 확인] token: {}", token);
		Long USERID = jwtTokenProvider.getUserId(token);
		log.info("[할일 일간 달성도 조회] 달성도 조회 요청. userId : {}", USERID);

		List<Todo> todoList = todoRepository.findByUserIdAndDate(USERID, date);

		int totalTodos = todoList.size();

		if(totalTodos == 0){
			return -1;
		}

		long completedTodos = todoList.stream().filter(Todo::getDone).count();

		double successRate = totalTodos == 0 ? 0 : ((double) completedTodos / totalTodos) * 100;

		log.info("[할일 일간 달성도 조회] 달성도 조회 완료. userId : {}, date : {}, successRate : {}", USERID, date, successRate);

		return (int)successRate;

	}

	@Override
	public void checkTodo(Long todoId, String token) {
		Long USERID = jwtTokenProvider.getUserId(token);
		log.info("[할일 완료 체크] 완료 체크 요청. todoId : {}", todoId);

		// 1. 존재하는 할일인지 체크
		Todo todo = todoRepository.findById(todoId)
				.orElseThrow(() -> {
					log.error("[할일 상세] 존재하지 않는 할일입니다.");
					return new TodoNotFoundException("[FIND_TODO] " + TodoExceptionMessage.TODO_NOT_FOUND.getMsg());
				});

		// 2. 로그인 사용자의 할일인지 체크
		if(todo.getUserId() != USERID){
			log.error("[할일 수정] 권한이 없는 할일입니다.");
			throw new TodoForbiddenException("[UPDATE_TODO] " + TodoExceptionMessage.TODO_FORBIDDEN.getMsg());
		}

		todo.toggleDone();
		todoRepository.save(todo);

		log.info("[할일 완료 체크] 할일 완료. todoId : {}", todoId);
	}

	@Override
	public List<ChangeOrderResDto> changeOrder(List<ChangeOrderReqDto> changeOrderReqDto, String token) {
		Long userId = jwtTokenProvider.getUserId(token);
		log.info("[할일 순서 변경] 순서 변경 요청. userId : {}", userId);

		List<ChangeOrderResDto> updatedTodoList = new ArrayList<>();

		for (ChangeOrderReqDto dto : changeOrderReqDto) {
			Long todoId = dto.getTodoId();
			Integer todoOrder = dto.getTodoOrder();

			// 해당 ID의 할일을 조회
			Todo todo = todoRepository.findById(todoId)
					.orElseThrow(() -> {
						log.error("[할일 순서 변경] 할일을 찾을 수 없습니다. todoId : {}", todoId);
						return new TodoNotFoundException("할일을 찾을 수 없습니다.");
					});

			// 로그인한 사용자의 할일인지 확인
			if (!todo.getUserId().equals(userId)) {
				log.error("[할일 순서 변경] 권한이 없는 할일입니다. todoId : {}", todoId);
				throw new TodoForbiddenException("권한이 없는 할일입니다.");
			}

			// 순서를 변경하고 저장
			todo.changeOrder(todoOrder);
			Todo updatedTodo = todoRepository.save(todo);

			// 변경된 Todo의 정보를 ChangeOrderResDto로 매핑하여 목록에 추가
			ChangeOrderResDto updatedTodoDto = new ChangeOrderResDto();
			updatedTodoDto.setTodoId(updatedTodo.getTodoId());
			updatedTodoDto.setTodoOrder(updatedTodo.getTodoOrder());
			updatedTodoList.add(updatedTodoDto);

			log.info("[할일 순서 변경] 할일 순서 변경 완료. todoId : {}, order : {}", todoId, todoOrder);
		}

		log.info("[할일 순서 변경] 모든 할일의 순서 변경 완료");

		return updatedTodoList;
	}


}
