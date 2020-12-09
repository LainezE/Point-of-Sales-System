/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  Eli
 * Created: Oct 1, 2019
 */

/******* PoS Database Setup *******/
-- MySQL dump 10.13  Distrib 8.0.17, for Win64 (x86_64)
--
-- Host: 192.168.1.20    Database: pos
-- ------------------------------------------------------
-- Server version	8.0.17-0ubuntu2

--
-- Table structure for table `Employees`
--

DROP TABLE IF EXISTS `Employees`;
CREATE TABLE `Employees` (
  `Employee_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `First_Name` varchar(30) NOT NULL,
  `Last_Name` varchar(30) NOT NULL,
  `Title` varchar(30) NOT NULL,
  `Phone_Number` varchar(10) NOT NULL,
  `Email` varchar(60) NOT NULL,
  `Street` varchar(40) NOT NULL,
  `City` varchar(40) NOT NULL,
  `State` char(2) NOT NULL,
  `Zip` mediumint(8) unsigned NOT NULL,
  `Start_Date` date NOT NULL,
  PRIMARY KEY (`Employee_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Table structure for table `Customers`
--

DROP TABLE IF EXISTS `Customers`;
CREATE TABLE `Customers` (
  `Customer_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `First_Name` varchar(30) NOT NULL,
  `Last_Name` varchar(30) NOT NULL,
  `Phone_Number` varchar(10) NOT NULL,
  `Email` varchar(60) NOT NULL,
  `Street` varchar(40) NOT NULL,
  `City` varchar(40) NOT NULL,
  `State` char(2) NOT NULL,
  `Zip` mediumint(8) NOT NULL,
  `Join_Date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Customer_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

--
-- Table structure for table `Vendors`
--

DROP TABLE IF EXISTS `Vendors`;
CREATE TABLE `Vendors` (
  `Vendor_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Vendor_Name` varchar(60) NOT NULL,
  `Phone_Number` varchar(10) NOT NULL,
  `Email` varchar(60) NOT NULL,
  `Street` varchar(40) NOT NULL,
  `City` varchar(40) NOT NULL,
  `State` char(2) NOT NULL,
  `Zip` mediumint(8) NOT NULL,
  PRIMARY KEY (`Vendor_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `Inventory`
--

DROP TABLE IF EXISTS `Inventory`;
CREATE TABLE `Inventory` (
  `Serial_Number` varchar(50) NOT NULL,
  `Manufacturer` varchar(60) NOT NULL,
  `Model_Number` int(10) unsigned NOT NULL,
  `Model_Name` varchar(60) NOT NULL,
  `Description` varchar(200) NOT NULL,
  `Cost` decimal(13,4) NOT NULL,
  `MSRP` decimal(13,4) NOT NULL,
  `List_Price` decimal(13,4) NOT NULL,
  `Supplier_Link` varchar(100) NOT NULL,
  `Stock` int(10) unsigned NOT NULL,
  `ReOrder_Amount` int(10) unsigned NOT NULL,
  `Base_Stock` int(10) unsigned NOT NULL,
  PRIMARY KEY (`Serial_Number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `User_Accounts`
--

DROP TABLE IF EXISTS `User_Accounts`;
CREATE TABLE `User_Accounts` (
  `User_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Employee_ID` int(10) unsigned NOT NULL,
  `Username` varchar(30) NOT NULL,
  `Password` varchar(64) NOT NULL,
  `Access_Level` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`User_ID`),
  UNIQUE KEY `UNIQUE_UA_Username` (`Username`),
  KEY `IDX_UA_Employee_ID` (`Employee_ID`),
  CONSTRAINT `FK_UA_Employee_ID` FOREIGN KEY (`Employee_ID`) REFERENCES `Employees` (`Employee_ID`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1;

--
-- Table structure for table `Cart_Customer`
--

DROP TABLE IF EXISTS `Cart_Customer`;
CREATE TABLE `Cart_Customer` (
  `Cart_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Customer_ID` int(10) unsigned NOT NULL,
  `Employee_ID` int(10) unsigned NOT NULL,
  `Cart_DateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Cart_ID`),
  KEY `IDX_CC_Customer_ID` (`Customer_ID`),
  KEY `IDX_CC_Employee_ID` (`Employee_ID`),
  CONSTRAINT `FK_CC_Customer_ID` FOREIGN KEY (`Customer_ID`) REFERENCES `Customers` (`Customer_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_CC_Employee_ID` FOREIGN KEY (`Employee_ID`) REFERENCES `Employees` (`Employee_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;

--
-- Table structure for table `Cart_Customer_Items`
--

DROP TABLE IF EXISTS `Cart_Customer_Items`;
CREATE TABLE `Cart_Customer_Items` (
  `Cart_ID` int(10) unsigned NOT NULL,
  `Serial_Number` varchar(50) NOT NULL,
  `Quantity` int(10) unsigned NOT NULL,
  PRIMARY KEY (`Cart_ID`,`Serial_Number`),
  UNIQUE KEY `UNIQUE KEY` (`Cart_ID`,`Serial_Number`),
  KEY `IDX_CCI_Serial_Number` (`Serial_Number`),
  CONSTRAINT `FK_CCI_Cart_ID` FOREIGN KEY (`Cart_ID`) REFERENCES `Cart_Customer` (`Cart_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_CCI_Serial_Number` FOREIGN KEY (`Serial_Number`) REFERENCES `Inventory` (`Serial_Number`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `Cart_Vendor`
--

DROP TABLE IF EXISTS `Cart_Vendor`;
CREATE TABLE `Cart_Vendor` (
  `Cart_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Vendor_ID` int(10) unsigned NOT NULL,
  `Creation_DateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Cart_ID`),
  KEY `IDX_CV_Vendor_ID` (`Vendor_ID`),
  CONSTRAINT `FK_CV_Vendor_ID` FOREIGN KEY (`Vendor_ID`) REFERENCES `Vendors` (`Vendor_ID`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `Cart_Vendor_Items`
--

DROP TABLE IF EXISTS `Cart_Vendor_Items`;
CREATE TABLE `Cart_Vendor_Items` (
  `Cart_ID` int(10) unsigned NOT NULL,
  `Serial_Number` varchar(50) NOT NULL,
  `Quantity` int(10) unsigned NOT NULL,
  PRIMARY KEY (`Cart_ID`,`Serial_Number`),
  KEY `IDX_CVI_Serial_Number` (`Serial_Number`),
  CONSTRAINT `FK_CVI_Cart_ID` FOREIGN KEY (`Cart_ID`) REFERENCES `Cart_Vendor` (`Cart_ID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `FK_CVI_Serial_Number` FOREIGN KEY (`Serial_Number`) REFERENCES `Inventory` (`Serial_Number`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `Transaction_Customer`
--

DROP TABLE IF EXISTS `Transaction_Customer`;
CREATE TABLE `Transaction_Customer` (
  `Transaction_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Customer_ID` int(10) unsigned NOT NULL,
  `Employee_ID` int(10) unsigned NOT NULL,
  `Subtotal` decimal(15,4) unsigned NOT NULL,
  `Total` decimal(15,4) unsigned NOT NULL,
  `Payment_Type` enum('Credit','Cash') NOT NULL,
  `Transaction_DateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Transaction_ID`),
  KEY `IDX_TC_Customer_ID` (`Customer_ID`),
  KEY `IDX_TC_Employee_ID` (`Employee_ID`),
  CONSTRAINT `FK_TC_Customer_ID` FOREIGN KEY (`Customer_ID`) REFERENCES `Customers` (`Customer_ID`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_TC_Employee_ID` FOREIGN KEY (`Employee_ID`) REFERENCES `Employees` (`Employee_ID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Table structure for table `Transaction_Customer_Items`
--

DROP TABLE IF EXISTS `Transaction_Customer_Items`;
CREATE TABLE `Transaction_Customer_Items` (
  `Transaction_ID` int(10) unsigned NOT NULL,
  `Serial_Number` varchar(50) NOT NULL,
  `Quantity` int(10) unsigned NOT NULL,
  PRIMARY KEY (`Transaction_ID`,`Serial_Number`),
  KEY `IDX_TCI_Serial_Number` (`Serial_Number`),
  CONSTRAINT `FK_TCI_Serial_Number` FOREIGN KEY (`Serial_Number`) REFERENCES `Inventory` (`Serial_Number`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_TCI_Transaction_ID` FOREIGN KEY (`Transaction_ID`) REFERENCES `Transaction_Customer` (`Transaction_ID`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `Transaction_Vendor`
--

DROP TABLE IF EXISTS `Transaction_Vendor`;
CREATE TABLE `Transaction_Vendor` (
  `Transaction_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Vendor_ID` int(10) unsigned NOT NULL,
  `SubTotal_Price` decimal(15,4) unsigned NOT NULL,
  `Total_Price` decimal(15,4) unsigned NOT NULL,
  `Payment_Type` enum('Card','Cash') NOT NULL,
  PRIMARY KEY (`Transaction_ID`),
  KEY `IDX_TV_Vendor_ID` (`Vendor_ID`),
  CONSTRAINT `FK_TV_Vendor_ID` FOREIGN KEY (`Vendor_ID`) REFERENCES `Vendors` (`Vendor_ID`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `Transaction_Vendor_Items`
--

DROP TABLE IF EXISTS `Transaction_Vendor_Items`;
CREATE TABLE `Transaction_Vendor_Items` (
  `Transaction_ID` int(10) unsigned NOT NULL,
  `Serial_Number` varchar(50) NOT NULL,
  `Final Price` decimal(15,4) unsigned NOT NULL,
  `Quantity` int(10) unsigned NOT NULL,
  PRIMARY KEY (`Transaction_ID`),
  KEY `IDX_TVI_Serial_Number` (`Serial_Number`),
  CONSTRAINT `FK_TVI_Serial_Number` FOREIGN KEY (`Serial_Number`) REFERENCES `Inventory` (`Serial_Number`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_TVI_Transaction_ID` FOREIGN KEY (`Transaction_ID`) REFERENCES `Transaction_Vendor` (`Transaction_ID`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dump completed on 2019-10-29 22:22:10
