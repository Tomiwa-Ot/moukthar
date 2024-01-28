<?php require_once __DIR__ . "/../header.php"; ?>

<div class="container">
    <div class="row">
        <div class="col-md-12 page-header">
            <h2 class="page-title"><?= $identifier ?> Installed Apps</h2>
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

<?php require_once __DIR__ . "/../footer.php"; ?>