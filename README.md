# Documentation
Simple tool to monitor files in a directory, which should be moved and renamed.

## Requirements
[Java 6](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase6-419409.html#jre-6u45-oth-JPR) or [higher](https://java.com/de/download/)

## Usage
[Download](https://github.com/Xpitfire/MonitoringRenamingTool/releases) release java file.

Open command prompt or PowerShell and execute:
```
java -jar MonitoringRenamer.jar -path <path-to-monitor> -file-prefix <prefix> -output <path-to-output> -interval <seconds>
```

Parameter | Description
--- | ---
-path | path which should be monitored
-file-prefix | prefix of the generated file (includes internal counter)
-output | optional parameter for defining the destination path
-interval | optional interval for the monitoring in seconds

## Example

```
java -jar MonitoringRenamer.jar -path /tmp/test -file-prefix test_ -output /tmp/processed -interval 10
```
