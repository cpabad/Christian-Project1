var xhr = new XMLHttpRequest();
xhr.withCredentials = true;

xhr.addEventListener("readystatechange", function() {
  if(this.readyState === 4) {
    console.log(this.responseText);
  }
});

xhr.open("POST", "/ReimbursementManagement/login?username=employee1&password=employeePassword");
xhr.setRequestHeader("Cookie", "JSESSIONID=[SCRUBBED-SESSION-ID]");

xhr.send();