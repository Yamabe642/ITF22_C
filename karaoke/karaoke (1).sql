-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- ホスト: 127.0.0.1
-- 生成日時: 2026-07-09 06:41:19
-- サーバのバージョン： 10.4.32-MariaDB
-- PHP のバージョン: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- データベース: `karaoke`
--

-- --------------------------------------------------------

--
-- テーブルの構造 `reservations`
--

CREATE TABLE `reservations` (
  `reservation_id` int(11) NOT NULL,
  `room_id` int(11) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `reservation_date` date DEFAULT NULL,
  `start_time` time DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `people` int(11) DEFAULT NULL,
  `drink` tinyint(1) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- テーブルのデータのダンプ `reservations`
--

INSERT INTO `reservations` (`reservation_id`, `room_id`, `name`, `phone`, `reservation_date`, `start_time`, `duration`, `people`, `drink`, `status`) VALUES(10, 101, '山田太郎', '090-1111-2222', '2026-07-10', '18:00:00', 2, 3, 1, '予約中');
INSERT INTO `reservations` (`reservation_id`, `room_id`, `name`, `phone`, `reservation_date`, `start_time`, `duration`, `people`, `drink`, `status`) VALUES(11, 103, '佐藤花子', '080-3333-4444', '2026-07-10', '19:00:00', 3, 5, 0, '予約中');
INSERT INTO `reservations` (`reservation_id`, `room_id`, `name`, `phone`, `reservation_date`, `start_time`, `duration`, `people`, `drink`, `status`) VALUES(12, 105, '鈴木一郎', '070-5555-6666', '2026-07-11', '20:00:00', 2, 8, 1, '予約中');

-- --------------------------------------------------------

--
-- テーブルの構造 `rooms`
--

CREATE TABLE `rooms` (
  `room_id` int(11) NOT NULL,
  `capacity` int(11) NOT NULL,
  `machine` varchar(20) NOT NULL,
  `available` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- テーブルのデータのダンプ `rooms`
--

INSERT INTO `rooms` (`room_id`, `capacity`, `machine`, `available`) VALUES(101, 4, 'DAM', 1);
INSERT INTO `rooms` (`room_id`, `capacity`, `machine`, `available`) VALUES(102, 4, 'JOYSOUND', 1);
INSERT INTO `rooms` (`room_id`, `capacity`, `machine`, `available`) VALUES(103, 6, 'DAM', 1);
INSERT INTO `rooms` (`room_id`, `capacity`, `machine`, `available`) VALUES(104, 6, 'JOYSOUND', 1);
INSERT INTO `rooms` (`room_id`, `capacity`, `machine`, `available`) VALUES(105, 10, 'DAM', 1);
INSERT INTO `rooms` (`room_id`, `capacity`, `machine`, `available`) VALUES(106, 10, 'JOYSOUND', 1);
INSERT INTO `rooms` (`room_id`, `capacity`, `machine`, `available`) VALUES(201, 4, 'DAM', 1);
INSERT INTO `rooms` (`room_id`, `capacity`, `machine`, `available`) VALUES(202, 8, 'JOYSOUND', 1);
INSERT INTO `rooms` (`room_id`, `capacity`, `machine`, `available`) VALUES(203, 10, 'DAM', 1);

--
-- ダンプしたテーブルのインデックス
--

--
-- テーブルのインデックス `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`reservation_id`),
  ADD KEY `room_id` (`room_id`);

--
-- テーブルのインデックス `rooms`
--
ALTER TABLE `rooms`
  ADD PRIMARY KEY (`room_id`);

--
-- ダンプしたテーブルの AUTO_INCREMENT
--

--
-- テーブルの AUTO_INCREMENT `reservations`
--
ALTER TABLE `reservations`
  MODIFY `reservation_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- ダンプしたテーブルの制約
--

--
-- テーブルの制約 `reservations`
--
ALTER TABLE `reservations`
  ADD CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
