ALTER TABLE `click_histories`
   ADD COLUMN  `account_id` INT DEFAULT NULL;
ALTER TABLE `click_histories`
  ADD FOREIGN KEY fk_click_histories_account(account_id)
  REFERENCES accounts(id) ON DELETE CASCADE;
ALTER TABLE `click_sent_histories`
   ADD COLUMN  `account_id` INT DEFAULT NULL;
ALTER TABLE `click_sent_histories`
  ADD FOREIGN KEY fk_click_sent_histories_account(account_id)
  REFERENCES accounts(id) ON DELETE CASCADE;