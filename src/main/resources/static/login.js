document.getElementById("login").addEventListener("submit", function(event) {
    event.preventDefault();


    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    fetch("/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password })
    })
        .then(response =>{
            if (!response.ok){
                throw  new Error(" login error")
            }
            return response.text();
        })
        .then(token =>{
            localStorage.setItem("jwt", token);
            window.location.href = "index.html"

    })
        .catch(error => alert("Login Error"+error.message))
});