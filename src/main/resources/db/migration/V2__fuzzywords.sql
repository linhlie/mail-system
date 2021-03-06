CREATE TABLE `words` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `word` VARCHAR(191) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE `fuzzy_words` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `word_id` INT NOT NULL,
  `with_word_id` INT NOT NULL,
  `fuzzy_type` SMALLINT(6) NOT NULL DEFAULT '1' COMMENT '0、除外 1. 同一',
  FOREIGN KEY fk_word(word_id)
  REFERENCES words(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY fk_with_word(with_word_id)
  REFERENCES words(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;