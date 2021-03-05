function login(){
    let xhr = new XMLHttpRequest()
    loginUsername = document.getElementById('username').value
    loginPassword = document.getElementById('password').value
    let url = 'http://localhost:8080/ReimbursementManagement/app/login?' + 'username=' + loginUsername + '&' + 'password=' + loginPassword
    xhr.onreadystatechange = function(){
        if(xhr.status === 200 && xhr.readyState === 4){
        window.alert('Welcome back, ' + loginUsername + '!')
        window.open('http://localhost:8080/ReimbursementManagement/employeehomepage.html', '_self')        
        }
    }
    xhr.open('POST', url)
    xhr.send()
}
let loginButton = document.getElementById('loginButton')
loginButton.addEventListener('click', login)

function logout(){
    let xhr = new XMLHttpRequest()
    xhr.open('POST', 'http://localhost:8080/ReimbursementManagement/app/logout')
    xhr.send()
}
let logoutButton = document.getElementById('logoutButton')
logoutButton.addEventListener('click', logout)