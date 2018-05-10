CREATE TABLE `upload_files` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `file_name` VARCHAR(191) NOT NULL,
  `storage_path` TEXT NOT NULL,
  `created_at` DATETIME DEFAULT NULL,
  `size` LONG
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;