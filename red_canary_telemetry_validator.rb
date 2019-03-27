require 'yaml'
require 'os'
require 'sys/proctable'
require 'date'
require 'socket'
require 'csv'
require 'json'
require './telemetery_reporter.rb'


include Sys

class RedCanaryTelemetryValidator
  def initialize
    @@config  = YAML.load_file('config.yaml')
    puts @@config.inspect

    @reporter = TelemeteryReporter.new
    @user = `whoami`.strip
    @output_mode = @@config['output_mode']
    p "output_mode: " + @output_mode

    if OS.windows?
      @target_os = "windows"
    elsif OS.linux?
      @target_os = "linux"
    else
      @target_os = "undetermined"
    end

  end

  def validate_telemetry
    # validate process information
    validate_process_info()

    # validate file functions
    validate_file_functions()

    # validate network TCP send
    validate_network()
  end

  def validate_network
    if @target_os == "windows"
      network_info_list = @@config['windows']['network']
      validate_network_info(network_info_list)
    elsif @target_os == "linux"
      network_info_list = @@config['linux']['network']
      validate_network_info(network_info_list)
    else
      # undetected operating system - throw an error
    end
  end

  def validate_network_info(network_info_list)
    current_pid = Process.pid
    current_process = ProcTable.ps(pid: current_pid)
    command_line = current_process[:cmdline]
    process_name = current_process[:comm]
    if @target_os == "windows"
      start_time = current_process[:creation_date].to_s
    elsif @target_os == "linux"
      start_time = current_process[:starttime].to_s
    else
      # unsupported OS
    end

    network_info_list.each do |network_info_hash|
      url = network_info_hash['url']
      port = network_info_hash['port']
      data = network_info_hash['data']
      data_size = data.bytesize

      sock = TCPSocket.new(url,port)
      source_ip = sock.local_address.ip_address
      source_port = sock.local_address.ip_port
      sock.write(data)

      @reporter.add_network_info(current_pid, source_ip, source_port, url, port, data_size, process_name, command_line, start_time, @user)
    end

  end

  def validate_file_info(file_info_list)
    current_pid = Process.pid

    current_process = ProcTable.ps(pid: current_pid)
    command_line = current_process[:cmdline]
    process_name = current_process[:comm]
    if @target_os == "windows"
      start_time = current_process[:creation_date].to_s
    elsif @target_os == "linux"
      start_time = current_process[:starttime].to_s
    else
      # unsupported OS
    end

    file_info_list.each do |file_info_hash|
      # validate file creation
      file_name = file_info_hash['file_name']
      file_text = file_info_hash['text']
      out_file = File.new(file_name, "w+")
      file_size_empty = out_file.size
      file_path = File::absolute_path(file_name)

      file_create_status = "fail"
      if File::exist?(file_name)
        file_create_status = "pass"
      end
      @reporter.add_file_info(file_create_status, current_pid, file_path, "create file", process_name, command_line, start_time, @user)

      out_file.write(file_text)
      file_size_mod = out_file.size
      out_file.close()


      # validate file modify
      file_modify_status = "fail"
      if file_size_mod.size != file_size_empty
        file_modify_status = "pass"
      end
      @reporter.add_file_info(file_modify_status, current_pid, file_path, "modify file", process_name, command_line, start_time, @user)


      # validate file delete
      File.delete(file_name)
      file_delete_status = "fail"
      if(!File.exist?(file_name))
        file_delete_status = "pass"
      end

      @reporter.add_file_info(file_delete_status, current_pid, file_path, "delete file", process_name, command_line, start_time, @user)
    end

  end

  def validate_file_functions
    if @target_os == "windows"
      file_info_list = @@config['windows']['file']
      validate_file_info(file_info_list)
    elsif @target_os == "linux"
      file_info_list = @@config['linux']['file']
      validate_file_info(file_info_list)
    else
      # undetected operating system - throw an error
    end

  end

  def validate_process_info
    if @target_os == "windows"
      process_list = @@config['windows']['process']
      validate_process_list(process_list)
    elsif @target_os == "linux"
      process_list = @@config['linux']['process']
      validate_process_list(process_list)
    else
      # undetected operating system - throw an error
    end
  end

  def validate_process_list(process_list)
    process_exe_list = Array.new
    process_list.each do |process_hash|
      process_name = process_hash['process_name']
      process_exe_list << process_name
    end

    process_exe_list.each do |process|
      pid = spawn(process)
      process = ProcTable.ps(pid: pid)
      if @target_os == "windows"
        start_time = process[:creation_date].to_s
        process_name = process[:cmdline]
        exe_path = process[:executable_path]
      elsif @target_os == "linux"	
        start_time = process[:starttime].to_s
        process_name = process[:cmdline]
        exe_path = `which #{process_name}`
      else
        # undetected operating system - throw an error
      end
      @reporter.add_process_info(pid, process_name, start_time, exe_path, @user)
    end

  end

  def generate_report
    if @output_mode == "csv"
      generate_csv()
    elsif @output_mode == "json"
      generate_json()
    end
  end

  def generate_json
    time = Time.new
    date_stamp = time.month.to_s+time.year.to_s+time.day.to_s+time.hour.to_s+time.min.to_s+time.sec.to_s
    output_file_name = "telemetry_report."+date_stamp+".json"

    output_file = File.open(output_file_name, 'w')

    metric_array = Array.new

    process_info_array = Array.new
    metric_array << process_info_array
    process_info_list = @reporter.process_info_list
    process_info_list.each do |process_info|
      pid = process_info.pid
      process_name = process_info.process_name
      start_time = process_info.start_time
      exe_path = process_info.exe_path
      user = process_info.user
      stats_hash = ["pid" => pid, "process_name" => process_name, "start_time" => start_time, "exe_path" => exe_path, "user" => user]
      process_info_array << stats_hash
    end

    file_info_array = Array.new
    metric_array << file_info_array
    file_info_list = @reporter.file_info_list
    file_info_list.each do |file_info|
      status = file_info.status
      pid = file_info.pid
      file_path = file_info.file_path
      activity_descriptor = file_info.activity_descriptor
      process_name = file_info.process_name
      command_line = file_info.command_line
      start_time = file_info.start_time
      user = file_info.user
      stats_hash = ["status" => status, "pid," => pid, "file_path" => file_path, "activity_descriptor" => activity_descriptor,
                    "process_name" => process_name, "command_line" => command_line, "start_time" => start_time, "user" => user]
      file_info_array << stats_hash
    end


    network_info_array = Array.new
    metric_array << network_info_array
    network_info_list = @reporter.network_info_list
    network_info_list.each do |network_info|
      pid = network_info.pid
      source_addr = network_info.source_addr
      source_port = network_info.source_port
      dest_addr = network_info.dest_addr
      dest_port = network_info.dest_port
      process_name = network_info.process_name
      command_line = network_info.command_line
      start_time = network_info.start_time
      data_size = network_info.data_size.to_s
      user = network_info.user
      protocol = network_info.protocol
      p "network info: "+network_info.inspect
      stats_hash = ["pid" => pid, "source_addr" => source_addr, "source_port" => source_port, "dest_addr" => dest_addr,
                    "dest_port" => dest_port, "data_size" => data_size, "process_name" => process_name,
                    "command_line" => command_line, "start_time" => start_time, "user" => user, "protocol" => protocol]
      network_info_array << stats_hash
    end

    output_file.write(metric_array.to_json)

    end

  end

  def generate_csv
    time = Time.new
    date_stamp = time.month.to_s+time.year.to_s+time.day.to_s+time.hour.to_s+time.min.to_s+time.sec.to_s
    
    # generate process telemetry report
    proc_file_name = "process_telemetry_report."+date_stamp+".csv"
    CSV.open(proc_file_name, "w",
             :write_headers=>true, :headers => ["Process ID","Process Name","Start Time","Process Command Line","User"]) do |csv|
      process_info_list = @reporter.process_info_list
      process_info_list.each do |process_info|
        pid = process_info.pid
        process_name = process_info.process_name
        start_time = process_info.start_time
        exe_path = process_info.exe_path
        user = process_info.user
        csv << [pid, process_name, start_time, exe_path, user]
      end
    end

    # generate file telemetry report
    file_file_name = "file_telemetry_report."+date_stamp+".csv"
    CSV.open(file_file_name, "w",
             :write_headers=>true, :headers => ["Status", "Process ID","File Path","Activity","Process Name","Process Command Line","Start Time","User"]) do |csv|
      file_info_list = @reporter.file_info_list
      file_info_list.each do |file_info|
        status = file_info.status
        pid = file_info.pid
        file_path = file_info.file_path
        activity_descriptor = file_info.activity_descriptor
        process_name = file_info.process_name
        command_line = file_info.command_line
        start_time = file_info.start_time
        user = file_info.user
        csv << [status, pid, file_path, activity_descriptor, process_name, command_line, start_time, user]
      end
    end

    # generate network telemetry report
    network_file_name = "network_telemetry_report."+date_stamp+".csv"
    CSV.open(network_file_name, "w",
             :write_headers=>true,
                         :headers => ["Process ID", "Source Address", "Source Port", "Destination Address",
                                      "Destination Port", "Data Size", "Process Name", "Command Line", "Start Time", "User", "Protocol"]) do |csv|
      network_info_list = @reporter.network_info_list
      network_info_list.each do |network_info|
        pid = network_info.pid
        source_addr = network_info.source_addr
        source_port = network_info.source_port
        dest_addr = network_info.dest_addr
        dest_port = network_info.dest_port
        process_name = network_info.process_name
        command_line = network_info.command_line
        start_time = network_info.start_time
        data_size = network_info.data_size.to_s
        user = network_info.user
        protocol = network_info.protocol

        csv << [pid, source_addr, source_port, dest_addr, dest_port, data_size, process_name, command_line, start_time, user, protocol]
      end
    end
  end


# create telemetry validator
validator = RedCanaryTelemetryValidator.new

# validate telemetry
validator.validate_telemetry()

# generate output files
validator.generate_report()

