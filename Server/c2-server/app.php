<?php

require __DIR__ . '/routes.php';

$route->get('/', fn() => $controlPanel->home());

$route->get('/login', fn() => $authentication->loginView());
$route->post('/login', fn() => $authentication->login());

$route->submit();