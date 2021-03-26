let pendingRequestEmpDiv = document.getElementById('pendingRequestEmpDiv')
let hasNoPendingEmpTable = true
let pendingRequestEmpTable = document.getElementById('pendingRequestEmpTable')
// let pendingRequestEmpTable = document.createElement('table')
window.onload = function viewPendingEmpRequest(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.readyState === 4 && xhr.status === 200) {
            let requests = JSON.parse(xhr.response)
            
            if(hasNoPendingEmpTable){
                hasNoPendingEmpTable = false
                // if(tbody.hasChildNodes()){
                //     pendingRequestEmpTable.removeChild(pendingRequestEmpTable.getElementsByTagName('tbody')[0]) 
                // }
                // let thead = document.createElement('thead')
                // let trHead = document.createElement('tr')
                // let labelh1 = document.createElement('td')
                // let labelh2 = document.createElement('td')
                // let labelh3 = document.createElement('td')
                // let labelh5 = document.createElement('td')
                // let labelh6 = document.createElement('td')
                // let labelh7 = document.createElement('td')
                // labelh1.innerText = 'Request Id#'
                // labelh2.innerText = 'Amount Requested'
                // labelh3.innerText = 'Event Date'
                // labelh5.innerText = 'Requested Event'
                // labelh6.innerText = 'Requester Email'
                // labelh7.innerText = 'Status'
                // trHead.append(labelh1)
                // trHead.append(labelh2)
                // trHead.append(labelh3)
                // trHead.append(labelh5)
                // trHead.append(labelh6)
                // trHead.append(labelh7)
                // thead.append(trHead)
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
                    // let eved = eventDate.getFullYear() + "-" + eventDate.getMonth() + "-" + eventDate.getDay()
                    
                    trBody.append(labelb1)
                    trBody.append(labelb2)
                    trBody.append(eventDate)
                    trBody.append(labelb5)
                    trBody.append(labelb6)
                    trBody.append(labelb7)
                    tbody.append(trBody)
                    
                    pendingRequestEmpTable.append(tbody)
                    pendingRequestEmpDiv.append(pendingRequestEmpTable)                }
            } 
        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/employee/view-pending-requests')
    xhr.send()
}

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
let homepageButton = document.getElementById('homepage')
homepageButton.addEventListener('click', goToHomepage)

let logoutButton = document.getElementById('logoutButton')
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
