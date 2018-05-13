USE `mailsys`;
SET NAMES utf8 COLLATE utf8_unicode_ci;
SET FOREIGN_KEY_CHECKS=0;

INSERT INTO replace_numbers(`character`, replace_value)
VALUES
  ('K', 1000),
  ('千', 1000),
  ('万', 10000);

INSERT INTO replace_units(unit, replace_unit)
VALUES ('円', '円');
INSERT INTO replace_units(unit, replace_unit)
VALUES ('YEN', '円');

INSERT INTO `number_treatments` (`name`, `upper_limit_name`, `upper_limit_sign`, `upper_limit_rate`, `lower_limit_name`, `lower_limit_sign`, `lower_limit_rate`, `left_boundary_value`, `left_boundary_operator`, `combine_operator`, `right_boundary_value`, `right_boundary_operator`, `enable_replace_letter`)
VALUES
	('金額', '上代数値', 0, 1.2, '下代数値', 0, 0.8, 300000, 0, 0, 900000, 1, 1);

INSERT INTO `replace_letters` (`letter`, `position`, `replace`, `hidden`)
VALUES
  ('~', 0, 1, 0),
  ('~', 1, 0, 0),
	('〜', 0, 1, 0),
	('〜', 1, 0, 0);

