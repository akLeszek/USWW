CREATE TABLE [users] (
  [id] integer PRIMARY KEY IDENTITY(1, 1),
  [login] varchar(32) NOT NULL,
  [password] binary(64) NOT NULL,
  [forename] varchar(32) NOT NULL,
  [surname] varchar(64) NOT NULL,
  [admin] bit NOT NULL DEFAULT (0),
  [login_ban] bit NOT NULL DEFAULT (0)
)
GO

CREATE TABLE [requests] (
  [id] integer PRIMARY KEY IDENTITY(1, 1),
  [sender_id] integer NOT NULL,
  [recipient_id] integer NOT NULL,
  [status] varchar(12) NOT NULL,
  [insert_date] timestamp NOT NULL,
  [change_date] timestamp
)
GO

CREATE TABLE [request_messages] (
  [id] integer PRIMARY KEY IDENTITY(1, 1),
  [user_id] integer NOT NULL,
  [request_id] integer NOT NULL,
  [value] nvarchar(255),
  [insert_date] timestamp NOT NULL
)
GO

CREATE TABLE [attachments] (
  [id] integer PRIMARY KEY IDENTITY(1, 1),
  [request_message_id] integer,
  [attachment] varbinary
)
GO
