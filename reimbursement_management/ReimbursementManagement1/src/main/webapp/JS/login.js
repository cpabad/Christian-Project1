function User(username, password){
    this.username = username
    this.password = password
}

function login(){
    let xhr = new XMLHttpRequest()
    loginUsername = document.getElementById('username').value
    loginPassword = document.getElementById('password').value
    let user = new User(loginUsername, loginPassword)
    xhr.open('POST', 'http://localhost:8080/ReimbursementManagement/login')
    xhr.send(JSON.stringify(user))
}

let button = document.getElementById('button')
button.addEventListener('click', login)
