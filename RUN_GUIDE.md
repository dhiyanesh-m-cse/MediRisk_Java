# MediRisk AI - Final Run Guide

Follow these simple steps to run the MediRisk Core Java prototype on any Windows machine. 

## Prerequisites
- **Java JDK** installed (Java 8 or higher).
- A modern web browser (Chrome, Edge, Firefox).
- No external database (MySQL) or framework (Spring) is required.

## 1. Compile the Project
Open a command prompt (Terminal / PowerShell) in the root of the project directory `MediRisk_Java_PBL` and run:

```powershell
# Create the bin output directory if it doesn't exist
mkdir bin

# Compile all Java files into the bin directory
$files = Get-ChildItem -Path src/com/medirisk -Recurse -Filter *.java
javac -d bin $files.FullName
```

*(Note for CMD users: `javac -d bin src/com/medirisk/**/*.java` may not work on older Windows CMD due to wildcard limits. If so, use the PowerShell command above).*

## 2. Start the Java Server
Once compiled successfully, run the main application:

```powershell
java -cp bin com.medirisk.Main
```

You should see console output indicating that the files were loaded, the diagnostic self-test passed, and the server is listening on port 8080:
```text
[SUCCESS] MediRisk Backend initialized successfully!
[READY] Listening for HTTP requests at: http://localhost:8080/api/status
```

## 3. Access the Application
1. Open your web browser.
2. Navigate to: `http://localhost:8080`
3. You should see the MediRisk Login screen.

## 4. Default Demo Credentials
Use these credentials to log in:
- **Username:** `dhiyanesh`
- **Password:** `password`

*(Or you can click "Create Account" on the login screen to register a new user).*

## 5. Typical Workflow to Test
1. **Login** -> Access the Dashboard.
2. Click **Patients** (in top or bottom nav) -> Click "Add New Patient".
3. Click **New Assessment** -> Select the patient you just made.
4. Enter Vital Signs (e.g., HR: 108, BP: 125/82, SpO2: 96).
5. Click **Run Risk Analysis**.
6. View the Result Page.
7. Click **History** -> Verify the assessment was permanently saved.
8. **Restart Test:** Go to your terminal, press `Ctrl+C` to stop the server. Run `java -cp bin com.medirisk.Main` again. Refresh the browser. Your patient and history will still be there, proving the file-based database works!

---

## Troubleshooting

- **Error: `java.net.BindException: Address already in use: bind`**
  Port 8080 is already being used by another application (like Tomcat or another web server). You must stop that application, or change the port in `Main.java` (line 52) to `8081` and recompile.

- **Error: `javac is not recognized as an internal or external command`**
  You need to install the Java Development Kit (JDK) and ensure that the `bin` directory of your JDK installation is added to your Windows system's `PATH` environment variable.

- **Where is my data?**
  All data is stored in plain text files inside the `data/` folder (e.g., `data/patients.txt`). You can open them in Notepad to view the pipe-delimited records. If you want to reset the database, simply delete the `.txt` files inside the `data/` folder and restart the server; it will recreate them with default sample data.
