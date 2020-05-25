PGDMP     '                    x            crm-api %   10.12 (Ubuntu 10.12-0ubuntu0.18.04.1)     11.6 (Ubuntu 11.6-1.pgdg18.04+1)     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                       false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                       false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                       false            �           1262    42879    crm-api    DATABASE     o   CREATE DATABASE "crm-api" WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_IN' LC_CTYPE = 'en_IN';
    DROP DATABASE "crm-api";
             postgres    false                        2615    54090    notification    SCHEMA        CREATE SCHEMA notification;
    DROP SCHEMA notification;
             postgres    false            �           1255    54122 L   fn_insertNotification(bigint, text, text, text, timestamp without time zone)    FUNCTION     �  CREATE FUNCTION notification."fn_insertNotification"("p_CompanyExecutiveID" bigint, "p_CompanyExecutiveName" text, "p_NotificationSubject" text, "p_NotificationDescription" text, "p_NotificationTime" timestamp without time zone) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
INSERT INTO notification."Notification"(
	"CompanyExecutiveID", 
	"CompanyExecutiveName", 
	"NotificationSubject", 
	"NotificationDescription", 
	"NotificationTime"
)
VALUES (
	"p_CompanyExecutiveID",
	"p_CompanyExecutiveName",
	"p_NotificationSubject",
	"p_NotificationDescription",
	"p_NotificationTime"
);
RETURN true;

EXCEPTION WHEN OTHERS THEN
RETURN false;
ROLLBACK;
END;

$$;
 �   DROP FUNCTION notification."fn_insertNotification"("p_CompanyExecutiveID" bigint, "p_CompanyExecutiveName" text, "p_NotificationSubject" text, "p_NotificationDescription" text, "p_NotificationTime" timestamp without time zone);
       notification       postgres    false    16            �           1255    62283    fn_revokeMarkForDelete(bigint)    FUNCTION     I  CREATE FUNCTION notification."fn_revokeMarkForDelete"("p_NotificationID" bigint) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
	
	UPDATE notification."Notification"
	SET "MarkForDelete"=false
	WHERE "NotificationID"="p_NotificationID";
	RETURN true;

    EXCEPTION WHEN OTHERS THEN
    RETURN false;
    ROLLBACK;
END
$$;
 P   DROP FUNCTION notification."fn_revokeMarkForDelete"("p_NotificationID" bigint);
       notification       postgres    false    16            �           1255    62282    fn_selectNotifications(bigint)    FUNCTION     �  CREATE FUNCTION notification."fn_selectNotifications"("p_CompanyExecutiveID" bigint) RETURNS TABLE("CompanyExecutiveID" bigint, "CompanyExecutiveName" text, "NotificationSubject" text, "NotificationDescription" text, "ReadIndex" boolean, "NotificationTime" timestamp without time zone, "NotificationID" bigint)
    LANGUAGE sql
    AS $$

SELECT  "CompanyExecutiveID", 
		"CompanyExecutiveName", 
		"NotificationSubject", 
		"NotificationDescription", 
		"ReadIndex", 
		"NotificationTime", 
		"NotificationID"
FROM notification."Notification"
WHERE "MarkForDelete"=false
AND "JustArrived"=true
AND "CompanyExecutiveID" = "p_CompanyExecutiveID";

$$;
 T   DROP FUNCTION notification."fn_selectNotifications"("p_CompanyExecutiveID" bigint);
       notification       postgres    false    16            �           1255    54125 %   fn_updateJustArrived(bigint, boolean)    FUNCTION     h  CREATE FUNCTION notification."fn_updateJustArrived"("p_NotificationID" bigint, "p_JustArrived" boolean) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
	
	UPDATE notification."Notification"
	SET "JustArrived"="p_JustArrived"
	WHERE "NotificationID"="p_NotificationID";
	RETURN true;

    EXCEPTION WHEN OTHERS THEN
    RETURN false;
    ROLLBACK;
END
$$;
 g   DROP FUNCTION notification."fn_updateJustArrived"("p_NotificationID" bigint, "p_JustArrived" boolean);
       notification       postgres    false    16            �           1255    62284    fn_updateMarkForDelete(bigint)    FUNCTION     I  CREATE FUNCTION notification."fn_updateMarkForDelete"("p_NotificationID" bigint) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
	
	UPDATE notification."Notification"
	SET "MarkForDelete"= true
	WHERE "NotificationID"="p_NotificationID";
	RETURN true;

    EXCEPTION WHEN OTHERS THEN
    RETURN false;
    ROLLBACK;
END
$$;
 P   DROP FUNCTION notification."fn_updateMarkForDelete"("p_NotificationID" bigint);
       notification       postgres    false    16            �           1255    54127 #   fn_updateReadIndex(bigint, boolean)    FUNCTION     `  CREATE FUNCTION notification."fn_updateReadIndex"("p_NotificationID" bigint, "p_ReadIndex" boolean) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
BEGIN
	
	UPDATE notification."Notification"
	SET "ReadIndex"="p_ReadIndex"
	WHERE "NotificationID"="p_NotificationID";
	RETURN true;

    EXCEPTION WHEN OTHERS THEN
    RETURN false;
    ROLLBACK;
END
$$;
 c   DROP FUNCTION notification."fn_updateReadIndex"("p_NotificationID" bigint, "p_ReadIndex" boolean);
       notification       postgres    false    16                       1259    54091    Notification    TABLE     �  CREATE TABLE notification."Notification" (
    "CompanyExecutiveID" bigint,
    "CompanyExecutiveName" text,
    "NotificationSubject" text,
    "NotificationDescription" text,
    "ReadIndex" boolean DEFAULT false,
    "NotificationTime" timestamp without time zone,
    "MarkForDelete" boolean DEFAULT false,
    "JustArrived" boolean DEFAULT true,
    "NotificationID" bigint NOT NULL
);
 (   DROP TABLE notification."Notification";
       notification         postgres    false    16                       1259    54112    Notification_NotificationID_seq    SEQUENCE     �   CREATE SEQUENCE notification."Notification_NotificationID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 >   DROP SEQUENCE notification."Notification_NotificationID_seq";
       notification       postgres    false    284    16            �           0    0    Notification_NotificationID_seq    SEQUENCE OWNED BY     u   ALTER SEQUENCE notification."Notification_NotificationID_seq" OWNED BY notification."Notification"."NotificationID";
            notification       postgres    false    285            `           2604    54114    Notification NotificationID    DEFAULT     �   ALTER TABLE ONLY notification."Notification" ALTER COLUMN "NotificationID" SET DEFAULT nextval('notification."Notification_NotificationID_seq"'::regclass);
 T   ALTER TABLE notification."Notification" ALTER COLUMN "NotificationID" DROP DEFAULT;
       notification       postgres    false    285    284           