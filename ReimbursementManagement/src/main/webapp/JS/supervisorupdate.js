let homepageButton = document.getElementById('homepage')
let updateButton = document.getElementById('updateUser')
let logoutButton = document.getElementById('logoutButton')

let newUsernameInput = document.getElementById('newusername')
let confirmUsernameInput = document.getElementById('confirmnewusername')
let updateUsernameDiv = document.getElementById('updateUsernameDiv')
let hasNoUsernameMessage = true

let newPasswordInput = document.getElementById('newpassword')
let confirmPasswordInput = document.getElementById('confirmnewpassword')
let updatePasswordDiv = document.getElementById('updatePasswordDiv')
let hasNoPasswordMessage = true

let newEmailInput = document.getElementById('newemail')
let confirmEmailInput = document.getElementById('confirmnewemail')
let updateEmailDiv = document.getElementById('updateEmailDiv')
let hasNoEmailMessage = true

confirmUsernameInput.oninput = function validateUsername(){
    if(confirmUsernameInput.value === newUsernameInput.value){
        updateUsernameDiv.removeChild(updateUsernameDiv.getElementsByTagName('div')[0])
        hasNoUsernameMessage = true
    } else {
        if(hasNoUsernameMessage){
            let messageUsernameDiv = document.createElement('div')
            messageUsernameDiv.innerText = 'New usernames do not match'
            updateUsernameDiv.append(messageUsernameDiv)
            hasNoUsernameMessage = false
        }
    }
}

confirmPasswordInput.oninput = function validatePassword(){
    if(confirmPasswordInput.value === newPasswordInput.value){
        updatePasswordDiv.removeChild(updatePasswordDiv.getElementsByTagName('div')[0])
        hasNoPasswordMessage = true
    } else {
        if(hasNoPasswordMessage){
            let messagePasswordDiv = document.createElement('div')
            messagePasswordDiv.innerText = 'New passwords do not match'
            updatePasswordDiv.append(messagePasswordDiv)
            hasNoPasswordMessage = false
        }
    }
}

confirmEmailInput.oninput = function validateEmail(){
    if(confirmEmailInput.value === newEmailInput.value){
        updateEmailDiv.removeChild(updateEmailDiv.getElementsByTagName('div')[0])
        hasNoEmailMessage = true
    } else {
        if(hasNoEmailMessage){
            let messageEmailDiv = document.createElement('div')
            messageEmailDiv.innerText = 'New emails do not match'
            updateEmailDiv.append(messageEmailDiv)
            hasNoEmailMessage = false
        }
    }
}

function goToHomepage(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.status === 200 && xhr.readyState ===4){
            window.open('http://localhost:8080/ReimbursementManagement/supervisorhomepage.html', '_self')
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/supervisorhomepage.html')
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
    let confirmusername = document.getElementById('confirmnewusername').value
    let confirmpassword = document.getElementById('confirmnewpassword').value
    let confirmemail = document.getElementById('confirmnewemail').value
    let url = 'http://localhost:8080/ReimbursementManagement/app/manager/update-user-information?oldusername=' + oldusername + '&newusername=' + newusername + '&oldpassword=' + oldpassword + '&newpassword=' + newpassword + '&oldemail=' + oldemail + '&newemail=' + newemail + '&confirmusername=' + confirmusername + '&confirmpassword=' + confirmpassword + '&confirmemail=' + confirmemail
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.status === 200 && xhr.readyState === 4){
            window.alert('Update was successful')
            window.open('http://localhost:8080/ReimbursementManagement/supervisorhomepage.html', '_self')
        }
        if(xhr.status === 400 && xhr.readyState === 4){
            alert(xhr.responseText)
        }
    }
    xhr.open('POST', url)
    xhr.send()
}
updateButton.addEventListener('click', function(event){
    if(confirmUsernameInput.value === newUsernameInput.value && confirmPasswordInput.value === newPasswordInput.value && confirmEmailInput.value === newEmailInput.value){
        updateUserInfo()
    } else {
        event.preventDefault()
    }
    
})

function logout(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.status === 200 && xhr.readyState ===4){
            window.alert('Hope your adventures take you back here and you have a nice day!')
            window.open('http://localhost:8080/ReimbursementManagement/index.html', '_self')
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/logout')
    xhr.send()
}
logoutButton.addEventListener('click', logout)