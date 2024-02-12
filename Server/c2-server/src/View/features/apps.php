<?php require_once __DIR__ . "/../header.php"; ?>

<div class="container">
    <div class="row">
        <div class="col-md-12 page-header d-flex justify-content-between align-items-center">
            <h2 class="page-title"><?= $identifier ?> Installed Apps</h2>
            <button class="btn btn-dark" data-toggle="modal" data-target="#appsModal"><i class="fas fa-plus"></i></button>
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
                                    <th>Application Name</th>
                                    <th>Package Name</th>
                                </tr>
                            </thead>
                            <tbody class="files-class">
                                <?php if (count($installedApps) > 0): ?>
                                    <?php foreach ($installedApps as $app): ?>
                                        <tr>
                                            <td><?= $app->getAppName(); ?></td>
                                            <td><?= $app->getPackageName(); ?></td>
                                        </tr>
                                    <?php endforeach ?>
                                <?php else: ?>
                                    <tr class="text-center">
                                        No apps
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

<div class="modal fade" id="appsModal" tabindex="-1" role="dialog" aria-labelledby="appsModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="apps-modal-title" id="appsModalLabel">Installed Apps</h5>
          
        </div>
        <div class="modal-body">
          
        </div>
      </div>
    </div>
</div>

<?php require_once __DIR__ . "/../footer.php"; ?>