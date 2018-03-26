CREATE TABLE `key_values` (
  `key` VARCHAR(191) PRIMARY KEY,
  `value` TEXT DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `accounts` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  user_name VARCHAR(50) NOT NULL,
  active BIT NOT NULL,
  encrypted_password VARCHAR(255) NOT NULL,
  user_role VARCHAR(20) NOT NULL,
  UNIQUE KEY unique_user_name (user_name)
) ENGINE = InnoDB;

CREATE TABLE `email_account_settings` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `account` VARCHAR(120) NOT NULL,
  `user_name` VARCHAR(120) DEFAULT NULL,
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
  `deleted_at` DATETIME DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

CREATE TABLE `files` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `message_id` VARCHAR(191) NOT NULL,
  `file_name` VARCHAR(191) NOT NULL COLLATE utf8_general_ci,
  `storage_path` TEXT NOT NULL,
  `created_at` DATETIME DEFAULT NULL,
  `meta_data` TEXT DEFAULT NULL,
  `deleted` BOOLEAN DEFAULT FALSE,
  `deleted_at` DATETIME DEFAULT NULL,
  `size` LONG,
  FOREIGN KEY fk_receive_email(message_id)
  REFERENCES emails(message_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
