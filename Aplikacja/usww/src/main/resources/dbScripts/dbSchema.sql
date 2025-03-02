IF OBJECT_ID(N'USER_GROUP', N'U') IS NULL BEGIN
    CREATE TABLE USER_GROUP (
        id INT IDENTITY(1, 1) PRIMARY KEY,
        idn VARCHAR(15) NOT NULL,
        name VARCHAR(255),
        CONSTRAINT unique_user_group_idn UNIQUE (idn)
    );
END

IF OBJECT_ID (N'ORGANIZATION_UNIT', N'U') IS NULL BEGIN
    CREATE TABLE ORGANIZATION_UNIT (
        id INT IDENTITY(1, 1) PRIMARY KEY,
        idn VARCHAR(15) NOT NULL,
        name VARCHAR(255),
        CONSTRAINT unique_organization_unit_idn UNIQUE (idn)
    );
END

IF OBJECT_ID (N'TICKET_CATEGORY', N'U') IS NULL BEGIN
    CREATE TABLE TICKET_CATEGORY (
        id INT IDENTITY(1, 1) PRIMARY KEY,
        idn VARCHAR(15) NOT NULL,
        name VARCHAR(255),
        CONSTRAINT unique_ticket_cateogry_idn UNIQUE (idn)
    );
END

IF OBJECT_ID (N'TICKET_STATUS', N'U') IS NULL BEGIN
    CREATE TABLE TICKET_STATUS (
       id INT IDENTITY(1, 1) PRIMARY KEY,
       idn VARCHAR(15) NOT NULL,
       name VARCHAR(255),
       CONSTRAINT unique_ticket_status_idn UNIQUE (idn)
    );
END

IF OBJECT_ID (N'USERS', N'U') IS NULL BEGIN
    CREATE TABLE USERS (
        id INT IDENTITY(1, 1) PRIMARY KEY,
        login VARCHAR(32) NOT NULL,
        password VARCHAR(255) NOT NULL,
        forename VARCHAR(32),
        surname VARCHAR(64),
        login_ban BIT DEFAULT 0,
        last_login DATETIME,
        group_id INT NOT NULL,
        organization_unit_id INT,
        archive BIT DEFAULT 0,
        CONSTRAINT unique_login UNIQUE (login),
        CONSTRAINT fk_user_user_group FOREIGN KEY (group_id) REFERENCES USER_GROUP (id),
        CONSTRAINT fk_user_organization_unit FOREIGN KEY (organization_unit_id) REFERENCES ORGANIZATION_UNIT (id)
    );
END

IF OBJECT_ID (N'TICKET', N'U') IS NULL BEGIN
CREATE TABLE TICKET (
        id INT IDENTITY(1, 1) PRIMARY KEY,
        operator_id INT NOT NULL,
        student_id INT NOT NULL,
        status_id INT NOT NULL,
        category_id INT NOT NULL,
        title VARCHAR(30) NOT NULL,
        inserted_date DATETIME,
        change_date DATETIME,
        archive BIT DEFAULT 0,
        CONSTRAINT fk_ticket_operator FOREIGN KEY (operator_id) REFERENCES USERS (id),
        CONSTRAINT fk_ticket_student FOREIGN KEY (student_id) REFERENCES USERS (id),
        CONSTRAINT fk_ticket_status FOREIGN KEY (status_id) REFERENCES TICKET_STATUS (id),
        CONSTRAINT fk_ticket_category FOREIGN KEY (category_id) REFERENCES TICKET_CATEGORY (id)
    );
END

IF OBJECT_ID (N'TICKET_MESSAGE', N'U') IS NULL BEGIN
    CREATE TABLE TICKET_MESSAGE (
        id INT IDENTITY(1, 1) PRIMARY KEY,
        ticket_id INT NOT NULL,
        sender_id INT NOT NULL,
        message_text VARCHAR(MAX),
        insert_date DATETIME,
        archive	BIT DEFAULT 0,
        CONSTRAINT fk_ticket_message_ticket FOREIGN KEY (ticket_id) REFERENCES TICKET (id),
        CONSTRAINT fk_ticket_message_sender FOREIGN KEY (sender_id) REFERENCES USERS (id)
    );
END

IF OBJECT_ID (N'MESSAGE_ATTACHMENT', N'U') IS NULL BEGIN
    CREATE TABLE MESSAGE_ATTACHMENT (
        id INT IDENTITY(1, 1) PRIMARY KEY,
        message_id INT NOT NULL,
        attachment VARBINARY(2000),
        CONSTRAINT fk_message_attachment_ticket_message FOREIGN KEY (message_id) REFERENCES TICKET_MESSAGE (id)
    );
END
