IF NOT EXISTS (SELECT 1 FROM USER_GROUP) BEGIN
    INSERT INTO USER_GROUP (idn, name) VALUES
    ('ADMIN', 'Administratorzy'),
    ('OPERATOR', 'Operatorzy'),
    ('STUDENT', 'Studenci')
END;

IF NOT EXISTS (SELECT 1 FROM ORGANIZATION_UNIT) BEGIN
    INSERT INTO ORGANIZATION_UNIT (idn, name) VALUES
    ('WH', 'Wydział Humanistyczny'),
    ('WNP', 'Wydział Nauk Przyrodniczych'),
    ('WNS', 'Wydział Nauk Społecznych'),
    ('WNST', 'Wydział Nauk Ścisłych i Technicznych'),
    ('WPA', 'Wydział Prawa i Administracji'),
    ('WSNE', 'Wydział Sztuki i Nauk o Edukacji'),
    ('WT', 'Wydział Teologiczny')
END;

IF NOT EXISTS (SELECT 1 FROM TICKET_CATEGORY) BEGIN
    INSERT INTO TICKET_CATEGORY (idn, name) VALUES
    ('LOGOWANIE', 'Problemy z logowaniem'),
    ('KURS', 'Problemy z dostępem do kursów'),
    ('INNE', 'Inne zgłoszenia')
END;

IF NOT EXISTS (SELECT 1 FROM TICKET_STATUS) BEGIN
    INSERT INTO TICKET_STATUS (idn, name) VALUES
    ('NEW', 'Nowe'),
    ('IN_PROGRESS', 'W trakcie realizacji'),
    ('CLOSED', 'Zamknięte')
END;

IF NOT EXISTS (SELECT 1 FROM TICKET_PRIORITY) BEGIN
    INSERT INTO TICKET_PRIORITY (idn, name) VALUES
    ('LOW', 'Niski'),
    ('MEDIUM', 'Średni'),
    ('HIGH', 'Wysoki'),
    ('CRITICAL', 'Krytyczny')
END;

IF NOT EXISTS (SELECT 1 FROM USERS) BEGIN
    INSERT INTO USERS (login, password, forename, surname, login_ban, last_login, group_id, organization_unit_id, archive, first_login) VALUES
    ('admin', '$argon2id$v=19$m=16384,t=2,p=1$t5lo4QeLZYKegCG4Fvu6CA$RnLfB+GSdBnIKmPhW6rrE5WsfdHITGXhtEEEW7f3qDw', 'Administrator', 'USWW', 0, null, 1, null, 0, 0),
    ('unknown_operator', '', 'Nieokreślony', 'Operator', 1, null, 2, null, 0, 0)
END;
