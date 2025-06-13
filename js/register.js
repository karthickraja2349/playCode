//register
document.getElementById("registerForm").addEventListener("submit", function(e) {
    e.preventDefault(); 

    const userName = document.getElementById("userName").value.trim();
    const password = document.getElementById("userPassword").value.trim();
    const email = document.getElementById("emailID").value.trim();

    const nameRegex = /^[A-Za-z][A-Za-z ]{2,29}$/;
    const passRegex = /^[A-Za-z][A-Za-z0-9!@#$%^&*]{7}$/;
    const emailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;

    if (!nameRegex.test(userName)) {
        alert("Invalid Name: Must start with a letter and be 3-30 characters long.");
        return;
    }

    if (!passRegex.test(password)) {
        alert("Invalid Password: Must be 8 characters, start with a letter, and can include !@#$%^&*");
        return;
    }

    if (!emailRegex.test(email)) {
        alert("Invalid Email: Must be a valid Gmail address.");
        return;
    }

    const formData = new FormData(this);
    const data = new URLSearchParams(formData);

    fetch("/playCode/register", {
        method: "POST",
        body: data
    })
    .then(response => response.text())
    .then(text => {
        if (text === "success") {
            alert("Account created successfully!");
            
            fetch("/playCode/mail", {
                   method : "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                     },
                     body: `userName=${encodeURIComponent(userName)}&emailID=${encodeURIComponent(email)}`
             })
             .then(response => response.text())
             .then(mailResponse => {
                  console.log("Email sent:", mailResponse);
               })
             .catch(error => {
                   console.error("Email error:", error);
               });
            setTimeout(() => {
                    window.location.href = "/playCode/html/index.html";
                }, 2000);
        } 
        else if (text === "exists") {
            alert("User already exists!");
        } 
        else if (text === "invalid_name") {
            alert("Name is not valid.");
        } 
        else if (text === "invalid_password") {
            alert("Password is not valid.");
        } 
        else if (text === "invalid_email") {
            alert("Email is not valid.");
        } 
        else {
            alert("Failed to register. Try again.");
        }
    })
    .catch(error => {
        console.error("Error:", error);
        alert("An error occurred.");
    });
});
