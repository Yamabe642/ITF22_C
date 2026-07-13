-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- ホスト: 127.0.0.1
-- 生成日時: 2026-07-10 06:41:19
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
-- テーブルの構造 `customers`
--

CREATE TABLE `customers` (
  `customer_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- テーブルのデータのダンプ `customers`
--

INSERT INTO `customers` (`customer_id`, `name`, `phone_number`, `created_at`) VALUES
(1, '山田太郎', '090-1111-2222', '2026-07-09 10:00:00'),
(2, '佐藤花子', '080-3333-4444', '2026-07-09 11:30:00'),
(3, '鈴木一郎', '070-5555-6666', '2026-07-09 15:20:00');

-- --------------------------------------------------------

--
-- テーブルの構造 `rooms`
--

CREATE TABLE `rooms` (
  `room_id` int(11) NOT NULL,
  `room_number` varchar(10) NOT NULL,
  `capacity` int(11) NOT NULL,
  `unit_price` int(11) NOT NULL,
  `option_darts` tinyint(1) DEFAULT 0,
  `option_karaoke_hd` tinyint(1) DEFAULT 0,
  `option_party_set` tinyint(1) DEFAULT 0,
  `is_available` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- テーブルのデータのダンプ `rooms`
--

INSERT INTO `rooms` (`room_id`, `room_number`, `capacity`, `unit_price`, `option_darts`, `option_karaoke_hd`, `option_party_set`, `is_available`) VALUES
(1, '101', 4, 300, 0, 1, 0, 1),
(2, '102', 4, 300, 0, 0, 0, 1),
(3, '103', 6, 450, 0, 1, 0, 1),
(4, '104', 6, 450, 1, 0, 0, 1),
(5, '105', 10, 700, 0, 1, 1, 1),
(6, '106', 10, 700, 1, 1, 1, 1),
(7, '201', 4, 300, 0, 0, 0, 1),
(8, '202', 8, 550, 1, 0, 1, 1),
(9, '203', 10, 700, 0, 1, 1, 1);

-- --------------------------------------------------------

--
-- テーブルの構造 `reservations`
--

CREATE TABLE `reservations` (
  `reservation_id` int(11) NOT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `room_number` varchar(10) DEFAULT NULL,
  `reservation_datetime` datetime NOT NULL,
  `duration_minutes` int(11) NOT NULL,
  `number_of_people` int(11) NOT NULL,
  `drink_bar` tinyint(1) DEFAULT 0,
  `total_fee` int(11) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'confirmed',
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- テーブルのデータのダンプ `reservations`
--

INSERT INTO `reservations` (`reservation_id`, `customer_id`, `room_number`, `reservation_datetime`, `duration_minutes`, `number_of_people`, `drink_bar`, `total_fee`, `status`, `created_at`, `updated_at`) VALUES
(10, 1, '101', '2026-07-10 18:00:00', 120, 3, 1, 1200, 'confirmed', '2026-07-09 10:05:00', NULL),
(11, 2, '103', '2026-07-10 19:00:00', 180, 5, 0, 2700, 'confirmed', '2026-07-09 11:35:00', NULL),
(12, 3, '105', '2026-07-11 20:00:00', 120, 8, 1, 2800, 'confirmed', '2026-07-09 15:25:00', NULL);

--
-- ダンプしたテーブルのインデックス
--

--
-- テーブルのインデックス `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`customer_id`),
  ADD UNIQUE KEY `phone_number` (`phone_number`);

--
-- テーブルのインデックス `rooms`
--
ALTER TABLE `rooms`
  ADD PRIMARY KEY (`room_id`),
  ADD UNIQUE KEY `room_number` (`room_number`);

--
-- テーブルのインデックス `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`reservation_id`),
  ADD KEY `customer_id` (`customer_id`),
  ADD KEY `room_number` (`room_number`);

--
-- ダンプしたテーブルの AUTO_INCREMENT
--

--
-- テーブルの AUTO_INCREMENT `customers`
--
ALTER TABLE `customers`
  MODIFY `customer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- テーブルの AUTO_INCREMENT `rooms`
--
ALTER TABLE `rooms`
  MODIFY `room_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

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
  ADD CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  ADD CONSTRAINT `reservations_ibfk_2` FOREIGN KEY (`room_number`) REFERENCES `rooms` (`room_number`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
