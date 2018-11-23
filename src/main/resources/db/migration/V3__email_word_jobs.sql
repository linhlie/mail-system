CREATE TABLE `email_word_jobs` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `message_id` VARCHAR(500) NOT NULL,
  `word_id` INT NOT NULL,
  FOREIGN KEY fk_belong_to_email(message_id)
  REFERENCES emails(message_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY fk_belong_to_word(word_id)
  REFERENCES words(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DELIMITER #
CREATE TRIGGER ins_email AFTER INSERT ON emails
FOR EACH ROW
BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE c1 INT;
  DECLARE cur CURSOR FOR SELECT id FROM words;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
  OPEN cur;
    ins_loop: LOOP
      FETCH cur INTO c1;
      IF done THEN
        LEAVE ins_loop;
      END IF;
      INSERT INTO email_word_jobs (message_id, word_id) values (new.message_id, c1);
    END LOOP;
  CLOSE cur;
END#
DELIMITER ;

DELIMITER //
CREATE TRIGGER ins_word AFTER INSERT ON words
FOR EACH ROW
BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE c1 VARCHAR(191);
  DECLARE cur CURSOR FOR SELECT message_id FROM emails WHERE emails.deleted = FALSE;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
  OPEN cur;
    ins_loop: LOOP
      FETCH cur INTO c1;
      IF done THEN
        LEAVE ins_loop;
      END IF;
      INSERT INTO email_word_jobs (message_id, word_id) values (c1, new.id);
    END LOOP;
  CLOSE cur;
END//
DELIMITER ;