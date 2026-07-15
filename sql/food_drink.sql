-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- ホスト: 127.0.0.1
-- 生成日時: 2026-07-14 06:11:16
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
-- テーブルの構造 `food_drink`
--

CREATE TABLE `food_drink` (
  `food_drink_id` int(11) NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `category` varchar(50) NOT NULL,
  `price` int(11) NOT NULL,
  `stock` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- テーブルのデータのダンプ `food_drink`
--

INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(1, 'フライドポテト', 'Food', 550, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(2, '唐揚げ', 'Food', 600, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(3, 'ピザ', 'Food', 900, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(4, 'チャーハン', 'Food', 750, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(5, 'オムライス', 'Food', 850, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(6, 'カレーライス', 'Food', 700, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(7, 'グリーンサラダ', 'Food', 500, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(8, 'たこ焼き', 'Food', 550, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(9, '焼きそば', 'Food', 750, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(10, 'ナゲット', 'Food', 400, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(11, 'チョコパフェ', 'Food', 650, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(12, 'いちごパフェ', 'Food', 650, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(13, 'バニラアイス', 'Food', 350, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(14, 'チョコアイス', 'Food', 350, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(15, 'いちごアイス', 'Food', 350, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(16, 'ビール', 'Drink', 700, 20);
INSERT INTO `food_drink` (`food_drink_id`, `item_name`, `category`, `price`, `stock`) VALUES(17, 'ハイボール', 'Drink', 600, 20);

--
-- ダンプしたテーブルのインデックス
--

--
-- テーブルのインデックス `food_drink`
--
ALTER TABLE `food_drink`
  ADD PRIMARY KEY (`food_drink_id`);

--
-- ダンプしたテーブルの AUTO_INCREMENT
--

--
-- テーブルの AUTO_INCREMENT `food_drink`
--
ALTER TABLE `food_drink`
  MODIFY `food_drink_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
