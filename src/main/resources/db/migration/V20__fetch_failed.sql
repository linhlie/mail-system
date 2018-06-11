CREATE TABLE `fetch_mail_error_logs` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `created_at` DATETIME DEFAULT NULL,
  `error_log` TEXT DEFAULT NULL
) ENGINE = InnoDB;
