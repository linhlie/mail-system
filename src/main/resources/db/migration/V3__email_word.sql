CREATE TABLE `Emails_Words` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `message_id` VARCHAR(200) NOT NULL,
  `word_id` INT NOT NULL,
  `appear_indexs` TEXT DEFAULT NULL,
  `state` SMALLINT(6) DEFAULT '0' COMMENT '0、NEW 1. DONE',
  FOREIGN KEY fk_belong_to_email(message_id)
  REFERENCES Emails(message_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY fk_belong_to_word(word_id)
  REFERENCES Words(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DELIMITER #
CREATE TRIGGER ins_email AFTER INSERT ON Emails
FOR EACH ROW
BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE c1 INT;
  DECLARE cur CURSOR FOR SELECT id FROM Words;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
  OPEN cur;
    ins_loop: LOOP
      FETCH cur INTO c1;
      IF done THEN
        LEAVE ins_loop;
      END IF;
      INSERT INTO Emails_Words (message_id, word_id, appear_indexs) values (new.message_id, c1 , '');
    END LOOP;
  CLOSE cur;
END#
DELIMITER ;

DELIMITER //
CREATE TRIGGER ins_word AFTER INSERT ON Words
FOR EACH ROW
BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE c1 VARCHAR(200);
  DECLARE cur CURSOR FOR SELECT message_id FROM Emails WHERE Emails.deleted = FALSE;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
  OPEN cur;
    ins_loop: LOOP
      FETCH cur INTO c1;
      IF done THEN
        LEAVE ins_loop;
      END IF;
      INSERT INTO Emails_Words (message_id, word_id, appear_indexs) values (c1, new.id , '');
    END LOOP;
  CLOSE cur;
END//
DELIMITER ;