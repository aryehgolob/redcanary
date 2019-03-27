# Red Canary Telemetry Framework

The approach I have taken to this project is to create a generic framework to provide 3 primary forms of telemetry metrics.  With scalability in mind, other forms of telemetry may be added later and additional operating systems supported.

* Process Metrics
* File Metrics
* Network Metrics

All of this functionality is controlled through the following config.yaml file which has been validated on my local Windows 10 machine as well as the following virtual image: https://app.vagrantup.com/ubuntu/boxes/trusty64

As I would imagine, in a real life scenario we may want to validate certain core processes on particular operating systems in various scenario's.

For this project, I have decided to make the number of individual telemetry metrics for all three categories (process, file, network) unlimited.  That is to say, by simply editing the config file and using that as our source definition file, we can easily tweak as we hit real life scenario's.

The following YAML file which has been validated against the two hosts mentioned above (windows local host, trusty64 ubuntu vm).

Interpretation:

When run on a windows host

* kick off cmd.exe and powershell.exe and gather telemetry metrics.
* create a file named telemetry_test_1.tmp and telemetery_test_2.tmp
* verify creation success and report fail if file could not be created
* modify both files by passing it the specific text as defined in the config file
* compare size of file before and size of file afterward to validate file modification success
* delete file and validate delete
* open a tcp connection to the url/port list identified in configuration data
* send the data mapped to tcp connection in configuration file
* gather tcp metrics for connection
* report results in time-series sequenced csv files

```javascript
# Red Canary Telemetry Configuration

windows:
    # windows process to measure
    process:        
        - process_name: cmd.exe 
        - process_name: powershell.exe
    file: 
        - file_name: telemetry_test_1.tmp
          text: >
              Red Canary telementery test file.
              File modify - test1
        - file_name: telemetry_test_2.tmp
          text: >
              Red Canary telementery test file.
              File modify - test2
    network:
        - url: google.com
          port: 80
          data: >
              Send this data to Google
        - url: yahoo.com
          port: 80
          data: >
              Send this data to Yahoo

linux:
    # linux process to measure
    process:        
        - process_name: pwd
        - process_name: ls
    file: 
        - file_name: telemetry_test_1.tmp
          text: >
              Red Canary telementery test file.
              File modify - test1
        - file_name: telemetry_test_2.tmp
          text: >
              Red Canary telementery test file.
              File modify - test2
    network:
        - url: google.com
          port: 80
          data: >
              Send this data to Google
        - url: yahoo.com
          port: 80
          data: >
              Send this data to Yahoo
```


