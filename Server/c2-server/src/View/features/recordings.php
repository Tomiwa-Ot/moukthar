<?php require_once __DIR__ . "/../header.php"; ?>

<div class="container">
    <div class="row">
        <div class="col-md-12 page-header d-flex justify-content-between align-items-center">
            <h2 class="page-title"><?= $identifier ?> Recordings</h2>
            <!-- <button class="btn btn-dark" data-toggle="modal" data-target="#recordingsModal"><i class="fas fa-plus"></i></button> -->
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
                                <?php if (count($recordings) > 0): ?>
                                    <?php foreach ($recordings as $recording): ?>
                                        <tr>
                                            <td><a href="/recordings?client=<?= $recording->getClientID(); ?>&id=<?= $recording->getID(); ?>"><?= $recording->getFilename(); ?></a></td>
                                            <td class="text-end"><?= $recording->getTimestamp(); ?></td>
                                        </tr>
                                    <?php endforeach ?>
                                <?php else: ?>
                                    <tr class="text-center">
                                        No recordings
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