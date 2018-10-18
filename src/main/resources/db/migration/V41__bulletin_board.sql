CREATE TABLE `bulletin_board` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `bulletin` MEDIUMTEXT NULL,
  `account_id` INT NOT NULL,
  `time_edit` DATETIME NOT NULL,
  FOREIGN KEY fk_account_bulletin(account_id)
  REFERENCES accounts(id)
  ON DELETE CASCADE
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
