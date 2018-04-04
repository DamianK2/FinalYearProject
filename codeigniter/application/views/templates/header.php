<html>
    <head>
        <title>Conferences</title>
        <link rel="stylesheet" type="text/css" href="<?php echo base_url();?>css/styles.css">
    </head>
    <body>
        <header>
            <h1><?php
                    if(isset($conference_information)) {
                        $heading_to_display = '';
                        if ($conference_information[0]['acronym'] == '' && $conference_information[0]['title'] != '') {
                            $heading_to_display = $conference_information[0]['title'];
                        } else {
                            $heading_to_display = $conference_information[0]['acronym'];
                        }

                        echo $heading_to_display . ' - ' . $conference_information[0]['current_year'];
                    }
                    else
                        echo '<a href =' . base_url() . '/index.php/conferences>' . $site_title . '</a>';
                    ?>
            </h1>
        </header>

