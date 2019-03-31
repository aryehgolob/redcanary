### Red Canary Telemetry Validator

#### Running
```
git clone https://github.com/aryehgolob/redcanary.git
mvn install
mvn exec:java -Dexec.mainClass="com.redcanary.tools.telemetry.TelemetryValidator"
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

