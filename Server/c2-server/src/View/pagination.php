<nav>
    <ul class="pagination">
        <?php
            $uriParts = parse_url($_SERVER['REQUEST_URI']);
            $path = $uriParts['path'];

            $page = 1;
            if (isset($_GET['page']))
                $page = $_GET['page'];

            // Define the number of links to show before and after the current page
            $numLinks = 1;
            
            // Previous page link
            if ($page > 1)
                echo '<li class="page-item"><a class="page-link" href="'. $path . '?client=' . $_GET['client'] . '&page=' . ($page - 1) . '">Previous</a></li>';
            
            // Page links
            for ($i = max(1, $page - $numLinks); $i <= min($page + $numLinks, $numberOfPages); $i++)
                echo '<li class="page-item"><a class="page-link" href="'. $path . '?client=' . $_GET['client'] . '&page=' . $i . '"' . ($i == $page ? ' class="active"' : '') . '>' . $i . '</a></li>';
            
            // Next page link
            if ($page < $numberOfPages)
                echo '<li class="page-item"><a class="page-link" href="'. $path . '?client=' . $_GET['client'] . '&page=' . ($page + 1) . '">Next</a></li>';
        ?>
    </ul>
</nav>