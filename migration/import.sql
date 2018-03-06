DROP DATABASE IF EXISTS `mailsys`;
CREATE DATABASE `mailsys` CHARACTER SET utf8 COLLATE utf8_unicode_ci;
USE `mailsys`;

SET NAMES utf8 COLLATE utf8_unicode_ci;
SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `key_values`;
CREATE TABLE `key_values` (
  `key` VARCHAR(191) PRIMARY KEY,
  `value` TEXT DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (
  user_name VARCHAR(50) NOT NULL PRIMARY KEY,
  active BIT NOT NULL,
  encrypted_password VARCHAR(255) NOT NULL,
  user_role VARCHAR(20) NOT NULL
) ENGINE = InnoDB;

DROP TABLE IF EXISTS `email_account_settings`;
CREATE TABLE `email_account_settings` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `account` VARCHAR(120) NOT NULL,
  `password` VARCHAR(32) NOT NULL,
  `mail_server_address` VARCHAR(191) NOT NULL,
  `mail_server_port` INT NOT NULL,
  `mail_protocol` SMALLINT(6) NOT NULL DEFAULT '0' COMMENT '0、IMAP 1. POP3 2.SMTP',
  `encryption_protocol`SMALLINT(6) NOT NULL DEFAULT '0'COMMENT '0、なし 1. SSL/TLS, 2. STARTTLS',
  `authentication_protocol` SMALLINT(6) NOT NULL DEFAULT '0' COMMENT '0. 通常のパスワード認証 1. 暗号化されたパスワード認証 2. Kerberos/GSSAPI 3. NTLM 4. TLS証明書 5. OAuth2',
  `proxy_server` VARCHAR(191) DEFAULT NULL,
  `disabled` BOOLEAN DEFAULT FALSE,
  `created_at` DATETIME DEFAULT NULL,
  `updated_at` DATETIME DEFAULT NULL,
  `type` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、受信 1. 送信',
  UNIQUE KEY unique_account (account, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO email_account_settings(account, password, mail_server_address, mail_server_port)
    VALUES ('khanhlvb@ows.vn', 'Lekhanh281', 'imap.gmail.com', 993);

INSERT INTO email_account_settings(account, password, mail_server_address, mail_server_port, disabled)
    VALUES ('baokhanhlv@gmail.com', 'Lekhanh28011993', 'imap.gmail.com', 993, false);

INSERT INTO email_account_settings(account, password, mail_server_address, mail_server_port)
    VALUES ('ows-test@world-link-system.com', 'o2018wa01e', 'af125.secure.ne.jp', 993);

DROP TABLE IF EXISTS `emails`;
CREATE TABLE `emails` (
  `message_id` VARCHAR(191) PRIMARY KEY COLLATE utf8_unicode_ci,
  `account_id` INT NOT NULL,
  `from` VARCHAR(120) NOT NULL,
  `subject` TEXT NOT NULL,
  `to` TEXT NOT NULL,
  `cc` TEXT DEFAULT NULL,
  `bcc` TEXT DEFAULT NULL,
  `reply_to` VARCHAR(120) DEFAULT NULL,
  `sent_at` DATETIME NOT NULL,
  `received_at` DATETIME DEFAULT NULL,
  `has_attachment` BOOLEAN DEFAULT FALSE,
  `content_type` SMALLINT(6) NOT NULL DEFAULT '0' COMMENT '0、TEXT 1. HTML',
  `original_body`MEDIUMTEXT DEFAULT NULL,
  `optimized_body`TEXT DEFAULT NULL,
  `header` TEXT DEFAULT NULL,
  `created_at` DATETIME DEFAULT NULL,
  `meta_data` TEXT DEFAULT NULL,
  `deleted` BOOLEAN DEFAULT FALSE,
  `deleted_at` DATETIME DEFAULT NULL,
  FOREIGN KEY fk_receive_email_account(account_id)
  REFERENCES email_account_settings(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

# INSERT INTO emails(message_id, account_id, `from`, subject, `to`, sent_at)
# VALUES ('abcd+khanhlvb@ows.vn', 1, 'abc', 'hello', 'khanhlvb@ows.vn', NOW());

DROP TABLE IF EXISTS `files`;

CREATE TABLE `files` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `message_id` VARCHAR(191) NOT NULL,
  `file_name` VARCHAR(191) NOT NULL,
  `storage_path` TEXT NOT NULL,
  `created_at` DATETIME DEFAULT NULL,
  `meta_data` TEXT DEFAULT NULL,
  `deleted` BOOLEAN DEFAULT FALSE,
  `deleted_at` DATETIME DEFAULT NULL,
  FOREIGN KEY fk_receive_email(message_id)
  REFERENCES emails(message_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `words`;
CREATE TABLE `words` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `word` VARCHAR(191) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

INSERT INTO words(id, word)
VALUES (1, 'java');
INSERT INTO words(id, word)
VALUES (2, 'JAVA');

DROP TABLE IF EXISTS `fuzzy_words`;
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

ALTER TABLE fuzzy_words
ADD CONSTRAINT uc_fuzzy_word UNIQUE (word_id,with_word_id);

INSERT INTO fuzzy_words(word_id, with_word_id, fuzzy_type)
VALUES (1, 2, 1);

DROP TABLE IF EXISTS `email_word_jobs`;
CREATE TABLE `email_word_jobs` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `message_id` VARCHAR(191) NOT NULL,
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

ALTER TABLE email_word_jobs
ADD CONSTRAINT uc_email_word_job UNIQUE (message_id,word_id);

INSERT INTO email_word_jobs(message_id, word_id)
VALUES ('khanhlvb@ows.vn+<001a114425824f4509056431a819@google.com>', 2);

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

CREATE TABLE `emails_words` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `message_id` VARCHAR(191) NOT NULL,
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

ALTER TABLE emails_words
ADD CONSTRAINT uc_email_word UNIQUE (message_id,word_id);

COMMIT;

CREATE TABLE `replace_numbers` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `character` VARCHAR(191) NOT NULL,
  `replace_value` INT NOT NULL,
  UNIQUE KEY unique_character (`character`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `replace_units` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `unit` VARCHAR(191) NOT NULL,
  `replace_unit` VARCHAR(191) NOT NULL,
  UNIQUE KEY unique_unit (unit)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO replace_numbers(`character`, replace_value)
VALUES ('K', 1000);
INSERT INTO replace_numbers(`character`, replace_value)
VALUES ('千', 1000);
INSERT INTO replace_numbers(`character`, replace_value)
VALUES ('万', 10000);

INSERT INTO replace_units(unit, replace_unit)
VALUES ('円', '円');
INSERT INTO replace_units(unit, replace_unit)
VALUES ('YEN', '円');

CREATE TABLE `replace_letters` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `letter` VARCHAR(191) NOT NULL,
  `position` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、数値の前の 1. 数値の後の',
  `replace` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、以上として認識する 1. 以下として認識する 2. 未満として認識する 3. 超として認識する 4. None',
  `hidden` BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

ALTER TABLE replace_letters
ADD CONSTRAINT uc_replace_letter UNIQUE (letter, position);

INSERT INTO replace_letters(letter, position, `replace`, hidden)
VALUES ('以上', 0, 4, TRUE);
INSERT INTO replace_letters(letter, position, `replace`, hidden)
VALUES ('以上', 1, 0, TRUE);
INSERT INTO replace_letters(letter, position, `replace`, hidden)
VALUES ('以下', 0, 4, TRUE);
INSERT INTO replace_letters(letter, position, `replace`, hidden)
VALUES ('以下', 1, 1, TRUE);
INSERT INTO replace_letters(letter, position, `replace`, hidden)
VALUES ('超', 0, 4, TRUE);
INSERT INTO replace_letters(letter, position, `replace`, hidden)
VALUES ('超', 1, 3, TRUE);
INSERT INTO replace_letters(letter, position, `replace`, hidden)
VALUES ('未満', 0, 4, TRUE);
INSERT INTO replace_letters(letter, position, `replace`, hidden)
VALUES ('未満', 1, 2, TRUE);


CREATE TABLE `number_treatments` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(191) NOT NULL,
  `upper_limit_name` VARCHAR(191) NOT NULL,
  `upper_limit_sign` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、x',
  `upper_limit_rate` DOUBLE NOT NULL,
  `lower_limit_name` VARCHAR(191) NOT NULL,
  `lower_limit_sign` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、x',
  `lower_limit_rate` DOUBLE NOT NULL,
  `left_boundary_value` DOUBLE DEFAULT NULL,
  `left_boundary_operator` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、以上を数字として扱う 3. 超を通じとして扱う',
  `combine_operator` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、AND 1. OR',
  `right_boundary_value` DOUBLE DEFAULT NULL,
  `right_boundary_operator` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '1、以下を数字として扱う 2. 未満を通じとして扱う',
  `enable_replace_letter` BOOLEAN DEFAULT FALSE,
  UNIQUE KEY unique_mumber_treatment_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO number_treatments(name, upper_limit_name, upper_limit_rate, lower_limit_name, lower_limit_rate)
VALUES ('name', 'upper_limit_name', 1.2, 'lower_limit_name', 0.8);

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `matching_conditions` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `group` BOOLEAN NOT NULL DEFAULT FALSE,
  `combine`SMALLINT(6) NOT NULL DEFAULT '-1' COMMENT '-1、NONE O. AND 1.OR',
  `item`SMALLINT(6) NOT NULL DEFAULT '-1',
  `condition`SMALLINT(6) NOT NULL DEFAULT '-1',
  `value` VARCHAR(191) DEFAULT NULL,
  `type` TINYINT(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
