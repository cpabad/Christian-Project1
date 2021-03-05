let homepageButton = document.getElementById('homepage')
let updateButton = document.getElementById('updateUser')
let logoutButton = document.getElementById('logoutButton')

function goToHomepage(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.status === 200 && xhr.readyState ===4){
            window.open('http://localhost:8080/ReimbursementManagement/employeehomepage.html', '_self')
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/employeehomepage.html')
    xhr.send()
}
homepageButton.addEventListener('click', goToHomepage)

function updateUserInfo(){
    let oldusername = document.getElementById('oldusername').value
    let newusername = document.getElementById('newusername').value
    let oldpassword = document.getElementById('oldpassword').value
    let newpassword = document.getElementById('newpassword').value
    let oldemail = document.getElementById('oldemail').value
    let newemail = document.getElementById('newemail').value
    let url = 'http://localhost:8080/ReimbursementManagement/app/employee/update-user-information?oldusername=' + oldusername + '&newusername=' + newusername + '&oldpassword=' + oldpassword + '&newpassword=' + newpassword + '&oldemail=' + oldemail + '&newemail=' + newemail
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.status === 200 && xhr.readyState ===4){
            window.open('http://localhost:8080/ReimbursementManagement/employeehomepage.html', '_self')
        }
    }
    xhr.open('POST', url)
    xhr.send()
}
updateButton.addEventListener('click', updateUserInfo)

function logout(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.status === 200 && xhr.readyState ===4){
            window.alert('Hope your adventures take you back here and you have a nice day!')
            window.open('http://localhost:8080/ReimbursementManagement/index.html', '_self')
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/logout')
    xhr.send()
}
logoutButton.addEventListener('click', logout)