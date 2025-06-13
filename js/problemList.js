document.addEventListener("DOMContentLoaded", function() {
  fetch("/playCode/problems")
    .then(response => {
      if (!response.ok) {
        alert("Not logged in or session expired");
        window.location.href = "index.html";
        return;
      }
      return response.json();
    })
    .then(data => {
      const container = document.getElementById("problemList");

      data.forEach(problem => {
        const div = document.createElement("div");
        div.className = "problem-card";
        div.innerHTML = `
          <h2>${problem.id}</h2>
          <h3>${problem.title} ${problem.solved ? "✅" : ""}</h3>
          <p>${problem.description}</p>
          <button onclick="solve(${problem.problem_id})">Solve</button>
          <hr>
        `;
        container.appendChild(div);
      });
    })
    .catch(error => {
      console.error("Failed to load problems:", error);
    });

  window.solve = function(pid) {
    alert("You clicked problem ID: " + pid);
  };
});

