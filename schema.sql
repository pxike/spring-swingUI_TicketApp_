create table USERS
(
    ID       NUMBER(19) generated as identity primary key,
    PASSWORD VARCHAR2(255 char) not null,
    ROLE     VARCHAR2(255 char) not null check (role in ('EMPLOYEE', 'IT_SUPPORT')),
    USERNAME VARCHAR2(255 char) not null unique
);

create table TICKET
(
    CREATED_BY_USER_ID NUMBER(19) not null references USERS (ID),
    CREATION_DATE      TIMESTAMP(6),
    ID                 NUMBER(19) generated as identity primary key,
    CATEGORY           VARCHAR2(255 char) not null check (category in ('NETWORK', 'HARDWARE', 'SOFTWARE', 'OTHER')),
    DESCRIPTION        VARCHAR2(255 char) not null,
    PRIORITY           VARCHAR2(255 char) not null check (priority in ('LOW', 'MEDIUM', 'HIGH')),
    STATUS             VARCHAR2(255 char) not null check (status in ('NEW', 'IN_PROGRESS', 'RESOLVED')),
    TITLE              VARCHAR2(255 char) not null
);

create table AUDIT_LOG
(
    ID           NUMBER(19) generated as identity primary key,
    TICKET_ID    NUMBER(19)         not null references TICKET (ID),
    TIMESTAMP    TIMESTAMP(6)       not null,
    USER_ID      NUMBER(19)         not null references USERS (ID),
    ACTION_TYPE  VARCHAR2(255 char) not null check (action_type in ('STATUS_CHANGE', 'COMMENT')),
    COMMENT_TEXT VARCHAR2(255 char),
    NEW_STATUS   VARCHAR2(255 char) check (new_status in ('NEW', 'IN_PROGRESS', 'RESOLVED')),
    OLD_STATUS   VARCHAR2(255 char) check (old_status in ('NEW', 'IN_PROGRESS', 'RESOLVED'))
);
