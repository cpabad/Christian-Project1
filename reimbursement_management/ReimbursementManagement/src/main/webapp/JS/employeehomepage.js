let pendingRequestDiv = document.getElementById('pendingRequestDiv')
// function showPendingRequests(){
//     if(pendingrequesttable === "none") {
//         pendingrequesttable.style.display = "block"
//     } else {
//         pendingrequesttable.style.display = "none"
//     }
// }
let hasNoPendingTable = true
let pendingrequesttable = document.createElement('table')
function viewPendingRequest(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.readyState === 4 && xhr.status === 200) {
            let requests = JSON.parse(xhr.response)
            
            if(hasNoPendingTable){
                hasNoPendingTable = false
                
            
                let thead = document.createElement('thead')
                let trHead = document.createElement('tr')
                let labelh1 = document.createElement('td')
                let labelh2 = document.createElement('td')
                let labelh3 = document.createElement('td')
                let labelh5 = document.createElement('td')
                let labelh6 = document.createElement('td')
                let labelh7 = document.createElement('td')
                labelh1.innerText = 'Request Id#'
                labelh2.innerText = 'Amount Requested'
                labelh3.innerText = 'Event Date'
                labelh5.innerText = 'Requested Event'
                labelh6.innerText = 'Requester Email'
                labelh7.innerText = 'Status'
                trHead.append(labelh1)
                trHead.append(labelh2)
                trHead.append(labelh3)
                trHead.append(labelh5)
                trHead.append(labelh6)
                trHead.append(labelh7)
                thead.append(trHead)
                
                let tbody = document.createElement('tbody')
                for(r of requests){
                    let trBody = document.createElement('tr')
                    let labelb1 = document.createElement('td')
                    let labelb2 = document.createElement('td')
                    let labelb3 = document.createElement('td')
                    let labelb5 = document.createElement('td')
                    let labelb6 = document.createElement('td')
                    let labelb7 = document.createElement('td')

                    

                    labelb1.innerHTML = r["requestId"]
                    labelb2.innerHTML = r["amount"]
                    labelb3.innerHTML = r["eventDate"]
                    labelb5.innerHTML = r["requestedEvent"]
                    labelb6.innerHTML = r["requester"]["email"]
                    labelb7.innerHTML = r["requestStatus"]["status"]

                    // let eventDate = new Date(labelb3)
                    // let eved = eventDate.getFullYear() + "-" + eventDate.getMonth() + "-" + eventDate.getDay()
                    trBody.append(labelb1)
                    trBody.append(labelb2)
                    trBody.append(labelb3)
                    trBody.append(labelb5)
                    trBody.append(labelb6)
                    trBody.append(labelb7)
                    tbody.append(trBody)
                    
                    pendingrequesttable.append(thead)
                    pendingrequesttable.append(tbody)
                    pendingRequestDiv.append(pendingrequesttable)
                }
            
            } 

        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/employee/view-pending-requests')
    xhr.send()
}

let viewPendingRequestButton = document.getElementById('viewPendingRequestStatus')
viewPendingRequestButton.addEventListener('click', viewPendingRequest)
// viewPendingRequestButton.addEventListener('click', showPendingRequests)

let resolvedRequestDiv = document.getElementById('resolvedRequestDiv')
let hasNoResolvedTable = true
let resolvedrequesttable = document.createElement('table')
function viewResolvedRequest(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.readyState === 4 && xhr.status === 200) {
            let requests = JSON.parse(xhr.response)
            
            if(hasNoResolvedTable){
                hasNoResolvedTable = false
                
            
                let thead = document.createElement('thead')
                let trHead = document.createElement('tr')
                let labelh1 = document.createElement('td')
                let labelh2 = document.createElement('td')
                let labelh3 = document.createElement('td')
                let labelh5 = document.createElement('td')
                let labelh6 = document.createElement('td')
                let labelh7 = document.createElement('td')
                labelh1.innerText = 'Request Id#'
                labelh2.innerText = 'Amount Requested'
                labelh3.innerText = 'Event Date'
                labelh5.innerText = 'Requested Event'
                labelh6.innerText = 'Requester Email'
                labelh7.innerText = 'Status'
                trHead.append(labelh1)
                trHead.append(labelh2)
                trHead.append(labelh3)
                trHead.append(labelh5)
                trHead.append(labelh6)
                trHead.append(labelh7)
                thead.append(trHead)
                
                let tbody = document.createElement('tbody')
                for(r of requests){
                    let trBody = document.createElement('tr')
                    let labelb1 = document.createElement('td')
                    let labelb2 = document.createElement('td')
                    let labelb3 = document.createElement('td')
                    let labelb5 = document.createElement('td')
                    let labelb6 = document.createElement('td')
                    let labelb7 = document.createElement('td')

                    labelb1.innerHTML = r["requestId"]
                    labelb2.innerHTML = r["amount"]
                    labelb3.innerHTML = r["eventDate"]
                    labelb5.innerHTML = r["requestedEvent"]
                    labelb6.innerHTML = r["requester"]["email"]
                    labelb7.innerHTML = r["requestStatus"]["status"]

                    let eventDate = new Date(labelb3)
                    let eved = eventDate.getFullYear() + "-" + eventDate.getMonth() + "-" + eventDate.getDay()
                    trBody.append(labelb1)
                    trBody.append(labelb2)
                    trBody.append(eved)
                    trBody.append(labelb5)
                    trBody.append(labelb6)
                    trBody.append(labelb7)
                    tbody.append(trBody)
                    
                    resolvedrequesttable.append(thead)
                    resolvedrequesttable.append(tbody)
                    resolvedRequestDiv.append(resolvedrequesttable)
                }
            
            }
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/employee/view-resolved-requests')
    xhr.send()
}

let viewResolvedRequestButton = document.getElementById('viewResolvedRequestStatus')
viewResolvedRequestButton.addEventListener('click', viewResolvedRequest)



let hasNoUserTable = true
let viewUserDiv = document.getElementById('viewUserDiv')
let usertable = document.createElement('table')
function viewUser(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.readyState === 4 && xhr.status === 200) {
            let requests = JSON.parse(xhr.response)
            
            if(hasNoUserTable){
                hasNoUserTable = false
                
            
                let thead = document.createElement('thead')
                let trHead = document.createElement('tr')
                let labelh1 = document.createElement('td')
                let labelh2 = document.createElement('td')
                let labelh3 = document.createElement('td')
                let labelh5 = document.createElement('td')
                let labelh6 = document.createElement('td')
                let labelh7 = document.createElement('td')
                labelh1.innerText = 'User Id#'
                labelh2.innerText = 'Username'
                labelh3.innerText = 'First Name'
                labelh5.innerText = 'Last Name'
                labelh6.innerText = 'Email'
                labelh7.innerText = 'Role'
                trHead.append(labelh1)
                trHead.append(labelh2)
                trHead.append(labelh3)
                trHead.append(labelh5)
                trHead.append(labelh6)
                trHead.append(labelh7)
                thead.append(trHead)
                
                let tbody = document.createElement('tbody')
                let trBody = document.createElement('tr')
                let labelb1 = document.createElement('td')
                let labelb2 = document.createElement('td')
                let labelb3 = document.createElement('td')
                let labelb5 = document.createElement('td')
                let labelb6 = document.createElement('td')
                let labelb7 = document.createElement('td')

                labelb1.innerHTML = requests["userId"]
                labelb2.innerHTML = requests["username"]
                labelb3.innerHTML = requests["firstName"]
                labelb5.innerHTML = requests["lastName"]
                labelb6.innerHTML = requests["email"]
                labelb7.innerHTML = requests["role"]["role"]

                trBody.append(labelb1)
                trBody.append(labelb2)
                trBody.append(labelb3)
                trBody.append(labelb5)
                trBody.append(labelb6)
                trBody.append(labelb7)
                tbody.append(trBody)
                
                usertable.append(thead)
                usertable.append(tbody)
                viewUserDiv.append(usertable)
        
            }
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/employee/view-user-information')
    xhr.send()
}

let viewUserButton = document.getElementById('viewUser')
viewUserButton.addEventListener('click', viewUser)

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

let logoutButton = document.getElementById('logoutButton')
logoutButton.addEventListener('click', logout)

// function viewUserInfo(){
//     let xhr = new XMLHttpRequest()
//     xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/employee/view-user-information')
//     xhr.send()
// }
// let viewUserButton = document.getElementById('viewUser')
// viewUserButton.addEventListener('click', viewUserInfo)

function goToUpdateUser(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.status === 200 && xhr.readyState === 4) {
            window.open('http://localhost:8080/ReimbursementManagement/employeeupdate.html', '_self')
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/employeeupdate.html')
    xhr.send()
}
let updateUserButton = document.getElementById('updateUser')
updateUserButton.addEventListener('click', goToUpdateUser)

function goToSubmitRequest(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.status === 200 && xhr.readyState === 4) {
            window.open('http://localhost:8080/ReimbursementManagement/employeerequestsubmission.html', '_self')
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/employeerequestsubmission.html')
    xhr.send()
}
let requestButton = document.getElementById('submitRequest')
requestButton.addEventListener('click', goToSubmitRequest)

// function upload(){
//     let formData = new FormData()
//     formData.append('myFile', document.getElementById(myFileField).files[0])

//     let xhr = new XMLHttpRequest()
//     // xhr.onreadystatechange = function(){
//     //     if(xhr.status === 200 && xhr.readyState === 4){
//     //         window.open('http://localhost:8080/ReimbursementManagement/employeehomepage.html', '_self')
//     //     }
//     // }
//     xhr.open('POST', 'http://localhost:8080/ReimbursementManagement/upload-file')
//     xhr.setRequestHeader('Content-Type', 'multipart/form-data')
//     xhr.send(formData)
// }

// uploadButton = document.getElementById('submitRequest')
// uploadButton.addEventListener('click', upload)

