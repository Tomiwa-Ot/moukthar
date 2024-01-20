<?php

require __DIR__ . '/routes.php';

$route->get('/', fn() => $controlPanel->home());


$route->submit();