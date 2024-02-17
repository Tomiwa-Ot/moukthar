<?php

namespace Server\Model;

/**
 * Victim's installed applications
 */
class InstalledApp
{
    /** @var int $id Installed app's ID */
    private int $id;

    /** @var int $clientID Victim's ID */
    private int $clientID;

    /** @var string $packageName Package name */
    private string $packageName;

    /** @var string $appName Application name */
    private string $appName;

    /** @var int $timestamp Timestamp */
    private int $timestamp;

    public function __construct(
        int $id,
        int $clientID,
        string $packageName,
        string $appName,
        int $timestamp)
    {
        $this->id = $id;
        $this->clientID = $clientID;
        $this->packageName = $packageName;
        $this->appName = $appName;
        $this->timestamp = $timestamp;
    }

    /**
     * Get installed app's ID
     * 
     * @return int
     */
    public function getID(): int
    {
        return $this->id;
    }

    /**
     * Get victim's ID
     * 
     * @return int
     */
    public function getClientID(): int
    {
        return $this->clientID;
    }

    /**
     * Get package name
     * 
     * @return string
     */
    public function getPackageName(): string
    {
        return $this->packageName;
    }

    /**
     * Get application's name
     * 
     * @return string
     */
    public function getAppName(): string
    {
        return $this->appName;
    }

    /**
     * Get timestamp
     * 
     * @return int
     */
    public function getTimestamp(): int
    {
        return $this->timestamp;
    }
}