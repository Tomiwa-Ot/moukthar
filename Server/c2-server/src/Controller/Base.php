<?php

namespace Server\Controller;

use Server\Library;
use Server\Library\render;
use Server\Library\Database;

/**
 * Controller base class
 */
class Base
{
    /** @var Database $database Database instance */
    protected Database $database;

    public function __construct()
    {
        $this->database = new Database();
    }

     /**
     * Check if admin is logged in
     * 
     * @return bool
     */
    protected function isLoggedIn(): bool
    {
        $this->initializeSession();
        return isset($_SESSION['admin']);
    }


    /**
     * Initialize session
     */
    protected function initializeSession(): void
    {  
        ob_start();
        session_start();
    }
}