ALTER TABLE `sent_mail_histories`
   ADD COLUMN  `account_sent_mail_id` INT DEFAULT NULL;
ALTER TABLE `sent_mail_histories`
  ADD FOREIGN KEY fk_sent_email_histories_account(account_sent_mail_id)
  REFERENCES accounts(id) ON DELETE CASCADE;
