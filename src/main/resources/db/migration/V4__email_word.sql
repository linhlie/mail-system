CREATE TABLE `emails_words` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `message_id` VARCHAR(500) NOT NULL,
  `word_id` INT NOT NULL,
  `appear_indexs` TEXT DEFAULT NULL,
  FOREIGN KEY fk_belong_to_email(message_id)
  REFERENCES emails(message_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY fk_belong_to_word(word_id)
  REFERENCES words(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;