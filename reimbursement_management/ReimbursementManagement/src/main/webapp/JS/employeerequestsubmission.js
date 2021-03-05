let submitRequestButton = document.getElementById('submitRequest')
function submitRequest(){
    let eventTitle = document.getElementById('eventTitle').value
    let eventDay = document.getElementById('eventDay').value
    let eventMonth = document.getElementById('eventMonth').value
    let eventYear = document.getElementById('eventYear').value
    let streetNumber = document.getElementById('streetNumber').value
    let streetName = document.getElementById('streetName').value
    let city = document.getElementById('city').value
    let state = document.getElementById('state').value
    let zipCode = document.getElementById('zipCode').value
    let amount = document.getElementById('amount').value
    let url = 'http://localhost:8080/ReimbursementManagement/employee/submit-request?eventTitle=' + eventTitle + '&eventDay=' + eventDay + '&eventMonth=' + eventMonth + '&eventYear=' + eventYear + '&streetNumber=' + streetNumber + '&streetName=' + streetName + '&city=' + city + '&state=' + state + '&zipCode=' + zipCode + '&amount=' + amount
    let xhr = new XMLHttpRequest()
    xhr.open('POST', url)
    xhr.send()
}
submitRequestButton.addEventListener('click', submitRequest)

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