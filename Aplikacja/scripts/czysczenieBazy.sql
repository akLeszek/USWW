-- WY��CZENIE KLUCZY OBYCH
PRINT 'Wy��czanie kluczy obcych...'
EXEC sp_MSforeachtable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL'

-- USUWANIE WIDOK�W
PRINT 'Usuwanie widok�w...'
DECLARE @sql NVARCHAR(MAX) = ''
SELECT @sql += 'DROP VIEW [' + SCHEMA_NAME(schema_id) + '].[' + name + '];' + CHAR(10)
FROM sys.views
EXEC sp_executesql @sql

-- USUWANIE PROCEDUR SK�ADOWANYCH
PRINT 'Usuwanie procedur sk�adowanych...'
SET @sql = ''
SELECT @sql += 'DROP PROCEDURE [' + SCHEMA_NAME(schema_id) + '].[' + name + '];' + CHAR(10)
FROM sys.procedures
EXEC sp_executesql @sql

-- USUWANIE FUNKCJI
PRINT 'Usuwanie funkcji u�ytkownika...'
SET @sql = ''
SELECT @sql += 'DROP FUNCTION [' + SCHEMA_NAME(schema_id) + '].[' + name + '];' + CHAR(10)
FROM sys.objects WHERE type IN ('FN', 'IF', 'TF') -- FN: skalarnie, IF: inline, TF: tabela
EXEC sp_executesql @sql

-- USUWANIE TRIGGER�W NA POZIOMIE BAZY
PRINT 'Usuwanie trigger�w na bazie...'
SET @sql = ''
SELECT @sql += 'DROP TRIGGER [' + name + '];' + CHAR(10)
FROM sys.triggers
EXEC sp_executesql @sql

-- USUWANIE KLUCZY OBYCH
PRINT 'Usuwanie kluczy obcych...'
SET @sql = ''
SELECT @sql += 'ALTER TABLE [' + SCHEMA_NAME(schema_id) + '].[' + OBJECT_NAME(parent_object_id) + '] DROP CONSTRAINT [' + name + '];' + CHAR(10)
FROM sys.foreign_keys
EXEC sp_executesql @sql

-- USUWANIE TABEL
PRINT 'Usuwanie tabel...'
EXEC sp_MSforeachtable 'DROP TABLE ?'

-- USUWANIE SCHEMAT�W U�YTKOWNIKA (opcjonalnie)
PRINT 'Usuwanie niestandardowych schemat�w...'
SET @sql = ''
SELECT @sql += 'DROP SCHEMA [' + name + '];' + CHAR(10)
FROM sys.schemas WHERE schema_id > 4 -- Pomija domy�lne schematy (dbo, guest, sys, etc.)
EXEC sp_executesql @sql

PRINT 'Baza danych zosta�a ca�kowicie wyczyszczona!'
