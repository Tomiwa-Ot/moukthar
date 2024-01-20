<?php

require_once __DIR__ . '/vendor/autoload.php';

use Server\Library\Env;
use Server\Library\Router;


Env::load(__DIR__ . '/.env');

/** @var object $route */
$route = new Router();