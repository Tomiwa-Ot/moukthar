<?php require_once __DIR__ . "/../header.php"; ?>

<div class="container">
    <div class="row">
        <div class="col-md-12 page-header d-flex justify-content-between align-items-center">
            <h2 class="page-title"><?= $device ?> Keylogs</h2>
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
                                <?php if (count($keylogs) > 0): ?>
                                    <?php foreach ($keylogs as $keylog): ?>
                                        <tr>
                                            <td><?= $keylog->getText(); ?></td>
                                            <td class="text-end"><?= date("d/m/Y H:i:s", intval($keylog->getTimestamp()) / 1000); ?></td>
                                        </tr>
                                    <?php endforeach ?>
                                <?php else: ?>
                                    <tr class="text-center">
                                        No data
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