class TelemeteryReporter
  attr_reader :process_info_list, :file_info_list, :network_info_list

  ProcessInfo = Struct.new(:pid, :process_name, :start_time, :exe_path, :user)
  FileInfo = Struct.new(:status, :pid, :file_path, :activity_descriptor, :process_name, :command_line, :start_time, :user)
  NetworkInfo = Struct.new(:pid, :source_addr, :source_port, :dest_addr, :dest_port, :data_size, :process_name, :command_line, :start_time, :user, :protocol)

  def initialize
    @process_info_list = Array.new
    @file_info_list = Array.new
    @network_info_list = Array.new
  end

  def add_process_info(pid, process_name, start_time, exe_path, user)
    process_info = ProcessInfo.new(pid, process_name, start_time, exe_path, user)
    @process_info_list.push(process_info)
  end

  def add_file_info(status, pid, file_path, activity_descriptor, process_name, command_line, start_time, user)
    file_info = FileInfo.new(status, pid, file_path, activity_descriptor, process_name, command_line, start_time, user)
    @file_info_list.push(file_info)
  end

  def add_network_info(pid, source_addr, source_port, dest_addr, dest_port, data_size, process_name, command_line, start_time, user)
    network_info = NetworkInfo.new(pid, source_addr, source_port, dest_addr, dest_port, data_size, process_name, command_line, start_time, user, "TCP")
    @network_info_list.push(network_info)
  end
end