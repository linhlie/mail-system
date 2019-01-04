ALTER TABLE `emails`
ADD COLUMN `reply_times` INT(4) NULL DEFAULT 0 AFTER `mark`;
