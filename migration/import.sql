DROP DATABASE IF EXISTS `mailsys`;
CREATE DATABASE `mailsys` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `mailsys`;

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `Key_Values`;
CREATE TABLE `Key_Values` (
  `key` VARCHAR(191) PRIMARY KEY,
  `value` TEXT DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `Users`;
CREATE TABLE `Users` (
  username VARCHAR(50)  NOT NULL PRIMARY KEY,
  password VARCHAR(255) NOT NULL,
  enabled  BOOLEAN      NOT NULL
) ENGINE = InnoDB;

DROP TABLE IF EXISTS `Authorities`;
CREATE TABLE `Authorities` (
  username  VARCHAR(50) NOT NULL,
  authority VARCHAR(50) NOT NULL,
  FOREIGN KEY (username) REFERENCES users (username),
  UNIQUE INDEX authorities_idx_1 (username, authority)
) ENGINE = InnoDB;

DROP TABLE IF EXISTS `Receive_Email_Account_Settings`;
CREATE TABLE `Receive_Email_Account_Settings` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `account` VARCHAR(60) NOT NULL,
  `password` VARCHAR(32) NOT NULL,
  `mail_server_address` VARCHAR(191) NOT NULL,
  `mail_server_port` INT NOT NULL,
  `receive_mail_protocol` SMALLINT(6) NOT NULL DEFAULT '0' COMMENT '0、IMAP 1. POP3',
  `encryption_protocol`SMALLINT(6) NOT NULL DEFAULT '0'COMMENT '0、なし 1. SSL/TLS, 2. STARTTLS',
  `authentication_protocol` SMALLINT(6) NOT NULL DEFAULT '0' COMMENT '0. 通常のパスワード認証 1. 暗号化されたパスワード認証 2. Kerberos/GSSAPI 3. NTLM 4. TLS証明書 5. OAuth2',
  `proxy_server` VARCHAR(191) DEFAULT NULL,
  `disabled` BOOLEAN DEFAULT FALSE,
  `created_at` DATETIME DEFAULT NULL,
  `updated_at` DATETIME DEFAULT NULL,
  UNIQUE KEY unique_account (account)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO Receive_Email_Account_Settings(account, password, mail_server_address, mail_server_port)
    VALUES ('khanhlvb@ows.vn', 'Lekhanh281', 'imap.gmail.com', 993);

INSERT INTO Receive_Email_Account_Settings(account, password, mail_server_address, mail_server_port, disabled)
    VALUES ('baokhanhlv@gmail.com', 'Lekhanh28011993', 'imap.gmail.com', 993, false);

INSERT INTO Receive_Email_Account_Settings(account, password, mail_server_address, mail_server_port)
    VALUES ('ows-test@world-link-system.com', 'o2018wa01e', 'af125.secure.ne.jp', 993);

DROP TABLE IF EXISTS `Emails`;
CREATE TABLE `Emails` (
  `message_id` VARCHAR(191) PRIMARY KEY,
  `account_id` INT NOT NULL,
  `from` VARCHAR(60) NOT NULL,
  `subject` TEXT COLLATE utf8mb4_unicode_ci NOT NULL,
  `to` TEXT NOT NULL,
  `cc` TEXT DEFAULT NULL,
  `bcc` TEXT DEFAULT NULL,
  `reply_to` VARCHAR(60) DEFAULT NULL,
  `sent_at` DATETIME NOT NULL,
  `received_at` DATETIME DEFAULT NULL,
  `has_attachment` BOOLEAN DEFAULT FALSE,
  `content_type` SMALLINT(6) NOT NULL DEFAULT '0' COMMENT '0、TEXT 1. HTML',
  `original_body`TEXT COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `optimized_body`TEXT COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `header` TEXT DEFAULT NULL,
  `created_at` DATETIME DEFAULT NULL,
  `meta_data` TEXT COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deleted` BOOLEAN DEFAULT FALSE,
  `deleted_at` DATETIME DEFAULT NULL,
  FOREIGN KEY fk_receive_email_account(account_id)
  REFERENCES Receive_Email_Account_Settings(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

# INSERT INTO Emails(message_id, account_id, `from`, subject, `to`, sent_at)
# VALUES ('abcd+khanhlvb@ows.vn', 1, 'abc', 'hello', 'khanhlvb@ows.vn', NOW());

DROP TABLE IF EXISTS `Files`;

CREATE TABLE `Files` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `message_id` VARCHAR(191) NOT NULL,
  `file_name` VARCHAR(60) NOT NULL,
  `storage_path` TEXT NOT NULL,
  `created_at` DATETIME DEFAULT NULL,
  `meta_data` TEXT DEFAULT NULL,
  `deleted` BOOLEAN DEFAULT FALSE,
  `deleted_at` DATETIME DEFAULT NULL,
  FOREIGN KEY fk_receive_email(message_id)
  REFERENCES Emails(message_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `Words`;
CREATE TABLE `Words` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `word` VARCHAR(191) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO Words(id, word)
VALUES (1, 'java');
INSERT INTO Words(id, word)
VALUES (2, 'JAVA');

DROP TABLE IF EXISTS `Fuzzy_Words`;
CREATE TABLE `Fuzzy_Words` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `word_id` INT NOT NULL,
  `with_word_id` INT NOT NULL,
  `fuzzy_type` SMALLINT(6) NOT NULL DEFAULT '1' COMMENT '0、除外 1. 同一',
  FOREIGN KEY fk_word(word_id)
  REFERENCES Words(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY fk_with_word(with_word_id)
  REFERENCES Words(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE Fuzzy_Words
ADD CONSTRAINT uc_fuzzy_word UNIQUE (word_id,with_word_id);

INSERT INTO Fuzzy_Words(word_id, with_word_id, fuzzy_type)
VALUES (1, 2, 1);

DROP TABLE IF EXISTS `Email_Word_Jobs`;
CREATE TABLE `Email_Word_Jobs` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `message_id` VARCHAR(191) NOT NULL,
  `word_id` INT NOT NULL,
  FOREIGN KEY fk_belong_to_email(message_id)
  REFERENCES Emails(message_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY fk_belong_to_word(word_id)
  REFERENCES Words(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE Email_Word_Jobs
ADD CONSTRAINT uc_email_word_job UNIQUE (message_id,word_id);

INSERT INTO Email_Word_Jobs(message_id, word_id)
VALUES ('khanhlvb@ows.vn+<001a114425824f4509056431a819@google.com>', 2);

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
      INSERT INTO Email_Word_Jobs (message_id, word_id) values (new.message_id, c1);
    END LOOP;
  CLOSE cur;
END#
DELIMITER ;

DELIMITER //
CREATE TRIGGER ins_word AFTER INSERT ON Words
FOR EACH ROW
BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE c1 VARCHAR(191);
  DECLARE cur CURSOR FOR SELECT message_id FROM Emails WHERE Emails.deleted = FALSE;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
  OPEN cur;
    ins_loop: LOOP
      FETCH cur INTO c1;
      IF done THEN
        LEAVE ins_loop;
      END IF;
      INSERT INTO Email_Word_Jobs (message_id, word_id) values (c1, new.id);
    END LOOP;
  CLOSE cur;
END//
DELIMITER ;

CREATE TABLE `Emails_Words` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `message_id` VARCHAR(191) NOT NULL,
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

ALTER TABLE Emails_Words
ADD CONSTRAINT uc_email_word UNIQUE (message_id,word_id);

COMMIT;

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

INSERT INTO Replace_Numbers(`character`, replace_value)
VALUES ('K', 1000);
INSERT INTO Replace_Numbers(`character`, replace_value)
VALUES ('千', 1000);
INSERT INTO Replace_Numbers(`character`, replace_value)
VALUES ('万', 10000);

INSERT INTO Replace_Units(unit, replace_unit)
VALUES ('円', '円');
INSERT INTO Replace_Units(unit, replace_unit)
VALUES ('YEN', '円');

CREATE TABLE `Replace_Letters` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `letter` VARCHAR(191) NOT NULL,
  `position` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、数値の前の 1. 数値の後の',
  `replace` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、以上として認識する 1. 以下として認識する 2. 未満として認識する 3. 超として認識する'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE Replace_Letters
ADD CONSTRAINT uc_replace_letter UNIQUE (letter, position);

INSERT INTO Replace_Letters(letter, position, `replace`)
VALUES ('~', 0, 0);
INSERT INTO Replace_Letters(letter, position, `replace`)
VALUES ('~', 1, 1);

CREATE TABLE `Number_Treatments` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(191) NOT NULL,
  `upper_limit_name` VARCHAR(191) NOT NULL,
  `upper_limit_sign` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、x',
  `upper_limit_rate` DOUBLE NOT NULL,
  `lower_limit_name` VARCHAR(191) NOT NULL,
  `lower_limit_sign` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、x',
  `lower_limit_rate` DOUBLE NOT NULL,
  `left_boundary_value` DOUBLE DEFAULT NULL,
  `left_boundary_operator` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、以上を数字として扱う 1. 超を通じとして扱う',
  `combine_operator` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、AND 1. OR',
  `right_boundary_value` DOUBLE DEFAULT NULL,
  `right_boundary_operator` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、以下を数字として扱う 1. 未満を通じとして扱う',
  `enable_replace_letter` BOOLEAN DEFAULT FALSE,
  UNIQUE KEY unique_mumber_treatment_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO Number_Treatments(name, upper_limit_name, upper_limit_rate, lower_limit_name, lower_limit_rate)
VALUES ('name', 'upper_limit_name', 1.2, 'lower_limit_name', 0.8);
