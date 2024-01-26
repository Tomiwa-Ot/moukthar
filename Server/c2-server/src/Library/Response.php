<?php

namespace Server\Library;

/**
 * Render html view
 * 
 * @param string $view
 * @param array $data
 */
function render(string $view, array $data = array()): void
{
    if (count($data)) extract($data);
    require __DIR__ . '/../View/' . $view;
}