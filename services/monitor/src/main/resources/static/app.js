let stompClient = null;
const eventsPerTransaction = {};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
}

function newEventReceived(event) {
    //console.log(event);

    const jsTransactionId = event.transactionId;

    if (!eventsPerTransaction[jsTransactionId]) {
        eventsPerTransaction[jsTransactionId] = [];
        $("#events").prepend('<tr><td><a href="/bpmn.html?traceId=' + event.transactionId + '" target="blank_">' + event.transactionId + '</a></td><td id="' + event.transactionId + '"></td></tr>');
    }

    eventsPerTransaction[jsTransactionId].push(event);

    let color = 'info';
    if (event.type === 'Command') {
        color = 'warning'//'danger'; // or 'warning'?
    }
    const randomId = Math.floor((1 + Math.random()) * 0x10000);
    const prettyJson = JSON.stringify(JSON.parse(event.sourceJson), null, 2);
    const html =
        '<div class="alert alert-' + color + '">' + event.type + ': ' + event.name + ' (from ' + event.sender + ') '
        + '<a label="Подробнее" data-toggle="collapse" data-target="#' + randomId + '" class="btn btn-default table-row-btn"><span class="glyphicon glyphicon-eye-open"></span></a>'
        + '<div class="collapse" id="' + randomId + '"><pre>' + prettyJson + '</pre></div>'
        + '</div>';
    $('td[id^="' + jsTransactionId + '"]').append(html);
}

function connect() {
    const socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/events', function (event) {
            newEventReceived(JSON.parse(event.body));
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendName();
    });
});