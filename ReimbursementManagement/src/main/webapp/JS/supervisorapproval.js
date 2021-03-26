let requestTable = document.getElementById('requestTable')
let hasNoRequests = true
window.onload = function viewPendingRequest(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.readyState === 4 && xhr.status === 200) {
            let requests = JSON.parse(xhr.response)

            if(hasNoRequests & requests.length > 0){
                let tbody = document.createElement('tbody')

                for(r of requests){
                    let trBody = document.createElement('tr')

                    let labelb1 = document.createElement('td')
                    let labelb2 = document.createElement('td')
                    let labelb5 = document.createElement('td')
                    let labelb6 = document.createElement('td')
                    let labelb7 = document.createElement('td')
                    let approvebtn = document.createElement('button')
                    let denybtn = document.createElement('button')

                    let btnurl = 'http://localhost:8080/ReimbursementManagement/app/manager/update-approval?requestId=' + r["request"]["requestId"]
                    
                    approvebtn.innerText = "Approve"
                    approvebtn.addEventListener('click', function(event){
                        let xhrApprove = new XMLHttpRequest()
                        xhrApprove.onreadystatechange = function(){
                            if(xhrApprove.readyState === 4 && xhrApprove.status === 200){
                                window.open('http://localhost:8080/ReimbursementManagement/supervisorapproval.html', '_self')
                            } else if(xhrApprove.readyState === 4 && xhrApprove.status === 400){
                                window.alert(xhrApprove.response)
                            }
                        }
                        xhrApprove.open('POST', btnurl + '&decision=true')
                        xhrApprove.send()
                    })
                    
                    denybtn.innerText = "Deny"
                    denybtn.addEventListener('click', function(event){
                        let xhrDeny = new XMLHttpRequest()
                        xhrDeny.onreadystatechange = function(){
                            if(xhrDeny.readyState === 4 && xhrDeny.status === 200){
                                window.open('http://localhost:8080/ReimbursementManagement/supervisorapproval.html', '_self')
                            } else if(xhrDeny.readyState === 4 && xhrDeny.status === 400){
                                window.alert(xhrDeny.response)
                            }
                        }
                        xhrDeny.open('POST', btnurl + '&decision=false')
                        xhrDeny.send()
                    })
                    
                    labelb1.innerHTML = r["request"]["requestId"]
                    labelb5.innerHTML = r["request"]["requestedEvent"]
                    labelb6.innerHTML = r["request"]["requester"]["email"]
                    labelb2.innerHTML = r["request"]["amount"]
                    labelb7.append(approvebtn)
                    labelb7.append(denybtn)
                    
                    let date = new Date(r["request"]["eventDate"])
                    let eventDate = date.toDateString()
                    
                    trBody.append(labelb1)
                    trBody.append(labelb2)
                    trBody.append(eventDate)
                    trBody.append(labelb5)
                    trBody.append(labelb6)
                    trBody.append(labelb7)
                    tbody.append(trBody)
                    
                    requestTable.append(tbody)
                }

            }
        }
        
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/manager/view-employee-pending-approvals')
    xhr.send()
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