// Function to change webpage background color
function changeBodyBg(color){
    if (color === 'red'){
        document.getElementById("msg").innerText = "I see you have bad taste :p";
    } else {
        document.body.style.background = color;
        document.getElementById("msg").innerText = "";
    }
}