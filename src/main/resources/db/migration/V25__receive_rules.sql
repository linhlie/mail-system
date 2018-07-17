CREATE TABLE `receive_rules` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `type` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、Receive 1、Mark A 2、Mark B',
  `name` VARCHAR(191) NOT NULL,
  `rule` TEXT DEFAULT NULL,
  `last_update` DATETIME NOT NULL,
  UNIQUE KEY unique_rule_name_type (name, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;