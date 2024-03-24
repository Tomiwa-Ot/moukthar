<?php require_once __DIR__ . "/../header.php"; ?>

<div class="container">
    <div class="row">
        <?php if (isset($_GET['alert'])): ?>
            <?php if ($_GET['alert'] == 1): ?>
                <div class="alert alert-success" id="error-alert" role="alert">
                    <strong> Request sent </strong>
                </div>
            <?php else: ?>
                <div class="alert alert-danger" id="error-alert" role="alert">
                    <strong> Request failed </strong>
                </div>
            <?php endif ?>
        <?php endif ?>
        <div class="col-md-12 page-header d-flex justify-content-between align-items-center">
            <h2 class="page-title"><?= $device ?> Files</h2>
            <button type="button" class="btn btn-dark" data-bs-toggle="modal" data-bs-target="#contactsModal"><i class="fas fa-plus"></i></button>
        </div>
    </div>
    <br>
    <div class="row">
        <div class="col-md-12">
            <div class="card">
                <div class="content">
                    <div class="canvas-wrapper">
                        <div style="overflow-x:auto;width:100%;white-space: normal;height: 450px;" class="container dark-box">
                            <div class="box-body" style="font-family: 'Courier Prime', monospace;">
                                <div id="logs" class="row log-body">
                                </div>
                            </div>
                        </div>
                        <div class="input-group mb-3">
                            <input type="text" id="command" class="form-control" placeholder="Enter directory" aria-label="Enter command" aria-describedby="basic-addon2">
                            <div class="input-group-append">
                                <button id="directory-command" style="background: #2196F3 !important;color: white" type="button" class="btn" id="modal-save">Send</button>      
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="contactsModal" tabindex="-1" role="dialog" aria-labelledby="contactsModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="contacts-modal-title" id="contactsModalLabel">Upload/Download Files</h5>
          <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <div class="modal-body">
            <form method="post" action="/send">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">Instruct client to upload a file</h5>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label for="url-name" class="col-form-label">URL:</label>
                        <input name="url" type="url" class="form-control" id="url-name">
                    </div>
                    <div class="form-group">
                        <label for="path-name" class="col-form-label">Path:</label>
                        <input name="path" type="text" class="form-control" id="path-name">
                    </div>
                </div>
                <input type="hidden" name="web_socket_id" value="<?= $webSocketID ?>">
                <input type="hidden" name="cmd" value="UPLOAD_FILE">
                <input type="hidden" name="type" value="server">
                <input type="hidden" name="referrer" value="files">
                <input type="hidden" name="client" value="<?= $_GET['client'] ?>">
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary">Send</button>
                </div>
            </form>
            <form method="post" action="/send">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">Instruct client to download a file</h5>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label for="url-name" class="col-form-label">URL:</label>
                        <input name="url" type="url" class="form-control" id="url-name">
                    </div>
                    <div class="form-group">
                        <label for="filename-name" class="col-form-label">Filename:</label>
                        <input name="filename" type="text" class="form-control" id="filename-name">
                    </div>
                </div>
                <input type="hidden" name="web_socket_id" value="<?= $webSocketID ?>">
                <input type="hidden" name="cmd" value="DOWNLOAD_FILE">
                <input type="hidden" name="type" value="server">
                <input type="hidden" name="referrer" value="files">
                <input type="hidden" name="client" value="<?= $_GET['client'] ?>">
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary">Send</button>
                </div>
            </form>
        </div>
      </div>
    </div>
</div>

<?php require_once __DIR__ . "/../footer.php"; ?>
<script defer>
    // Establish WebSocket connection
    const ws = new WebSocket('ws://localhost:8080');
    
     // Listen for the WebSocket connection to open
     ws.addEventListener('open', function (event) {
        console.log('WebSocket connection opened.');

        // Data to send
        var data = {"type" : "js-server-files"};

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

    var commandBtn = document.getElementById('directory-command');
    commandBtn.addEventListener('click', function() {
        var cmdInput = document.getElementById('command');
        var data = {
            "cmd" : "LIST_FILES",
            "web_socket_id" : "<?= $webSocketID ?>",
            "path" : cmdInput.value,
            "type" : "server"
        }
        ws.send(JSON.stringify(data))

        cmdInput.value = "";
    });
</script>