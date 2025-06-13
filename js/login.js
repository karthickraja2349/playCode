//login
document.getElementById("LoginForm").addEventListener("submit", function(e){
         e.preventDefault();
        
         const formData = new FormData(this);
         const data = new URLSearchParams(formData);
         
         fetch("../login", {
               method : "POST",
               body      : data
         })
         .then(response => response.text())
         .then(text => {
                 if(text == "success"){
                      alert("Login successful!"); 
                      window.location.href = "/playCode/html/problems.html"; 
                 }
                 else if(text == "invalid"){
                      alert("Incorrect password.");
                 }
                 else if(text == "not_found"){
                      alert("User not found.");
                 }
                else {
                     alert("Login failed. Try again.");
                }
         })
         .catch(error => {
               console.error("Error during login:", error);
              alert("An error occurred during login.");
         });
});
