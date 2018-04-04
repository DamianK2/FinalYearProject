

<!--echo '<pre>';-->
<!--echo print_r($conference_item);-->
<!--echo '</pre>';-->


<div class = "container">
<!--    --><?php
//    echo '<pre>';
//    echo print_r($conference_information[0]);
//    echo '</pre>';
//    ?>

    <?php
        foreach($conference_information[0] as $key => $value) {
            if(array_key_exists($key, $headings)) {
                if($key == "link") {
                    echo '<div class = "information_container"><div class = "heading"><h2>';
                    echo $headings[$key] . '</h2></div><div class = "content">';
                    echo '<a href="' . $value . '">' . $value . '</a>' . '</div></div>';
                } else {
                    echo '<div class = "information_container"><div class = "heading"><h2>';
                    echo $headings[$key] . '</h2></div><div class = "content">';
                    echo $value . '</div></div>';
                }
            }
        }
    ?>

    <div class = "information_container">
        <div class = "heading">
            <?php
                echo '<h2>' . $headings['deadlines'] . '</h2>'; ?>
        </div>

        <div class = "content">
            <?php
                if(!empty($conference_deadlines)) {
                    $d_type = $conference_deadlines[0]['d_type'];
                    if(!empty($d_type)) {
                        echo '<strong>' . $d_type . '</strong><br /><br />';
                    }
                    foreach ($conference_deadlines as $deadline) {
                        if($deadline['d_type'] != $d_type) {
                            $d_type = $deadline['d_type'];
                            if(!empty($d_type)) {
                                echo '<br /><strong>' . $d_type . '</strong><br /><br />';
                            }
                        }

                        echo $deadline['d_title'] . ': ' . $deadline['d_date'] . '<br />';
                    }
                }
            ?>
        </div>
    </div>

    <div class = "information_container">
        <div class = "heading">
            <?php echo '<h2>' . $headings['committees'] . '</h2>'; ?>
        </div>

        <div class = "content">
            <?php
                if(!empty($conference_committee)) {
                    $com_title = $conference_committee[0]['committee_title'];
                    echo '<strong>' . $com_title . '</strong><br /><br />';

                    foreach ($conference_committee as $committee) {
                        if($committee['committee_title'] != $com_title) {
                            $com_title = $committee['committee_title'];
                            echo '<br /><strong>' . $com_title . '</strong><br /><br />';
                        }

                        echo $committee['member'] . '<br />';
                    }

                }
            ?>
        </div>
    </div>
</div>


