CREATE TABLE `Replace_Numbers` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `character` VARCHAR(191) NOT NULL,
  `replace_value` INT NOT NULL,
  UNIQUE KEY unique_character (`character`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `Replace_Units` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `unit` VARCHAR(191) NOT NULL,
  `replace_unit` VARCHAR(191) NOT NULL,
  UNIQUE KEY unique_unit (unit)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;