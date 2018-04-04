-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 04, 2018 at 02:29 PM
-- Server version: 10.1.30-MariaDB
-- PHP Version: 7.0.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `conferences`
--

-- --------------------------------------------------------

--
-- Table structure for table `available_proceedings`
--

CREATE TABLE `available_proceedings` (
  `id` int(11) NOT NULL,
  `proceeding` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `available_proceedings`
--

INSERT INTO `available_proceedings` (`id`, `proceeding`) VALUES
(1, 'ACM'),
(2, 'SPEC'),
(3, 'Springer'),
(4, 'IEEE'),
(5, 'IFIP');

-- --------------------------------------------------------

--
-- Table structure for table `available_sponsors`
--

CREATE TABLE `available_sponsors` (
  `id` int(11) NOT NULL,
  `sponsor` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `available_sponsors`
--

INSERT INTO `available_sponsors` (`id`, `sponsor`) VALUES
(1, 'ACM'),
(2, 'SPEC'),
(3, 'UNESCO'),
(4, 'IEEE'),
(5, 'AFIS'),
(6, 'INCOSE');

-- --------------------------------------------------------

--
-- Table structure for table `committees`
--

CREATE TABLE `committees` (
  `id` int(11) NOT NULL,
  `titleID` int(11) NOT NULL,
  `memberID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `committee_titles`
--

CREATE TABLE `committee_titles` (
  `id` int(11) NOT NULL,
  `committee_title` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `deadlines`
--

CREATE TABLE `deadlines` (
  `id` int(11) NOT NULL,
  `deadline_type` int(11) NOT NULL,
  `deadline_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `deadlines_titles`
--

CREATE TABLE `deadlines_titles` (
  `id` int(11) NOT NULL,
  `d_title` varchar(150) NOT NULL,
  `d_date` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `deadline_types`
--

CREATE TABLE `deadline_types` (
  `id` int(11) NOT NULL,
  `d_type` varchar(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `links_to_crawl`
--

CREATE TABLE `links_to_crawl` (
  `id` int(11) NOT NULL,
  `link` varchar(400) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `links_to_crawl`
--

INSERT INTO `links_to_crawl` (`id`, `link`) VALUES
(1, 'https://icpe2018.spec.org/home.html'),
(2, 'http://lsds.hesge.ch/ISPDC2018/'),
(3, 'https://unescoprivacychair.urv.cat/psd2018/index.php'),
(4, 'https://2018.splashcon.org/home'),
(5, 'https://pldi18.sigplan.org/home'),
(6, 'https://2018.ecoop.org/'),
(7, 'https://2018.fseconference.org/home'),
(8, 'https://www.icse2018.org/'),
(9, 'https://conf.researchr.org/home/issta-2018'),
(10, 'https://conf.researchr.org/home/icgse-2018'),
(11, 'https://itrust.sutd.edu.sg/hase2017/'),
(12, 'http://www.ispass.org/ispass2018/'),
(13, 'https://www.computer.org/web/compsac2018'),
(14, 'https://www.isf.cs.tu-bs.de/cms/events/sefm2018/'),
(15, 'http://www.es.mdh.se/icst2018/'),
(16, 'https://icssea.org/'),
(17, 'http://www.icsoft.org/'),
(18, 'http://issre.net/'),
(19, 'https://sites.uoit.ca/ifiptm2018/index.php'),
(20, 'http://cseet2017.com/'),
(21, 'http://www.ieee-iccse.org/');

-- --------------------------------------------------------

--
-- Table structure for table `member_names`
--

CREATE TABLE `member_names` (
  `id` int(11) NOT NULL,
  `member` varchar(300) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `potential_committee_names`
--

CREATE TABLE `potential_committee_names` (
  `id` int(11) NOT NULL,
  `potential_com_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `potential_committee_names`
--

INSERT INTO `potential_committee_names` (`id`, `potential_com_name`) VALUES
(1, 'committee'),
(2, 'chair'),
(3, 'paper'),
(4, 'member');

-- --------------------------------------------------------

--
-- Table structure for table `venues`
--

CREATE TABLE `venues` (
  `id` int(11) NOT NULL,
  `venue` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `websites`
--

CREATE TABLE `websites` (
  `id` int(11) NOT NULL,
  `acronym` varchar(8) NOT NULL DEFAULT '',
  `link` varchar(1000) NOT NULL DEFAULT '',
  `title` varchar(300) NOT NULL DEFAULT '',
  `description` varchar(3000) NOT NULL DEFAULT '',
  `conference_days` varchar(50) NOT NULL DEFAULT '',
  `sponsors` varchar(100) NOT NULL DEFAULT '',
  `proceedings` varchar(100) NOT NULL DEFAULT '',
  `venueID` int(11) DEFAULT NULL,
  `current_year` int(11) DEFAULT NULL,
  `antiquity` varchar(50) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `available_proceedings`
--
ALTER TABLE `available_proceedings`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `available_sponsors`
--
ALTER TABLE `available_sponsors`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `committees`
--
ALTER TABLE `committees`
  ADD PRIMARY KEY (`id`,`titleID`,`memberID`),
  ADD KEY `titleID` (`titleID`),
  ADD KEY `memberID` (`memberID`);

--
-- Indexes for table `committee_titles`
--
ALTER TABLE `committee_titles`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `deadlines`
--
ALTER TABLE `deadlines`
  ADD PRIMARY KEY (`id`,`deadline_type`,`deadline_id`),
  ADD KEY `deadlines_ibfk_1` (`deadline_type`),
  ADD KEY `deadlines_ibfk_2` (`deadline_id`);

--
-- Indexes for table `deadlines_titles`
--
ALTER TABLE `deadlines_titles`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `deadline_types`
--
ALTER TABLE `deadline_types`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `links_to_crawl`
--
ALTER TABLE `links_to_crawl`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `member_names`
--
ALTER TABLE `member_names`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `potential_committee_names`
--
ALTER TABLE `potential_committee_names`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `venues`
--
ALTER TABLE `venues`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `websites`
--
ALTER TABLE `websites`
  ADD PRIMARY KEY (`id`),
  ADD KEY `venue` (`venueID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `available_proceedings`
--
ALTER TABLE `available_proceedings`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `available_sponsors`
--
ALTER TABLE `available_sponsors`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `committee_titles`
--
ALTER TABLE `committee_titles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `deadlines_titles`
--
ALTER TABLE `deadlines_titles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `deadline_types`
--
ALTER TABLE `deadline_types`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `links_to_crawl`
--
ALTER TABLE `links_to_crawl`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `member_names`
--
ALTER TABLE `member_names`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `potential_committee_names`
--
ALTER TABLE `potential_committee_names`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `venues`
--
ALTER TABLE `venues`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `websites`
--
ALTER TABLE `websites`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `committees`
--
ALTER TABLE `committees`
  ADD CONSTRAINT `committees_ibfk_1` FOREIGN KEY (`titleID`) REFERENCES `committee_titles` (`id`) ON DELETE NO ACTION,
  ADD CONSTRAINT `committees_ibfk_2` FOREIGN KEY (`memberID`) REFERENCES `member_names` (`id`) ON DELETE NO ACTION,
  ADD CONSTRAINT `committees_ibfk_3` FOREIGN KEY (`id`) REFERENCES `websites` (`id`);

--
-- Constraints for table `deadlines`
--
ALTER TABLE `deadlines`
  ADD CONSTRAINT `deadlines_ibfk_1` FOREIGN KEY (`deadline_type`) REFERENCES `deadline_types` (`id`) ON DELETE NO ACTION,
  ADD CONSTRAINT `deadlines_ibfk_2` FOREIGN KEY (`deadline_id`) REFERENCES `deadlines_titles` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `deadlines_ibfk_3` FOREIGN KEY (`id`) REFERENCES `websites` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `websites`
--
ALTER TABLE `websites`
  ADD CONSTRAINT `websites_ibfk_1` FOREIGN KEY (`venueID`) REFERENCES `venues` (`id`) ON DELETE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
