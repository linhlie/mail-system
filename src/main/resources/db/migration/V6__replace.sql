CREATE TABLE `Replace_Numbers` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `character` VARCHAR(191) NOT NULL,
  `replace_value` INT NOT NULL,
  UNIQUE KEY unique_character (`character`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `Replace_Units` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `unit` VARCHAR(191) NOT NULL,
  `replace_unit` VARCHAR(191) NOT NULL,
  UNIQUE KEY unique_unit (unit)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `Replace_Letters` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `letter` VARCHAR(191) NOT NULL,
  `position` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、数値の前の 1. 数値の後の',
  `replace` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '0、以上として認識する 1. 以下として認識する 2. 未満として認識する 3. 超として認識する 4. None',
  `hidden` BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE Replace_Letters
ADD CONSTRAINT uc_replace_letter UNIQUE (letter, position);

INSERT INTO Replace_Letters(letter, position, `replace`, hidden)
VALUES ('以上', 0, 4, TRUE);
INSERT INTO Replace_Letters(letter, position, `replace`, hidden)
VALUES ('以上', 1, 0, TRUE);
INSERT INTO Replace_Letters(letter, position, `replace`, hidden)
VALUES ('以下', 0, 4, TRUE);
INSERT INTO Replace_Letters(letter, position, `replace`, hidden)
VALUES ('以下', 1, 1, TRUE);
INSERT INTO Replace_Letters(letter, position, `replace`, hidden)
VALUES ('超', 0, 4, TRUE);
INSERT INTO Replace_Letters(letter, position, `replace`, hidden)
VALUES ('超', 1, 3, TRUE);
INSERT INTO Replace_Letters(letter, position, `replace`, hidden)
VALUES ('未満', 0, 4, TRUE);
INSERT INTO Replace_Letters(letter, position, `replace`, hidden)
VALUES ('未満', 1, 2, TRUE);