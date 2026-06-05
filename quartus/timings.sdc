# A 50 MHz = 20ns clock period
create_clock -name sys_clk -period 20.000 [get_ports {clk}]

