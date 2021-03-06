### Red Canary Telemetry Validator

#### Architecture Discussion

The Red Canary Telemetry Validator is a fully functional encapsulated framework intent on probing telemetry metrics on a host and reporting back in any format necessary.  For this project the result is a simple JSON array with nested hashes representing the individual properties, but the output format may be extended easily due to the OO nature of the application.

As I wanted to be able to support as many as many potential "metrics" as possible, I will point you to the class ```GenericMetric``` which looks like this.

```
public abstract class GenericMetric implements Callable {
	private static Logger log = Logger.getLogger(GenericMetric.class);

	public abstract String toJson();
}
```

Notice 2 things about this class ...

* ```GenericMetric implements Callable```
* The following absract method ```public abstract String toJson();```

Regarding ```implements Callable``` this simply indicates that the inherting classes will be run through Java's executor framework for multi-threading.  If you will look in ```TelemetryProber``` class, you will see that we are passing GenericMetric objects to the executor framework.

```
List<GenericMetric> metricList = this.definition.getMetricList();
....

for(GenericMetric metric : metricList) {
	Future<Boolean> future = executor.submit(metric);
	futureList.add(future);
}
```

Through the magic of polymorphism, the indivdual GenericMetrics are executed at runtime as their constituent classes (FileMetric, ProcessMetric, NetworkMetric). 

Regarding the output, notice again the abstract method in GenericMetric ...

```
public abstract String toJson();
```

If you will look at ```TelemetryProber``` in the ```generateReport()``` method, I am first getting a list of GenericMetric's and calling the polymorphic ```toJson()``` method.

```
List<GenericMetric> metricList = this.definition.getMetricList();
....


String json = metricList.get(i).toJson();
```

With regard to initialization of metric settings, see ```TelemetryDefinitionXml```

Telemetric settings are only parsed for the OS detected.

```
if(!os.equals(TelemetryValidator.OPERATING_SYSTEM)) {
      // pass over XML config section of OS does not match
      continue;
}
```

Metric instances are parsed according to their type (network, process, file) but stored in a list of ```GenericMetric```'s

```
private List<GenericMetric> metricList = new ArrayList<GenericMetric>();
...
metricList.add(fileMetric);
...
metricList.add(procMetric);
...
metricList.add(netMetric);
```

##### Some features

* Optional: "dir" attribute for file to write to specific directory
```
<file dir="C:/dev/redcanary" name="file2.txt">
```

* Optional: generate fixed size random data instead of pre-defined data
```
<text random="true" size_bytes="4000"/>
```

*If the data we are writing is unimportant, it "should" be random.*

* Optional: args parameter for processes
```
<process name="ls" args="-altr"/>
```



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
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\javaw.exe",
        "element_type": "file_validate",
        "file_name": "C:\\dev\\redcanary\\file1.txt",
        "pid": 3348,
        "process_name": "javaw.exe",
        "start_time": "2019-04-01T13:54:47.508Z",
        "status": "pass",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "activity_descriptor": "modify",
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\javaw.exe",
        "element_type": "file_validate",
        "file_name": "C:\\dev\\redcanary\\file1.txt",
        "pid": 3348,
        "process_name": "javaw.exe",
        "start_time": "2019-04-01T13:54:47.508Z",
        "status": "pass",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "activity_descriptor": "delete",
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\javaw.exe",
        "element_type": "file_validate",
        "file_name": "C:\\dev\\redcanary\\file1.txt",
        "pid": 3348,
        "process_name": "javaw.exe",
        "start_time": "2019-04-01T13:54:47.508Z",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "activity_descriptor": "create",
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\javaw.exe",
        "element_type": "file_validate",
        "file_name": "C:\\dev\\redcanary\\file2.txt",
        "pid": 3348,
        "process_name": "javaw.exe",
        "start_time": "2019-04-01T13:54:47.508Z",
        "status": "pass",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "activity_descriptor": "modify",
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\javaw.exe",
        "element_type": "file_validate",
        "file_name": "C:\\dev\\redcanary\\file2.txt",
        "pid": 3348,
        "process_name": "javaw.exe",
        "start_time": "2019-04-01T13:54:47.508Z",
        "status": "pass",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "activity_descriptor": "delete",
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\javaw.exe",
        "element_type": "file_validate",
        "file_name": "C:\\dev\\redcanary\\file2.txt",
        "pid": 3348,
        "process_name": "javaw.exe",
        "start_time": "2019-04-01T13:54:47.508Z",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "command_line": "C:\\Windows\\System32\\ipconfig.exe",
        "element_type": "process_validate",
        "pid": 7404,
        "process_name": "ipconfig /all",
        "start_time": "2019-04-01T13:54:47.721Z",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "command_line": "C:\\Windows\\System32\\notepad.exe",
        "element_type": "process_validate",
        "pid": 24128,
        "process_name": "notepad ",
        "start_time": "2019-04-01T13:54:47.708Z",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "command_line": "C:\\Windows\\System32\\calc.exe",
        "element_type": "process_validate",
        "pid": 8680,
        "process_name": "calc ",
        "start_time": "2019-04-01T13:54:47.716Z",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\javaw.exe",
        "data_size": 24,
        "destination_ip": "google.com",
        "destination_port": 80,
        "element_type": "network_validate",
        "pid": 3348,
        "process_name": "javaw.exe",
        "protocol": "TCP",
        "source_ip": "10.40.60.90",
        "source_port": 59855,
        "start_time": "2019-04-01T13:54:47.508Z",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    },
    {
        "command_line": "C:\\Program Files\\Java\\jdk-12\\bin\\javaw.exe",
        "data_size": 23,
        "destination_ip": "yahoo.com",
        "destination_port": 80,
        "element_type": "network_validate",
        "pid": 3348,
        "process_name": "javaw.exe",
        "protocol": "TCP",
        "source_ip": "10.40.60.90",
        "source_port": 59854,
        "start_time": "2019-04-01T13:54:47.508Z",
        "user": "DESKTOP-5EB3BRJ\\aryeh"
    }
]
```


