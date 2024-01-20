<?php

namespace Server\Model;

/**
 * Victim's location
 */
class Location
{
    /** @var int $id Contact id */
    private int $id;

    /** @var int $clientID Victim's ID */
    private int $clientID;

    /** @var string $timestamp */
    private string $timestamp;

    public function __construct(
        int $id,
        int $clientID,
        string $timestamp)
    {
        $this->id = $id;
        $this->clientID = $clientID;
        $this->timestamp = $timestamp;
    }

    /**
     * Get location's ID
     * 
     * @return int
     */
    public function getID(): int
    {
        return $this->id;
    }

    /**
     * Get client's ID
     * 
     * @return int
     */
    public function getClientID(): int
    {
        return $this->clientID;
    }

    /**
     * Get location's timestamp
     * 
     * @return string
     */
    public function getTimestamp(): string
    {
        return $this->timestamp;
    }
}