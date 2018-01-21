DROP DATABASE IF EXISTS `mailsys`;
CREATE DATABASE `mailsys`;
USE `mailsys`;

SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `ReceiveEmailAccountSettings`;
CREATE TABLE `ReceiveEmailAccountSettings` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `account` VARCHAR(60) NOT NULL,
  `password` VARCHAR(32) NOT NULL,
  `mail_server_address` VARCHAR(200) NOT NULL,
  `mail_server_port` INT NOT NULL,
  `receive_mail_protocol` SMALLINT(6) NOT NULL DEFAULT '0' COMMENT '0、IMAP 1. POP3',
  `encryption_protocol`SMALLINT(6) NOT NULL DEFAULT '0'COMMENT '0、なし 1. SSL/TLS, 2. STARTTLS',
  `authentication_protocol` SMALLINT(6) NOT NULL DEFAULT '0' COMMENT '0. 通常のパスワード認証 1. 暗号化されたパスワード認証 2. Kerberos/GSSAPI 3. NTLM 4. TLS証明書 5. OAuth2',
  `proxy_server` VARCHAR(200) DEFAULT NULL,
  `disabled` BOOLEAN DEFAULT FALSE,
  `created_at` DATETIME DEFAULT NULL,
  `updated_at` DATETIME DEFAULT NULL,
  `meta_data` TEXT DEFAULT NULL,
  UNIQUE KEY unique_account (account)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO ReceiveEmailAccountSettings(id, account, password, mail_server_address, mail_server_port)
    VALUES (1, 'khanhlvb@ows.vn', 'Lekhanh281', 'imap.gmail.com', 993);

DROP TABLE IF EXISTS `Emails`;
CREATE TABLE `Emails` (
  `message_id` VARCHAR(200) PRIMARY KEY,
  `account_id` INT NOT NULL,
  `from` VARCHAR(60) NOT NULL,
  `subject` VARCHAR(200) NOT NULL,
  `to` VARCHAR(200) NOT NULL,
  `cc` TEXT DEFAULT NULL,
  `bcc` TEXT DEFAULT NULL,
  `reply_to` VARCHAR(60) DEFAULT NULL,
  `sent_at` DATETIME NOT NULL,
  `received_at` DATETIME DEFAULT NULL,
  `has_attachment` BOOLEAN DEFAULT FALSE,
  `content_type` SMALLINT(6) NOT NULL DEFAULT '0' COMMENT '0、TEXT 1. HTML',
  `original_body`TEXT DEFAULT NULL,
  `optimized_body`TEXT DEFAULT NULL,
  `header` TEXT DEFAULT NULL,
  `created_at` DATETIME DEFAULT NULL,
  `meta_data` TEXT DEFAULT NULL,
  FOREIGN KEY fk_receive_email_account(account_id)
  REFERENCES ReceiveEmailAccountSettings(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Emails(message_id, account_id, `from`, subject, `to`, sent_at)
VALUES ('abcd+khanhlvb@ows.vn', 1, 'abc', 'hello', 'khanhlvb@ows.vn', NOW());

DROP TABLE IF EXISTS `Files`;

CREATE TABLE `Files` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `message_id` VARCHAR(200) NOT NULL,
  `file_name` VARCHAR(60) NOT NULL,
  `storage_path` VARCHAR(200) NOT NULL,
  `created_at` DATETIME DEFAULT NULL,
  `meta_data` TEXT DEFAULT NULL,
  FOREIGN KEY fk_receive_email_account(message_id)
  REFERENCES Emails(message_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

COMMIT;
