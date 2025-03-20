-- USER_GROUP - tabela grup użytkowników
CREATE TABLE USER_GROUP (
    id INT AUTO_INCREMENT PRIMARY KEY,
    idn VARCHAR(15) NOT NULL,
    name VARCHAR(255),
    CONSTRAINT unique_user_group_idn UNIQUE (idn)
);

-- ORGANIZATION_UNIT - tabela jednostek organizacyjnych
CREATE TABLE ORGANIZATION_UNIT (
    id INT AUTO_INCREMENT PRIMARY KEY,
    idn VARCHAR(15) NOT NULL,
    name VARCHAR(255),
    CONSTRAINT unique_organization_unit_idn UNIQUE (idn)
);

-- TICKET_CATEGORY - tabela kategorii zgłoszeń
CREATE TABLE TICKET_CATEGORY (
    id INT AUTO_INCREMENT PRIMARY KEY,
    idn VARCHAR(15) NOT NULL,
    name VARCHAR(255),
    CONSTRAINT unique_ticket_cateogry_idn UNIQUE (idn)
);

-- TICKET_STATUS - tabela statusów zgłoszeń
CREATE TABLE TICKET_STATUS (
    id INT AUTO_INCREMENT PRIMARY KEY,
    idn VARCHAR(15) NOT NULL,
    name VARCHAR(255),
    CONSTRAINT unique_ticket_status_idn UNIQUE (idn)
);

-- TICKET_PRIORITY - tabela priorytetów zgłoszeń
CREATE TABLE TICKET_PRIORITY (
    id INT AUTO_INCREMENT PRIMARY KEY,
    idn VARCHAR(15) NOT NULL,
    name VARCHAR(255),
    CONSTRAINT unique_ticket_priority_idn UNIQUE (idn)
);

-- USERS - tabela użytkowników
CREATE TABLE USERS (
    id INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(32) NOT NULL,
    password VARCHAR(255) NOT NULL,
    forename VARCHAR(32),
    surname VARCHAR(64),
    login_ban BOOLEAN DEFAULT FALSE,
    last_login TIMESTAMP,
    group_id INT NOT NULL,
    organization_unit_id INT,
    archive BOOLEAN DEFAULT FALSE,
    first_login BOOLEAN DEFAULT TRUE,
    CONSTRAINT unique_login UNIQUE (login),
    CONSTRAINT fk_user_user_group FOREIGN KEY (group_id) REFERENCES USER_GROUP (id),
    CONSTRAINT fk_user_organization_unit FOREIGN KEY (organization_unit_id) REFERENCES ORGANIZATION_UNIT (id)
);

-- TICKET - tabela zgłoszeń
CREATE TABLE TICKET (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(30) NOT NULL,
    operator_id INT NOT NULL,
    student_id INT NOT NULL,
    status_id INT NOT NULL,
    category_id INT NOT NULL,
    priority_id INT,
    inserted_date TIMESTAMP,
    change_date TIMESTAMP,
    last_activity_date TIMESTAMP,
    archive BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_ticket_operator FOREIGN KEY (operator_id) REFERENCES USERS (id),
    CONSTRAINT fk_ticket_student FOREIGN KEY (student_id) REFERENCES USERS (id),
    CONSTRAINT fk_ticket_status FOREIGN KEY (status_id) REFERENCES TICKET_STATUS (id),
    CONSTRAINT fk_ticket_category FOREIGN KEY (category_id) REFERENCES TICKET_CATEGORY (id),
    CONSTRAINT fk_ticket_priority FOREIGN KEY (priority_id) REFERENCES TICKET_PRIORITY (id)
);

-- TICKET_MESSAGE - tabela wiadomości w zgłoszeniach
CREATE TABLE TICKET_MESSAGE (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id INT NOT NULL,
    sender_id INT NOT NULL,
    message_text CLOB,  -- Zamiast VARCHAR(MAX)
    insert_date TIMESTAMP,
    archive BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_ticket_message_ticket FOREIGN KEY (ticket_id) REFERENCES TICKET (id),
    CONSTRAINT fk_ticket_message_sender FOREIGN KEY (sender_id) REFERENCES USERS (id)
);

-- MESSAGE_ATTACHMENT - tabela załączników
CREATE TABLE MESSAGE_ATTACHMENT (
    id INT AUTO_INCREMENT PRIMARY KEY,
    message_id INT NOT NULL,
    attachment BLOB,  -- Zamiast VARBINARY(2000)
    CONSTRAINT fk_message_attachment_ticket_message FOREIGN KEY (message_id) REFERENCES TICKET_MESSAGE (id)
);
