<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>moukthar | Login</title>
    <link href="/src/View/assets/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/src/View/assets/css/auth.css" rel="stylesheet">
</head>

<body>
    <div class="wrapper">
        <div class="auth-content">
            <?php if(isset($error)): ?>
                <div class="alert alert-danger" id="error-alert" role="alert">
                    <strong> <?= $error ?> </strong>
                </div>
            <?php endif ?>
        
            <div class="card">
                <div class="card-body text-center">                   
                    <h6 class="mb-4 text-muted">Login to your account</h6>
                    <form action="<?= $_SERVER['REQUEST_URI'] ?>" method="POST">
                        <div class="mb-3 text-start">
                            <label for="email" class="form-label">Username</label>
                            <input name="username" type="text" class="form-control" placeholder="Username" required>
                        </div>
                        <div class="mb-3 text-start">
                            <label for="password" class="form-label">Password</label>
                            <input name="password" type="password" class="form-control" placeholder="Password" required>
                        </div>
                        <input type="submit" id="btn-login" class="btn btn-primary shadow-2 mb-4" style="background-color: #fa184b;border: none;" value="Login">
                    </form>
                </div>
            </div>
        </div>
    </div>
    <script src="/src/View/assets/vendor/jquery/jquery.min.js"></script>
    <script src="/src/View/assets/vendor/bootstrap/js/bootstrap.min.js"></script>

    <script>
        $(document).ready(function (){
            if($('#error-alert').length != 0){
                $('#error-alert').fadeTo(2000, 500).slideUp(500, function (){
                    $('#error-alert').slideUp(500);
                });
            }
        });
    </script>
</body>

</html>