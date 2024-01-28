<!DOCTYPE html>

<html lang="en">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>moukthar | Dashboard</title>
    <link href="/src/View/assets/vendor/fontawesome/css/fontawesome.min.css" rel="stylesheet">
    <link href="/src/View/assets/vendor/fontawesome/css/solid.min.css" rel="stylesheet">
    <link href="/src/View/assets/vendor/fontawesome/css/brands.min.css" rel="stylesheet">
    <link href="/src/View/assets/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/src/View/assets/css/master.css" rel="stylesheet">
    <link href="/src/View/assets/vendor/datatables/datatables.min.css" rel="stylesheet">
    <link href="/src/View/assets/vendor/flagiconcss/css/flag-icon.min.css" rel="stylesheet">
</head>


<body>
    <div class="wrapper">
        <nav id="sidebar" class="active">
            <ul class="list-unstyled components text-secondary">
                <li>
                    <a id="nav-dashboard"><i class="fas fa-home"></i> Dashboard</a>
                </li>
                <li>
                    <a id="nav-files"><i class="fas fa-file"></i> Victim Files</a>
                </li>
            </ul>
        </nav>
        <div id="body" class="active">
            <!-- Navbar -->
            <nav class="navbar navbar-expand-lg navbar-white bg-white">
                <button type="button" id="sidebarCollapse" class="btn btn-light">
                    <i class="fas fa-bars"></i><span></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul class="nav navbar-nav ms-auto">
                     
                        <li class="nav-item dropdown">
                            <div class="nav-dropdown">
                                <a href="#" id="nav2" class="nav-item nav-link dropdown-toggle text-secondary" data-bs-toggle="dropdown" aria-expanded="false">
                                    <i class="fas fa-user"></i> <span><?= $_SESSION['username'] ?></span> <i style="font-size: .8em;" class="fas fa-caret-down"></i>
                                </a>
                                <div class="dropdown-menu dropdown-menu-end nav-link-menu">
                                    <ul class="nav-list">
                                        <li><a href="https://github.com/Tomiwa-Ot/moukthar" target="_blank" class="dropdown-item"><i class="fas fa-info"></i> About</a></li>
                                        <li><a href="/reset" class="dropdown-item"><i class="fas fa-key"></i> Reset password</a></li>
                                        <li><a href="/logout" class="dropdown-item"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
                                    </ul>
                                </div>
                            </div>
                        </li>
                    </ul>
                </div>
            </nav>
            <!-- End of Navbar -->
            <div class="content">