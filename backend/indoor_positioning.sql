-- phpMyAdmin SQL Dump
-- version 4.4.15.7
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Jan 09, 2018 at 07:50 AM
-- Server version: 5.6.36
-- PHP Version: 5.3.29

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `indoor_positioning`
--

-- --------------------------------------------------------

--
-- Table structure for table `access_points`
--

CREATE TABLE IF NOT EXISTS `access_points` (
  `building_id` char(50) NOT NULL,
  `ssid` char(50) NOT NULL,
  `mac_id` char(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `readings`
--

CREATE TABLE IF NOT EXISTS `readings` (
  `building_id` char(50) NOT NULL,
  `position_id` char(50) NOT NULL,
  `mac_id` char(50) NOT NULL,
  `ssid` char(50) NOT NULL,
  `rssi` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
