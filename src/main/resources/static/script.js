document.getElementById("student-form").addEventListener("submit", function(event) {
    event.preventDefault();

    const firstName = document.getElementById("first-name").value;
    const lastName = document.getElementById("last-name").value;
    const classNumber = document.getElementById("class-number").value;

    const token = localStorage.getItem("jwt");

    fetch("/students", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer"+token
        },
        body: JSON.stringify({ firstName, lastName, classNumber })
    })
        .then(response => response.json())
        .then(data => {
            alert("Учня додано!");

        })
        .catch(error => console.error("Помилка при додаванні учня:", error));
});

// Завантаження списку студентів при відкритті сторінки
document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("jwt");

    fetch("/students", {
        headers: {
            "Authorization": "Bearer "+token
        }
    })
        .then(response => response.json())
        .then(data => {
            const list = document.getElementById("students-list");
            list.innerHTML = ""; // Очищаємо список перед додаванням
            data.forEach(student => {
                const li = document.createElement("li");
                li.textContent = `${student.firstName} ${student.lastName} (Клас: ${student.classNumber})`;
                list.appendChild(li);
            });
        })
        .catch(error => console.error("Помилка при завантаженні студентів:", error));
});