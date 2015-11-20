--------------------------------------------------------
--  File created - Friday-November-20-2015   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Sequence DEL_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "NAZAR"."DEL_SEQ"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 21 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence DEP_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "NAZAR"."DEP_SEQ"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 21 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence ITEM_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "NAZAR"."ITEM_SEQ"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 21 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence ORDERITEM_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "NAZAR"."ORDERITEM_SEQ"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 21 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence ORDER_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "NAZAR"."ORDER_SEQ"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 21 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Table DELIVERY
--------------------------------------------------------

  CREATE TABLE "NAZAR"."DELIVERY" 
   (	"DEL_ID" NUMBER(*,0), 
	"DEL_ZIP" CHAR(20 BYTE), 
	"DEL_STATE" CHAR(10 BYTE), 
	"DEL_CITY" CHAR(255 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table DEPARTURE
--------------------------------------------------------

  CREATE TABLE "NAZAR"."DEPARTURE" 
   (	"DEP_ID" NUMBER(*,0), 
	"DEP_ZIP" CHAR(20 BYTE), 
	"DEP_STATE" CHAR(10 BYTE), 
	"DEP_CITY" CHAR(255 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ITEMS
--------------------------------------------------------

  CREATE TABLE "NAZAR"."ITEMS" 
   (	"ITEM_ID" NUMBER(*,0), 
	"ITEM_WEIGHT" NUMBER(8,2), 
	"ITEM_VOL" NUMBER(8,2), 
	"ITEM_HAZ" CHAR(1 BYTE), 
	"ITEM_PROD" VARCHAR2(255 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ORDERITEMS
--------------------------------------------------------

  CREATE TABLE "NAZAR"."ORDERITEMS" 
   (	"ORDER_ID" NUMBER(*,0), 
	"ORDER_ITEM" NUMBER(*,0), 
	"ITEM_ID" NUMBER(*,0)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ORDERS
--------------------------------------------------------

  CREATE TABLE "NAZAR"."ORDERS" 
   (	"ORDER_ID" NUMBER(*,0), 
	"DEP_ID" NUMBER(*,0), 
	"DEL_ID" NUMBER(*,0)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
REM INSERTING into NAZAR.DELIVERY
SET DEFINE OFF;
Insert into NAZAR.DELIVERY (DEL_ID,DEL_ZIP,DEL_STATE,DEL_CITY) values (2,'20001               ','NY        ','New York                                                                                                                                                                                                                                                       ');
Insert into NAZAR.DELIVERY (DEL_ID,DEL_ZIP,DEL_STATE,DEL_CITY) values (3,'34564               ','BB        ','Hernata                                                                                                                                                                                                                                                        ');
Insert into NAZAR.DELIVERY (DEL_ID,DEL_ZIP,DEL_STATE,DEL_CITY) values (4,'20001               ','DC        ','WASHINGTON                                                                                                                                                                                                                                                     ');
Insert into NAZAR.DELIVERY (DEL_ID,DEL_ZIP,DEL_STATE,DEL_CITY) values (5,'20001               ','DC        ','WASHINGTON                                                                                                                                                                                                                                                     ');
Insert into NAZAR.DELIVERY (DEL_ID,DEL_ZIP,DEL_STATE,DEL_CITY) values (6,'20001               ','DC        ','WASHINGTON                                                                                                                                                                                                                                                     ');
Insert into NAZAR.DELIVERY (DEL_ID,DEL_ZIP,DEL_STATE,DEL_CITY) values (1,'20001               ','DC        ','WASHINGTON                                                                                                                                                                                                                                                     ');
REM INSERTING into NAZAR.DEPARTURE
SET DEFINE OFF;
Insert into NAZAR.DEPARTURE (DEP_ID,DEP_ZIP,DEP_STATE,DEP_CITY) values (2,'3057                ','KO        ','Kiev                                                                                                                                                                                                                                                           ');
Insert into NAZAR.DEPARTURE (DEP_ID,DEP_ZIP,DEP_STATE,DEP_CITY) values (3,'32222               ','DB        ','Zalupinks                                                                                                                                                                                                                                                      ');
Insert into NAZAR.DEPARTURE (DEP_ID,DEP_ZIP,DEP_STATE,DEP_CITY) values (4,'10001               ','NY        ','NEW YORK                                                                                                                                                                                                                                                       ');
Insert into NAZAR.DEPARTURE (DEP_ID,DEP_ZIP,DEP_STATE,DEP_CITY) values (5,'10001               ','NY        ','NEW YORK                                                                                                                                                                                                                                                       ');
Insert into NAZAR.DEPARTURE (DEP_ID,DEP_ZIP,DEP_STATE,DEP_CITY) values (6,'10001               ','NY        ','NEW YORK                                                                                                                                                                                                                                                       ');
Insert into NAZAR.DEPARTURE (DEP_ID,DEP_ZIP,DEP_STATE,DEP_CITY) values (1,'10001               ','NY        ','NEW YORK                                                                                                                                                                                                                                                       ');
REM INSERTING into NAZAR.ITEMS
SET DEFINE OFF;
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (3,530.2,15,'0','case');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (4,2023.43,35000,'0','box');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (5,11545.4,9584,'0','toy');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (8,1000.1,323,'1','petrol');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (6,9685,3232,'1','pussyl');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (7,2000.67,3662,'0','iphone');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (9,3333,555,'0','fgfgfg');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (10,222,666,'1','ytytyt');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (11,5555,777,'0','qwertyyy');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (12,66766,888,'1','qewrty');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (13,1232,999,'0','hghg');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (16,1000.1,1,'1','petrol');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (17,2000,2,'0','water');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (14,1000.1,1,'1','petrol');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (15,2000,2,'0','water');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (1,1000.1,1,'1','petrol');
Insert into NAZAR.ITEMS (ITEM_ID,ITEM_WEIGHT,ITEM_VOL,ITEM_HAZ,ITEM_PROD) values (2,2000,2,'0','water');
REM INSERTING into NAZAR.ORDERITEMS
SET DEFINE OFF;
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (2,3,3);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (2,4,4);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (2,5,5);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (4,8,8);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (3,6,6);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (3,7,7);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (4,9,9);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (4,10,10);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (4,11,11);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (4,12,12);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (4,13,13);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (6,16,16);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (6,17,17);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (5,14,14);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (5,15,15);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (1,1,1);
Insert into NAZAR.ORDERITEMS (ORDER_ID,ORDER_ITEM,ITEM_ID) values (1,2,2);
REM INSERTING into NAZAR.ORDERS
SET DEFINE OFF;
Insert into NAZAR.ORDERS (ORDER_ID,DEP_ID,DEL_ID) values (2,2,2);
Insert into NAZAR.ORDERS (ORDER_ID,DEP_ID,DEL_ID) values (3,3,3);
Insert into NAZAR.ORDERS (ORDER_ID,DEP_ID,DEL_ID) values (4,4,4);
Insert into NAZAR.ORDERS (ORDER_ID,DEP_ID,DEL_ID) values (5,5,5);
Insert into NAZAR.ORDERS (ORDER_ID,DEP_ID,DEL_ID) values (6,6,6);
Insert into NAZAR.ORDERS (ORDER_ID,DEP_ID,DEL_ID) values (1,1,1);
--------------------------------------------------------
--  DDL for Index PK_ITEMS
--------------------------------------------------------

  CREATE UNIQUE INDEX "NAZAR"."PK_ITEMS" ON "NAZAR"."ITEMS" ("ITEM_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index PK_DELIVERY
--------------------------------------------------------

  CREATE UNIQUE INDEX "NAZAR"."PK_DELIVERY" ON "NAZAR"."DELIVERY" ("DEL_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index PK_DEPARTURE
--------------------------------------------------------

  CREATE UNIQUE INDEX "NAZAR"."PK_DEPARTURE" ON "NAZAR"."DEPARTURE" ("DEP_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index PK_ORDERS
--------------------------------------------------------

  CREATE UNIQUE INDEX "NAZAR"."PK_ORDERS" ON "NAZAR"."ORDERS" ("ORDER_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index PK_ORDERITEMS
--------------------------------------------------------

  CREATE UNIQUE INDEX "NAZAR"."PK_ORDERITEMS" ON "NAZAR"."ORDERITEMS" ("ORDER_ID", "ORDER_ITEM") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  Constraints for Table ITEMS
--------------------------------------------------------

  ALTER TABLE "NAZAR"."ITEMS" ADD CONSTRAINT "PK_ITEMS" PRIMARY KEY ("ITEM_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "NAZAR"."ITEMS" MODIFY ("ITEM_ID" NOT NULL ENABLE);
 
  ALTER TABLE "NAZAR"."ITEMS" MODIFY ("ITEM_WEIGHT" NOT NULL ENABLE);
 
  ALTER TABLE "NAZAR"."ITEMS" MODIFY ("ITEM_VOL" NOT NULL ENABLE);
 
  ALTER TABLE "NAZAR"."ITEMS" MODIFY ("ITEM_HAZ" NOT NULL ENABLE);
 
  ALTER TABLE "NAZAR"."ITEMS" MODIFY ("ITEM_PROD" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table DEPARTURE
--------------------------------------------------------

  ALTER TABLE "NAZAR"."DEPARTURE" ADD CONSTRAINT "PK_DEPARTURE" PRIMARY KEY ("DEP_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "NAZAR"."DEPARTURE" MODIFY ("DEP_ID" NOT NULL ENABLE);
 
  ALTER TABLE "NAZAR"."DEPARTURE" MODIFY ("DEP_ZIP" NOT NULL ENABLE);
 
  ALTER TABLE "NAZAR"."DEPARTURE" MODIFY ("DEP_STATE" NOT NULL ENABLE);
 
  ALTER TABLE "NAZAR"."DEPARTURE" MODIFY ("DEP_CITY" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table ORDERITEMS
--------------------------------------------------------

  ALTER TABLE "NAZAR"."ORDERITEMS" ADD CONSTRAINT "PK_ORDERITEMS" PRIMARY KEY ("ORDER_ID", "ORDER_ITEM")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "NAZAR"."ORDERITEMS" MODIFY ("ORDER_ID" NOT NULL ENABLE);
 
  ALTER TABLE "NAZAR"."ORDERITEMS" MODIFY ("ORDER_ITEM" NOT NULL ENABLE);
 
  ALTER TABLE "NAZAR"."ORDERITEMS" MODIFY ("ITEM_ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table DELIVERY
--------------------------------------------------------

  ALTER TABLE "NAZAR"."DELIVERY" ADD CONSTRAINT "PK_DELIVERY" PRIMARY KEY ("DEL_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "NAZAR"."DELIVERY" MODIFY ("DEL_ID" NOT NULL ENABLE);
 
  ALTER TABLE "NAZAR"."DELIVERY" MODIFY ("DEL_ZIP" NOT NULL ENABLE);
 
  ALTER TABLE "NAZAR"."DELIVERY" MODIFY ("DEL_STATE" NOT NULL ENABLE);
 
  ALTER TABLE "NAZAR"."DELIVERY" MODIFY ("DEL_CITY" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table ORDERS
--------------------------------------------------------

  ALTER TABLE "NAZAR"."ORDERS" ADD CONSTRAINT "PK_ORDERS" PRIMARY KEY ("ORDER_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "NAZAR"."ORDERS" MODIFY ("ORDER_ID" NOT NULL ENABLE);
 
  ALTER TABLE "NAZAR"."ORDERS" MODIFY ("DEP_ID" NOT NULL ENABLE);
 
  ALTER TABLE "NAZAR"."ORDERS" MODIFY ("DEL_ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Ref Constraints for Table ORDERITEMS
--------------------------------------------------------

  ALTER TABLE "NAZAR"."ORDERITEMS" ADD CONSTRAINT "FK_ORDERITEMS_ITEMS" FOREIGN KEY ("ITEM_ID")
	  REFERENCES "NAZAR"."ITEMS" ("ITEM_ID") ENABLE;
 
  ALTER TABLE "NAZAR"."ORDERITEMS" ADD CONSTRAINT "FK_ORDERITEMS_ORDERS" FOREIGN KEY ("ORDER_ID")
	  REFERENCES "NAZAR"."ORDERS" ("ORDER_ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ORDERS
--------------------------------------------------------

  ALTER TABLE "NAZAR"."ORDERS" ADD CONSTRAINT "FK_ORDERS_DELIVERY" FOREIGN KEY ("DEL_ID")
	  REFERENCES "NAZAR"."DELIVERY" ("DEL_ID") ENABLE;
 
  ALTER TABLE "NAZAR"."ORDERS" ADD CONSTRAINT "FK_ORDERS_DEPARTURE" FOREIGN KEY ("DEP_ID")
	  REFERENCES "NAZAR"."DEPARTURE" ("DEP_ID") ENABLE;
--------------------------------------------------------
--  DDL for Procedure RESET_SEQ
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "NAZAR"."RESET_SEQ" ( p_seq_name in varchar2 )
is
    l_val number;
begin
    execute immediate
    'select ' || p_seq_name || '.nextval from dual' INTO l_val;

    execute immediate
    'alter sequence ' || p_seq_name || ' increment by -' || l_val || 
                                                          ' minvalue 0';

    execute immediate
    'select ' || p_seq_name || '.nextval from dual' INTO l_val;

    execute immediate
    'alter sequence ' || p_seq_name || ' increment by 1 minvalue 0';
end;

/
