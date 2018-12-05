CREATE TABLE `bulletin_permission` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `account_id` INT NOT NULL,
  `bulletin_board_id` INT NOT NULL,
  `can_view` BOOLEAN DEFAULT TRUE,
  `can_edit` BOOLEAN DEFAULT TRUE,
  `can_delete` BOOLEAN DEFAULT TRUE,
  FOREIGN KEY fk_account_bulletin_permission(account_id)
  REFERENCES accounts(id)
  ON UPDATE CASCADE
  ON DELETE CASCADE,
FOREIGN KEY fk_bulletin_board_bulletin_permission(bulletin_board_id)
REFERENCES bulletin_board(id)
  ON UPDATE CASCADE
  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
