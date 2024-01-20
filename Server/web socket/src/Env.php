<?php

namespace C2;

/**
 * Load variables in .env file into system environment variables
 */
class Env
{
    /**
     * Load .env variables
     * 
     * @param string $path
     */
    public static function load(string $path): void
    {
        if (!file_exists($path)) {
            throw new \Exception(sprintf(".%s does not exist.", $path));
        }

        if (!is_readable($path)) {
            throw new \Exception(sprintf("%s file is not readable.", $path));
        }

        $lines = file($path, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
        foreach ($lines as $line) {
            if (strpos(trim($line), '#') === 0)
                continue;

            list($name, $value) = explode('=', $line, 2);
            $name = str_replace('\'', '', trim($name));
            $value = str_replace('\'', '', trim($value));

            if (!array_key_exists($name, $_ENV)) {
                $_ENV[$name] = $value;
            }
        }
    }
}