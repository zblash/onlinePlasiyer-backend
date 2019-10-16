var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    stompClient = Stomp.client("ws://localhost:8080/ws");
    var headers = {
        'Authorization': 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZXJjaGFudCIsInJvbGUiOiJST0xFX01FUkNIQU5UIiwidXNlcklkIjo0OSwiZXhwIjoxNTcxMjc3NTMyfQ.H-6KYy3C1MTfVSf2LnU3yQo5A69rtZo7mDGHse6L5k5zxgNnlzFyK7Z6j71WqCn4aai98CSutMN_YA_V74Tm5A'
    };
    stompClient.connect({Authorization: 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZXJjaGFudCIsInJvbGUiOiJST0xFX01FUkNIQU5UIiwidXNlcklkIjo0OSwiZXhwIjoxNTcxMjc3NTMyfQ.H-6KYy3C1MTfVSf2LnU3yQo5A69rtZo7mDGHse6L5k5zxgNnlzFyK7Z6j71WqCn4aai98CSutMN_YA_V74Tm5A'}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/channel/'+$("#name").val(), function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

