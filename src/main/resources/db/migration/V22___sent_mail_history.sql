CREATE TABLE `sent_mail_histories` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `message_id` VARCHAR(191) NOT NULL COLLATE utf8_unicode_ci,
  `matching_message_id` VARCHAR(191) DEFAULT NULL COLLATE utf8_unicode_ci,
  `account_id` INT NOT NULL,
  `from` VARCHAR(120) NOT NULL,
  `subject` TEXT NOT NULL,
  `to` TEXT NOT NULL,
  `cc` TEXT DEFAULT NULL,
  `bcc` TEXT DEFAULT NULL,
  `reply_to` VARCHAR(120) DEFAULT NULL,
  `sent_at` DATETIME NOT NULL,
  `original_received_at` DATETIME DEFAULT NULL,
  `matching_received_at` DATETIME DEFAULT NULL,
  `has_attachment` BOOLEAN DEFAULT FALSE,
  `body` MEDIUMTEXT DEFAULT NULL,
  `send_type` VARCHAR(20) DEFAULT NULL,
  `matching_mail_address` VARCHAR(120) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;