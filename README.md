# Red Canary Telemetry Framework

The approach I have taken to this project is to create a generic framework to provide 3 primary forms of telemetry metrics.

* Process Metrics
* File Metrics
* Network Metrics

All of this functionality is controlled through the following config.yaml file which has been validated on my local Windows 10 machine as well as the following virtual image: https://app.vagrantup.com/ubuntu/boxes/trusty64

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


