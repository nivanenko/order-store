--------------------------------------------------------
--  File created - Wednesday-January-11-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Sequence DEL_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "DEL_SEQ"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 881 CACHE 20 NOORDER  NOCYCLE  NOPARTITION
--------------------------------------------------------
--  DDL for Sequence DEP_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "DEP_SEQ"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 921 CACHE 20 NOORDER  NOCYCLE  NOPARTITION
--------------------------------------------------------
--  DDL for Sequence ITEM_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "ITEM_SEQ"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1771881 CACHE 20 NOORDER  NOCYCLE  NOPARTITION
--------------------------------------------------------
--  DDL for Sequence ORDERITEM_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "ORDERITEM_SEQ"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1771561 CACHE 20 NOORDER  NOCYCLE  NOPARTITION
--------------------------------------------------------
--  DDL for Sequence ORDER_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "ORDER_SEQ"  MINVALUE 0 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 841 CACHE 20 NOORDER  NOCYCLE  NOPARTITION
--------------------------------------------------------
--  DDL for Table DELIVERY
--------------------------------------------------------

  CREATE TABLE "DELIVERY" ("DEL_ID" NUMBER(*,0), "DEL_ZIP" CHAR(20), "DEL_STATE" CHAR(10), "DEL_CITY" CHAR(255))
--------------------------------------------------------
--  DDL for Table DEPARTURE
--------------------------------------------------------

  CREATE TABLE "DEPARTURE" ("DEP_ID" NUMBER(*,0), "DEP_ZIP" CHAR(20), "DEP_STATE" CHAR(10), "DEP_CITY" CHAR(255))
--------------------------------------------------------
--  DDL for Table ITEMS
--------------------------------------------------------

  CREATE TABLE "ITEMS" ("ITEM_ID" NUMBER(*,0), "ITEM_WEIGHT" NUMBER(8,2), "ITEM_VOL" NUMBER(8,2), "ITEM_HAZ" CHAR(1), "ITEM_PROD" VARCHAR2(255))
--------------------------------------------------------
--  DDL for Table ORDERITEMS
--------------------------------------------------------

  CREATE TABLE "ORDERITEMS" ("ORDER_ID" NUMBER(*,0), "ORDER_ITEM" NUMBER(*,0), "ITEM_ID" NUMBER(*,0))
--------------------------------------------------------
--  DDL for Table ORDERS
--------------------------------------------------------

  CREATE TABLE "ORDERS" ("ORDER_ID" NUMBER(*,0), "DEP_ID" NUMBER(*,0), "DEL_ID" NUMBER(*,0))
--------------------------------------------------------
--  DDL for Index PK_ITEMS
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_ITEMS" ON "ITEMS" ("ITEM_ID")
--------------------------------------------------------
--  DDL for Index PK_DELIVERY
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_DELIVERY" ON "DELIVERY" ("DEL_ID")
--------------------------------------------------------
--  DDL for Index PK_DEPARTURE
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_DEPARTURE" ON "DEPARTURE" ("DEP_ID")
--------------------------------------------------------
--  DDL for Index PK_ORDERS
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_ORDERS" ON "ORDERS" ("ORDER_ID")
--------------------------------------------------------
--  DDL for Index PK_ORDERITEMS
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_ORDERITEMS" ON "ORDERITEMS" ("ORDER_ID", "ORDER_ITEM")
--------------------------------------------------------
--  Constraints for Table ITEMS
--------------------------------------------------------

  ALTER TABLE "ITEMS" MODIFY ("ITEM_PROD" NOT NULL ENABLE)
  ALTER TABLE "ITEMS" MODIFY ("ITEM_HAZ" NOT NULL ENABLE)
  ALTER TABLE "ITEMS" MODIFY ("ITEM_VOL" NOT NULL ENABLE)
  ALTER TABLE "ITEMS" MODIFY ("ITEM_WEIGHT" NOT NULL ENABLE)
  ALTER TABLE "ITEMS" MODIFY ("ITEM_ID" NOT NULL ENABLE)
  ALTER TABLE "ITEMS" ADD CONSTRAINT "PK_ITEMS" PRIMARY KEY ("ITEM_ID") USING INDEX  ENABLE
--------------------------------------------------------
--  Constraints for Table DEPARTURE
--------------------------------------------------------

  ALTER TABLE "DEPARTURE" MODIFY ("DEP_CITY" NOT NULL ENABLE)
  ALTER TABLE "DEPARTURE" MODIFY ("DEP_STATE" NOT NULL ENABLE)
  ALTER TABLE "DEPARTURE" MODIFY ("DEP_ZIP" NOT NULL ENABLE)
  ALTER TABLE "DEPARTURE" MODIFY ("DEP_ID" NOT NULL ENABLE)
  ALTER TABLE "DEPARTURE" ADD CONSTRAINT "PK_DEPARTURE" PRIMARY KEY ("DEP_ID") USING INDEX  ENABLE
--------------------------------------------------------
--  Constraints for Table ORDERITEMS
--------------------------------------------------------

  ALTER TABLE "ORDERITEMS" MODIFY ("ITEM_ID" NOT NULL ENABLE)
  ALTER TABLE "ORDERITEMS" MODIFY ("ORDER_ITEM" NOT NULL ENABLE)
  ALTER TABLE "ORDERITEMS" MODIFY ("ORDER_ID" NOT NULL ENABLE)
  ALTER TABLE "ORDERITEMS" ADD CONSTRAINT "PK_ORDERITEMS" PRIMARY KEY ("ORDER_ID", "ORDER_ITEM") USING INDEX  ENABLE
--------------------------------------------------------
--  Constraints for Table DELIVERY
--------------------------------------------------------

  ALTER TABLE "DELIVERY" MODIFY ("DEL_CITY" NOT NULL ENABLE)
  ALTER TABLE "DELIVERY" MODIFY ("DEL_STATE" NOT NULL ENABLE)
  ALTER TABLE "DELIVERY" MODIFY ("DEL_ZIP" NOT NULL ENABLE)
  ALTER TABLE "DELIVERY" MODIFY ("DEL_ID" NOT NULL ENABLE)
  ALTER TABLE "DELIVERY" ADD CONSTRAINT "PK_DELIVERY" PRIMARY KEY ("DEL_ID") USING INDEX  ENABLE
--------------------------------------------------------
--  Constraints for Table ORDERS
--------------------------------------------------------

  ALTER TABLE "ORDERS" MODIFY ("DEL_ID" NOT NULL ENABLE)
  ALTER TABLE "ORDERS" MODIFY ("DEP_ID" NOT NULL ENABLE)
  ALTER TABLE "ORDERS" MODIFY ("ORDER_ID" NOT NULL ENABLE)
  ALTER TABLE "ORDERS" ADD CONSTRAINT "PK_ORDERS" PRIMARY KEY ("ORDER_ID") USING INDEX  ENABLE
--------------------------------------------------------
--  Ref Constraints for Table ORDERITEMS
--------------------------------------------------------

  ALTER TABLE "ORDERITEMS" ADD CONSTRAINT "FK_ORDERITEMS_ITEMS" FOREIGN KEY ("ITEM_ID") REFERENCES "ITEMS" ("ITEM_ID") ENABLE
  ALTER TABLE "ORDERITEMS" ADD CONSTRAINT "FK_ORDERITEMS_ORDERS" FOREIGN KEY ("ORDER_ID") REFERENCES "ORDERS" ("ORDER_ID") ENABLE
--------------------------------------------------------
--  Ref Constraints for Table ORDERS
--------------------------------------------------------

  ALTER TABLE "ORDERS" ADD CONSTRAINT "FK_ORDERS_DELIVERY" FOREIGN KEY ("DEL_ID") REFERENCES "DELIVERY" ("DEL_ID") ENABLE
  ALTER TABLE "ORDERS" ADD CONSTRAINT "FK_ORDERS_DEPARTURE" FOREIGN KEY ("DEP_ID") REFERENCES "DEPARTURE" ("DEP_ID") ENABLE
--------------------------------------------------------
--  DDL for Procedure RESET_SEQ
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "RESET_SEQ" ( p_seq_name in varchar2 )
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
