ALTER TABLE `bulletin_board`
ADD COLUMN `account_create_id` INT(11) NULL AFTER `bulletin`,
ADD COLUMN `time_create` DATETIME NULL AFTER `account_create_id`;

ALTER TABLE `bulletin_board`
  ADD FOREIGN KEY fk_account_create_id_bulletin(account_create_id)
  REFERENCES accounts(id) ON DELETE CASCADE;