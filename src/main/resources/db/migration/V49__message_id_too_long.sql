ALTER TABLE `files`
DROP FOREIGN KEY `files_ibfk_1`;

ALTER TABLE `number_ranges`
DROP FOREIGN KEY `number_ranges_ibfk_1`;

ALTER TABLE `files`
CHANGE COLUMN `message_id` `message_id` VARCHAR(500) CHARACTER SET 'utf8' NOT NULL ;

ALTER TABLE `emails`
CHANGE COLUMN `message_id` `message_id` VARCHAR(500) CHARACTER SET 'utf8' NOT NULL ;

ALTER TABLE `number_ranges`
CHANGE COLUMN `message_id` `message_id` VARCHAR(500) CHARACTER SET 'utf8' NOT NULL ;

ALTER TABLE `sent_mail_histories`
CHANGE COLUMN `message_id` `message_id` VARCHAR(500) CHARACTER SET 'utf8' NOT NULL ,
CHANGE COLUMN `matching_message_id` `matching_message_id` VARCHAR(500) CHARACTER SET 'utf8' NULL DEFAULT NULL ;

ALTER TABLE `files`
ADD FOREIGN KEY files_ibfk_1(`message_id`)
REFERENCES `emails` (`message_id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

ALTER TABLE `number_ranges`
ADD FOREIGN KEY number_ranges_ibfk_1(`message_id`)
REFERENCES `emails` (`message_id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;
