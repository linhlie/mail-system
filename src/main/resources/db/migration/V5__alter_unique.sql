ALTER TABLE Fuzzy_Words
ADD CONSTRAINT uc_fuzzy_word UNIQUE (word_id,with_word_id);

ALTER TABLE Email_Word_Jobs
ADD CONSTRAINT uc_email_word_job UNIQUE (message_id,word_id);

ALTER TABLE Emails_Words
ADD CONSTRAINT uc_email_word UNIQUE (message_id,word_id);