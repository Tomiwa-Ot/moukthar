<?php

require __DIR__ . '/routes.php';

$route->get('/', fn() => $home->get());

$route->get('/faq', fn() => $faq->get());

$route->get('/contact', fn() => $contact->get());

$route->post('/contact', fn() => $contact->post());

$route->get('/admin/home', fn() => $admin->home());

$route->get('/admin/search/artist', fn() => $event->artistSearch());

$route->get('/admin/search/track', fn() => $event->trackSearch());

$route->post('/admin/track/add', fn() => $event->addTrack());

$route->post('/admin/track/remove', fn() => $event->removeTrack());

$route->post('/admin/create-event', fn() => $event->createEvent());

$route->get('/admin/faq', fn() => $admin->faq());

$route->get('/admin/faq/add', fn() => $faq->add());

$route->post('/admin/faq/add', fn() => $faq->add());

$route->get('/admin/faq/edit', fn() => $faq->edit());

$route->post('/admin/faq/edit', fn() => $faq->edit());

$route->get('/admin/faq/delete', fn() => $faq->delete());

$route->get('/admin/contact', fn() => $contact->admin());

$route->post('/admin/contact', fn() => $contact->admin());

$route->get('/admin/messages', fn() => $admin->messages());

$route->get('/admin/login', fn() => $admin->login());

$route->post('/admin/login', fn() => $admin->authenticate());

$route->get('/admin/reset-password', fn() => $admin->resetPassword());

$route->post('/admin/reset-password', fn() => $admin->updatePassword());

$route->get('/admin/logout', fn() => $admin->logout());

$route->submit();