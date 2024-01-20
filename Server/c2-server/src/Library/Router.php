<?php

namespace Server\Library;

require_once __DIR__ . '/Response.php';

class Router
{
    /** @var array $uriList Valid URI(s) */
    private array $uriList = array();

    /** @var array $uriCallback Callbacks for URI(s) */
    private array $uriCallback = array();

    public function __call($name, $arguments): void
    {
        $this->uriList[strtoupper($name)][] = $arguments[0];
        $this->uriCallback[strtoupper($name)][$arguments[0]] = $arguments[1];
    }

    /**
     * Parse requested route and trigger registered callback
     */
    public function submit(): void
    {
        $requestURI = explode('?', $_SERVER['REQUEST_URI'])[0];
        $foundURIMatch = false;

        foreach ($this->uriList[$_SERVER['REQUEST_METHOD']] as $uri) {
            if ($uri === $requestURI) {
                $foundURIMatch = true;
                break;
            }
        }

        if ($foundURIMatch) {
            call_user_func($this->uriCallback[strtoupper($_SERVER['REQUEST_METHOD'])][$requestURI]);
        } else {
            http_response_code(404);
            render('error.php', ['title' => 'Page not found', 'code' => 404, 'message' => 'Page not found']);
        }
    }
}