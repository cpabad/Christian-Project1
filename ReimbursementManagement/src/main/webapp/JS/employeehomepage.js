
let pendingRequestEmpDiv = document.getElementById('pendingRequestEmpDiv')
let hasNoPendingEmpTable = true
function viewPendingEmpRequest(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.readyState === 4 && xhr.status === 200) {
            let requests = JSON.parse(xhr.response)
            
            if(hasNoPendingEmpTable && requests.length > 0){
                hasNoPendingEmpTable = false
                if(hasNoResolvedEmpTable === false){
                    hasNoResolvedEmpTable = true
                    resolvedRequestEmpDiv.removeChild(resolvedRequestEmpDiv.getElementsByTagName('table')[0])

                }
                if(hasNoUserTable === false){
                    hasNoUserTable = true
                    viewUserDiv.removeChild(viewUserDiv.getElementsByTagName('table')[0])

                }
                let pendingRequestEmpTable = document.createElement('table')
                let thead = document.createElement('thead')
                let trHead = document.createElement('tr')
                let labelh1 = document.createElement('td')
                let labelh2 = document.createElement('td')
                let labelh3 = document.createElement('td')
                let labelh5 = document.createElement('td')
                let labelh6 = document.createElement('td')
                let labelh7 = document.createElement('td')
                labelh1.innerText = 'Request Id#'
                labelh2.innerText = 'Amount Requested in USD'
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
                pendingRequestEmpTable.append(thead)
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

                    let date = new Date(r["eventDate"])
                    let eventDate = date.toDateString()
                    
                    trBody.append(labelb1)
                    trBody.append(labelb2)
                    trBody.append(eventDate)
                    trBody.append(labelb5)
                    trBody.append(labelb6)
                    trBody.append(labelb7)
                    tbody.append(trBody)
                    
                    pendingRequestEmpTable.append(tbody)
                    pendingRequestEmpDiv.append(pendingRequestEmpTable)                
                }
            } else if(hasNoPendingEmpTable && requests.length == 0){
                hasNoPendingEmpTable = false
                let table = document.createElement('table')
                let message = 'No Requests Found'
                table.append(message)
                pendingRequestEmpDiv.append(table)
                if(hasNoResolvedEmpTable === false){
                    hasNoResolvedEmpTable = true
                    resolvedRequestEmpDiv.removeChild(resolvedRequestEmpDiv.getElementsByTagName('table')[0])

                }
                if(hasNoUserTable === false){
                    hasNoUserTable = true
                    viewUserDiv.removeChild(viewUserDiv.getElementsByTagName('table')[0])

                }
            } 
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/employee/view-pending-requests')
    xhr.send()
}
let viewPendingRequestEmpButton = document.getElementById('viewPendingRequestEmpStatus')
viewPendingRequestEmpButton.addEventListener('click', viewPendingEmpRequest)


let resolvedRequestEmpDiv = document.getElementById('resolvedRequestEmpDiv')
let hasNoResolvedEmpTable = true
function viewResolvedEmpRequest(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.readyState === 4 && xhr.status === 200) {
            let requests = JSON.parse(xhr.response)
            
            if(hasNoResolvedEmpTable && requests.length > 0){
                hasNoResolvedEmpTable = false
                if(hasNoPendingEmpTable === false){
                    hasNoPendingEmpTable = true
                    pendingRequestEmpDiv.removeChild(pendingRequestEmpDiv.getElementsByTagName('table')[0])

                }
                if(hasNoUserTable === false){
                    hasNoUserTable = true
                    viewUserDiv.removeChild(viewUserDiv.getElementsByTagName('table')[0])

                }
                let resolvedRequestEmpTable = document.createElement('table')
                let thead = document.createElement('thead')
                let trHead = document.createElement('tr')
                let labelh1 = document.createElement('td')
                let labelh2 = document.createElement('td')
                let labelh3 = document.createElement('td')
                let labelh5 = document.createElement('td')
                let labelh6 = document.createElement('td')
                let labelh7 = document.createElement('td')
                labelh1.innerText = 'Request Id#'
                labelh2.innerText = 'Amount Requested in USD'
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
                resolvedRequestEmpTable.append(thead)
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

                    let date = new Date(r["eventDate"])
                    let eventDate = date.toDateString()
                    
                    trBody.append(labelb1)
                    trBody.append(labelb2)
                    trBody.append(eventDate)
                    trBody.append(labelb5)
                    trBody.append(labelb6)
                    trBody.append(labelb7)
                    tbody.append(trBody)
                    
                    resolvedRequestEmpTable.append(tbody)
                    resolvedRequestEmpDiv.append(resolvedRequestEmpTable)                
                }
            } else if(hasNoResolvedEmpTable && requests.length == 0){
                hasNoResolvedEmpTable = false
                let table = document.createElement('table')
                let message = 'No Requests Found'
                table.append(message)
                resolvedRequestEmpDiv.append(table)
                if(hasNoPendingEmpTable === false){
                    hasNoPendingEmpTable = true
                    pendingRequestEmpDiv.removeChild(pendingRequestEmpDiv.getElementsByTagName('table')[0])

                }
                if(hasNoUserTable === false){
                    hasNoUserTable = true
                    viewUserDiv.removeChild(viewUserDiv.getElementsByTagName('table')[0])

                }
            } 
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/employee/view-resolved-requests')
    xhr.send()
}
let viewResolvedRequestEmpButton = document.getElementById('viewResolvedRequestEmpStatus')
viewResolvedRequestEmpButton.addEventListener('click', viewResolvedEmpRequest)




let hasNoUserTable = true
let viewUserDiv = document.getElementById('viewUserDiv')
function viewUser(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.readyState === 4 && xhr.status === 200) {
            let requests = JSON.parse(xhr.response)
            
            if(hasNoUserTable){
                hasNoUserTable = false
                if(hasNoResolvedEmpTable === false){
                    hasNoResolvedEmpTable = true
                    resolvedRequestEmpDiv.removeChild(resolvedRequestEmpDiv.getElementsByTagName('table')[0])

                }
                if(hasNoPendingEmpTable === false){
                    hasNoPendingEmpTable = true
                    pendingRequestEmpDiv.removeChild(pendingRequestEmpDiv.getElementsByTagName('table')[0])

                }
                let usertable = document.createElement('table')
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
