CREATE TABLE `number_ranges` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `message_id` VARCHAR(191) NOT NULL,
  `number` DOUBLE DEFAULT NULL,
  `letter_id` INT DEFAULT NULL,
  `appear_order` INT NOT NULL DEFAULT '0',
  FOREIGN KEY fk_belong_to_email(message_id)
  REFERENCES emails(message_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY fk_with_letter_id(letter_id)
  REFERENCES replace_letters(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;