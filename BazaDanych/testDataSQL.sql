-- Dodawanie przykładowych jednostek organizacyjnych
INSERT INTO ORGANIZATION_UNIT (idn, name) VALUES
('WI', 'Wydział Informatyki'),
('WM', 'Wydział modelarstwa');

-- Dodawanie przykładowych kategorii zgłoszeń
INSERT INTO TICKET_CATEGORY (idn, name) VALUES
('POPS', 'Podanie o przedłużenie sesji'),
('ZOSS', 'Zaświadczenie o statusie studenta');

-- Dodawanie przykładowych użytkowników
INSERT INTO USER (id, login, password, forename, surname, last_login, login_ban, archive, group_id, organization_user_id) VALUES
('admin', 'admin', 'Admin', 'USWW', '2024-02-10 08:30:00', 0, 0, 1, null),
('pracownik1', 'pracownik1', 'Marek', 'Wiśniewski', '2024-02-08 10:15:00', 0, 0, 3, 1),
('pracownik2', 'pracownik2', 'Andrzej', 'Fonfara', '2024-02-08 10:15:00', 0, 0, 3, 2),
('student1', 'student1', 'Jan', 'Kowalski', '2024-02-10 08:30:00', 0, 0, 2, 1),
('student2', 'student2', 'Anna', 'Nowak', '2024-02-09 14:50:00', 0, 0, 2, 1),
('student_ban', 'student_ban', 'Student', 'Ban', '2024-02-09 14:50:00', 1, 0, 2, 1);

-- Dodawanie przykładowych zgłoszeń
INSERT INTO TICKET (student_id, operator_id, category_id, status_id, inserted_date, change_date, archive) VALUES
(3, 2, 2, 1, '2024-02-05 09:00:00', '2024-02-06 12:00:00', 0),
(4, 2, 3, 2, '2024-02-06 14:30:00', '2024-02-07 10:15:00', 0);

-- Dodawanie przykładowych wiadomości do zgłoszeń
INSERT INTO TICKET_MESSAGE (ticket_id, sender_id, message_text, inserted_date) VALUES
(1, 3, 'Czy mogę uzyskać informację na temat przedłużenia sesji?', '2024-02-05 09:15:00'),
(1, 2, 'Tak, wniosek musi być zatwierdzony przez dziekana.', '2024-02-06 11:30:00'),
(2, 4, 'Proszę o zaświadczenie o statusie studenta.', '2024-02-06 14:45:00'),
(2, 2, 'Zaświadczenie zostało wysłane na e-mail.', '2024-02-07 10:00:00');
