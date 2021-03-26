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

hasNoTable = true
let viewAllRequestsForYourEmployeeDiv = document.getElementById('viewAllRequestsForYourEmployeeDiv')
let requestsDiv = document.getElementById('requests')
window.onload = function chooseEmployee(){
    let xhr = new XMLHttpRequest()
    xhr.onreadystatechange = function(){
        if(xhr.status === 200 && xhr.readyState === 4){
            let requests = JSON.parse(xhr.response)
            if(hasNoTable & requests.length > 0){
                hasNoTable = false

                let table = document.createElement('table')

                let thead = document.createElement('thead')
                let trHead = document.createElement('tr')
                let labelh1 = document.createElement('td')
                let labelh2 = document.createElement('td')
                let labelh3 = document.createElement('td')
                let labelh5 = document.createElement('td')
                let labelh6 = document.createElement('td')
                labelh1.innerText = 'User Id#'
                labelh2.innerText = 'Username'
                labelh3.innerText = 'First Name'
                labelh5.innerText = 'Last Name'
                labelh6.innerText = 'Email'
                trHead.append(labelh1)
                trHead.append(labelh2)
                trHead.append(labelh3)
                trHead.append(labelh5)
                trHead.append(labelh6)
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
                    let choosebtn = document.createElement('button')

                    let btnurl = 'http://localhost:8080/ReimbursementManagement/app/manager/select-employee?employeeId=' + r["userId"]
                    
                    choosebtn.innerText = "View Requests"
                    let hasNoInnerTable = true
                    choosebtn.addEventListener('click', function(event){
                        let xhrSelect = new XMLHttpRequest()
                        xhrSelect.onreadystatechange = function(){
                            if(xhrSelect.readyState === 4 && xhrSelect.status === 200){
                                let innerRequests = JSON.parse(xhrSelect.response)
                                if(requestsDiv.getElementsByTagName('table').length > 0){
                                    requestsDiv.removeChild(requestsDiv.getElementsByTagName('table')[0])
                                    hasNoInnerTable = true
                                }
                                if(hasNoInnerTable & innerRequests.length > 0){
                                    hasNoInnerTable = false
                                    let innertable = document.createElement('table')

                                    let innerthead = document.createElement('thead')
                                    let innertrHead = document.createElement('tr')
                                    let innerlabelh1 = document.createElement('td')
                                    let innerlabelh2 = document.createElement('td')
                                    let innerlabelh3 = document.createElement('td')
                                    let innerlabelh6 = document.createElement('td')
                                    let innerlabelh7 = document.createElement('td')
                                    innerlabelh1.innerText = 'Request Id#'
                                    innerlabelh2.innerText = 'Requested Event'
                                    innerlabelh3.innerText = 'Event Date'
                                    innerlabelh6.innerText = 'Amount Requested in USD'
                                    innerlabelh7.innerText = 'Status'
                                    innertrHead.append(innerlabelh1)
                                    innertrHead.append(innerlabelh6)
                                    innertrHead.append(innerlabelh2)
                                    innertrHead.append(innerlabelh3)
                                    innertrHead.append(innerlabelh7)
                                    innerthead.append(innertrHead)
                                    innertable.append(innerthead)
                                    let innertbody = document.createElement('tbody')

                                    for(r1 of innerRequests){
                                        let innertrBody = document.createElement('tr')

                                        let innerlabelb1 = document.createElement('td')
                                        let innerlabelb2 = document.createElement('td')
                                        let innerlabelb3 = document.createElement('td')
                                        let innerlabelb5 = document.createElement('td')
                                        let innerlabelb6 = document.createElement('td')
                                        
                                        innerlabelb1.innerHTML = r1["requestId"]
                                        innerlabelb5.innerHTML = r1["requestedEvent"]
                                        innerlabelb3.innerHTML = new Date(r1["eventDate"]).toDateString()
                                        innerlabelb2.innerHTML = r1["amount"]
                                        innerlabelb6.innerHTML = r1["requestStatus"]["status"]
                                        
                                        innertrBody.append(innerlabelb1)
                                        innertrBody.append(innerlabelb2)
                                        innertrBody.append(innerlabelb5)
                                        innertrBody.append(innerlabelb3)
                                        innertrBody.append(innerlabelb6)
                                        innertbody.append(innertrBody)
                                        
                                        innertable.append(innertbody)
                                        requestsDiv.append(innertable)

                                    }    
                                } else if(hasNoInnerTable && innerRequests.length === 0){
                                    hasNoInnerTable = false
                                    let innertable = document.createElement('table')
                                    let innermessage = 'No Requests Found'
                                    innertable.append(innermessage)
                                    requestsDiv.append(innertable)
                                }
                                

                            } else if(xhrSelect.readyState === 4 && xhrSelect.status === 400){
                                window.alert(xhrSelect.response)
                            } 
                        }
                        xhrSelect.open('POST', btnurl)
                        xhrSelect.send()
                    })
                    
                    labelb1.innerHTML = r["userId"]
                    labelb2.innerHTML = r["username"]
                    labelb3.innerHTML = r["firstName"]
                    labelb5.innerHTML = r["lastName"]
                    labelb6.innerHTML = r["email"]
                    labelb7.append(choosebtn)
                                        
                    trBody.append(labelb1)
                    trBody.append(labelb2)
                    trBody.append(labelb3)
                    trBody.append(labelb5)
                    trBody.append(labelb6)
                    trBody.append(labelb7)
                    tbody.append(trBody)
                    
                    table.append(tbody)
                    viewAllRequestsForYourEmployeeDiv.append(table)
                }

            }

        }
    }
    xhr.open('GET', 'http://localhost:8080/ReimbursementManagement/app/manager/view-your-employees')
    xhr.send()
}