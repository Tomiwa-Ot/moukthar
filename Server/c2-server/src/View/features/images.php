<?php require_once __DIR__ . "/../header.php"; ?>

<div class="container">
    <div class="row">
        <div class="col-md-12 page-header d-flex justify-content-between align-items-center">
            <h2 class="page-title"><?= $identifier ?> Images</h2>
            <button class="btn btn-dark" data-toggle="modal" data-target="#imagesModal"><i class="fas fa-plus"></i></button>
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
                                <?php if (count($images) > 0): ?>
                                    <?php foreach ($images as $image): ?>
                                        <tr>
                                            <td><a href="/images?client=<?= $image->getClientID(); ?>&id=<?= $image->getID(); ?>"><?= $image->getFilename(); ?></a></td>
                                            <td class="text-end"><?= $image->getTimestamp(); ?></td>
                                        </tr>
                                    <?php endforeach ?>
                                <?php else: ?>
                                    <tr class="text-center">
                                        No images
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

<div class="modal fade" id="imagesModal" tabindex="-1" role="dialog" aria-labelledby="imagesModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="images-modal-title" id="imagesModalLabel">Images</h5>
          
        </div>
        <div class="modal-body">
          
        </div>
      </div>
    </div>
</div>

<?php require_once __DIR__ . "/../footer.php"; ?>