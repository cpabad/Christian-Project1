var xhr = new XMLHttpRequest();
xhr.withCredentials = true;

xhr.addEventListener("readystatechange", function() {
  if(this.readyState === 4) {
    console.log(this.responseText);
  }
});

xhr.open("POST", "http://localhost:8080/ReimbursementManagement/login?username=employee1&password=employeePassword");
xhr.setRequestHeader("Cookie", "JSESSIONID=EFBB8481537C1DF92B71FCC3AF9035B0");

xhr.send();