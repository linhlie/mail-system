CREATE TABLE `people_in_charge_partner_unregister` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `email` VARCHAR(255) NOT NULL,
  `status` INT(5) NOT NULL DEFAULT 1,
  UNIQUE KEY unique_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;