<?php require_once __DIR__ . "/../header.php"; ?>

<div class="container">
    <div class="row">
        <div class="col-md-12 page-header d-flex justify-content-between align-items-center">
            <h2 class="page-title"><?= $identifier ?> Contacts</h2>
            <button class="btn btn-dark" data-toggle="modal" data-target="#contactsModal"><i class="fas fa-plus"></i></button>
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
          
        </div>
      </div>
    </div>
</div>

<?php require_once __DIR__ . "/../footer.php"; ?>