<?php

namespace Server\Model;

/**
 * Victim device
 */
class Client
{
    /** @var int $id Client's ID */
    private int $id;

    /** @var string $model Android device model */
    private string $model;

    /** @var string $deviceId Android device ID */
    private string $deviceId;

    /** @var string $ipAddress Device IP address */
    private string $ipAddress;

    /** @var string $deviceAPI Android device API */
    private string $deviceAPI;

    /** @var string $phone Phone number */
    private string $phone;

    /** @var string $websocketId Web socket connection ID */
    private string $websocketId;

    public function __construct(
        int $id,
        string $model,
        string $deviceId,
        string $ipAddress,
        string $deviceAPI,
        string $phone,
        string $websocketId)
    {
        $this->id = $id;
        $this->model = $model;
        $this->deviceId = $deviceId;
        $this->ipAddress = $ipAddress;
        $this->deviceAPI = $deviceAPI;
        $this->phone = $phone;
        $this->websocketId = $websocketId;
    }

    /**
     * Get client's ID
     * 
     * @return int
     */
    public function getID(): int
    {
        return $this->id;
    }

    /**
     * Get device model
     * 
     * @return string
     */
    public function getModel(): string
    {
        return $this->model;
    }

    /**
     * Get device id
     * 
     * @return string
     */
    public function getDeviceID(): string
    {
        return $this->deviceId;
    }

    /**
     * Get device IP address
     * 
     * @return string
     */
    public function getIPAddress(): string
    {
        return $this->ipAddress;
    }

    /**
     * Get device API
     * 
     * @return string
     */
    public function getDeviceAPI(): string
    {
        return $this->deviceAPI;
    }

    /**
     * Get device phone number
     * 
     * @return string
     */
    public function getPhone(): string
    {
        return $this->phone;
    }

    /**
     * Get device web socket connection ID
     * 
     * @return string
     */
    public function getWebSocketID(): string
    {
        return $this->websocketId;
    }

    public static function parse(array $data): Client
    {
        if ($data['id']
         && isset($data['model'])
         && isset($data['device_id'])
         && isset($data['ip_address'])
         && isset($data['device_api'])
         && isset($data['phone'])
         && isset($data['web_socket_id'])) 
        {
            return new Client(
                $data['id'],
                $data['model'],
                $data['device_id'],
                $data['ip_address'],
                $data['device_api'],
                $data['phone'],
                $data['web_socket_id']
            );
        }

        return null;
    }
}