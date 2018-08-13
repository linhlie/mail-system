CREATE TABLE `business_partners` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `partner_code` VARCHAR(20) NOT NULL,
  `name` VARCHAR(191) NOT NULL,
  `kana_name` VARCHAR(191) DEFAULT NULL,
  `company_type` SMALLINT(6) NOT NULL DEFAULT '1' COMMENT '1. 株式会社 2. 有限会社 3. 合名会社 4. 株式会社 5. 財団法人 6. 社団法人 7.その他',
  `company_specific_type` VARCHAR(191) DEFAULT NULL,
  `stock_share` SMALLINT(6) NOT NULL DEFAULT '1' COMMENT '1. 前 2. 後',
  `domain1` TEXT NOT NULL,
  `domain2` TEXT DEFAULT NULL,
  `domain3` TEXT DEFAULT NULL,
  `our_company` BOOLEAN DEFAULT FALSE,
  UNIQUE KEY unique_partner_code (partner_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;