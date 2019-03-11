CREATE TABLE `scheduler_send_email_file` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `scheduler_send_email_id` INT NOT NULL,
  `upload_files_id` INT NOT NULL,
  FOREIGN KEY fk_scheduler_relationship(scheduler_send_email_id)
  REFERENCES scheduler_send_email(id)
    ON DELETE CASCADE,
  FOREIGN KEY fk_upload_files_relationship(upload_files_id)
  REFERENCES upload_files(id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;