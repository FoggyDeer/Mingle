-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2024-06-02 00:07:58.006

-- foreign keys
ALTER TABLE Chat DROP CONSTRAINT Chat_Image;

ALTER TABLE Chat_User DROP CONSTRAINT Chat_User_Chat;

ALTER TABLE Chat_User DROP CONSTRAINT Chat_User_User;

ALTER TABLE Message DROP CONSTRAINT Message_Chat;

ALTER TABLE Message DROP CONSTRAINT Message_User;

ALTER TABLE Password DROP CONSTRAINT Password_User;

ALTER TABLE Refresh_Token DROP CONSTRAINT Refresh_Token_User;

ALTER TABLE "User" DROP CONSTRAINT User_Image;

-- tables
DROP TABLE Chat;

DROP TABLE Chat_User;

DROP TABLE Image;

DROP TABLE Message;

DROP TABLE Password;

DROP TABLE Refresh_Token;

DROP TABLE "User";

-- End of file.

