# Medicine Reminder (Java Swing + JDBC + MySQL)

Entities: User, Medicine, Dose Time, Alert. Data structure: PriorityQueue (service/AlertScheduler.java).

## Setup
1. Install JDK 17+ and MySQL 8.
2. Run `schema.sql` in MySQL (Workbench or `mysql -u root -p < schema.sql`).
3. Download MySQL Connector/J (platform independent .zip) from
   https://dev.mysql.com/downloads/connector/j/ and copy the jar into `lib/`.
4. Edit `src/dao/DBConnection.java` -> set your MySQL password.

## Run from terminal
Linux/Mac:
    javac -cp "lib/*" -d out $(find src -name "*.java")
    java -cp "out:lib/*" Main
Windows (PowerShell):
    New-Item -ItemType Directory -Force .\out
    $files = Get-ChildItem .\src -Recurse -Filter *.java | ForEach-Object FullName
    javac -cp "lib\*" -d .\out $files
    java -cp ".\out;lib\*" Main

The MySQL password is configured as `root` in `src/dao/DBConnection.java`.

## Fixing "Communications link failure"
This error means the Java application cannot connect to MySQL. Open PowerShell as Administrator and start MySQL:

```powershell
Start-Service -Name MySQL80
Get-Service -Name MySQL80
```

The service status must be `Running`. Then execute `schema.sql` in MySQL Workbench or add MySQL to `PATH` and run:

```powershell
mysql -u root -proot < schema.sql
```

After MySQL is running, start the application again. This is a database-server problem, not a Java compilation error.

## Run from an IDE
Create a Java project, mark `src` as the source folder, add the jar in `lib/` to the classpath, run `Main`.

## Using it
Add a user -> add a medicine -> add dose times -> open Reminders tab -> Start.
To test, add a dose time 1-2 minutes from now.
