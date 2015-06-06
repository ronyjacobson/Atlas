-- MySQL Script generated by MySQL Workbench
-- 06/06/15 16:40:27
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema DbMysql06
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema DbMysql06
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `DbMysql06` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `DbMysql06` ;

-- -----------------------------------------------------
-- Table `DbMysql06`.`location`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DbMysql06`.`location` (
  `geo_name` VARCHAR(100) NOT NULL,
  `latitude` DOUBLE NOT NULL,
  `longtitude` DOUBLE NOT NULL,
  `wikiURL` VARCHAR(100) NULL,
  `geo_ID` INT NOT NULL,
  `yago_ID` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`geo_ID`),
  UNIQUE INDEX `yago_ID_UNIQUE` (`yago_ID` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DbMysql06`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DbMysql06`.`user` (
  `username` VARCHAR(16) NOT NULL,
  `password` VARCHAR(32) NOT NULL,
  `wasBornOnDate` DATE NULL,
  `wasBornInLocation` INT NOT NULL,
  `user_ID` INT NOT NULL AUTO_INCREMENT,
  INDEX `fk_user_location1_idx` (`wasBornInLocation` ASC),
  PRIMARY KEY (`user_ID`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  CONSTRAINT `fk_user_location1`
    FOREIGN KEY (`wasBornInLocation`)
    REFERENCES `DbMysql06`.`location` (`geo_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `DbMysql06`.`person`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DbMysql06`.`person` (
  `person_ID` INT NOT NULL AUTO_INCREMENT,
  `wikiURL` VARCHAR(100) NULL,
  `diedOnDate` DATE NULL,
  `wasBornOnDate` DATE NOT NULL,
  `addedByUser` INT NOT NULL,
  `wasBornInLocation` INT NOT NULL,
  `diedInLocation` INT NULL,
  `isFemale` TINYINT(1) NOT NULL,
  `yago_ID` VARCHAR(100) NULL,
  PRIMARY KEY (`person_ID`),
  INDEX `fk_person_user1_idx` (`addedByUser` ASC),
  INDEX `fk_person_location1_idx` (`wasBornInLocation` ASC),
  INDEX `fk_person_location2_idx` (`diedInLocation` ASC),
  UNIQUE INDEX `yago_ID_UNIQUE` (`yago_ID` ASC),
  CONSTRAINT `fk_person_user1`
    FOREIGN KEY (`addedByUser`)
    REFERENCES `DbMysql06`.`user` (`user_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_person_location1`
    FOREIGN KEY (`wasBornInLocation`)
    REFERENCES `DbMysql06`.`location` (`geo_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_person_location2`
    FOREIGN KEY (`diedInLocation`)
    REFERENCES `DbMysql06`.`location` (`geo_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DbMysql06`.`category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DbMysql06`.`category` (
  `category_ID` INT NOT NULL AUTO_INCREMENT,
  `categoryName` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`category_ID`));


-- -----------------------------------------------------
-- Table `DbMysql06`.`person_has_category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DbMysql06`.`person_has_category` (
  `person_ID` INT NOT NULL,
  `category_ID` INT NOT NULL,
  PRIMARY KEY (`person_ID`, `category_ID`),
  INDEX `fk_person_has_category_category1_idx` (`category_ID` ASC),
  INDEX `fk_person_has_category_person1_idx` (`person_ID` ASC),
  CONSTRAINT `fk_person_has_category_person1`
    FOREIGN KEY (`person_ID`)
    REFERENCES `DbMysql06`.`person` (`person_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_person_has_category_category1`
    FOREIGN KEY (`category_ID`)
    REFERENCES `DbMysql06`.`category` (`category_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DbMysql06`.`person_labels`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DbMysql06`.`person_labels` (
  `label` VARCHAR(100) NOT NULL,
  `person_ID` INT NOT NULL,
  `isPref` TINYINT(1) NULL COMMENT 'Should be true only for one row',
  INDEX `fk_person_labels_person1_idx` (`person_ID` ASC),
  PRIMARY KEY (`label`),
  CONSTRAINT `fk_person_labels_person1`
    FOREIGN KEY (`person_ID`)
    REFERENCES `DbMysql06`.`person` (`person_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DbMysql06`.`user_favorites`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DbMysql06`.`user_favorites` (
  `user_ID` INT NOT NULL,
  `person_ID` INT NOT NULL,
  PRIMARY KEY (`user_ID`, `person_ID`),
  INDEX `fk_user_has_person_person1_idx` (`person_ID` ASC),
  INDEX `fk_user_has_person_user1_idx` (`user_ID` ASC),
  CONSTRAINT `fk_user_has_person_user1`
    FOREIGN KEY (`user_ID`)
    REFERENCES `DbMysql06`.`user` (`user_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_person_person1`
    FOREIGN KEY (`person_ID`)
    REFERENCES `DbMysql06`.`person` (`person_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
