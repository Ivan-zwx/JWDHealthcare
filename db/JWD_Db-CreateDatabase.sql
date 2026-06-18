-- noinspection SqlNoDataSourceInspectionForFile

----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Healthcare - Database Creation
----------------------------------------------------------------------------------------------------------------------------------------------------------
-- DB

use master
go

if db_id('Healthcare') is not null
begin
	alter database Healthcare
	set single_user
	with rollback immediate
	drop database Healthcare
end

----------------------------------------------------------------------------------------------------------------------------------------------------------
-- DB

create database Healthcare
go

alter database Healthcare
set single_user
with rollback immediate

use Healthcare
go

----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Entities

create table [UserAccount]
(
    [IDUserAccount] int primary key identity,
    [FullName] nvarchar(300) not null,
    [Email] nvarchar(300) not null,
    [Username] nvarchar(300) not null,
    [PasswordHash] nvarchar(300) not null,
    [Role] nvarchar(300) not null,
    [Enabled] bit not null
    )

create table [Doctor]
(
    [IDDoctor] int primary key identity,
    [UserAccountID] int foreign key references [UserAccount]([IDUserAccount]) not null,
    [Specialty] nvarchar(300) not null
    )

create table [Patient]
(
    [IDPatient] int primary key identity,
    [UserAccountID] int foreign key references [UserAccount]([IDUserAccount]) not null,
    [Address] nvarchar(300) null,
    [Phone] nvarchar(300) null
    )

create table [Appointment]
(
    [IDAppointment] int primary key identity,
    [DoctorID] int foreign key references [Doctor]([IDDoctor]) not null,
    [PatientID] int foreign key references [Patient]([IDPatient]) not null,
    [Reason] nvarchar(1000) null,
    [CreatedAt] datetime2(0) not null,
    [ScheduledAt] datetime2(0) not null,
    [Status] nvarchar(300) not null,
    [ReminderGeneratedAt] datetime2(0) null
    )

create table [MedicalRecord]
(
    [IDMedicalRecord] int primary key identity,
    [AppointmentID] int foreign key references [Appointment]([IDAppointment]) not null,
    [Diagnosis] nvarchar(1000) null,
    [Treatment] nvarchar(1000) null,
    [Notes] nvarchar(1000) null,
    [UpdatedAt] datetime2(0) not null
    )

create table [Report]
(
    [IDReport] int primary key identity,
    [Title] nvarchar(300) not null,
    [Summary] nvarchar(max) not null,
    [GeneratedAt] datetime2(0) not null
    )

----------------------------------------------------------------------------------------------------------------------------------------------------------
-- DB

alter database Healthcare
set multi_user

----------------------------------------------------------------------------------------------------------------------------------------------------------