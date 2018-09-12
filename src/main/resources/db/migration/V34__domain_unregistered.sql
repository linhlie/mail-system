CREATE TABLE `domain_unregister` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `domain` VARCHAR(255) NOT NULL,
  UNIQUE KEY unique_domain (domain)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
