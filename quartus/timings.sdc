# A 50 MHz = 20ns clock period
create_clock -name clk -period 20.000 [get_ports {CLOCK_50}]

# Add small input delay to all io signals, so quartus does not expect them to be right on clock edges.
set_input_delay -clock clk 2.0 [get_ports {io_txEnable}]
set_input_delay -clock clk 2.0 [get_ports {io_txData}]
set_input_delay -clock clk 2.0 [get_ports {io_rxEnable}]
set_input_delay -clock clk 2.0 [get_ports {reset}]

set_output_delay -clock clk 2.0 [get_ports {io_decoded}]
set_output_delay -clock clk 2.0 [get_ports {io_syncd}]
