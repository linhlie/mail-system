ALTER TABLE `number_treatments`
   ADD COLUMN  `enable_pretty_number` BOOLEAN DEFAULT FALSE,
   ADD COLUMN  `pretty_number_step` INT DEFAULT 1000;