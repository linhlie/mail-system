CREATE TABLE `number_treatments` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(191) NOT NULL,
  `upper_limit_name` VARCHAR(191) NOT NULL,
  `upper_limit_sign` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、x',
  `upper_limit_rate` DOUBLE NOT NULL,
  `lower_limit_name` VARCHAR(191) NOT NULL,
  `lower_limit_sign` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、x',
  `lower_limit_rate` DOUBLE NOT NULL,
  `left_boundary_value` DOUBLE DEFAULT NULL,
  `left_boundary_operator` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、以上を数字として扱う 3. 超を通じとして扱う',
  `combine_operator` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、AND 1. OR',
  `right_boundary_value` DOUBLE DEFAULT NULL,
  `right_boundary_operator` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '1、以下を数字として扱う 2. 未満を通じとして扱う',
  `enable_replace_letter` BOOLEAN DEFAULT FALSE,
  UNIQUE KEY unique_mumber_treatment_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;