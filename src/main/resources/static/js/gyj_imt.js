const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl))

function show_error(msg){
    /*
    */
    ep = document.getElementById("error_panel_p");
    ep.innerHTML = msg;(
    console.warnmsg);
    setTimeout(() => {
         ep.innerHTML = "";
         ep.style.visibility = 'hidden';
       }, 5000);
}

function async_call(url, domid, append = false){
    /*
    */
    fetch(url).then(function (response) {
        return response.text();
    }).then(function (html) {
        console.log(html)
        if(append)
            document.getElementById(domid).innerHTML += html;
        else
            document.getElementById(domid).innerHTML = html;
    }).catch(function (err) {
       show_error('Something went wrong while getting the login form.'+ err);
    });
}


function check_login(){
     /*
    */
    id = document.getElementById("uid").value;
    fetch("checklogin").then(function (html) {
        alert('response session ' + sessionStorage.getItem('uid'));
        if(sessionStorage.getItem('uid') == id){
            location.href = 'index.html';
        }
        else{
            show_error('Wrong credentials');
        }
    }).catch(function (err) {
       show_error('Error while checking credentials : '+ err);
    });
}

$(document).ready(function () {
    $('.table .delBtn').on('click', function (event) {
        event.preventDefault();
        
        // Récupérer l'ID de l'élément à supprimer
        var href = $(this).attr('href');
        console.log($(this).attr('title'));
        // Mettre à jour le message de suppression dans la modale
        $('#deleteModal #delRecord').text($(this).attr('title'));
        // Mettre à jour le lien de suppression dans la modale
        $('#deleteModal #delRef').attr('href', href);
        
        // Afficher la modale
        $('#deleteModal').modal();
    });
});


 
