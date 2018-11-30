CREATE TABLE `sent_mail_files` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `sent_mail_histories_id` INT NOT NULL,
  `upload_files_id` INT NOT NULL,
  FOREIGN KEY fk_sent_mail_histories_relationship(sent_mail_histories_id)
  REFERENCES sent_mail_histories(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY fk_upload_files_relationship(upload_files_id)
  REFERENCES upload_files(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;