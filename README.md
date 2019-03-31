### Red Canary Telemetry Validator

#### Running (JAVA_VERSION >= 9 required)
```
git clone https://github.com/aryehgolob/redcanary.git
cd redcanary
mvn install
mvn exec:java -Dexec.mainClass="com.redcanary.tools.telemetry.TelemetryValidator"

#telemetry_report.<timestamp>.json file created
```


#### Configuration File (etc/telemetry_validator.properties)
```
# mode
telemetry_validator.mode=run_telemetry_report

# thread count
telemetry_validator.thread_count=10
```

#### Definition File (etc/telemetry_definition.xml)
```
<telemetry_definition>
   <platform os="windows">
       <metric_element_group type="file">
           <file name="file1.txt">
                <text>write this text to file</text>
           </file>
           <file dir="C:/dev/redcanary" name="file2.txt">
                <text random="true" size_bytes="4000"/>
           </file>
       </metric_element_group>

       <metric_element_group type="process">
           <process name="ipconfig" args="/all"/>
           <process name="notepad"/>
           <process name="calc"/>
       </metric_element_group>

       <metric_element_group type="network">
           <network_connection ip="google.com" port="80">
                <data>Send this data to google</data>
           </network_connection>
           <network_connection ip="yahoo.com" port="80">
                <data>Send this data to yahoo</data>
           </network_connection>
       </metric_element_group>
   </platform>

   <platform os="linux">
       <metric_element_group type="file">
           <file name="file1.txt">
                <text>write this text to file</text>
           </file>
           <file name="file2.txt">
                <text random="true" size_bytes="4000"/>
           </file>
       </metric_element_group>

       <metric_element_group type="process">
           <process name="pwd"/>
           <process name="ls" args="-altr"/>
           <process name="cd" args="/"/>
       </metric_element_group>

       <metric_element_group type="network">
           <network_connection ip="google.com" port="80">
                <data>Send this data to google</data>
           </network_connection>
           <network_connection ip="yahoo.com" port="80">
                <data>Send this data to yahoo</data>
           </network_connection>
       </metric_element_group>
   </platform>

   <platform os="mac">
       <metric_element_group type="file">
           <file name="file1.txt">
                <text>write this text to file</text>
           </file>
           <file name="file2.txt">
                <text random="true" size_bytes="4000"/>
           </file>
       </metric_element_group>

       <metric_element_group type="process">
           <process name="pwd"/>
           <process name="ls" args="-altr"/>
           <process name="cd" args="/"/>
       </metric_element_group>

       <metric_element_group type="network">
           <network_connection ip="google.com" port="80">
                <data>Send this data to google</data>
           </network_connection>
           <network_connection ip="yahoo.com" port="80">
                <data>Send this data to yahoo</data>
           </network_connection>
       </metric_element_group>
   </platform>
</telemetry_definition>
```

#### Sample Output
```
[
    {
        "activity_descriptor": "create",
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\java.exe",
        "element_type": "file_validate",
        "file_name": "C:\\dev\\redcanary\\tmp2\\redcanary\\file1.txt",
        "pid": 14764,
        "process_name": "java.exe",
        "start_time": "2019-03-31T23:32:38.002Z",
        "status": "pass",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "activity_descriptor": "modify",
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\java.exe",
        "element_type": "file_validate",
        "file_name": "C:\\dev\\redcanary\\tmp2\\redcanary\\file1.txt",
        "pid": 14764,
        "process_name": "java.exe",
        "start_time": "2019-03-31T23:32:38.002Z",
        "status": "pass",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "activity_descriptor": "delete",
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\java.exe",
        "element_type": "file_validate",
        "file_name": "C:\\dev\\redcanary\\tmp2\\redcanary\\file1.txt",
        "pid": 14764,
        "process_name": "java.exe",
        "start_time": "2019-03-31T23:32:38.002Z",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "activity_descriptor": "create",
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\java.exe",
        "element_type": "file_validate",
        "file_name": "C:\\dev\\redcanary\\file2.txt",
        "pid": 14764,
        "process_name": "java.exe",
        "start_time": "2019-03-31T23:32:38.002Z",
        "status": "pass",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "activity_descriptor": "modify",
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\java.exe",
        "element_type": "file_validate",
        "file_name": "C:\\dev\\redcanary\\file2.txt",
        "pid": 14764,
        "process_name": "java.exe",
        "start_time": "2019-03-31T23:32:38.002Z",
        "status": "pass",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "activity_descriptor": "delete",
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\java.exe",
        "element_type": "file_validate",
        "file_name": "C:\\dev\\redcanary\\file2.txt",
        "pid": 14764,
        "process_name": "java.exe",
        "start_time": "2019-03-31T23:32:38.002Z",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "command_line": "C:\\Windows\\System32\\ipconfig.exe",
        "element_type": "process_validate",
        "pid": 18868,
        "process_name": "ipconfig /all",
        "start_time": "2019-03-31T23:32:39.639Z",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "command_line": "C:\\Windows\\System32\\notepad.exe",
        "element_type": "process_validate",
        "pid": 2272,
        "process_name": "notepad ",
        "start_time": "2019-03-31T23:32:39.630Z",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "command_line": "C:\\Windows\\System32\\calc.exe",
        "element_type": "process_validate",
        "pid": 6072,
        "process_name": "calc ",
        "start_time": "2019-03-31T23:32:39.643Z",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\java.exe",
        "data_size": 24,
        "destination_ip": "10.40.60.90",
        "destination_port": 80,
        "element_type": "network_validate",
        "pid": 14764,
        "process_name": "java.exe",
        "protocol": "TCP",
        "source_ip": "10.40.60.90",
        "source_port": 57775,
        "start_time": "2019-03-31T23:32:38.002Z",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\java.exe",
        "data_size": 23,
        "destination_ip": "10.40.60.90",
        "destination_port": 80,
        "element_type": "network_validate",
        "pid": 14764,
        "process_name": "java.exe",
        "protocol": "TCP",
        "source_ip": "10.40.60.90",
        "source_port": 57776,
        "start_time": "2019-03-31T23:32:38.002Z",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    }
]
```


