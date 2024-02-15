<?php require_once __DIR__ . "/../header.php"; ?>

<div class="container">
    <div class="row">
        <?php if (isset($_GET['alert'])): ?>
            <?php if ($_GET['alert'] == 1): ?>
                <div class="alert alert-success" id="error-alert" role="alert">
                    <strong> Screenshot request sent </strong>
                </div>
            <?php else: ?>
                <div class="alert alert-danger" id="error-alert" role="alert">
                    <strong> Screenshot request failed </strong>
                </div>
            <?php endif ?>
        <?php endif ?>
        <div class="col-md-12 page-header d-flex justify-content-between align-items-center">
            <h2 class="page-title"><?= $device ?> Screenshots</h2>
            <form action="/send" method="post">
                <input type="hidden" name="web_socket_id" value="<?= $webSocketID ?>">
                <input type="hidden" name="cmd" value="SCREENSHOT">
                <input type="hidden" name="type" value="server">
                <input type="hidden" name="referrer" value="screenshots">
                <input type="hidden" name="client" value="<?= $_GET['client'] ?>">
                <button class="btn btn-dark" type="submit"><i class="fas fa-plus"></i></button>
            </form>
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
                                    <th></th>
                                    <th class="text-end">Timestamp</th>
                                </tr>
                            </thead>
                            <tbody class="files-class">
                                <?php if (count($screenshots) > 0): ?>
                                    <?php foreach ($screenshots as $screenshot): ?>
                                        <tr>
                                            <td><a href="/screenshots?client=<?= $screenshot->getClientID(); ?>&id=<?= $screenshot->getID(); ?>"><?= $screenshot->getFilename(); ?></a></td>
                                            <td class="text-end"><?= $screenshot->getTimestamp(); ?></td>
                                        </tr>
                                    <?php endforeach ?>
                                <?php else: ?>
                                    <tr class="text-center">
                                        No screenshots
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

<?php require_once __DIR__ . "/../footer.php"; ?>