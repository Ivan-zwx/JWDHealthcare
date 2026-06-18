-- noinspection SqlNoDataSourceInspectionForFile

----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Healthcare - Baseline Development Data
----------------------------------------------------------------------------------------------------------------------------------------------------------

use Healthcare
go

----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Shared BCrypt password hash
-- Raw password for all baseline users: password
--
-- The hash below is a BCrypt hash. Later, Spring Security can validate it using BCryptPasswordEncoder.
----------------------------------------------------------------------------------------------------------------------------------------------------------

declare @PasswordHash nvarchar(300) = N'$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'

----------------------------------------------------------------------------------------------------------------------------------------------------------
-- User accounts
----------------------------------------------------------------------------------------------------------------------------------------------------------

if not exists (select 1 from [UserAccount] where [Username] = N'admin')
begin
    insert into [UserAccount] ([FullName], [Email], [Username], [PasswordHash], [Role], [Enabled])
    values (N'Admin User', N'admin@healthcare.local', N'admin', @PasswordHash, N'ADMIN', 1)
end

if not exists (select 1 from [UserAccount] where [Username] = N'doctor')
begin
    insert into [UserAccount] ([FullName], [Email], [Username], [PasswordHash], [Role], [Enabled])
    values (N'Dr. Alice Morgan', N'doctor@healthcare.local', N'doctor', @PasswordHash, N'DOCTOR', 1)
end

if not exists (select 1 from [UserAccount] where [Username] = N'patient')
begin
    insert into [UserAccount] ([FullName], [Email], [Username], [PasswordHash], [Role], [Enabled])
    values (N'John Patient', N'patient@healthcare.local', N'patient', @PasswordHash, N'PATIENT', 1)
end

----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Doctor profile
----------------------------------------------------------------------------------------------------------------------------------------------------------

declare @DoctorUserAccountID int =
(
    select [IDUserAccount]
    from [UserAccount]
    where [Username] = N'doctor'
)

if @DoctorUserAccountID is not null
   and not exists (select 1 from [Doctor] where [UserAccountID] = @DoctorUserAccountID)
begin
    insert into [Doctor] ([UserAccountID], [Specialty])
    values (@DoctorUserAccountID, N'General Medicine')
end

----------------------------------------------------------------------------------------------------------------------------------------------------------
-- Patient profile
----------------------------------------------------------------------------------------------------------------------------------------------------------

declare @PatientUserAccountID int =
(
    select [IDUserAccount]
    from [UserAccount]
    where [Username] = N'patient'
)

if @PatientUserAccountID is not null
   and not exists (select 1 from [Patient] where [UserAccountID] = @PatientUserAccountID)
begin
    insert into [Patient] ([UserAccountID], [Address], [Phone])
    values (@PatientUserAccountID, N'Example Street 1', N'+385 91 000 0000')
end

----------------------------------------------------------------------------------------------------------------------------------------------------------