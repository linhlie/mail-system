CREATE TABLE `email_in_charge_unregister` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `email` VARCHAR(255) NOT NULL,
  `status` INT(5) NOT NULL DEFAULT 1,
  UNIQUE KEY unique_domain (domain)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;