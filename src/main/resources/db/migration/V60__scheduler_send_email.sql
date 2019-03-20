CREATE TABLE `scheduler_send_email` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `account_id` INT NOT NULL,
  `from` VARCHAR(120) NOT NULL,
  `subject` TEXT NOT NULL,
  `to` TEXT NOT NULL,
  `cc` TEXT DEFAULT NULL,
  `bcc` TEXT DEFAULT NULL,
  `sent_at` DATETIME NOT NULL,
  `has_attachment` BOOLEAN DEFAULT FALSE,
  `body` MEDIUMTEXT DEFAULT NULL,
  `account_sent_mail_id` INT NOT NULL,
  `type_send_email` INT NOT NULL,
  `date_send_email` VARCHAR(120) NULL,
  `hour_send_email` VARCHAR(120) NULL,
  `status` INT NOT NULL,
  FOREIGN KEY fk_scheduler_user(account_id)
  REFERENCES accounts(id)
      ON DELETE CASCADE,
  FOREIGN KEY fk_scheduler_account(account_sent_mail_id)
  REFERENCES email_accounts(id)
      ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;