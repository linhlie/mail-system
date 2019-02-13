CREATE TABLE `condition_notification` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `from_account_id` INT NOT NULL,
  `to_account_id` INT NOT NULL,
  `condition` MEDIUMTEXT NOT NULL,
  `condition_type` INT(4) NOT NULL,
  `sent_at` DATETIME NOT NULL,
  `status` INT(4) NOT NULL DEFAULT 0,
  FOREIGN KEY fk_account_condition_from(from_account_id)
  REFERENCES accounts(id)
  ON DELETE CASCADE,
  FOREIGN KEY fk_account_condition_to(to_account_id)
  REFERENCES accounts(id)
  ON DELETE CASCADE
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;