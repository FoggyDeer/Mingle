-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2024-06-01 23:10:10.088

-- tables
-- Table: Chat
CREATE TABLE Chat (
    ChatId int  NOT NULL IDENTITY,
    Type varchar(20)  NOT NULL,
    Name varchar(50)  NULL,
    ImageId int  NULL,
    CONSTRAINT ChatType CHECK (Type IN ('Group', 'Private')),
    CONSTRAINT Chat_pk PRIMARY KEY  (ChatId)
);

-- Table: Chat_User
CREATE TABLE Chat_User (
    ChatId int  NOT NULL,
    UserId int  NOT NULL,
    CONSTRAINT Chat_User_pk PRIMARY KEY  (ChatId,UserId)
);

-- Table: Image
CREATE TABLE Image (
    ImageId int  NOT NULL,
    Bytes varbinary(max)  NOT NULL,
    CONSTRAINT Image_pk PRIMARY KEY  (ImageId)
);

-- Table: Message
CREATE TABLE Message (
    MessageId int  NOT NULL,
    ChatId int  NOT NULL,
    SenderId int  NOT NULL,
    Text nvarchar(1024)  NOT NULL,
    Time datetimeoffset  NOT NULL DEFAULT GETDATE(),
    CONSTRAINT Message_pk PRIMARY KEY  (MessageId,ChatId)
);

-- Table: Password
CREATE TABLE Password (
    UserId int  NOT NULL,
    Hash char(64)  NOT NULL,
    Salt nvarchar(max)  NOT NULL,
    CONSTRAINT Password_pk PRIMARY KEY  (UserId)
);

-- Table: Refresh_Token
CREATE TABLE Refresh_Token (
    TokenId int  NOT NULL IDENTITY,
    UserId int  NOT NULL,
    Refresh_Token nvarchar(max)  NOT NULL,
    ExpirationDate datetime  NOT NULL,
    CreationDate datetime  NOT NULL,
    LastUsedDate datetime  NOT NULL,
    CONSTRAINT Refresh_Token_pk PRIMARY KEY  (TokenId,UserId)
);

-- Table: User
CREATE TABLE "User" (
    UserId int  NOT NULL IDENTITY,
    Username varchar(25)  NOT NULL,
    Name nvarchar(25)  NOT NULL,
    ImageId int  NULL,
    CONSTRAINT User_ak_Username UNIQUE (Username),
    CONSTRAINT User_pk PRIMARY KEY  (UserId)
);

-- foreign keys
-- Reference: Chat_Image (table: Chat)
ALTER TABLE Chat ADD CONSTRAINT Chat_Image
    FOREIGN KEY (ImageId)
    REFERENCES Image (ImageId);

-- Reference: Chat_User_Chat (table: Chat_User)
ALTER TABLE Chat_User ADD CONSTRAINT Chat_User_Chat
    FOREIGN KEY (ChatId)
    REFERENCES Chat (ChatId);

-- Reference: Chat_User_User (table: Chat_User)
ALTER TABLE Chat_User ADD CONSTRAINT Chat_User_User
    FOREIGN KEY (UserId)
    REFERENCES "User" (UserId);

-- Reference: Message_Chat (table: Message)
ALTER TABLE Message ADD CONSTRAINT Message_Chat
    FOREIGN KEY (ChatId)
    REFERENCES Chat (ChatId);

-- Reference: Message_User (table: Message)
ALTER TABLE Message ADD CONSTRAINT Message_User
    FOREIGN KEY (SenderId)
    REFERENCES "User" (UserId);

-- Reference: Password_User (table: Password)
ALTER TABLE Password ADD CONSTRAINT Password_User
    FOREIGN KEY (UserId)
    REFERENCES "User" (UserId);

-- Reference: Refresh_Token_User (table: Refresh_Token)
ALTER TABLE Refresh_Token ADD CONSTRAINT Refresh_Token_User
    FOREIGN KEY (UserId)
    REFERENCES "User" (UserId);

-- Reference: User_Image (table: User)
ALTER TABLE "User" ADD CONSTRAINT User_Image
    FOREIGN KEY (ImageId)
    REFERENCES Image (ImageId);

-- End of file.

CREATE OR ALTER PROCEDURE Clear_Database
AS
BEGIN
    DELETE FROM Message;
    DELETE FROM Chat;
    DELETE FROM Chat_User;
    DELETE FROM Refresh_Token;
    DELETE FROM Password;
    DELETE FROM [User];
    DELETE FROM Image;
END;
GO

EXEC Clear_Database;