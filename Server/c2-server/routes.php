<?php

require_once __DIR__ . '/vendor/autoload.php';

use Server\Controller\Authentication;
use Server\Library\Env;
use Server\Library\Router;
use Server\Controller\ControlPanel;

Env::load(__DIR__ . '/.env');

/** @var Router $route */
$route = new Router();

/** @var ControlPanel $controlPanel */
$controlPanel = new ControlPanel();

/** @var Authentication $authentication */
$authentication = new Authentication();