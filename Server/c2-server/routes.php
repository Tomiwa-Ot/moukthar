<?php

require_once __DIR__ . '/vendor/autoload.php';

use Server\Library\Env;
use Server\Library\Router;
use Server\Controller\ControlPanel;
use Server\Library\WebSocketClient;

Env::load(__DIR__ . '/.env');

/** @var Router $route */
$route = new Router();

/** @var WebSocketClient $socketClient */
$socketClient = new WebSocketClient();

/** @var ControlPanel $controlPanel */
$controlPanel = new ControlPanel($socketClient);