<?php

class Conferences extends CI_Controller {

    public function __construct() {
        parent::__construct();
        $this->load->model('conf_model');
        $this->load->helper('url_helper');
        $this->load->library("pagination");
    }

    public function index() {
        $data['site_title'] = "Conferences";

        $data['total_nof_conferences'] = $this->conf_model->get_number_of_conferences();
        $config['base_url'] = base_url() . 'index.php/conferences';
        $config['total_rows'] = $data['total_nof_conferences'] ;
        $config['per_page'] = 15;

        $this->pagination->initialize($config);

        $page = ($this->uri->segment(2)) ? $this->uri->segment(2) : 0;

        $page = $page / $config['per_page'];

        $data['pagination'] =  $this->pagination;

        $data['conferences'] = $this->conf_model->get_limited_conferences_list($config['per_page'], $page);

        $this->load->view('templates/header', $data);
        $this->load->view('conferences/index', $data);
        $this->load->view('templates/footer');
    }

    public function view($id = NULL) {

        $data['headings'] = array(
            "title" => "Title",
            "link" => "Link",
            "sponsors" => "Sponsors",
            "proceedings" => "Proceedings",
            "description" => "Description",
            "venue" => "Venue",
            "antiquity" => "Antiquity",
            "conference_days" => "Conference Days",
            "deadlines" => "Deadlines",
            "committees" => "Committees"
        );

        $data['conference_information'] = $this->conf_model->get_conference($id);

        $array = $this->conf_model->get_committees($id);
        if(!empty($array))
            $data['conference_committee'] = $array;

        $array = $this->conf_model->get_deadlines($id);
        if(!empty($array))
            $data['conference_deadlines'] = $array;

        if (empty($data['conference_information']))
        {
            show_404();
        }

        $this->load->view('templates/header', $data);
        $this->load->view('conferences/view', $data);
        $this->load->view('templates/footer');
    }

    public function search_keyword() {
        $keyword = $this->input->post('keyword', TRUE);
        $data['site_title'] = "Conferences";
        $data['conferences'] = $this->conf_model->search_acronym($keyword);
        $data['total_nof_conferences'] = $this->conf_model->get_number_of_conferences();
        $data['keyword'] = $keyword;

        $this->load->view('templates/header', $data);
        $this->load->view('conferences/index', $data);
        $this->load->view('templates/footer');
    }
}