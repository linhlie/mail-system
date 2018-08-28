CREATE TABLE `engineers` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL,
  `kana_name` VARCHAR(255) NOT NULL,
  `mail_address` VARCHAR(255) DEFAULT NULL,
  `employment_status` SMALLINT(6) NOT NULL DEFAULT '1' COMMENT '1. 自社正社員 2. 自社契約社員 3. 自社個人事業主 4. 自社嘱託その他 5. 取引先正社員 6. 取引先契約社員 7.取引先個人事業主 8.取引先雇用形態不明',
  `partner_id` INT NOT NULL,
  `project_period_start` LONG NOT NULL,
  `project_period_end` LONG NOT NULL,
  `auto_extend` BOOLEAN DEFAULT FALSE,
  `extend_month` INT DEFAULT NULL,
  `matching_word` TEXT DEFAULT NULL,
  `not_good_word` TEXT DEFAULT NULL,
  `monetary_money` VARCHAR(255) DEFAULT NULL,
  `station_line` VARCHAR(255) DEFAULT NULL,
  `station_nearest` VARCHAR(255) DEFAULT NULL,
  `commuting_time` DOUBLE DEFAULT NULL,
  `dormant` BOOLEAN DEFAULT FALSE,
  FOREIGN KEY fk_business_partner(partner_id)
  REFERENCES business_partners(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;