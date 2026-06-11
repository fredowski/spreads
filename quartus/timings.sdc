create_clock -name clk -period 20.000 [get_ports {CLOCK_50}]

set default_inputs [get_ports KEY0]
set default_inputs [add_to_collection $default_inputs [get_ports ADC_OTR*]]

# Some clock for ADC/DAC Board
set clock_outputs [get_ports ADC_CLK*]
set clock_outputs [add_to_collection $clock_outputs [get_ports DAC_CLK*]]
set clock_outputs [add_to_collection $clock_outputs [get_ports DAC_WRT*]]

set default_outputs [remove_from_collection [all_outputs] [get_ports DAC_D*]] 
set default_outputs [remove_from_collection $default_outputs $clock_outputs]

# Default Timing Constrains for Inputs/Outputs
set_input_delay -clock CLOCK_50 5 $default_inputs
set_output_delay -clock CLOCK_50  5 $default_outputs
# Special Timing for ADC and DAC Data I/O
set_input_delay -clock CLOCK_50 17 [get_ports ADC_D*]
set_output_delay -clock CLOCK_50 -clock_fall 7 [get_ports DAC_D*]
# Special Timing for clock outputs 
set_output_delay -clock CLOCK_50 2 $clock_outputs

# Add small input delay to all io signals, so quartus does not expect them to be right on clock edges.
set_input_delay -clock clk 2.0 [get_ports {io_txEnable}]
set_input_delay -clock clk 2.0 [get_ports {io_txData}]
set_input_delay -clock clk 2.0 [get_ports {io_rxEnable}]
set_input_delay -clock clk 2.0 [get_ports {reset}]

set_output_delay -clock clk 2.0 [get_ports {io_decoded}]
set_output_delay -clock clk 2.0 [get_ports {io_syncd}]



