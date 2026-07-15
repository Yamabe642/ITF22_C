-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- ホスト: 127.0.0.1
-- 生成日時: 2026-07-14 06:11:43
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
-- テーブルの構造 `room`
--

CREATE TABLE `room` (
  `room_id` int(11) NOT NULL,
  `room_name` varchar(100) NOT NULL,
  `min_capacity` int(11) NOT NULL,
  `max_capacity` int(11) NOT NULL,
  `price_per_hour` int(11) NOT NULL,
  `stock` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- テーブルのデータのダンプ `room`
--

INSERT INTO `room` (`room_id`, `room_name`, `min_capacity`, `max_capacity`, `price_per_hour`, `stock`) VALUES(1, 'スモールルーム', 2, 4, 1500, 5);
INSERT INTO `room` (`room_id`, `room_name`, `min_capacity`, `max_capacity`, `price_per_hour`, `stock`) VALUES(2, 'ミディアムルーム', 5, 8, 2500, 5);
INSERT INTO `room` (`room_id`, `room_name`, `min_capacity`, `max_capacity`, `price_per_hour`, `stock`) VALUES(3, 'VIPパーティールーム', 9, 15, 4500, 3);

--
-- ダンプしたテーブルのインデックス
--

--
-- テーブルのインデックス `room`
--
ALTER TABLE `room`
  ADD PRIMARY KEY (`room_id`);

--
-- ダンプしたテーブルの AUTO_INCREMENT
--

--
-- テーブルの AUTO_INCREMENT `room`
--
ALTER TABLE `room`
  MODIFY `room_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
