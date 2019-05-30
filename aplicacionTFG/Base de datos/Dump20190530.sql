-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: aplicacionbdtfg
-- ------------------------------------------------------
-- Server version	5.5.5-10.1.30-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `confirmacion`
--

DROP TABLE IF EXISTS `confirmacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `confirmacion` (
  `Usuario_idUsuario` int(11) NOT NULL,
  `Elemento_idElemento` int(11) NOT NULL,
  `Elemento_Usuario_idUsuario` int(11) NOT NULL,
  PRIMARY KEY (`Usuario_idUsuario`,`Elemento_idElemento`,`Elemento_Usuario_idUsuario`),
  KEY `fk_Usuario_has_Elemento_Elemento1_idx` (`Elemento_idElemento`,`Elemento_Usuario_idUsuario`),
  KEY `fk_Usuario_has_Elemento_Usuario1_idx` (`Usuario_idUsuario`),
  CONSTRAINT `fk_Usuario_has_Elemento_Elemento1` FOREIGN KEY (`Elemento_idElemento`, `Elemento_Usuario_idUsuario`) REFERENCES `elemento` (`idElemento`, `Usuario_idUsuario`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Usuario_has_Elemento_Usuario1` FOREIGN KEY (`Usuario_idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `elemento`
--

DROP TABLE IF EXISTS `elemento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `elemento` (
  `idElemento` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  `descripcion` varchar(45) DEFAULT NULL,
  `coordenada` varchar(75) DEFAULT NULL,
  `longitud` double DEFAULT NULL,
  `latitud` double DEFAULT NULL,
  `lugar` varchar(45) DEFAULT NULL,
  `tipo` varchar(45) NOT NULL,
  `accesible` tinyint(1) DEFAULT NULL,
  `Usuario_idUsuario` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`idElemento`,`Usuario_idUsuario`),
  KEY `fk_Elemento_Usuario1_idx` (`Usuario_idUsuario`),
  CONSTRAINT `fk_Elemento_Usuario1` FOREIGN KEY (`Usuario_idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `necesidad`
--

DROP TABLE IF EXISTS `necesidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `necesidad` (
  `idNecesidad` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idNecesidad`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `necesidad_has_elemento`
--

DROP TABLE IF EXISTS `necesidad_has_elemento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `necesidad_has_elemento` (
  `Necesidad_idNecesidad` int(11) NOT NULL,
  `Elemento_idElemento` int(11) NOT NULL,
  PRIMARY KEY (`Necesidad_idNecesidad`,`Elemento_idElemento`),
  KEY `fk_Necesidad_has_Elemento_Elemento1_idx` (`Elemento_idElemento`),
  KEY `fk_Necesidad_has_Elemento_Necesidad1_idx` (`Necesidad_idNecesidad`),
  CONSTRAINT `fk_Necesidad_has_Elemento_Elemento1` FOREIGN KEY (`Elemento_idElemento`) REFERENCES `elemento` (`idElemento`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Necesidad_has_Elemento_Necesidad1` FOREIGN KEY (`Necesidad_idNecesidad`) REFERENCES `necesidad` (`idNecesidad`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ruta`
--

DROP TABLE IF EXISTS `ruta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ruta` (
  `idRuta` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  `inicio` varchar(45) DEFAULT NULL,
  `final` varchar(45) DEFAULT NULL,
  `tipo` varchar(45) DEFAULT NULL,
  `Usuario_idUsuario` int(11) NOT NULL,
  PRIMARY KEY (`idRuta`,`Usuario_idUsuario`),
  KEY `fk_Ruta_Usuario1_idx` (`Usuario_idUsuario`),
  CONSTRAINT `fk_Ruta_Usuario1` FOREIGN KEY (`Usuario_idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sugerencias`
--

DROP TABLE IF EXISTS `sugerencias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sugerencias` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sugerencia` longtext,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuario` (
  `idUsuario` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  `usuario` varchar(45) DEFAULT NULL,
  `contraseña` varchar(45) DEFAULT NULL,
  `edad` int(11) DEFAULT NULL,
  `mostrar` tinyint(1) DEFAULT NULL,
  `foto` mediumblob,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`idUsuario`),
  UNIQUE KEY `usuario` (`usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario_has_necesidad`
--

DROP TABLE IF EXISTS `usuario_has_necesidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuario_has_necesidad` (
  `Usuario_idUsuario` int(11) NOT NULL,
  `Necesidad_idNecesidad` int(11) NOT NULL,
  PRIMARY KEY (`Usuario_idUsuario`,`Necesidad_idNecesidad`),
  KEY `fk_Usuario_has_Necesidad_Necesidad1_idx` (`Necesidad_idNecesidad`),
  KEY `fk_Usuario_has_Necesidad_Usuario_idx` (`Usuario_idUsuario`),
  CONSTRAINT `fk_Usuario_has_Necesidad_Necesidad1` FOREIGN KEY (`Necesidad_idNecesidad`) REFERENCES `necesidad` (`idNecesidad`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Usuario_has_Necesidad_Usuario` FOREIGN KEY (`Usuario_idUsuario`) REFERENCES `usuario` (`idUsuario`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'aplicacionbdtfg'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-05-30 23:52:04
