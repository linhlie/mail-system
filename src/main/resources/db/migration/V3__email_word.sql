CREATE TABLE `Emails_Words` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `message_id` VARCHAR(200) NOT NULL,
  `word_id` INT NOT NULL,
  `appear_indexs` TEXT DEFAULT NULL,
  FOREIGN KEY fk_belong_to_email(message_id)
  REFERENCES Emails(message_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY fk_belong_to_word(word_id)
  REFERENCES Words(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;