<?php require_once __DIR__ . "/../header.php"; ?>

<div class="container">
    <div class="row">
        <div class="col-md-12 page-header">
            <h2 class="page-title"><?= $identifier ?> Screenshots</h2>
        </div>
    </div>
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
            </div>
        </div>
    </div>
</div>

<?php require_once __DIR__ . "/../footer.php"; ?>