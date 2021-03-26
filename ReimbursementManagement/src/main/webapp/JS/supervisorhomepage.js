let pendingRequestMgrDiv = document.getElementById('pendingRequestMgrDiv')
let hasNoPendingMgrTable = true
function viewPendingMgrRequest(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.readyState === 4 && xhr.status === 200) {
            let requests = JSON.parse(xhr.response)
            
            if(hasNoPendingMgrTable && requests.length > 0){
                hasNoPendingMgrTable = false
                if(hasNoResolvedMgrTable === false){
                    hasNoResolvedMgrTable = true
                    resolvedRequestMgrDiv.removeChild(resolvedRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoUserTable === false){
                    hasNoUserTable = true
                    viewUserDiv.removeChild(viewUserDiv.getElementsByTagName('table')[0])

                }
                if(hasNoEmployeeManagerTable === false){
                    hasNoEmployeeManagerTable = true
                    viewEmployeeManagerDiv.removeChild(viewEmployeeManagerDiv.getElementsByTagName('table')[0])
                }
                if(hasNoPendingRequestsForOwnEmployeeTable === false){
                    hasNoPendingRequestsForOwnEmployeeTable = true
                    viewPendingRequestsForOwnEmployeesDiv.removeChild(viewPendingRequestsForOwnEmployeesDiv.getElementsByTagName('table')[0])
                }
                if(hasNoResolvedRequestTable === false){
                    hasNoResolvedRequestTable = true
                    viewAllResolvedRequestDiv.removeChild(viewAllResolvedRequestDiv.getElementsByTagName('table')[0])
                }
                let pendingRequestMgrTable = document.createElement('table')
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
                pendingRequestMgrTable.append(thead)
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
                    
                    pendingRequestMgrTable.append(tbody)
                    pendingRequestMgrDiv.append(pendingRequestMgrTable)                
                }
            } else if(hasNoPendingMgrTable && requests.length == 0){
                hasNoPendingMgrTable = false
                let table = document.createElement('table')
                let message = 'No Requests Found'
                table.append(message)
                pendingRequestMgrDiv.append(table)
                if(hasNoResolvedMgrTable === false){
                    hasNoResolvedMgrTable = true
                    resolvedRequestMgrDiv.removeChild(resolvedRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoUserTable === false){
                    hasNoUserTable = true
                    viewUserDiv.removeChild(viewUserDiv.getElementsByTagName('table')[0])

                }
                if(hasNoEmployeeManagerTable === false){
                    hasNoEmployeeManagerTable = true
                    viewEmployeeManagerDiv.removeChild(viewEmployeeManagerDiv.getElementsByTagName('table')[0])
                }
                if(hasNoPendingRequestsForOwnEmployeeTable === false){
                    hasNoPendingRequestsForOwnEmployeeTable = true
                    viewPendingRequestsForOwnEmployeesDiv.removeChild(viewPendingRequestsForOwnEmployeesDiv.getElementsByTagName('table')[0])
                }
                if(hasNoResolvedRequestTable === false){
                    hasNoResolvedRequestTable = true
                    viewAllResolvedRequestDiv.removeChild(viewAllResolvedRequestDiv.getElementsByTagName('table')[0])
                }
            } 
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/manager/view-pending-requests')
    xhr.send()
}
let viewPendingRequestMgrButton = document.getElementById('viewPendingRequestMgrStatus')
viewPendingRequestMgrButton.addEventListener('click', viewPendingMgrRequest)


let resolvedRequestMgrDiv = document.getElementById('resolvedRequestMgrDiv')
let hasNoResolvedMgrTable = true
function viewResolvedMgrRequest(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.readyState === 4 && xhr.status === 200) {
            let requests = JSON.parse(xhr.response)
            
            if(hasNoResolvedMgrTable && requests.length > 0){
                hasNoResolvedMgrTable = false
                if(hasNoPendingMgrTable === false){
                    hasNoPendingMgrTable = true
                    pendingRequestMgrDiv.removeChild(pendingRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoUserTable === false){
                    hasNoUserTable = true
                    viewUserDiv.removeChild(viewUserDiv.getElementsByTagName('table')[0])

                }
                if(hasNoEmployeeManagerTable === false){
                    hasNoEmployeeManagerTable = true
                    viewEmployeeManagerDiv.removeChild(viewEmployeeManagerDiv.getElementsByTagName('table')[0])
                }
                if(hasNoPendingRequestsForOwnEmployeeTable === false){
                    hasNoPendingRequestsForOwnEmployeeTable = true
                    viewPendingRequestsForOwnEmployeesDiv.removeChild(viewPendingRequestsForOwnEmployeesDiv.getElementsByTagName('table')[0])
                }
                if(hasNoResolvedRequestTable === false){
                    hasNoResolvedRequestTable = true
                    viewAllResolvedRequestDiv.removeChild(viewAllResolvedRequestDiv.getElementsByTagName('table')[0])
                }
                let resolvedRequestMgrTable = document.createElement('table')
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
                resolvedRequestMgrTable.append(thead)
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
                    
                    resolvedRequestMgrTable.append(tbody)
                    resolvedRequestMgrDiv.append(resolvedRequestMgrTable)                
                }
            } else if(hasNoResolvedMgrTable && requests.length === 0){
                hasNoResolvedMgrTable = false
                let table = document.createElement('table')
                let message = 'No Requests Found'
                table.append(message)
                resolvedRequestMgrDiv.append(table)
                if(hasNoPendingMgrTable === false){
                    hasNoPendingMgrTable = true
                    pendingRequestMgrDiv.removeChild(pendingRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoUserTable === false){
                    hasNoUserTable = true
                    viewUserDiv.removeChild(viewUserDiv.getElementsByTagName('table')[0])

                }
                if(hasNoEmployeeManagerTable === false){
                    hasNoEmployeeManagerTable = true
                    viewEmployeeManagerDiv.removeChild(viewEmployeeManagerDiv.getElementsByTagName('table')[0])
                }
                if(hasNoPendingRequestsForOwnEmployeeTable === false){
                    hasNoPendingRequestsForOwnEmployeeTable = true
                    viewPendingRequestsForOwnEmployeesDiv.removeChild(viewPendingRequestsForOwnEmployeesDiv.getElementsByTagName('table')[0])
                }
                if(hasNoResolvedRequestTable === false){
                    hasNoResolvedRequestTable = true
                    viewAllResolvedRequestDiv.removeChild(viewAllResolvedRequestDiv.getElementsByTagName('table')[0])
                }
            }
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/manager/view-resolved-requests')
    xhr.send()
}
let viewResolvedRequestMgrButton = document.getElementById('viewResolvedRequestMgrStatus')
viewResolvedRequestMgrButton.addEventListener('click', viewResolvedMgrRequest)




let hasNoUserTable = true
let viewUserDiv = document.getElementById('viewUserDiv')
function viewUser(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.readyState === 4 && xhr.status === 200) {
            let requests = JSON.parse(xhr.response)
            
            if(hasNoUserTable){
                hasNoUserTable = false
                if(hasNoResolvedMgrTable === false){
                    hasNoResolvedMgrTable = true
                    resolvedRequestMgrDiv.removeChild(resolvedRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoPendingMgrTable === false){
                    hasNoPendingMgrTable = true
                    pendingRequestMgrDiv.removeChild(pendingRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoEmployeeManagerTable === false){
                    hasNoEmployeeManagerTable = true
                    viewEmployeeManagerDiv.removeChild(viewEmployeeManagerDiv.getElementsByTagName('table')[0])
                }
                if(hasNoPendingRequestsForOwnEmployeeTable === false){
                    hasNoPendingRequestsForOwnEmployeeTable = true
                    viewPendingRequestsForOwnEmployeesDiv.removeChild(viewPendingRequestsForOwnEmployeesDiv.getElementsByTagName('table')[0])
                }
                if(hasNoResolvedRequestTable === false){
                    hasNoResolvedRequestTable = true
                    viewAllResolvedRequestDiv.removeChild(viewAllResolvedRequestDiv.getElementsByTagName('table')[0])
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
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/manager/view-user-information')
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
            window.open('http://localhost:8080/ReimbursementManagement/supervisorupdate.html', '_self')
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/supervisorupdate.html')
    xhr.send()
}
let updateUserButton = document.getElementById('updateUser')
updateUserButton.addEventListener('click', goToUpdateUser)

function goToSubmitRequest(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.status === 200 && xhr.readyState === 4) {
            window.open('http://localhost:8080/ReimbursementManagement/supervisorrequestsubmission.html', '_self')
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/supervisorrequestsubmission.html')
    xhr.send()
}
let requestButton = document.getElementById('submitRequest')
requestButton.addEventListener('click', goToSubmitRequest)

function goToApproval(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.status === 200 && xhr.readyState === 4){
            window.open('http://localhost:8080/ReimbursementManagement/supervisorapproval.html', '_self')
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/supervisorapproval.html')
    xhr.send()
}
let approvalButton = document.getElementById('approval')
approvalButton.addEventListener('click', goToApproval)

let viewPendingRequestsForOwnEmployeesDiv = document.getElementById('viewPendingRequestsForOwnEmployeesDiv')
let hasNoPendingRequestsForOwnEmployeeTable = true
function viewPendingRequest(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.readyState === 4 && xhr.status === 200) {
            let requests = JSON.parse(xhr.response)

            if(hasNoPendingRequestsForOwnEmployeeTable & requests.length > 0){
                hasNoPendingRequestsForOwnEmployeeTable = false
                let table = document.createElement('table')

                let thead = document.createElement('thead')
                let trHead = document.createElement('tr')
                let labelh1 = document.createElement('td')
                let labelh2 = document.createElement('td')
                let labelh3 = document.createElement('td')
                let labelh5 = document.createElement('td')
                let labelh6 = document.createElement('td')
                labelh1.innerText = 'Request Id#'
                labelh2.innerText = 'Requested Event'
                labelh3.innerText = 'Event Date'
                labelh5.innerText = 'Requester Email'
                labelh6.innerText = 'Amount Requested in USD'
                trHead.append(labelh1)
                trHead.append(labelh2)
                trHead.append(labelh5)
                trHead.append(labelh6)
                trHead.append(labelh3)
                
                thead.append(trHead)
                table.append(thead)
                let tbody = document.createElement('tbody')
                if(hasNoResolvedMgrTable === false){
                    hasNoResolvedMgrTable = true
                    resolvedRequestMgrDiv.removeChild(resolvedRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoPendingMgrTable === false){
                    hasNoPendingMgrTable = true
                    pendingRequestMgrDiv.removeChild(pendingRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoUserTable === false){
                    hasNoUserTable = true
                    viewUserDiv.removeChild(viewUserDiv.getElementsByTagName('table')[0])

                }
                if(hasNoEmployeeManagerTable === false){
                    hasNoEmployeeManagerTable = true
                    viewEmployeeManagerDiv.removeChild(viewEmployeeManagerDiv.getElementsByTagName('table')[0])
                }
                if(hasNoResolvedRequestTable === false){
                    hasNoResolvedRequestTable = true
                    viewAllResolvedRequestDiv.removeChild(viewAllResolvedRequestDiv.getElementsByTagName('table')[0])
                }
                for(r of requests){
                    let trBody = document.createElement('tr')

                    let labelb1 = document.createElement('td')
                    let labelb2 = document.createElement('td')
                    let labelb3 = document.createElement('td')
                    let labelb5 = document.createElement('td')
                    let labelb6 = document.createElement('td')
                    
                    labelb1.innerHTML = r["request"]["requestId"]
                    labelb5.innerHTML = r["request"]["requestedEvent"]
                    labelb6.innerHTML = r["request"]["requester"]["email"]
                    labelb2.innerHTML = r["request"]["amount"]
                    labelb3.innerHTML = new Date(r["request"]["eventDate"]).toDateString()
                    
                    trBody.append(labelb1)
                    trBody.append(labelb5)
                    trBody.append(labelb6)
                    trBody.append(labelb2)
                    trBody.append(labelb3)
                    tbody.append(trBody)
                    
                    table.append(tbody)
                    viewPendingRequestsForOwnEmployeesDiv.append(table)
                }

            } else if(hasNoPendingRequestsForOwnEmployeeTable && requests.length === 0){
                hasNoPendingRequestsForOwnEmployeeTable = false
                let table = document.createElement('table')
                let message = 'No Requests Found'
                table.append(message)
                viewPendingRequestsForOwnEmployeesDiv.append(table)
                if(hasNoResolvedMgrTable === false){
                    hasNoResolvedMgrTable = true
                    resolvedRequestMgrDiv.removeChild(resolvedRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoPendingMgrTable === false){
                    hasNoPendingMgrTable = true
                    pendingRequestMgrDiv.removeChild(pendingRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoUserTable === false){
                    hasNoUserTable = true
                    viewUserDiv.removeChild(viewUserDiv.getElementsByTagName('table')[0])

                }
                if(hasNoEmployeeManagerTable === false){
                    hasNoEmployeeManagerTable = true
                    viewEmployeeManagerDiv.removeChild(viewEmployeeManagerDiv.getElementsByTagName('table')[0])
                }
                if(hasNoResolvedRequestTable === false){
                    hasNoResolvedRequestTable = true
                    viewAllResolvedRequestDiv.removeChild(viewAllResolvedRequestDiv.getElementsByTagName('table')[0])
                }
            }
        }
        
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/manager/view-employee-pending-approvals')
    xhr.send()
}
let viewPendingRequestsForOwnEmployeeButton = document.getElementById('viewPendingRequestsForOwnEmployees')
viewPendingRequestsForOwnEmployeeButton.addEventListener('click', viewPendingRequest)

let hasNoResolvedRequestTable = true
let viewAllResolvedRequestDiv = document.getElementById('viewAllResolvedRequestsDiv')
function viewAllResolvedRequest(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.readyState === 4 && xhr.status === 200){
            let requests = JSON.parse(xhr.response)
            if(hasNoResolvedRequestTable & requests.length > 0){
                hasNoResolvedRequestTable = false
                if(hasNoResolvedMgrTable === false){
                    hasNoResolvedMgrTable = true
                    resolvedRequestMgrDiv.removeChild(resolvedRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoPendingMgrTable === false){
                    hasNoPendingMgrTable = true
                    pendingRequestMgrDiv.removeChild(pendingRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoUserTable === false){
                    hasNoUserTable = true
                    viewUserDiv.removeChild(viewUserDiv.getElementsByTagName('table')[0])

                }
                if(hasNoEmployeeManagerTable === false){
                    hasNoEmployeeManagerTable = true
                    viewEmployeeManagerDiv.removeChild(viewEmployeeManagerDiv.getElementsByTagName('table')[0])
                }
                if(hasNoPendingRequestsForOwnEmployeeTable === false){
                    hasNoPendingRequestsForOwnEmployeeTable = true
                    viewPendingRequestsForOwnEmployeesDiv.removeChild(viewPendingRequestsForOwnEmployeesDiv.getElementsByTagName('table')[0])
                }
                let table = document.createElement('table')

                let thead = document.createElement('thead')
                let trHead = document.createElement('tr')
                let labelh1 = document.createElement('td')
                let labelh2 = document.createElement('td')
                let labelh3 = document.createElement('td')
                let labelh5 = document.createElement('td')
                let labelh6 = document.createElement('td')
                let labelh7 = document.createElement('td')
                labelh1.innerText = 'Request Id#'
                labelh2.innerText = 'Requested Event'
                labelh3.innerText = 'Event Date'
                labelh5.innerText = 'Requester Email'
                labelh6.innerText = 'Amount Requested in USD'
                labelh7.innerHTML = 'Resolved By'
                trHead.append(labelh1)
                trHead.append(labelh2)
                trHead.append(labelh5)
                trHead.append(labelh6)
                trHead.append(labelh3)
                trHead.append(labelh7)
                thead.append(trHead)
                table.append(thead)
                let tbody = document.createElement('tbody')

                for(r of requests){
                    let trBody = document.createElement('tr')

                    let labelb1 = document.createElement('td')
                    let labelb2 = document.createElement('td')
                    let labelb3 = document.createElement('td')
                    let labelb5 = document.createElement('td')
                    let labelb6 = document.createElement('td')
                    let labelb7 = document.createElement('td')
                    
                    labelb1.innerHTML = r["request"]["requestId"]
                    labelb5.innerHTML = r["request"]["requestedEvent"]
                    labelb6.innerHTML = r["request"]["requester"]["email"]
                    labelb2.innerHTML = r["request"]["amount"]
                    labelb3.innerHTML = new Date(r["request"]["eventDate"]).toDateString()
                    labelb7.innerHTML = r["hierarchy"]["supervisorUser"]["email"]
                    
                    trBody.append(labelb1)
                    trBody.append(labelb5)
                    trBody.append(labelb6)
                    trBody.append(labelb2)
                    trBody.append(labelb3)
                    trBody.append(labelb7)
                    tbody.append(trBody)
                    
                    table.append(tbody)
                    viewAllResolvedRequestDiv.append(table)
                }
            } else if(hasNoResolvedRequestTable && requests.length === 0){
                hasNoResolvedRequestTable = false
                let table = document.createElement('table')
                let message = 'No Requests Found'
                table.append(message)
                viewAllResolvedRequestDiv.append(table)
                if(hasNoResolvedMgrTable === false){
                    hasNoResolvedMgrTable = true
                    resolvedRequestMgrDiv.removeChild(resolvedRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoPendingMgrTable === false){
                    hasNoPendingMgrTable = true
                    pendingRequestMgrDiv.removeChild(pendingRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoUserTable === false){
                    hasNoUserTable = true
                    viewUserDiv.removeChild(viewUserDiv.getElementsByTagName('table')[0])

                }
                if(hasNoEmployeeManagerTable === false){
                    hasNoEmployeeManagerTable = true
                    viewEmployeeManagerDiv.removeChild(viewEmployeeManagerDiv.getElementsByTagName('table')[0])
                }
                if(hasNoPendingRequestsForOwnEmployeeTable === false){
                    hasNoPendingRequestsForOwnEmployeeTable = true
                    viewPendingRequestsForOwnEmployeesDiv.removeChild(viewPendingRequestsForOwnEmployeesDiv.getElementsByTagName('table')[0])
                }
            }
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/manager/view-employee-resolved-approvals')
    xhr.send()
}
let viewAllResolvedRequestsButton = document.getElementById('viewAllResolvedRequests')
viewAllResolvedRequestsButton.addEventListener('click', viewAllResolvedRequest)

let hasNoEmployeeManagerTable = true
let viewEmployeeManagerDiv = document.getElementById('viewEmployeeManagerDiv')
function viewemployeemanager(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.readyState === 4 && xhr.status === 200) {
            let requests = JSON.parse(xhr.response)
            
            if(hasNoEmployeeManagerTable & requests.length > 0){
                hasNoEmployeeManagerTable = false
                if(hasNoResolvedMgrTable === false){
                    hasNoResolvedMgrTable = true
                    resolvedRequestMgrDiv.removeChild(resolvedRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoPendingMgrTable === false){
                    hasNoPendingMgrTable = true
                    pendingRequestMgrDiv.removeChild(pendingRequestMgrDiv.getElementsByTagName('table')[0])

                }
                if(hasNoUserTable === false){
                    hasNoUserTable = true
                    viewUserDiv.removeChild(viewUserDiv.getElementsByTagName('table')[0])

                }
                if(hasNoPendingRequestsForOwnEmployeeTable === false){
                    hasNoPendingRequestsForOwnEmployeeTable = true
                    viewPendingRequestsForOwnEmployeesDiv.removeChild(viewPendingRequestsForOwnEmployeesDiv.getElementsByTagName('table')[0])
                }
                if(hasNoResolvedRequestTable === false){
                    hasNoResolvedRequestTable = true
                    viewAllResolvedRequestDiv.removeChild(viewAllResolvedRequestDiv.getElementsByTagName('table')[0])
                }
                let employeemanagertable = document.createElement('table')
                let thead = document.createElement('thead')
                let trHead = document.createElement('tr')
                let labelh1 = document.createElement('td')
                let labelh2 = document.createElement('td')
                labelh1.innerText = 'Employee'
                labelh2.innerText = 'Managed By'
                trHead.append(labelh1)
                trHead.append(labelh2)

                thead.append(trHead)
                let tbody = document.createElement('tbody')
                for(r of requests){
                    
                let trBody = document.createElement('tr')
                let labelb1 = document.createElement('td')
                let labelb2 = document.createElement('td')


                labelb1.innerHTML = r["employeeUser"]["username"]
                labelb2.innerHTML = r["supervisorUser"]["username"]

                trBody.append(labelb1)
                trBody.append(labelb2)
                tbody.append(trBody)
                
                
                }
                employeemanagertable.append(thead)
                employeemanagertable.append(tbody)
                viewEmployeeManagerDiv.append(employeemanagertable)
                
        
            }
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/manager/view-employees-and-managers')
    xhr.send()
}

let viewAllEmployeesManagerButton = document.getElementById('viewAllEmployeesManagers')
viewAllEmployeesManagerButton.addEventListener('click', viewemployeemanager)

let viewAllRequestsForYourEmployeeButton = document.getElementById('viewAllRequestsForYourEmployee')
function goToAllRequestsForYourEmployee(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.readyState === 4 && xhr.status === 200) {
            window.open('http://localhost:8080/ReimbursementManagement/supervisorrequestsforemployee.html', '_self')
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/supervisorrequestsforemployee.html')
    xhr.send()
}
viewAllRequestsForYourEmployeeButton.addEventListener('click', goToAllRequestsForYourEmployee)