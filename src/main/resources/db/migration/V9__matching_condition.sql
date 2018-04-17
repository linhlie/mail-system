CREATE TABLE `matching_conditions` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `group` BOOLEAN DEFAULT FALSE,
  `combine`SMALLINT(6) NOT NULL DEFAULT '-1' COMMENT '-1、NONE O. AND 1.OR',
  `item`SMALLINT(6) NOT NULL DEFAULT '-1',
  `condition`SMALLINT(6) NOT NULL DEFAULT '-1',
  `value` VARCHAR(191) DEFAULT NULL,
  `type` TINYINT(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;