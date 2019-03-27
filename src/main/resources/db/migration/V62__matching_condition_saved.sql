CREATE TABLE `matching_condition_saved` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `account_created_id` INT NOT NULL,
  `condition_name` VARCHAR(100) NOT NULL,
  `condition` MEDIUMTEXT NOT NULL,
  `condition_type` INT NOT NULL,
  FOREIGN KEY fk_account_matching_condition(account_created_id)
  REFERENCES accounts(id)
  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;