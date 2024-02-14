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

        if (!isset($_POST['username']) && !isset($_POST['password'])) {
            header("Location: /login");
            return;
        }

        $query = "SELECT * FROM USER WHERE username=?";
        $rows = $this->database->select($query, [$_POST['username']]);

        if (count($rows) > 0) {
            if (password_verify($_POST['password'], $rows[0]['password'])) {
                $_SESSION['username'] = $rows[0]['username'];
                $_SESSION['id'] = $rows[0]['id'];
                header("Location: /");

                return;
            }
        }

        render("login.php", ["error" => "Username/password incorrect"]);

    }

    /**
     * Login view
     */
    public function loginView(): void
    {
        render("login.php", []);
    }

    /**
     * Reset user password
     */
    public function resetPassword(): void
    {
        if (!$this->isLoggedIn()) {
            header("Location: /login");
            return;
        }

        $password = password_hash($_POST['password'], PASSWORD_DEFAULT);
        $query = "UPDATE USER SET password=? WHERE id=?";
        $this->database->update($query, [$password, $_SESSION['id']]);

        header("Location: /");
    }

    /**
     * Reset password view
     */
    public function resetPasswordView(): void
    {
        if (!$this->isLoggedIn()) {
            header("Location: /login");
            return;
        }

        render("reset-password.php", []);
    }

    /**
     * Sign user out
     */
    public function logout(): void
    {
        if ($this->isLoggedIn()) {
            $this->initializeSession();
            unset($_SESSION['id']);
            unset($_SESSION['username']);
        }

        header("Location: /login");
    }
}