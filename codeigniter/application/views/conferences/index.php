
<div class="search_bar_container">
    <div class="results">
        <?php
            if(isset($keyword)) {
                echo '<p>Found ' . sizeof($conferences). ' conferences for the search: <i>' . $keyword . '</i></p>';
            }
        ?>
    </div>

    <div class="search_box">
        <form action="<?php echo site_url('conferences/search_keyword');?>" method = "post">
            <input type="text" name = "keyword" />
            <input type="submit" value = "Search" />
        </form>
    </div>
</div>

<div class="container">

<!--    --><?php
//    echo '<pre>';
//    echo print_r($conferences);
//    echo '</pre>';
//    ?>

    <div class="list content">
        <?php
        $item_to_display = '';

        foreach ($conferences as $conference) {
            if($conference['acronym'] == '' && $conference['title'] != '')
                $item_to_display = $conference['title'];
            else
                $item_to_display = $conference['acronym'];

            echo '<a href="' . base_url() . 'index.php/conferences/conference/' . $conference['id'] . '">' . $item_to_display . ' ' . $conference['current_year'] . '</a>' . '<br />';
        }

        ?>
    </div>

    <div class="side_panel content">
        <h3>Total number of conferences: <?php echo $total_nof_conferences; ?></h3>
    </div>

    <?php
        if(isset($pagination))
            echo '<div class="previous_next">' . $pagination->create_links() . '</div>';
    ?>
</div>
