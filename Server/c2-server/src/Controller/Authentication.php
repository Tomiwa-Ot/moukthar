<?php

namespace Server\Controller;

use function Server\Library\render;

class Authentication extends Base
{
    public function __construct()
    {
        parent::__construct();
    }

    public function login(): void
    {
        if ($this->isLoggedIn()) {
            header("Location: /");
            return;
        }   

        if (!isset($_POST['username'])) {
            header("/login");
            return;
        }

        $query = "SELECT * FROM USER WHERE username=?";
        $rows = $this->database->select($query, [$_POST['username']]);

        if (count($rows) > 0) {
            if (password_verify($_POST['password'], $rows[0]['password'])) {
                $_SESSION['admin'] = $rows[0]['id'];
                header("Location: /");
            }
        }

    }

    public function loginView(): void
    {
        render("login.php", []);
    }

    public function resetPassword(): void
    {
        $password = password_hash($_POST['password'], PASSWORD_DEFAULT);
        $query = "UPDATE USER SET password=? WHERE id=?";
        $this->database->update();
    }

    public function resetPasswordView(): void
    {
        render("reset-password.php", []);
    }

    public function logout(): void
    {
        if ($this->isLoggedIn()) {
            $this->initializeSession();
            unset($_SESSION['admin']);

            header("/login");
            return;
        }
    }
}