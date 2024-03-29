/* SQL 작성용 */

INSERT INTO ACCOUNT (ACCOUNT_ID, USER_ID, AMOUNT, CATEGORY, CONTENT, CREATED_AT, DATE, TYPE, DELETED_AT)
VALUES (100, 101, 1000000, null, '월급이 들어왔당 히히', '2024-01-25 23:25:59', '2024-01-15', '수입', null),
       (101, 101, -33000, '식비', '월급 들어온 기념으로 짜장면 먹음', '2024-01-25 23:27:00', '2024-01-15', '지출', null),
       (102, 101, -4500, '식비', '후식으로 탕후루', '2024-01-25 23:29:00', '2024-01-15', '지출', null),
       (103, 101, 500, null, '내 전재산', '2024-01-01 23:59:59', '2024-01-01', '수입', null),
       (104, 101, -20000, '교통', '지각해서 택시탐', '2024-01-28 20:38:55', '2024-01-16', '지출', null),
       (105, 101, -400000, '생활', '관리비', '2024-01-01 00:00:00', '2024-01-01', '지출', '2024-01-02 00:00:00'),
       (200, 106, 500000, null, '한달 용돈', '2023-01-01 00:00:00', '2023-01-01', '수입', null),
       (201, 106, -5000, '기타', '로또 구매', '2023-01-01 10:00:00', '2023-01-01', '지출', null),
       (202, 106, -5000, '기타', '로또 구매', '2023-01-01 10:00:00', '2023-01-01', '지출', '2024-01-02 00:00:00'),
       (203, 106, -2500, '카페', '아이스 아메리카노', '2023-01-03 10:00:00', '2023-01-03', '지출', null);

INSERT INTO USERS (USER_ID, EMAIL, PASSWORD, NICKNAME, TYPE, DELETED_AT)
VALUES (101, 'test1@naver.com', '{bcrypt}$2a$10$O4PtZTti4R48sqjQGyKD6exSAfKZWuYGzqAqC/zdj1ITlQ7DfY5ZS', '테스트1', 'ROLE_USER', null),
       (200, 'admin1@naver.com', '{bcrypt}$2a$10$O4PtZTti4R48sqjQGyKD6exSAfKZWuYGzqAqC/zdj1ITlQ7DfY5ZS', '관리자1', 'ROLE_ADMIN', null),
       (201, 'admin2@naver.com', '{bcrypt}$2a$10$O4PtZTti4R48sqjQGyKD6exSAfKZWuYGzqAqC/zdj1ITlQ7DfY5ZS', '관리자2', 'ROLE_ADMIN', null),
       (102, 'test2@naver.com', '{bcrypt}$2a$10$O4PtZTti4R48sqjQGyKD6exSAfKZWuYGzqAqC/zdj1ITlQ7DfY5ZS', '테스트2', 'ROLE_USER', '2024-02-01'),
       (103, 'test3@naver.com', '{bcrypt}$2a$10$O4PtZTti4R48sqjQGyKD6exSAfKZWuYGzqAqC/zdj1ITlQ7DfY5ZS', '테스트2', 'ROLE_USER', null),
       (104, 'test4@naver.com', '{bcrypt}$2a$10$O4PtZTti4R48sqjQGyKD6exSAfKZWuYGzqAqC/zdj1ITlQ7DfY5ZS', '테스트3', 'ROLE_USER', null),
       (105, 'test5@naver.com', '{bcrypt}$2a$10$O4PtZTti4R48sqjQGyKD6exSAfKZWuYGzqAqC/zdj1ITlQ7DfY5ZS', '테스트4', 'ROLE_USER', null),
       (106, 'dd39@naver.com', '{bcrypt}$2a$10$O4PtZTti4R48sqjQGyKD6exSAfKZWuYGzqAqC/zdj1ITlQ7DfY5ZS', '천재윤영', 'ROLE_USER', null),
       (107, 'aa@naver.com', '{bcrypt}$2a$10$O4PtZTti4R48sqjQGyKD6exSAfKZWuYGzqAqC/zdj1ITlQ7DfY5ZS', '프로젝트언제끝나', 'ROLE_USER', '2024-02-14'),
--        (108, 'yoonyong27@gmail.com', '{bcrypt}$2a$10$O4PtZTti4R48sqjQGyKD6exSAfKZWuYGzqAqC/zdj1ITlQ7DfY5ZS', '귀염둥쓰', 'ROLE_USER', null),
       (109, 'test6@naver.com', '{bcrypt}$2a$10$O4PtZTti4R48sqjQGyKD6exSAfKZWuYGzqAqC/zdj1ITlQ7DfY5ZS', '조윤영', 'ROLE_USER', null);

INSERT INTO FRIEND (FRIEND_ID, TO_ID, FROM_ID, REQUEST_DATE, STATUS)
VALUES (10000, 101, 104, '2024-02-18', FALSE),
       (10001, 101, 103, '2024-02-18', TRUE),
       (10002, 101, 200, '2024-02-17', FALSE),
       (10003, 103, 104, '2024-01-31', TRUE),
       (10004, 103, 106, '2024-02-01', FALSE),
       (10005, 105, 101, '2024-01-01', TRUE);

INSERT INTO TODO (TODO_ID, USER_ID, DATE, CONTENT, DONE, REPEATITION, TODO_ORDER, CREATED_AT, UPDATED_AT, DELETED_AT )
VALUES (101, 101, '2024-02-27','1번 할일입니다.', FALSE, 1, 1, '2024-02-27', null, null),
       (102, 101, '2024-02-27','2번 할일입니다.', FALSE, 1, 2, '2024-02-27', null, null),
       (103, 101, '2024-02-27','3번 할일입니다.', FALSE, 1, 3, '2024-02-27', null, null),
       (104, 101, '2024-02-27','4번 할일입니다.', FALSE, 1, 4, '2024-02-27', null, null),
       (105, 101, '2024-02-28','5번 할일입니다.', FALSE, 1, 1, '2024-02-28', null, null),
       (106, 101, '2024-02-28','6번 할일입니다.', FALSE, 1, 2, '2024-02-28', null, null),
       (107, 101, '2024-02-26','7번 할일입니다.', FALSE, 1, 1, '2024-02-26', null, null),
       (108, 101, '2024-02-26','8번 할일입니다.', FALSE, 1, 2, '2024-02-26', null, null),
       (109, 101, '2024-02-26','9번 할일입니다.', FALSE, 1, 3, '2024-02-26', null, null),
       (110, 102, '2024-02-26','10번 할일입니다.', FALSE, 1, 3, '2024-02-26', null, null);
