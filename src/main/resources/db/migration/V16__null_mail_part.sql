ALTER TABLE `emails`
   MODIFY `to` TEXT DEFAULT NULL,
   ADD COLUMN  `error_log` TEXT DEFAULT NULL;