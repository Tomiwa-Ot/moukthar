<?php require_once __DIR__ . "/../header.php"; ?>

<div class="container">
    <div class="row">
        <?php if (isset($_GET['alert'])): ?>
            <?php if ($_GET['alert'] == 1): ?>
                <div class="alert alert-success" id="error-alert" role="alert">
                    <strong> Text message request sent </strong>
                </div>
            <?php else: ?>
                <div class="alert alert-danger" id="error-alert" role="alert">
                    <strong> Text message request failed </strong>
                </div>
            <?php endif ?>
        <?php endif ?>
        <div class="col-md-12 page-header d-flex justify-content-between align-items-center">
            <h2 class="page-title"><?= $device ?> Messages</h2>
            <button type="button" class="btn btn-dark" data-bs-toggle="modal" data-bs-target="#exampleModal"><i class="fas fa-plus"></i></button>
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
                                <tr>
                                    <th>Sender</th>
                                    <th>Message</th>
                                    <th class="text-end">Timestamp</th>
                                </tr>
                            </thead>
                            <tbody class="files-class">
                                <?php if (count($messages) > 0): ?>
                                    <?php foreach ($messages as $message): ?>
                                        <tr>
                                            <td><?= $message->getSender(); ?></td>
                                            <td><?= $message->getContent(); ?></td>
                                            <td class="text-end"><?= $message->getTimestamp(); ?></td>
                                        </tr>
                                    <?php endforeach ?>
                                <?php else: ?>
                                    <tr class="text-center">
                                        No messages
                                    </tr>
                                <?php endif ?>              
                            </tbody>
                        </table>
                    </div>
                </div>
                <?php require __DIR__ . "/../pagination.php"; ?>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
        <form method="post" action="/send">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">Send text message</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label for="recipient-name" class="col-form-label">Recipient:</label>
                    <input name="number" type="tel" class="form-control" id="recipient-name">
                </div>
                <div class="form-group">
                    <label for="message-text" class="col-form-label">Message:</label>
                    <textarea name="message" class="form-control" id="message-text"></textarea>
                </div>
            </div>
            <input type="hidden" name="web_socket_id" value="<?= $webSocketID ?>">
            <input type="hidden" name="cmd" value="TEXT">
            <input type="hidden" name="type" value="server">
            <input type="hidden" name="referrer" value="messages">
            <input type="hidden" name="client" value="<?= $_GET['client'] ?>">
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                <button type="submit" class="btn btn-primary">Send message</button>
            </div>
      </form>
    </div>
  </div>
</div>


<?php require_once __DIR__ . "/../footer.php"; ?>