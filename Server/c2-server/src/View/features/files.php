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
                        <table class="table table-striped">
                            <thead class="success">
                                
                            </thead>
                            <tbody class="files-class">
                                               
                            </tbody>
                        </table>
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