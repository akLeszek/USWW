IF NOT EXISTS (SELECT 1 FROM USER_GROUP) BEGIN
	INSERT INTO USER_GROUP (idn, name) VALUES
	('A', 'Administrator'),
	('S', 'Student'),
	('P', 'Pracownik Dziekanatu');
END

IF NOT EXISTS (SELECT 1 FROM TICKET_STATUS) BEGIN
	INSERT INTO TICKET_STATUS (idn, name) VALUES 
	('N', 'Nowe'),
	('W', 'W trakcie rozpatrywania'),
	('Z', 'Zakończone');
END