var stompClient = null;
var flag = true;
var secret = "";


function connect(username, password) {
    var socket = new SockJS('/hello');
    stompClient = Stomp.over(socket);
    stompClient.connect({ username: username, passcode: password}, function() {
        console.log('Web Socket is connected');
        stompClient.subscribe('/users/queue/chat', function (message) {
            $("#secret").text("Новый чат! Сохраните UUID, чтобы подключаться к нему в дальнейшем: " + message.body);
            secret = message.body;
            flag = true
        });
    });
}

async function subscribe_uuid() {
    await sleep(2000);
    if (flag) {
        stompClient.subscribe('/queue/' + secret, function (message) {
            console.log(message.body);
            console.log(message);
            console.log(message.body["message"]);
            $("#messages").append("<tr><td>" + message.body + "</td></tr>");
        });
        flag = false
    }
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
function disconnect(){
    stompClient.disconnect();
    console.log('Web Socket is disconnected');
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#open").prop("disabled", !connected);
    $("#sendMessage").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#messages").html("");
}


$(function() {
    setConnected(false);
    $("#close").prop("disabled", true);
    $("#close").hide();
    $("#open").prop("disabled", true);
    $("#history").prop("disabled", true);
    $("#sendMessage").prop("disabled", true);
    $("#start").prop("disabled", true);
    $("form").on('submit', function(e) {
        e.preventDefault();
    });
    $("#connect").click(function() {
        if ($("#username").val().length === 0 || $("#password").val().length === 0) {
            $("#warn").text("Поля обязательны для заполнения");
            return
        }
        $("#warn").text("");
        setConnected(true);
        connect($("#username").val(), $("#password").val());
        $("#start").prop("disabled", false);
    });
    $("#disconnect").click(function() {
        setConnected(false);
        disconnect();
        flag = true;
        $("#secret").text("Ожидаем...");
        $("#sendMessage").prop("disabled", true);
        $("#start").prop("disabled", true);
    });
    $("#start").click(function() {
        $("#close").prop("disabled", true);
        $("#open").prop("disabled", true);
        $("#start").prop("disabled", true);
        stompClient.send("/app/newChat", {}, {});
        subscribe_uuid();
        $("#sendMessage").prop("disabled", false);

    });
    $("#open").click(function() {
        stompClient.send("/app/openChat", {}, $("#secretOpen").val());
        secret = $("#secretOpen").val();
        console.log("!!!!!!!!!");
        subscribe_uuid();
        $("#close").prop("disabled", false);
        $("#open").prop("disabled", true);
        $("#history").prop("disabled", false);
        $("#start").prop("disabled", true);
        $("#secret").text("Чат " + secret);
        $("#sendMessage").prop("disabled", false);

    });

    $("#sendMessage").click(function () {
        stompClient.send("/app/sendMessage", {secret: secret}, $("#inputmessage").val());
        $("#inputmessage").prop("value", "");
        let objDiv = document.getElementById("scroll");
        objDiv.scrollTop = objDiv.scrollHeight + 20;
    });

    $("#history").click(function () {
        $("#messages").html("");
        stompClient.send("/app/loadMessages", {secret: secret}, {});
        stompClient.subscribe('/users/queue/' + secret, function (message) {
            console.log(message.body);
            $("#messages").append("<tr><td>" + message.body + "</td></tr>");
        });
        $("#history").prop("disabled", true);
    });
});