<?php

class Conf_model extends CI_Model {

    public function __construct() {
        $this->load->database();
    }

    public function get_limited_conferences_list($limit, $page) {
        $start_at = $page*$limit;

        $query = $this->db->query("select websites.*, venues.venue
                                  from websites
                                  left join venues on websites.venueID = venues.id
                                  LIMIT " . $limit . " OFFSET " . $start_at);

        return $query->result_array();
    }

    public function get_number_of_conferences() {
        $query = $this->db->get('websites');
        return sizeof($query->result_array());
    }

    public function get_conference($id) {
        $query = $this->db->query("select acronym, title, link, description, conference_days, sponsors, proceedings, venue, current_year, antiquity
                                  from websites
                                  left join venues on websites.venueID = venues.id
                                  where websites.id = '" . $this->db->conn_id->real_escape_string($id) . "'");

        return $query->result_array();
    }

    public function get_committees($id = FALSE) {

        if($id === FALSE) {
            return array();
        }

        $query = $this->db->query("select committee_title, member from committees, committee_titles, member_names where committees.id = '" . $this->db->conn_id->real_escape_string($id) . "'
                                   and committees.titleID = committee_titles.id and committees.memberID = member_names.id");

        return $query->result_array();
    }

    public function get_deadlines($id = FALSE) {

        if($id === FALSE) {
            return array();
        }

        $query = $this->db->query("select d_type, d_title, d_date from deadlines, deadline_types, deadlines_titles where deadlines.id = '" . $this->db->conn_id->real_escape_string($id) . "'
                                   and deadlines.deadline_type = deadline_types.id and deadlines.deadline_id = deadlines_titles.id");

        return $query->result_array();
    }

    public function search_acronym($keyword) {
        $this->db->like('acronym', $this->db->conn_id->real_escape_string($keyword));
        $query  =   $this->db->get('websites');
        return $query->result_array();
    }
}