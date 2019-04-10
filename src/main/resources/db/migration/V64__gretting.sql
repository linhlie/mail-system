CREATE TABLE `greeting` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `account_created_id` INT NOT NULL,
  `email_account_id` INT NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `greeting` MEDIUMTEXT DEFAULT NULL,
  `greeting_type` INT NOT NULL,
  `active` BOOLEAN DEFAULT FALSE,
  FOREIGN KEY fk_account_gretting(account_created_id)
    REFERENCES accounts(id)
    ON DELETE CASCADE,
  FOREIGN KEY fk_email_account_gretting(email_account_id)
    REFERENCES email_accounts(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;