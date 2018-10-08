ALTER TABLE `mailsys`.`domain_unregister` 
ADD COLUMN `status` INT(5) NOT NULL DEFAULT 1 AFTER `domain`;