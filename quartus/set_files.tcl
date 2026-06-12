set top_level [lindex $argv 0]
set v_file    [lindex $argv 1]

project_open spread_quartus
remove_all_global_assignments -name VERILOG_FILE
set_global_assignment -name TOP_LEVEL_ENTITY $top_level
set_global_assignment -name VERILOG_FILE $v_file
set_global_assignment -name RESERVE_ALL_UNUSED_PINS "AS INPUT TRI-STATED"
export_assignments
project_close
