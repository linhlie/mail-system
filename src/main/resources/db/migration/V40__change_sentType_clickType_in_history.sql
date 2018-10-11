ALTER TABLE `click_histories`
CHANGE COLUMN `type` `type` INT NOT NULL ;
ALTER TABLE `click_sent_histories`
CHANGE COLUMN `type` `type` INT NOT NULL ;
ALTER TABLE `sent_mail_histories`
CHANGE COLUMN `send_type` `send_type` INT NULL DEFAULT NULL ;