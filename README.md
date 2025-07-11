# PlayCode 

**An online code editor and problem-solving platform**  
Supports multiple users, secure login, test case evaluation, and submission tracking.

Demo Video:
https://drive.google.com/file/d/1KHOEiJ31EqEiyPu82gLa-HIgOcHIUD6t/view?usp=drive_link

---

## 🚀 Project Flow

1. **Home Page**
   - Options: `Login` / `Register`

2. **Register**
   - Inputs: `Username`, `Password`, `Email`
   - ✅ Password is encrypted
   - 📧 Email is sent using Jakarta Mail API
   - Redirects to login page

3. **Login**
   - Inputs: `Username`, `Password`
   - On success → redirect to problem list

4. **Problems Page**
   - List of all coding problems
   - ✅ Solved problems are marked with a **green tick**
   - 🔍 Search by problem name

5. **Problem Solve Flow**
   - Click a problem → Open the editor interface:

     ```  left side                           right side
     ┌───────────────────────────────────────┬──────────────────────────────────────┐
     │ Question & 3 hardcoded test cases     │ Code Editor                          │
     │ + 3 hidden test cases (not shown)     │ Run Button                           │
     │                                       │ Output Box                           │
     └───────────────────────────────────────┴──────────────────────────────────────┘
     ```

   - **Run Button**: Executes only visible test cases
   - **Submit Button**: Executes all test cases (including hidden ones)
     - ✅ If passed: save submission to MySQL
     - ❌ If hidden case fails: show `"Hidden test case failed"` alert only

6. **Submissions View**
   - User can see all previous submissions for a problem

---

## 🛠️ Tech Stack

- **Frontend**: HTML, CSS, JavaScript
- **Backend**: Java Servlets
- **Database**: MySQL
- **Libraries/Tools**:
  - Jakarta Mail API (for sending registration email)
- **App Server**: Apache Tomcat

---

## 🔐 Features

- Multi-user support
- Password encryption
- Email verification on registration
- Problem list with search and status tracking
- Code editor with:
  - Hardcoded & hidden test case evaluation
  - Run, Submit, View Submissions options
- Submission history tracking
- Fully integrated with MySQL

------





## 📂 Directory Structure (Example)
     playCode/
       ├── css/
       ├── js/
       ├── html/
       ├── servlets/
            └── *.java
       ├── database/
       ├── user_submissions/
       ├── utils/
       ├── WEB-INF/
           └── classes
       └── README.md
---

## 📝 Note

 Only Java is currently supported

To deploy:
- Place the folder under `webapps/` in Tomcat
- Ensure `WEB-INF/web.xml` is correctly configured or use annotations(@)
- Make sure MySQL DB is up and running

---

## 🙌 Author

Created by **Karthick Raja K**  
gmail -> karthickraja182356@gmail.com
